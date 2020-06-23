package lxx.ru.sendtodisk;

import android.content.Intent;
import com.yandex.disk.rest.Credentials;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        TextView textSend = findViewById(R.id.textSend);


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
            String infoText = String.format("%s %s:\n%s", getString(R.string.sending_str), receivedMimeType, sharedText);
            textSend.setText(infoText);
            // TODO upload sharedText
        }
        else{
            Uri streamUri = (Uri) receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            // generate remote filename
            String remoteFilename = generateRemoteFilename(baseDir);
            String streamFilename = streamUri.getPath();
            remoteFilename = remoteFilename.substring(0, remoteFilename.lastIndexOf("/")) +
                    streamFilename.substring(streamFilename.lastIndexOf("/"), streamFilename.length());

            String infoText = String.format("%s %s\n%s\n%s", getString(R.string.sending_str), receivedMimeType, streamFilename, remoteFilename);

            textSend.setText(infoText);
            yandexDiskHelper.uploadFile(streamFilename, remoteFilename);
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
        String pattern = "/yyyyMMdd/HHmmss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);
        return baseDir + todayAsString + ".txt";
    }

}
