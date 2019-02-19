package a3wish.main.com.aims212.sam.a3wish.member;

import android.accounts.AccountManager;
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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;
import a3wish.main.com.aims212.sam.a3wish.upload.ApiAsyncTaskPic;
import a3wish.main.com.aims212.sam.a3wish.upload.UploadPicActivity;

public class UpdateMemberActivityV2 extends AppCompatActivity {
    private Toolbar toolbar;


    private Button idBt;
    private EditText idEdt, nameEdt;
    private RadioButton boyRdb, girlRdb, hopeRdb, achieveRdb;

    private String token, email, name, sex, character, image, imageOld, member_sn, reasponStr, emailOld;

    private String str[];
    private ProgressDialong progressDialong;


    String TAG = "UpdateMemberActivity";


    GoogleAccountCredential credential, credentialOld;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    Drive mService;

    ProgressDialog mProgress;
    ProgressDialog mUploadProgress;
    private ProgressDialog testProgress;

    boolean checkDriveServer = false;

    boolean checkOthetEven=false;
    private static final java.util.Collection<String> SCOPES =
            DriveScopes.all();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member_v2);

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialMember();

        initial();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Drive API ...");
    }

    private void initialMember() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        member_sn = sharedPreferences.getString(getString(R.string.MEMBER_SN), "0");

        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.d(TAG + "myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        emailOld = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");

        Log.d(TAG + "myEmail:", email);
        name = sharedPreferences.getString(getString(R.string.MEMBER_NAME), "0");
        Log.d(TAG + "myName:", name);
        sex = sharedPreferences.getString(getString(R.string.MEMBER_SEX), "0");
        Log.d(TAG + "mySex:", sex);
        character = sharedPreferences.getString(getString(R.string.MEMBER_CHARACTER), "0");
        Log.d(TAG + "myCharacter:", character);
        image = sharedPreferences.getString(getString(R.string.MEMBER_IMAGE), "0");

        imageOld = sharedPreferences.getString(getString(R.string.MEMBER_IMAGE), "0");
    }


    private void initial() {


        idEdt = (EditText) this.findViewById(R.id.idEdt);
        idEdt.setEnabled(false);
        idEdt.setFocusable(false);
        idEdt.setInputType(InputType.TYPE_NULL);

        idEdt.setText(email);
        idEdt.setTextColor(getResources().getColor(R.color.colorBlack));
        idBt = (Button) this.findViewById(R.id.idBt);

        nameEdt = (EditText) this.findViewById(R.id.nameEdt);
        nameEdt.setText(name);
        nameEdt.setTextColor(getResources().getColor(R.color.colorBlack));

        boyRdb = (RadioButton) this.findViewById(R.id.boyRdb);
        girlRdb = (RadioButton) this.findViewById(R.id.girlRdb);
        if (sex.equals("True"))
            boyRdb.setChecked(true);
        else
            girlRdb.setChecked(true);

        hopeRdb = (RadioButton) this.findViewById(R.id.hopeRdb);
        achieveRdb = (RadioButton) this.findViewById(R.id.achieveRdb);

        if (character.equals("True"))
            hopeRdb.setChecked(true);
        else
            achieveRdb.setChecked(true);

        credential = GoogleAccountCredential.usingAudience(getApplicationContext(),
                "a3wish.main.com.aims212.sam.a3wish.member")
                .setSelectedAccountName(email);


    }

    public void OnAccountBT(View view) {

        if (isGooglePlayServicesAvailable()) {
            if (isDeviceOnline()) {
                if (image.equals(""))
                    chooseAccount();

                else {

                    if (!checkDriveServer) {
                        showDialogOK("要更換email，會刪除原先圖片",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                credentialOld = GoogleAccountCredential.usingOAuth2(
                                                        getApplicationContext(), SCOPES)
                                                        .setBackOff(new ExponentialBackOff())
                                                        .setSelectedAccountName(emailOld);

                                                mService = new com.google.api.services.drive.Drive.Builder(
                                                        transport, jsonFactory, credentialOld)
                                                        .setApplicationName("Drive API Android Quickstart")
                                                        .build();

                                                mProgress.show();
                                                new ApiAsyncTaskUpdateMemberData(UpdateMemberActivityV2.this).execute();

                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:

                                                //finish();
                                                break;
                                        }
                                    }
                                });


                    }else{
                        if(checkOthetEven)
                            chooseAccount();

                    }


                }

            }
        }


    }


    public void OnUpdateBT(View view) {
        if (!isDeviceOnline())
            return;


        if (idEdt.getText().toString().equals("")) {
            idEdt.setFocusable(true);
            Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
        } else {
            String mSex;
            if (boyRdb.isChecked())
                mSex = "True";
            else
                mSex = "False";

            String mCharacter;
            if (hopeRdb.isChecked())
                mCharacter = "True";
            else
                mCharacter = "False";

            sex = mSex;
            character = mCharacter;

            if (!image.equals("") && !emailOld.equals( idEdt.getText().toString()))
                image = "";


            progressDialong = new ProgressDialong(UpdateMemberActivityV2.this);
            progressDialong.DialongShow("修改中...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {


                        str = new String[]{emailOld, idEdt.getText().toString(), nameEdt.getText().toString(), sex, character, image, token};

                        reasponStr = Networks.post(ToolArray.UpdateMemberDataV3, str);


                        if (reasponStr.equals("ok")) {
                            if (!imageOld.equals("") && !emailOld.equals( idEdt.getText().toString())) {
                                deleteFile(mService, imageOld);
                                Log.e("image__deleteFile", "this");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        reasponStr = "upload fail";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("myServeLog", reasponStr);
                            progressDialong.DialongCancel();
                            if (reasponStr.equals("ok")) {
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.MEMBER_EMAIL), idEdt.getText().toString());
                                editor.putString(getString(R.string.MEMBER_NAME), nameEdt.getText().toString());
                                editor.putString(getString(R.string.MEMBER_SEX), sex);
                                editor.putString(getString(R.string.MEMBER_IMAGE), image);
                                editor.putString(getString(R.string.MEMBER_CHARACTER), character);
                                editor.commit();
                                ToolArray.refreshState = true;
//                                startActivity(new Intent(UpdateMemberActivityV2.this, MainActivity.class));
                                finish();
                            } else if (reasponStr.equals("upload fail")) {
                                checkOthetEven=true;
                                image=imageOld;
                                Toast.makeText(UpdateMemberActivityV2.this, "網路異常", Toast.LENGTH_LONG).show();
                            } else {
                                checkOthetEven=true;
                                image=imageOld;
                                Toast.makeText(UpdateMemberActivityV2.this, "信箱重複", Toast.LENGTH_LONG).show();
                                idEdt.setFocusable(true);
                                idEdt.setTextColor(getResources().getColor(R.color.colorAccent));
                                chooseAccount();
                            }


                        }
                    });

                }
            }).start();

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
//            startActivity(new Intent(UpdateMemberActivityV2.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(new Intent(UpdateMemberActivityV2.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {


                        credential.setSelectedAccountName(accountName);

                        idEdt.setText(accountName);
//                        SharedPreferences settings =
//                                getPreferences(Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {


//                    showDialogOK("沒有google帳號，無法使用本服務", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case DialogInterface.BUTTON_POSITIVE:
//                                    chooseAccount();
//                                    break;
//                                case DialogInterface.BUTTON_NEGATIVE:
//                                    finish();
//                                    break;
//                            }
//                        }
//                    });

                }
                break;


            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {

                    Log.e("REQUEST_AUTHORIZATION", "chooseAccount()");
                    checkDriveServer = false;
//                    idBt.setTextColor(Color.parseColor("#FF4081"));
                    idBt.setText("尚未獲得更換權限");

                } else {
                    Log.e("REQUEST_AUTHORIZATION", "Sussessful");
                    checkDriveServer = true;
                    checkOthetEven=true;
                    chooseAccount();
//                    idBt.setTextColor(Color.parseColor("#FF4081"));
                    idBt.setText("可上傳圖片");

                }
                break;
        }


    }


    public void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("確認", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
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


    private boolean deleteFile(Drive service, String fileId) {
        boolean mState = true;
        try {
            service.files().delete(fileId).execute();


        } catch (IOException e) {
            System.out.println("An error occurred: " + e);

            Log.e("An error occurred: ", "" + e);
            mState = false;
        }

        return mState;
    }

}


