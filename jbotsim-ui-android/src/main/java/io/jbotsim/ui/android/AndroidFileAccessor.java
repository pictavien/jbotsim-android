package io.jbotsim.ui.android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.ParcelFileDescriptor;
import io.jbotsim.io.FileManager;

public class AndroidFileAccessor
        extends FileManager {
    public static final String CONTENT_PREFIX = "content";
    public static final String RESOURCE_PREFIX = "res";
    public static final String ASSETS_PREFIX = "assets";

    private final Context ctx;

    public AndroidFileAccessor(Context context) {
        ctx = context;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @Override
    public InputStream getInputStreamForName(String s) throws IOException {
        int i = s.indexOf(':');
        if (i >= 0) {
            String prefix = s.substring(0, i);
            if (CONTENT_PREFIX.equals(prefix))
                return getInputStreamFromContentURI(s);
            s = s.substring(prefix.length() + 1);
            if (ASSETS_PREFIX.equals(prefix))
                return getInputStreamFromAssets(s);
            if (RESOURCE_PREFIX.equals(prefix))
                return getInputStreamFromResources(s);
            throw new IOException("invalid file location '" + prefix + ":" + s + "'.");
        }

        return getInputStreamFromClassloader(s);
    }

    private InputStream getInputStreamFromContentURI(String s) throws IOException {
        if (!isExternalStorageReadable()) {
            throw new IOException("external storage is not readable.");
        }
        InputStream result;
        Uri uri = Uri.parse(s);
        return ctx.getContentResolver().openInputStream(uri);
    }

    private InputStream getInputStreamFromClassloader(String s) {
        return getClass().getResourceAsStream(s);
    }

    private InputStream getInputStreamFromAssets(String s) throws IOException {
        return ctx.getResources().getAssets().open(s);
    }

    private InputStream getInputStreamFromResources(String s) throws IOException {
        Uri.Builder ub = new Uri.Builder();
        ub.scheme(ContentResolver.SCHEME_ANDROID_RESOURCE);
        try {
            int resid = Integer.parseInt(s);
            Resources resources = ctx.getResources();
            ub.authority(resources.getResourcePackageName(resid));
            ub.path(resources.getResourceTypeName(resid));
            ub.appendPath(resources.getResourceEntryName(resid));
        } catch(NumberFormatException| Resources.NotFoundException e) {
            ub.authority(ctx.getPackageName());
            ub.path(s);
        }

        return ctx.getContentResolver().openInputStream(ub.build());
    }

    @Override
    public OutputStream getOutputStreamForName(String s) {
        if (!isExternalStorageWritable())
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
}
