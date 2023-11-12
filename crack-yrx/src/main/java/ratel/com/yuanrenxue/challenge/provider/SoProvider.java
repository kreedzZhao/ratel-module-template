package ratel.com.yuanrenxue.challenge.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ratel.com.yuanrenxue.challenge.HookEntry;

public class SoProvider extends ContentProvider {
    private String TAG = "yrx";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        AssetFileDescriptor afd = null;
        Context context = getContext();
        if (context == null){
            throw new FileNotFoundException("context is null, file not found");
        }
        AssetManager am = context.getAssets();
        // /assets/ARM.../
        String path = uri.getPath().substring(8);
        try {
            afd = am.openFd(path);
        } catch (IOException e) {
            Log.i(TAG, "openAssetFile: "+e.getMessage());
            e.printStackTrace();
        }
        return afd;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
