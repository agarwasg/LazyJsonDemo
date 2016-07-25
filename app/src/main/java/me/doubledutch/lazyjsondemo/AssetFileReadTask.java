package me.doubledutch.lazyjsondemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.InputStream;
import java.util.concurrent.Callable;

import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by saketagarwal on 7/24/16.
 */
public class AssetFileReadTask {

    @NonNull
    private final String fileName;
    @NonNull
    private final Context context;

    public AssetFileReadTask(@NonNull String fileName, @NonNull Context context) {

        this.fileName = fileName;
        this.context = context;
    }

    public Task<String> readJsonFileAsync() {

        return Task.callInBackground(new Callable<String>() {

            @Override
            public String call() throws Exception {
                try {
                    Log.d("lazy", "Reading file");
                    InputStream is = context.getAssets().open(fileName);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    return new String(buffer, "UTF-8");
                } catch (Exception e) {
                    Log.e("lazy", e.getMessage(), e);
                    throw e;
                }
            }
        });

    }

}
