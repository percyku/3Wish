package a3wish.main.com.aims212.sam.a3wish.member;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {
    private Toolbar toolbar;

    private EditText idEdt, passwordsEdt, passwordsAgainEdt, nameEdt;
    private ImageView idIm, passwordsIm, passwordsAgainIm, nameIm;
    private RadioButton boyRdb, girlRdb, hopeRdb, achieveRdb;
    private SharedPreferences sharedPreferences;

    private String str[];

    private String reasponStr, token, sex, character;
    private ProgressDialong progressDialong;

    GoogleAccountCredential credential;


    private static final int REQUEST_ACCOUNT_PICKER = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        initial();


    }

    private void initial() {

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.v("member_token", token);

        idEdt = (EditText) this.findViewById(R.id.idEdt);
        idEdt.setInputType(InputType.TYPE_NULL);

        passwordsEdt = (EditText) this.findViewById(R.id.passwordsEdt);
        passwordsAgainEdt = (EditText) this.findViewById(R.id.passwordsAgainEdt);
        nameEdt = (EditText) this.findViewById(R.id.nameEdt);

        idEdt.addTextChangedListener(this);
        passwordsEdt.addTextChangedListener(this);
        passwordsAgainEdt.addTextChangedListener(this);
        nameEdt.addTextChangedListener(this);

        idIm = (ImageView) this.findViewById(R.id.idIm);
        passwordsIm = (ImageView) this.findViewById(R.id.passwordsIm);
        passwordsAgainIm = (ImageView) this.findViewById(R.id.passwordsAgainIm);
        nameIm = (ImageView) this.findViewById(R.id.nameIm);

        boyRdb = (RadioButton) this.findViewById(R.id.boyRdb);
        girlRdb = (RadioButton) this.findViewById(R.id.girlRdb);
        hopeRdb = (RadioButton) this.findViewById(R.id.girlRdb);
        achieveRdb = (RadioButton) this.findViewById(R.id.girlRdb);


        credential = GoogleAccountCredential.usingAudience(getApplicationContext(),
                "a3wish.main.com.aims212.sam.a3wish.member")
                .setSelectedAccountName(sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "null"));

        if (credential.getSelectedAccountName() == null) {
            if (isGooglePlayServicesAvailable()) {
                if (isDeviceOnline()) {
                    chooseAccount();
                }
            }
        }


    }


    public void OnRegisterBT(View view) {

        if (idEdt.getText().toString().equals("") || passwordsEdt.getText().toString().equals("") || passwordsAgainEdt.getText().toString().equals("") || nameEdt.getText().toString().equals("")) {
            Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
        }
//        else if(idIm.isShown()||passwordsEdt.isShown()||passwordsAgainEdt.isShown()){
//            Toast.makeText(this, "輸入不正確", Toast.LENGTH_SHORT).show();
//        }
        else {
            String sex, character;
            if (boyRdb.isChecked())
                sex = "true";
            else
                sex = "false";
            if (hopeRdb.isChecked())
                character = "true";
            else
                character = "false";
            str = new String[]{idEdt.getText().toString(), passwordsEdt.getText().toString(), nameEdt.getText().toString(), sex, character, sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0")};
            progressDialong = new ProgressDialong(RegisterActivity.this);
            progressDialong.DialongShow("驗證中...");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        reasponStr = Networks.post(ToolArray.RegisterV2, str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        reasponStr = "upload fail";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("myServeLog", reasponStr);
                            progressDialong.DialongCancel();
                           // String str[]=reasponStr.split(",");
                            if (reasponStr.equals("註冊過了")) {
                                Toast.makeText(RegisterActivity.this, "信箱重複", Toast.LENGTH_LONG).show();
                                idEdt.setFocusable(true);
                                idEdt.setTextColor(getColor(R.color.colorAccent));
                                idIm.setImageResource(android.R.drawable.ic_delete);
                                chooseAccount();
                            } else if (reasponStr.equals("upload fail")) {
                                Toast.makeText(RegisterActivity.this, "網路異常", Toast.LENGTH_LONG).show();
                            } else {
                                saveMemberData();
                                ToolArray.refreshState = true;
                                finish();
                                //startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }


                        }
                    });

                }
            }).start();
        }

//        sex="true";character="true";
//        str=new String[]{"jod96385256@gmail.com","123456","percyku","true","true",token};
//        progressDialong =new ProgressDialong(RegisterActivity.this);
//        progressDialong.DialongShow("驗證中...");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                try {
//                    reasponStr= Networks.post("http://140.137.51.169:8080/RegisterMember.ashx",str);
////                    reasponStr= Networks.post("http://140.137.51.169:8080/Handler.ashx",str);
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    reasponStr="upload fail";
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.v("myServeLog",reasponStr);
//                        progressDialong.DialongCancel();
//                        if(reasponStr.equals("ok")){
//                            saveMemberData();
//                            startActivity(new Intent(RegisterActivity.this,Main2Activity.class));
//                        }
//                        else if (reasponStr.equals("upload fail")){
//                            Toast.makeText(RegisterActivity.this,"網路異常",Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            Toast.makeText(RegisterActivity.this,"信箱重複",Toast.LENGTH_LONG).show();
//                            idEdt.setFocusable(true);
//                            idEdt.setTextColor(getColor(R.color.colorAccent));
//                        }
//
//
//                    }
//                });
//
//            }
//        }).start();

    }

    private void saveMemberData() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.MEMBER_SN), reasponStr);
        editor.putString(getString(R.string.MEMBER_EMAIL), idEdt.getText().toString());
        editor.putString(getString(R.string.MEMBER_PASSWORD), passwordsAgainEdt.getText().toString());
        editor.putString(getString(R.string.MEMBER_NAME), nameEdt.getText().toString());
        editor.putString(getString(R.string.MEMBER_SEX), sex);
        editor.putString(getString(R.string.MEMBER_CHARACTER), character);
        editor.commit();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {


        if (idEdt.getText().hashCode() == editable.hashCode()) {
            idIm.setVisibility(View.VISIBLE);
            Matcher idmatcher = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$").matcher(idEdt.getText().toString());
            if (idmatcher.matches())
                idIm.setImageResource(android.R.drawable.ic_notification_overlay);
            else
                idIm.setImageResource(android.R.drawable.ic_delete);

        } else if (passwordsEdt.getText().hashCode() == editable.hashCode()) {
            passwordsIm.setVisibility(View.VISIBLE);
            if (passwordsEdt.getText().length() >= 6) {
                passwordsIm.setImageResource(android.R.drawable.ic_notification_overlay);
                if (passwordsEdt.getText().toString().equals(passwordsAgainEdt.getText().toString()))
                    passwordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
                else
                    passwordsAgainIm.setImageResource(android.R.drawable.ic_delete);
            } else {
                passwordsIm.setImageResource(android.R.drawable.ic_delete);
                if (passwordsEdt.getText().toString().equals(passwordsAgainEdt.getText().toString()))
                    passwordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
                else
                    passwordsAgainIm.setImageResource(android.R.drawable.ic_delete);

            }
        } else if (passwordsAgainEdt.getText().hashCode() == editable.hashCode()) {
            passwordsAgainIm.setVisibility(View.VISIBLE);
            if (passwordsAgainEdt.getText().length() >= 6 && passwordsAgainEdt.getText().toString().equals(passwordsEdt.getText().toString()))
                passwordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
            else
                passwordsAgainIm.setImageResource(android.R.drawable.ic_delete);
        } else if (nameEdt.getText().hashCode() == editable.hashCode()) {

        }

    }


    @Override
    protected void onResume() {
//        if (credential.getSelectedAccountName() == null) {
//            if (isGooglePlayServicesAvailable()) {
//                if (isDeviceOnline()) {
//                    chooseAccount();
//                }
//            }
//        }

        super.onResume();
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

    private void chooseAccount() {
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
                        idIm.setImageResource(android.R.drawable.ic_notification_overlay);
//                        SharedPreferences settings =
//                                getPreferences(Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {



                    showDialogOK("沒有google帳號，無法使用本服務", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    chooseAccount();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    finish();
                                    break;
                            }
                        }
                    });

                }
                break;
        }
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("再次確認", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
        return super.onOptionsItemSelected(item);
    }

}
