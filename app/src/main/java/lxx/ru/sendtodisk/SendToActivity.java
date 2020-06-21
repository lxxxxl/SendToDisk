package lxx.ru.sendtodisk;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class SendToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        //get the received intent
        Intent receivedIntent = getIntent();
        //get the action
        String receivedAction = receivedIntent.getAction();


        //make sure it's an action and type we can handle
        if(receivedAction.equals(Intent.ACTION_SEND)){
            //content is being shared
        }
        else if(receivedAction.equals(Intent.ACTION_MAIN)){
            //app has been launched directly, not from share list
        }


        //find out what we are dealing with
        String receivedType = receivedIntent.getType();
    }

}
