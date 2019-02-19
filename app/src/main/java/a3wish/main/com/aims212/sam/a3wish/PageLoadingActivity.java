package a3wish.main.com.aims212.sam.a3wish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a3wish.main.com.aims212.sam.a3wish.member.LoginActivty;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class PageLoadingActivity extends AppCompatActivity {

    private String token, email, password, name, sex, character, reasponStr;
    private Boolean checkPasswords;
    private String[] str;

    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_loading);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {

                initial();
            }
        } else {
            initial();

        }
    }

    @SuppressLint("LongLogTag")
    private void initial() {

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.d("PageLoadingActivity_myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        Log.d("PageLoadingActivity_myEmail:", email);
        password = sharedPreferences.getString(getString(R.string.MEMBER_PASSWORD), "0");
        Log.d("PageLoadingActivity_myPassword:", password);
        name = sharedPreferences.getString(getString(R.string.MEMBER_NAME), "0");
        Log.d("PageLoadingActivity_myName:", name);
        sex = sharedPreferences.getString(getString(R.string.MEMBER_SEX), "0");
        Log.d("PageLoadingActivity_mySex:", sex);
        character = sharedPreferences.getString(getString(R.string.MEMBER_CHARACTER), "0");
        Log.d("PageLoadingActivity_myCharacter:", character);

        if (token.equals("0") || email.equals("0") || password.equals("0")) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PageLoadingActivity.this, LoginActivty.class));
                    finish();
                }
            }, 1000);


        } else {
            str = new String[]{email, password, token};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        reasponStr = Networks.post(ToolArray.LoginUpdate, str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        reasponStr = "upload fail";
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (reasponStr.equals("no")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(PageLoadingActivity.this, LoginActivty.class));
                                        finish();
                                    }
                                }, 1000);
                            } else if (reasponStr.equals("upload fail")) {
                                Toast.makeText(PageLoadingActivity.this, "網路異常", Toast.LENGTH_LONG).show();

                            } else {
                                saveMemberData();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(PageLoadingActivity.this, MainActivity.class));
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
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_SN), strServer[0]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_NAME), strServer[1]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_IMAGE), strServer[2]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_EMAIL), strServer[3]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_PASSWORD), strServer[4]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_SEX), strServer[5]);
        Log.d("PageLoadingActivity," + getString(R.string.MEMBER_CHARACTER), strServer[6]);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initial();
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK("假如權限沒全開選擇取消的話，無法使用本服務",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                finish();
                                                break;
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show();
                        //                            //proceed with logic by disabling the related features or quit the app.
                    }

                }


                break;
        }
    }


    private boolean checkAndRequestPermissions() {

        int accountPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (accountPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.GET_ACCOUNTS);
        }

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("再次確認", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
    }

}

