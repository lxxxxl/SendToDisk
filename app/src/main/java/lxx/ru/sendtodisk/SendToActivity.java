package lxx.ru.sendtodisk;

import android.content.Intent;
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


        //make sure it's an action and type we can handle
        if(!receivedAction.equals(Intent.ACTION_SEND)){
            return;
        }

        String sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        String infoText = String.format("%s %s (%s)", "Sending", receivedIntent.getType(), sharedText);
        textSend.setText(infoText);
    }

}
