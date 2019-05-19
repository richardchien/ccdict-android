package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Arrays;

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

    private static final int REQUEST_CODE_CHOOSE_FILE = 100;

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
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            startActivityForResult(Intent.createChooser(chooseFile, "请选择要导入的词典文件"), REQUEST_CODE_CHOOSE_FILE);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                importDict(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
