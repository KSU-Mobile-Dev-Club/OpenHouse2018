package com.example.jeremy.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class SettingsActivity extends AppCompatActivity {

    private final int CODE_USERS_OBTAINED = 1001;
    private UserCollection mUserCollection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Users Following");

        //get the list of all users (hard coded into UserCollection and display it in this activity)
        mUserCollection = UserCollection.getInstance();
        ListView userLV = findViewById(R.id.settings_listview);
        UserAdapter adapter = new UserAdapter(this, R.layout.user_item_layout, mUserCollection.getAllUsers());
        userLV.setAdapter(adapter);

        Button tweetsButton = (Button) findViewById(R.id.main_activity_button);
        tweetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
