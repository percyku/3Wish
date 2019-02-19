package a3wish.main.com.aims212.sam.a3wish.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class UpdatePasswordsActivity extends AppCompatActivity implements TextWatcher {
    private Toolbar toolbar;
    private EditText newPasswordsEdt, newPasswordsAgainEdt, oldPasswordsEdt;
    private ImageView newPasswordsIm, newPasswordsAgainIm, oldPasswordsIm;
    private String token, email, password,reasponStr;
    private String str[];
    private ProgressDialong progressDialong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_passwords);

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initial();
    }

    private void initial() {
        newPasswordsEdt = (EditText) this.findViewById(R.id.newPasswordsEdt);
        newPasswordsEdt.addTextChangedListener(this);
        newPasswordsAgainEdt = (EditText) this.findViewById(R.id.newPasswordsAgainEdt);
        newPasswordsAgainEdt.addTextChangedListener(this);
        oldPasswordsEdt = (EditText) this.findViewById(R.id.oldPasswordsEdt);
        oldPasswordsEdt.addTextChangedListener(this);
        newPasswordsIm = (ImageView) this.findViewById(R.id.newPasswordsIm);
        newPasswordsAgainIm = (ImageView) this.findViewById(R.id.newPasswordsAgainIm);
        oldPasswordsIm = (ImageView) this.findViewById(R.id.oldPasswordsIm);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.d("myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        Log.d("myEmail:", email);
        password = sharedPreferences.getString(getString(R.string.MEMBER_PASSWORD), "0");
        Log.d("myPasswords:", password);


    }


    public void OnUpadatePasswordsBT(View view){

        if(newPasswordsEdt.getText().toString().equals("")||newPasswordsAgainEdt.getText().toString().equals("")||oldPasswordsEdt.getText().toString().equals("")){
            Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
        }
//        else if(oldPasswordsEdt.isShown().||newPasswordsIm.isShown()||newPasswordsAgainIm.isShown()) {
//            Toast.makeText(this, "輸入不正確", Toast.LENGTH_SHORT).show();
//        }
        else if(newPasswordsEdt.getText().toString().equals(oldPasswordsEdt.getText().toString())){
            Toast.makeText(this, "密碼重複了！", Toast.LENGTH_SHORT).show();
        } else if(!oldPasswordsEdt.getText().toString().equals(password)){
            Toast.makeText(this, "密碼輸入錯誤！", Toast.LENGTH_SHORT).show();
        }else{
            str=new String[]{email,password,newPasswordsEdt.getText().toString(),token};

            progressDialong = new ProgressDialong(UpdatePasswordsActivity.this);
            progressDialong.DialongShow("修改中...");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        reasponStr = Networks.post(ToolArray.UpdatePasswords, str);
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
                                editor.putString(getString(R.string.MEMBER_PASSWORD), newPasswordsAgainEdt.getText().toString());
                                editor.commit();
                                ToolArray.refreshState=true;
//                                startActivity(new Intent(UpdatePasswordsActivity.this, MainActivity.class));
                                finish();
                            } else if (reasponStr.equals("upload fail")) {
                                Toast.makeText(UpdatePasswordsActivity.this, "網路異常", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UpdatePasswordsActivity.this, "密碼錯誤", Toast.LENGTH_LONG).show();
                                oldPasswordsEdt.setFocusable(true);
                                oldPasswordsEdt.setTextColor(getColor(R.color.colorAccent));
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
//            startActivity(new Intent(UpdatePasswordsActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(new Intent(UpdatePasswordsActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (oldPasswordsEdt.getText().hashCode() == editable.hashCode()) {
            oldPasswordsIm.setVisibility(View.VISIBLE);

            if(oldPasswordsEdt.getText().length()>=6)
                oldPasswordsIm.setImageResource(android.R.drawable.ic_notification_overlay);
            else
                oldPasswordsIm.setImageResource(android.R.drawable.ic_delete);

        } else if (newPasswordsEdt.getText().hashCode() == editable.hashCode()) {
            newPasswordsIm.setVisibility(View.VISIBLE);
            if (newPasswordsEdt.getText().length() >= 6) {
                newPasswordsIm.setImageResource(android.R.drawable.ic_notification_overlay);
                if (newPasswordsEdt.getText().toString().equals(newPasswordsAgainEdt.getText().toString()))
                    newPasswordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
                else
                    newPasswordsAgainIm.setImageResource(android.R.drawable.ic_delete);
            } else {
                newPasswordsIm.setImageResource(android.R.drawable.ic_delete);
                if (newPasswordsEdt.getText().toString().equals(newPasswordsAgainEdt.getText().toString()))
                    newPasswordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
                else
                    newPasswordsAgainIm.setImageResource(android.R.drawable.ic_delete);

            }
        } else if (newPasswordsAgainEdt.getText().hashCode() == editable.hashCode()) {
            newPasswordsAgainIm.setVisibility(View.VISIBLE);
            if (newPasswordsAgainEdt.getText().length() >= 6 && newPasswordsAgainEdt.getText().toString().equals(newPasswordsEdt.getText().toString()))
                newPasswordsAgainIm.setImageResource(android.R.drawable.ic_notification_overlay);
            else
                newPasswordsAgainIm.setImageResource(android.R.drawable.ic_delete);

        }
    }
}
