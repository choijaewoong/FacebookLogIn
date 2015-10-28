package com.example.androidchoi.facebooklogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginManager mLoginManager;
    Button loginButton;

    AccessTokenTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        mLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        LoginButton button = (LoginButton)findViewById(R.id.btn_auth_login);
        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                Toast.makeText(MainActivity.this, "id : " + token.getUserId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        loginButton = (Button)findViewById(R.id.btn_login);
        setLabel();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    login(null);
                } else {
                    mLoginManager.logOut();
                }
            }
        });

        Button btn = (Button)findViewById(R.id.btn_profile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    mode = MODE_PROFILE;
                    login(null);
                } else {
                    getProfile();
                }
            }
        });

        btn = (Button)findViewById(R.id.btn_post);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token == null || !token.getPermissions().contains("publish_actions")) {
                    mode = MODE_POST;
                    login(Arrays.asList("publish_actions"), false);
                } else {
                    postMessage();
                }
            }
        });

        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                setLabel();
            }
        };
    }

    private void postMessage() {
        String message = "facebook test message";
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String graphPath = "/me/feed";
        Bundle parameters = new Bundle();
        parameters.putString("message", message);
        parameters.putString("link", "http://developers.facebook.com/docs/android");
        parameters.putString("picture", "https://raw.github.com/fbsamples/.../iossdk_logo.png");
        parameters.putString("name", "Hello Facebook");
        parameters.putString("description", "The 'Hello Facebook' sample  showcases simple …");
        GraphRequest request = new GraphRequest(accessToken, graphPath, parameters, HttpMethod.POST,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject data = response.getJSONObject();
                        String id = (data == null)?null:data.optString("id");
                        if (id == null) {
                            Toast.makeText(MainActivity.this, "error : " + response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "post object id : " + id, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        request.executeAsync();
    }

    public static final int MODE_NONE = -1;
    public static final int MODE_PROFILE = 1;
    public static final int MODE_POST = 2;
    int mode = MODE_NONE;

    private void login(List<String> permissions){
        login(permissions, true);
    }

    private void login(List<String> permissions, boolean isRead){
        mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (mode == MODE_PROFILE) {
                    getProfile();
                    mode = MODE_NONE;
                } else if (mode == MODE_POST) {
                    postMessage();
                    mode = MODE_NONE;
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        if(isRead){
            mLoginManager.logInWithReadPermissions(MainActivity.this, permissions);
        }else{
            mLoginManager.logInWithPublishPermissions(MainActivity.this, permissions);
        }

    }

    private void getProfile() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = new GraphRequest(token, "/me", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject object = response.getJSONObject();
                if (object == null) {
                    String message = response.getError().getErrorMessage();
                    Toast.makeText(MainActivity.this, "error : " + message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "profile : " + object.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        request.executeAsync();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopTracking();
    }

    private void setLabel() {
        if (!isLogin()) {
            loginButton.setText("login");
        } else {
            loginButton.setText("logout");
        }
    }

    private boolean isLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return token==null?false:true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
