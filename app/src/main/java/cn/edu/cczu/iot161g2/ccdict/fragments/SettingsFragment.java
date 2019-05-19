package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import cn.edu.cczu.iot161g2.ccdict.utils.DictHelper;
import im.r_c.android.dbox.DBox;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";

    private static final String PREF_KEY_IMPORT_DICT = "import-dict";
    private static final String PREF_KEY_EXPORT_DICT = "export-dict";

    private static final int REQUEST_CODE_CHOOSE_IMPORT_FILE = 100;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 200;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        setupPreferenceCallbacks();
    }

    private void setupPreferenceCallbacks() {
        Arrays.stream(new Preference[]{
                findPreference(PREF_KEY_IMPORT_DICT),
                findPreference(PREF_KEY_EXPORT_DICT),
        }).forEach(preference -> preference.setOnPreferenceClickListener(this::onPreferenceClick));
    }

    private boolean onPreferenceClick(Preference preference) {
        Log.d(TAG, "onPreferenceClick: " + preference.getKey());
        if (PREF_KEY_IMPORT_DICT.equals(preference.getKey())) {
            onImportDictClicked();
            return true;
        } else if (PREF_KEY_EXPORT_DICT.equals(preference.getKey())) {
            onExportDictClicked();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_IMPORT_FILE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            importDict(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onImportDictClicked() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "请选择要导入的词典文件"), REQUEST_CODE_CHOOSE_IMPORT_FILE);
    }

    private void importDict(Uri uri) {
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Observable.just(uri)
                        .doOnNext(u -> DBox.of(DictEntry.class).drop())
                        .map(u -> DictHelper.importFromChosenFile(getContext(), u))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(count -> Toast.makeText(getContext(), count >= 0 ? "成功导入" + count + "条记录" : "导入失败", Toast.LENGTH_SHORT).show());
            }
        };

        if (DictHelper.hasDict()) {
            // 询问是否覆盖
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("确认覆盖当前词典?")
                    .setPositiveButton("覆盖", listener)
                    .setNegativeButton("取消", listener).show();
        }
    }

    private void onExportDictClicked() {
        if (!DictHelper.hasDict()) {
            // 实际上不会执行, 因为 App#onCreate 已经在第一次启动时导入了默认词典, 这里为了防止意外
            Toast.makeText(getContext(), "当前还没有导入过词典", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasStoragePermission()) {
            requestForStoragePermission(); // Android 6.0 之后需要运行时向用户请求写入外部存储权限
            return;
        }

        exportDict();
    }

    private void exportDict() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dest = new File(downloadDir, "exported_dict.json");
        Observable.just(dest)
                .map(file -> {
                    try (FileOutputStream fis = new FileOutputStream(file)) {
                        List<DictEntry> entryList = DBox.of(DictEntry.class).findAll().results().all();
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fis));
                        bw.write(new Gson().toJson(entryList));
                        return true;
                    } catch (IOException e) { // 打开文件可能出错
                        e.printStackTrace();
                    } catch (SQLiteException e) { // 读取数据库可能出错
                        e.printStackTrace();
                    } catch (JsonParseException e) { // 构造 JSON 可能出错
                        e.printStackTrace();
                    }
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ok -> Toast.makeText(getContext(), ok ? "导出成功, 文件位置: " + dest.getAbsolutePath() : "导出失败", Toast.LENGTH_SHORT).show());
    }

    private boolean hasStoragePermission() {
        return getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "您拒绝了文件写入权限请求, 无法导出", Toast.LENGTH_SHORT).show();
            } else {
                exportDict();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
