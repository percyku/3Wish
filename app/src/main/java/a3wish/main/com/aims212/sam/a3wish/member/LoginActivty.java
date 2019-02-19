package a3wish.main.com.aims212.sam.a3wish.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;

import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class LoginActivty extends AppCompatActivity {

    private EditText idEdt, passwordsEdt;
    private String str[];
    private ProgressDialong progressDialong;

    private String reasponStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        initial();
    }

    private void initial() {

        idEdt = (EditText) this.findViewById(R.id.idEdt);
        passwordsEdt = (EditText) this.findViewById(R.id.passwordsEdt);
    }

    public void OnAcountTxt(View v) {


        startActivity(new Intent(this, RegisterActivity.class));
    }


    public void OnpasswordsTxt(View v) {

        startActivity(new Intent(this, ForgetPasswordsActivty.class));

    }

    public void OnLoginBT(View view) {
        if (idEdt.getText().toString().equals("") || passwordsEdt.getText().toString().equals("")) {
            Toast.makeText(LoginActivty.this, "不得空白", Toast.LENGTH_SHORT).show();
        } else {


            progressDialong = new ProgressDialong(LoginActivty.this);
            progressDialong.DialongShow("登入中...");
            new Thread(new Runnable() {
                @Override
                public void run() {


                    try {
                        String token = FirebaseInstanceId.getInstance().getToken();
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

                        while (token == null)//this is used to get firebase token until its null so it will save you from null pointer exeption
                        {
                            token = FirebaseInstanceId.getInstance().getToken();
                            Log.d("myLog", "Refreshed token: " + token);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.FCM_TOKEN), token);
                            editor.commit();

                        }


                        str = new String[]{idEdt.getText().toString(), passwordsEdt.getText().toString(), token};


                        reasponStr = Networks.post(ToolArray.LoginUpdate, str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        reasponStr = "upload fail";
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (reasponStr.equals("no")) {
                                progressDialong.DialongCancel();
                                Toast.makeText(LoginActivty.this, "請再次確認帳密", Toast.LENGTH_LONG).show();
                            } else if (reasponStr.equals("upload fail")) {
                                progressDialong.DialongCancel();

                                Toast.makeText(LoginActivty.this, "網路異常", Toast.LENGTH_LONG).show();
                            } else {
                                saveMemberData();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialong.DialongCancel();
                                        startActivity(new Intent(LoginActivty.this, MainActivity.class));
                                        finish();
                                    }
                                }, 1000);
                            }

                        }
                    });
                }
            }).start();

        }
    }

    private void saveMemberData() {
        String[] strServer = reasponStr.split(",");
        Log.d("LoginActivty," + getString(R.string.MEMBER_SN), strServer[0]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_NAME), strServer[1]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_IMAGE), strServer[2]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_EMAIL), strServer[3]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_PASSWORD), strServer[4]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_SEX), strServer[5]);
        Log.d("LoginActivty," + getString(R.string.MEMBER_CHARACTER), strServer[6]);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.MEMBER_SN), strServer[0]);
        editor.putString(getString(R.string.MEMBER_NAME), strServer[1]);
        editor.putString(getString(R.string.MEMBER_IMAGE), strServer[2]);
        editor.putString(getString(R.string.MEMBER_EMAIL), strServer[3]);
        editor.putString(getString(R.string.MEMBER_PASSWORD), strServer[4]);
        editor.putString(getString(R.string.MEMBER_SEX), strServer[5]);
        editor.putString(getString(R.string.MEMBER_CHARACTER), strServer[6]);
        editor.commit();
    }


    @Override
    protected void onResume() {

        if(ToolArray.changePasswordsState){
            idEdt.setText(ToolArray.changePasswordsString);
            ToolArray.changePasswordsState=false;
            ToolArray.changePasswordsString="";
            passwordsEdt.setText("");
        }


        if(ToolArray.refreshState){
            ToolArray.refreshState=false;
            startActivity(new Intent(LoginActivty.this, MainActivity.class));
            finish();

        }

        super.onResume();
    }
}
