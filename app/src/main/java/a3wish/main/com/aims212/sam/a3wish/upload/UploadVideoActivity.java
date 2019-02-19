package a3wish.main.com.aims212.sam.a3wish.upload;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;

import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class UploadVideoActivity extends AppCompatActivity {
    private Toolbar toolbar;


    ProgressDialog mProgress;
    ProgressDialog mUploadProgress;
    Button uploadBT;

    VideoView mVideoView;
    MediaController mc;


    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    YouTube mService = null;

    public static final String[] SCOPES = {Scopes.PROFILE, YouTubeScopes.YOUTUBE};


    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    boolean checkDriveServer = false;


    private SharedPreferences sharedPreferences;


    private Uri mFileUri;

    private EditText achieveContentEdt;


    private String member_sn, token, email, password, name, sex, image, character, reasponStr123;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_video_activtiy);

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Youtube API ...");
        mUploadProgress = new ProgressDialog(this);

        initialMember();
        achieveContentEdt = (EditText) this.findViewById(R.id.achieveContentEdt);

        uploadBT = (Button) findViewById(R.id.upload_button);
        Intent intent = getIntent();

        mFileUri = intent.getData();
        position = Integer.valueOf(intent.getStringExtra("position"));

        Log.e("Integer.valueOf(intent.getStringExtra(\"position\"));", "" + position);
        reviewVideo(mFileUri);

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
       // email = "aims212.3wish@gmail.com";
        Log.e("email", email);


        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(email);


        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();

        checkState();
    }


    private void initialMember() {

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

        member_sn = sharedPreferences.getString(getString(R.string.MEMBER_SN), "0");
        Log.e("HeadViewActivity_member_sn:", member_sn);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.e("HeadViewActivity_myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        Log.e("HeadViewActivity_myEmail:", email);
        password = sharedPreferences.getString(getString(R.string.MEMBER_PASSWORD), "0");
        Log.e("HeadViewActivity_myPassword:", password);
        name = sharedPreferences.getString(getString(R.string.MEMBER_NAME), "0");
        Log.e("HeadViewActivity_myName:", name);
        sex = sharedPreferences.getString(getString(R.string.MEMBER_SEX), "0");
        Log.e("HeadViewActivity_mySex:", sex);
        character = sharedPreferences.getString(getString(R.string.MEMBER_CHARACTER), "0");
        Log.e("HeadViewActivity_myCharacter:", character);
        image = sharedPreferences.getString(getString(R.string.MEMBER_IMAGE), "0");

        if (image.equals("")) {

            Log.e("HeadViewActivity_image:", "是空直");
        }

        Log.e("HeadViewActivity_image:", image);


    }

    public void checkState() {
        if (isGooglePlayServicesAvailable()) {
            refreshResults();

        } else {
//            mStatusText.setText("Google Play Services required: " +
//                    "after installing, close and relaunch this app.");
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            showDialogOK("您的Google帳號無效，需更換，否則無法使用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ToolArray.changeAccountState = true;
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                                    uploadBT.setText("Google帳號有問題，需要更換");
                                    //finish();
                                    break;
                            }
                        }
                    });
        } else {
            if (isDeviceOnline()) {
                if (checkDriveServer) {

                    Toast.makeText(this, "上傳拉！", Toast.LENGTH_SHORT).show();
                    ToolArray.uploadState = true;
                    upLoadData();
                    finish();

                } else {
                    mProgress.show();
                    new ApiAsyncTaskVideo(UploadVideoActivity.this).execute();
                }


            } else {
//                mStatusText.setText("No network connection available.");
                uploadBT.setTextColor(Color.parseColor("#FF4081"));
                uploadBT.setText("沒有網路！");

            }
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        getParent(),
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }


    public void uploadVideoOnclick(View view) {

        checkState();


    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {

                    Log.e("REQUEST_AUTHORIZATION", "chooseAccount()");
                    checkDriveServer = false;
                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                    uploadBT.setText("尚未獲得上傳權限");

                } else {
                    Log.e("REQUEST_AUTHORIZATION", "Sussessful");
                    checkDriveServer = true;
                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                    uploadBT.setText("可上傳影片");

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("更換密碼", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
    }


    private void reviewVideo(Uri mFileUri) {
        try {
            mVideoView = (VideoView) findViewById(R.id.videoView);
            mc = new MediaController(this);
            mVideoView.setMediaController(mc);
            mVideoView.setVideoURI(mFileUri);
            mc.show();
            mVideoView.start();
        } catch (Exception e) {
            Log.e(this.getLocalClassName(), e.toString());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
        return super.onOptionsItemSelected(item);
    }


    private void upLoadData() {

        if (mFileUri != null) {
            Intent uploadIntent = new Intent(this, UploadService.class);
            uploadIntent.setData(mFileUri);
            uploadIntent.putExtra("sn", ToolArray.needHelpeListArryV2[position].getWish_Sn().toString());
            uploadIntent.putExtra("wish", ToolArray.needHelpeListArryV2[position].getMember_Sn().toString());
            uploadIntent.putExtra("help", "" + member_sn);
            uploadIntent.putExtra("note", "" + achieveContentEdt.getText().toString());
            uploadIntent.putExtra("name", "" + ToolArray.needHelpeListArryV2[position].getWish_Name().toString());
            uploadIntent.putExtra("content", "" + ToolArray.needHelpeListArryV2[position].getWish_Content().toString());


            if (ToolArray.needHelpeListArryV2[position].getWish_Type().toString().equals("2"))
                uploadIntent.putExtra("type", "1");

            else
                uploadIntent.putExtra("type", "2");


            startService(uploadIntent);

            // Go back to MainActivity after upload
            finish();
        }

    }

}
