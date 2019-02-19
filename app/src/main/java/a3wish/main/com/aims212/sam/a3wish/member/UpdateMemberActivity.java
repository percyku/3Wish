package a3wish.main.com.aims212.sam.a3wish.member;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class UpdateMemberActivity extends AppCompatActivity {
    private Toolbar toolbar;


    private EditText idEdt, nameEdt;
    private ImageView idIm;
    private RadioButton boyRdb, girlRdb;

    private String token, email, password, name, sex, character, reasponStr;

    private String str[];
    private ProgressDialong progressDialong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member);

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initial();
    }

    private void initial() {


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.d("myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        Log.d("myEmail:", email);
        name = sharedPreferences.getString(getString(R.string.MEMBER_NAME), "0");
        Log.d("myName:", name);
        sex = sharedPreferences.getString(getString(R.string.MEMBER_SEX), "0");
        Log.d("mySex:", sex);

        idEdt = (EditText) this.findViewById(R.id.idEdt);
        idEdt.setText(email);
        idEdt.setTextColor(getColor(R.color.colorBlack));
        idIm = (ImageView) this.findViewById(R.id.idIm);
        idEdt.addTextChangedListener(new TextWatcher() {
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


                }
            }
        });
        nameEdt = (EditText) this.findViewById(R.id.nameEdt);
        nameEdt.setText(name);
        nameEdt.setTextColor(getColor(R.color.colorBlack));
        boyRdb = (RadioButton) this.findViewById(R.id.boyRdb);
        girlRdb = (RadioButton) this.findViewById(R.id.girlRdb);
        if (sex.equals("True"))
            boyRdb.setChecked(true);
        else
            girlRdb.setChecked(true);


    }


    public void OnUpdateBT(View view) {
        if (idEdt.getText().toString().equals("")){
            idEdt.setFocusable(true);
            Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
        }else{
            String mSex;
            if (boyRdb.isChecked())
                mSex = "True";
            else
                mSex = "False";

            sex=mSex;
            str = new String[]{email,idEdt.getText().toString(),nameEdt.getText().toString(), sex,  token};
            progressDialong = new ProgressDialong(UpdateMemberActivity.this);
            progressDialong.DialongShow("修改中...");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        reasponStr = Networks.post(ToolArray.UpdateMemberData, str);
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
                                editor.commit();
                                ToolArray.refreshState=true;
                                startActivity(new Intent(UpdateMemberActivity.this, MainActivity.class));
                                finish();
                            } else if (reasponStr.equals("upload fail")) {
                                Toast.makeText(UpdateMemberActivity.this, "網路異常", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UpdateMemberActivity.this, "信箱重複", Toast.LENGTH_LONG).show();
                                idEdt.setFocusable(true);
                                idEdt.setTextColor(getColor(R.color.colorAccent));
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
//            startActivity(new Intent(UpdateMemberActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(new Intent(UpdateMemberActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}


