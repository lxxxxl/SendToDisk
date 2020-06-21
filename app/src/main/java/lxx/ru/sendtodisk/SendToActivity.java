package lxx.ru.sendtodisk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SendToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);

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
            String infoText = String.format("%s %s\n%s", getString(R.string.sending_str), receivedMimeType, streamUri.toString());
            textSend.setText(infoText);
            // TODO upload streamUri
        }
    }

}
