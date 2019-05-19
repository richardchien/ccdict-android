package cn.edu.cczu.iot161g2.ccdict.utils;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import im.r_c.android.dbox.DBox;

public class DictHelper {
    public static boolean hasDict() {
        try {
            return DBox.of(DictEntry.class).findAll().results().first() != null;
        } catch (SQLiteException ignored) {
        }
        return false;
    }

    public static long importFromChosenFile(Context context, Uri uri) {
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            return importFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long importFromAssets(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            return importFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static long importFromInputStream(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return Arrays.stream(new Gson().fromJson(reader, DictEntry[].class))
                    .map(entry -> DBox.of(DictEntry.class).save(entry))
                    .filter(Boolean::booleanValue)
                    .count();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
