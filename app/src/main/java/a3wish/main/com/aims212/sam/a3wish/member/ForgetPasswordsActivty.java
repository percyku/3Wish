package a3wish.main.com.aims212.sam.a3wish.member;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class ForgetPasswordsActivty extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText idEdt;
    private String reasponStr;
    private ProgressDialong progressDialong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwords_activty);

        initial();
    }

    private void initial() {
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idEdt = (EditText) this.findViewById(R.id.emailEdt);
//        idEdt.setInputType(InputType.TYPE_NULL);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }



    public void OnCheckBT(View view){

        if(!isDeviceOnline()){
            Toast.makeText(this, "沒有網路", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idEdt.getText().toString().equals("")) {
            Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialong = new ProgressDialong(ForgetPasswordsActivty.this);
        progressDialong.DialongShow("送出...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    reasponStr= Networks.post(ToolArray.ForgetPasswords,new String[]{idEdt.getText().toString()});
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    reasponStr = "upload fail";
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(reasponStr.equals("ok")){
                            progressDialong.DialongCancel();
//                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(getString(R.string.MEMBER_EMAIL), idEdt.getText().toString());
//                            editor.commit();

                            ToolArray.changePasswordsState=true;
                            ToolArray.changePasswordsString=idEdt.getText().toString();
                            Toast.makeText(ForgetPasswordsActivty.this,"已經寄到您輸入的信箱了",Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(reasponStr.equals("not found")){
                            Toast.makeText(ForgetPasswordsActivty.this,"輸入的信箱錯誤",Toast.LENGTH_SHORT).show();
                            progressDialong.DialongCancel();

                        }else{
                            progressDialong.DialongCancel();
                            Toast.makeText(ForgetPasswordsActivty.this,"網路有問題",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        }).start();

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
