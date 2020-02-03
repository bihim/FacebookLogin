package com.artificialsoft.facebooklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView facebookURL;
    ImageView imageView;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Button button;
    FacebookCallback<LoginResult> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.facebook_id);
        imageView = findViewById(R.id.image_facebook);
        loginButton = findViewById(R.id.login_button);
        facebookURL = findViewById(R.id.facebook_url);
        button = findViewById(R.id.open_facebook);

        //loginButton.setReadPermissions("public_profile");



        callbackManager = CallbackManager.Factory.create();

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    // get the Facebook app if possible
                    MainActivity.this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/2724518177626092"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Facebook app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/PROFILENAME"));
                }
                MainActivity.this.startActivity(intent);
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                callback = new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        textView.setText("User ID: "+loginResult.getAccessToken().getUserId());
                        String imageURL = "https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+"/picture?return_ssl_resources=1";
                        Glide.with(MainActivity.this).load(imageURL).into(imageView);


                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                try
                                {
                                    String url = object.getString("first_name");
                                    facebookURL.setText(url);
                                }
                                catch (JSONException e)
                                {

                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields","email");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                };
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            ProfileTracker profileTracker;
            @Override
            public void onSuccess(final LoginResult loginResult)
            {
                /*if (Profile.getCurrentProfile() == null)
                {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
                        {
                           facebookURL.setText("Facebook URL:"+currentProfile.getLinkUri().toString());
                           profileTracker.stopTracking();
                        }
                    };
                }
                else
                {
                    Profile profile = Profile.getCurrentProfile();
                    facebookURL.setText(profile.getLinkUri().toString());
                }*/
                textView.setText("User ID: "+loginResult.getAccessToken().getUserId());
                String imageURL = "https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+"/picture?return_ssl_resources=1";
                Glide.with(MainActivity.this).load(imageURL).into(imageView);


                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            String url = object.getString("link");
                            Log.d("URL", "onCompleted: "+url);
                            facebookURL.setText(url);
                        }
                        catch (JSONException e)
                        {

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","link");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {

            }
        });
      //  loginButton.setReadPermissions("email");
       // loginButton.registerCallback(callbackManager, callback);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
