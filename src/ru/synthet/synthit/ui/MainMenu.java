package ru.synthet.synthit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ru.synthet.synthit.R;

public class MainMenu extends Activity implements View.OnClickListener {

    private Button btnUsers;
    private Button btnComps;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);
        btnUsers = (Button) findViewById(R.id.buttonUsers);
        btnComps = (Button) findViewById(R.id.buttonComps);
        btnUsers.setOnClickListener(this);
        btnComps.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent myIntent;
        if (v == btnUsers) {
            myIntent = new Intent(MainMenu.this, UsersActivity.class);
        } else {
            myIntent = new Intent(MainMenu.this, CompsActivity.class);
        }
        MainMenu.this.startActivity(myIntent);
    }
}