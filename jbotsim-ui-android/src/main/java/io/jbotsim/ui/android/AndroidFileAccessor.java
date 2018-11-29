package io.jbotsim.ui.android;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.ParcelFileDescriptor;
import io.jbotsim.core.io.FileManager;

public class AndroidFileAccessor
        extends FileManager {
    private Context ctx;

    public AndroidFileAccessor(Context context) {
        ctx = context;
    }

    @Override
    public InputStream getInputStreamForName(String s) throws IOException {
        if(!isExternalStorageReadable())
            return null;
        InputStream result;
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
        if(!isExternalStorageWritable())
            return null;
        OutputStream result;
        try {
            Uri uri = Uri.parse(s);
            ParcelFileDescriptor pfd = ctx.getContentResolver().openFileDescriptor(uri, "w");
            result = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
        } catch (FileNotFoundException e) {
            result = null;
            e.printStackTrace();
        }

        return result;
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
