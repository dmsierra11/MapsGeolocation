package com.example.locationservices;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by danielsierraf on 4/26/17.
 */

public class FileManager {
    private static final String TAG = "FileManager";

    public File getExternalStorageDir(String albumName){
        // Get the directory for the app's private pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }

        return file;
    }

//    public void copyFileFromAssets(Context context, String filename){
//
//        File app_folder = getExternalStorageDir(context.getString(R.string.app_name));
//
//        if (!(new File(app_folder, filename)).exists()) {
//            try {
//
//                AssetManager assetManager = context.getAssets();
//                InputStream in = assetManager.open(filename);
//                //GZIPInputStream gin = new GZIPInputStream(in);
//                OutputStream out = new FileOutputStream(filename);
//
//                // Transfer bytes from in to out
//                byte[] buf = new byte[1024];
//                int len;
//                //while ((lenf = gin.read(buff)) > 0) {
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//                in.close();
//                //gin.close();
//                out.close();
//
//                Log.d(TAG, "Copied " + filename);
//            } catch (IOException e) {
//                Log.e(TAG, "Was unable to copy " + filename + " " + e.toString());
//            }
//        }
//    }

    public static void copyAssets(Context appContext, File dir) {
        dir.mkdirs();

        AssetManager assetManager = appContext.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        if (files != null){
            for(String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(dir, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            GeoJSONObject jsonObject = GeoJSON.parse(inputStream);

            Log.d("FileManager", jsonObject.toJSON().toString());
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
