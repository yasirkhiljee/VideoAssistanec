package com.example.videoassistance;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class DownloadTask extends AsyncTask<String, Integer, String>{
    private Context mContext;
    String  fileN;
    private PowerManager.WakeLock mWakeLock;
    public DownloadTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();
            fileN = "_fileDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".pdf";
            File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/"+Constants.FOLDER_NAME+"/", fileN);
            output = new FileOutputStream(filename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        if (result != null)
            Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext, "File downloaded", Toast.LENGTH_SHORT).show();
        MediaScannerConnection.scanFile(mContext,
                new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/"+Constants.FOLDER_NAME +"/"+ fileN}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String newpath, Uri newuri) {
                        Log.i("ExternalStorage", "Scanned " + newpath + ":");
                        Log.i("ExternalStorage", "-> uri=" + newuri);
                    }
                });

    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }
}
