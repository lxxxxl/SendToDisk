package lxx.ru.sendtodisk;

import android.content.ContentResolver;
import android.content.Intent;
import com.yandex.disk.rest.Credentials;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SendToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);

        // get username/token from local storage
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = pref.getString("username","");
        String token = pref.getString("token","");

        // get base dir on Yandex Disk
        String baseDir = getString(R.string.yadisk_topfolder);

        // init Yandex Disk API helper class
        Credentials credentials = new Credentials(username,token);
        YandexDiskHelper yandexDiskHelper = new YandexDiskHelper(credentials, new YandexDiskHelper.OnApiCallFinishListener(

        ) {
            @Override
            public void onApiCallFinish(boolean success) {
                String strResult = getString(R.string.upload_fail);
                if (success)
                    strResult = getString(R.string.upload_ok);

                showToast(strResult);
                SendToActivity.this.finish();
            }
        });

        //get the received intent
        Intent receivedIntent = getIntent();
        //get the action
        String receivedAction = receivedIntent.getAction();

        //make sure it's an action and type we can handle
        if(!receivedAction.equals(Intent.ACTION_SEND)){
            return;
        }

        // check if text was sent to us
        String sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // if it is link to something
            if (URLUtil.isHttpUrl(sharedText) || URLUtil.isHttpsUrl(sharedText)){
                yandexDiskHelper.saveFromUrl(sharedText, generateRemoteFilename(baseDir,sharedText));
            }
            // ... or it is text note
            else {
                try {
                    File tempFile = File.createTempFile("prefix", "txt", this.getCacheDir());
                    BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
                    bw.write(sharedText);
                    bw.close();
                    yandexDiskHelper.uploadFile(tempFile, generateRemoteFilename(baseDir));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // check if file was sent to us
        Uri streamUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (streamUri != null){
            // generate remote filename
            String origFilename = streamUri.getPath();
            if (streamUri.getScheme().equals("content"))
                origFilename = contentUriToFilename(streamUri);
            String remoteFilename = generateRemoteFilename(baseDir, origFilename);

            yandexDiskHelper.uploadFile(new File(origFilename), remoteFilename);
        }
    }

    public void showToast(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SendToActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });

    }

     public String generateRemoteFilename(String baseDir){
        DateFormat df = new SimpleDateFormat("/yyyyMMdd/HHmmss", Locale.getDefault());
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);
        return baseDir + todayAsString + ".txt";
    }

    public String generateRemoteFilename(String baseDir, String sourceFilename){
        DateFormat df = new SimpleDateFormat("/yyyyMMdd", Locale.getDefault());
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);

        String filename = sourceFilename.substring(sourceFilename.lastIndexOf("/"));

        return baseDir + todayAsString + filename;
    }

    public String contentUriToFilename(Uri uri){
        // https://stackoverflow.com/a/13275282/13040709
        String path = "";
        String[] projection = {MediaStore.MediaColumns.DATA};

        ContentResolver cr = getApplicationContext().getContentResolver();
        Cursor metaCursor = cr.query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return path;
    }
}
