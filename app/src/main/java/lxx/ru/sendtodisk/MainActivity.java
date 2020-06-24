package lxx.ru.sendtodisk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;

        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etToken = findViewById(R.id.etToken);
        Button bSave = findViewById(R.id.bSave);

        etUsername.setText(pref.getString("username",""));
        etToken.setText(pref.getString("token",""));

        // Request permission to access files
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 1);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("username", etUsername.getText().toString());
                ed.putString("token", etToken.getText().toString());
                ed.commit();
                Toast.makeText(MainActivity.this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        });



    }
}
