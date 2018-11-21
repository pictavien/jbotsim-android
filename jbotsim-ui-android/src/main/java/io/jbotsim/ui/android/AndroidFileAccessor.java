package io.jbotsim.ui.android;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.jbotsim.core.io.FileAccessor;

public class AndroidFileAccessor
        implements FileAccessor {
    private Context ctx;

    public AndroidFileAccessor(Context context) {
        ctx = context;
    }

    @Override
    public InputStream getInputStreamForName(String s) throws IOException {
        if(!isExternalStorageReadable())
            return null;
        InputStream result = null;
        try {
            Uri uri = Uri.parse(s);
            result = ctx.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public OutputStream getOutputStreamForName(String s) throws IOException {
        return null;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
