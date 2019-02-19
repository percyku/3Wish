package a3wish.main.com.aims212.sam.a3wish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import a3wish.main.com.aims212.sam.a3wish.member.LoginActivty;
import a3wish.main.com.aims212.sam.a3wish.member.UpdateMemberActivity;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class HopePageActivity extends AppCompatActivity implements Button.OnClickListener {
    private Toolbar toolbar;

    private String latitude, longitude;
    private Button typeLeftBt, typeCenterBt, typeRightBt, checkHopeBt;
    private String typePosition = "1";

    private EditText hopeContentEdt, hopeOtherEdt;



    private ProgressDialong progressDialong;
    private String str[];
    private String reasponStr ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hope_page);
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        latitude = bundle.getString("Latitude");
        longitude = bundle.getString("Longitude");
        Toast.makeText(this, "" + latitude + "," + longitude, Toast.LENGTH_SHORT).show();


        initial();


    }

    private void initial() {
        typeLeftBt = (Button) this.findViewById(R.id.typeLeftBt);
        typeLeftBt.setOnClickListener(this);
        typeCenterBt = (Button) this.findViewById(R.id.typeCenterBt);
        typeCenterBt.setOnClickListener(this);
        typeRightBt = (Button) this.findViewById(R.id.typeRightBt);
        typeRightBt.setOnClickListener(this);
        checkHopeBt = (Button) this.findViewById(R.id.checkHopeBt);
        checkHopeBt.setOnClickListener(this);
        hopeContentEdt = (EditText) this.findViewById(R.id.hopeContentEdt);
        hopeOtherEdt = (EditText) this.findViewById(R.id.hopeOtherEdt);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.typeLeftBt:
                typePosition = "1";
                typeCheck(typePosition);
                break;
            case R.id.typeCenterBt:
                typePosition = "2";
                typeCheck(typePosition);
                break;
            case R.id.typeRightBt:
                typePosition = "3";
                typeCheck(typePosition);
                break;
            case R.id.checkHopeBt:
                if (hopeContentEdt.getText().toString().equals("")) {
                    hopeContentEdt.requestFocus();
                    Toast.makeText(this, "不得空白", Toast.LENGTH_SHORT).show();
                } else {
                    upLoad();
                }

                break;
        }

    }

    private void upLoad()  {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        str=new String[]{ sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0"),
                latitude,longitude,hopeContentEdt.getText().toString(),hopeOtherEdt.getText().toString(),typePosition};

        progressDialong = new ProgressDialong(HopePageActivity.this);
        progressDialong.DialongShow("發送中...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    reasponStr = Networks.post(ToolArray.HopePageInsertV2, str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    reasponStr = "upload fail";
                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialong.DialongCancel();

                        if(reasponStr.equals("ok")){
                            finish();
                        }else if(reasponStr.equals("upload fail")){
                            Toast.makeText(HopePageActivity.this,"上傳失敗",Toast.LENGTH_SHORT).show();
                        }else if(reasponStr.equals("no")){
                            Toast.makeText(HopePageActivity.this,"帳密有問題",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();


    }



    private void typeCheck(String typePosition) {
        if (typePosition.equals("1")) {
            typeLeftBt.setTextColor(getColor(R.color.colorTextPress));
            typeCenterBt.setTextColor(getColor(R.color.colorTextNormal));
            typeRightBt.setTextColor(getColor(R.color.colorTextNormal));
            typeLeftBt.setBackground(getDrawable(R.drawable.custom_button_left_press));
            typeCenterBt.setBackground(getDrawable(R.drawable.custom_button_center_normal));
            typeRightBt.setBackground(getDrawable(R.drawable.custom_button_right_normal));


        }
        if (typePosition.equals("2")) {
            typeLeftBt.setTextColor(getColor(R.color.colorTextNormal));
            typeCenterBt.setTextColor(getColor(R.color.colorTextPress));
            typeRightBt.setTextColor(getColor(R.color.colorTextNormal));
            typeLeftBt.setBackground(getDrawable(R.drawable.custom_button_left_normal));
            typeCenterBt.setBackground(getDrawable(R.drawable.custom_button_center_press));
            typeRightBt.setBackground(getDrawable(R.drawable.custom_button_right_normal));

        }
        if (typePosition.equals("3")) {
            typeLeftBt.setTextColor(getColor(R.color.colorTextNormal));
            typeCenterBt.setTextColor(getColor(R.color.colorTextNormal));
            typeRightBt.setTextColor(getColor(R.color.colorTextPress));
            typeLeftBt.setBackground(getDrawable(R.drawable.custom_button_left_normal));
            typeCenterBt.setBackground(getDrawable(R.drawable.custom_button_center_normal));
            typeRightBt.setBackground(getDrawable(R.drawable.custom_button_right_press));

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
}
