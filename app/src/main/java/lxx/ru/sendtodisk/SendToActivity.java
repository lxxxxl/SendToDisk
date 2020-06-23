package lxx.ru.sendtodisk;

import android.content.ContentResolver;
import android.content.Intent;
import com.yandex.disk.rest.Credentials;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.Toast;

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

        // get base dir on Yandex Disk
        String baseDir = getString(R.string.yadisk_topfolder);

        // init Yandex Disk API helper class
        Credentials credentials = new Credentials(getString(R.string.yadisk_user),getString(R.string.yadisk_token));
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
        // get MIME type
        String receivedMimeType = receivedIntent.getType();


        //make sure it's an action and type we can handle
        if(!receivedAction.equals(Intent.ACTION_SEND)){
            return;
        }

        if (receivedMimeType.startsWith("text")) {
            String sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            // TODO upload sharedText
        }
        else{
            Uri streamUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            // generate remote filename
            String origFilename = streamUri.getPath();
            if (streamUri.getScheme().equals("content"))
                origFilename = contentUriToFilename(streamUri);
            String remoteFilename = generateRemoteFilename(baseDir, origFilename);

            yandexDiskHelper.uploadFile(origFilename, remoteFilename);
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
