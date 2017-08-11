package com.example.nanda.agrinai2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Home extends Activity {
    TextView tv;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv = (TextView) findViewById(R.id.tv);

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);
        userName = sp.getString("name", "0");
        tv.setText("Hello  " + userName + ",");
    }
}
