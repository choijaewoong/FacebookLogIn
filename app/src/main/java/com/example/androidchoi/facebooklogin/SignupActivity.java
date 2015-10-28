package com.example.androidchoi.facebooklogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;

/**
 * Created by Tacademy on 2015-10-28.
 */
public class SignupActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btn = (Button)findViewById(R.id.btn_finish);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkManager.getInstance().signupFacebook(SignupActivity.this, "message", new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        AccessToken token = AccessToken.getCurrentAccessToken();
                        PropertyManager.getInstance().setFacebookId(token.getUserId());
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(int code) {

                    }
                });
            }
        });
    }
}
