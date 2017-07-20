package com.andrewlaurien.chatApp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.andrewlaurien.chatApp.R;
import com.andrewlaurien.chatApp.fragments.LogInFragment;
import com.andrewlaurien.chatApp.fragments.MainFragment;
import com.andrewlaurien.chatApp.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentInteractionListener, LogInFragment.OnLoginFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {


    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static String PREF_NAME = "AWESOMECLAN";
    public static CallbackManager callbackManager;
    public static String TAG = "MainActivity";
    public static Context mcontext;
    public static String prefIsLoggedIn = "IsLoggedIN";
    public static User mUser;
    public static String CityName = "";


    String userid;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    private static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }

    private static MainActivity mainActivity;
    FragmentTransaction transaction;
    Fragment fragment;
    FragmentManager manager;


    FirebaseUser currentUser;
    public static FirebaseStorage fbStorage;
    public static FirebaseAuth mAuth;

    final int PERMISSION_LOCATION = 111;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    public static Location mylocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.setMainActivity(this);

        mcontext = this;
        mAuth = FirebaseAuth.getInstance();
        fbStorage = FirebaseStorage.getInstance();


        FacebookSdk.sdkInitialize(mcontext);
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(R.id.container_main);


        currentUser = mAuth.getCurrentUser();
        updateUI();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // ...
                                mylocation = location;

                                Log.d("ChatApp", "Old Location" + location);

                            }
                        }
                    });
        }

//        if (prefs.getBoolean(prefIsLoggedIn, false)) {
//            fragment = new MainFragment();
//        } else {
//            fragment = new LogInFragment();
//        }
//
//        transaction = manager.beginTransaction();
//        transaction.add(R.id.container_main, fragment);
//        transaction.commit();


    }


    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

        // Check if user is signed in (non-null) and update UI accordingly.
        // currentUser = mAuth.getCurrentUser();

        //if (currentUser == null) {
        // updateUI();
        //}

        //updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    public void updateUI() {
        //if (prefs.getBoolean(prefIsLoggedIn, false)) {

        Toast.makeText(getBaseContext(), "Current User = " + currentUser, Toast.LENGTH_SHORT).show();

        if (currentUser != null) {
            userid = FirebaseAuth.getInstance().getCurrentUser().getUid();


            mUser = new User(prefs.getString("id", ""), prefs.getString("name", ""),
                    prefs.getString("middle_name", ""),
                    prefs.getString("last_name", ""), prefs.getString("first_name", ""),
                    prefs.getString("link", ""));


            fragment = new MainFragment();
        } else {
            fragment = new LogInFragment();
        }


        transaction = manager.beginTransaction();
        transaction.replace(R.id.container_main, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getBaseContext(), "" + data.getExtras(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();


                } else {
                    //show a dialog saying something like, "I can't run your location dummy - you denied permission!"
                }
            }
        }
    }


    //region  Functions

    public void startLocationServices() {
        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);
        } catch (SecurityException exception) {

        }
    }

    public void LoadMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_main, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    public void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "" + userProfile.getProfilePictureUri(500, 500));
                            //Log.d(TAG, "signInWithCredential:success");
                            GraphRequest request = GraphRequest.newMeRequest(
                                    token, new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            try {
                                                Log.d(TAG, "" + response.getJSONObject());

                                                mUser = new User(response.getJSONObject().getString("id"), response.getJSONObject().getString("name"),
                                                        response.getJSONObject().getString("middle_name"),
                                                        response.getJSONObject().getString("last_name"), response.getJSONObject().getString("first_name"),
                                                        response.getJSONObject().getString("link"));


                                                editor.putString("id", response.getJSONObject().getString("id"));
                                                editor.putString("name", response.getJSONObject().getString("name"));
                                                editor.putString("middle_name", response.getJSONObject().getString("middle_name"));
                                                editor.putString("last_name", response.getJSONObject().getString("last_name"));
                                                editor.putString("link", response.getJSONObject().getString("link"));
                                                editor.putString("name", response.getJSONObject().getString("last_name"));
                                                editor.putString("first_name", response.getJSONObject().getString("first_name"));
                                                editor.putBoolean(prefIsLoggedIn, true);
                                                editor.commit();


                                                userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").setValue(mUser);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,first_name,gender,last_name,middle_name");
                            request.setParameters(parameters);
                            request.executeAsync();
                            currentUser = mAuth.getCurrentUser();

                            updateUI();

                            //LoadMainFragment();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //endregion


    //region GoogleAPI Listener

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.d(TAG, "" + location);

            mylocation = location;


//            Geocoder gcd = new Geocoder(getBaseContext());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                if (addresses != null) {
//                    if (addresses.size() > 0)
//                        CityName = addresses.get(0).getLocality();
//                    Log.d(TAG, "CityName=" + CityName);
//
//                }
//            } catch (IOException ioException) {
//                // Catch network or other I/O problems.
//                ioException.printStackTrace();
//                Log.e(TAG,"Error : "+ ioException.getLocalizedMessage());
//            } catch (IllegalArgumentException illegalArgumentException) {
//                // Catch invalid latitude or longitude values.
//                Log.e(TAG, "" + "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), illegalArgumentException);
//            }


        }


    }


    //endregion


    //region Interface Listener

    @Override
    public void onLoginFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMainFragmentInteraction(Uri uri) {

    }


    //endregion


}
