package a3wish.main.com.aims212.sam.a3wish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a3wish.main.com.aims212.sam.a3wish.tool.NeedHelpeListV2;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;
import a3wish.main.com.aims212.sam.a3wish.upload.UploadPicActivity;
import a3wish.main.com.aims212.sam.a3wish.upload.UploadVideoActivity;
import it.sephiroth.android.library.picasso.Picasso;

public class InformationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String TAG = "InformationActivity";


    private static String type = "圖片";

    final String[] picture = {"拍攝圖片", "選擇圖片"};

    final String[] video = {"拍攝影片", "選擇影片"};

    private Uri mFileURI = null;
    Uri uri;
    String picUri = "";


    private String mChosenAccountName;
    Bundle mSavedInstanceState;


    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;
    private final static int RESULT_CAMERA_IMAGE = 101;
    private final static int RESULT_PICK_IMAGE = 102;
    private final static int RESULT_CAMERA_VIDEO = 103;
    private static final int RESULT_PICK_VIDEO = 104;

    private TextView txt;

    public static int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mSavedInstanceState = savedInstanceState;

        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        position = bundle.getInt("position");


//        test();


        type = ToolArray.needHelpeListArryV2[position].getWish_Type().toString();


        txt = (TextView) this.findViewById(R.id.txt_information);
        txt.setText("姓名：" + ToolArray.needHelpeListArryV2[position].getWish_Name().toString() + "\n" +
                "圓夢類型：" + ToolArray.needHelpeListArryV2[position].getWish_Type().toString() + "\n" +
                "願望：" + ToolArray.needHelpeListArryV2[position].getWish_Content().toString() + "\n" +
                "備註：" + ToolArray.needHelpeListArryV2[position].getWish_Note().toString() + "\n");

//        Picasso.with(getApplicationContext()).load("https://docs.google.com/uc?id=18BDq24v4IrQjVZq9tC1n0kxTqXuL3_Sn")//download URL
        Picasso.with(getApplicationContext()).load(ToolArray.needHelpeListArryV2[position].getWish_Image().toString())//download URL
                .placeholder(R.drawable.progress_animation)//loading
                .error(R.drawable.headerror)//error
                .into((ImageView) findViewById(R.id.im_head));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        if (ToolArray.changeAccountState||ToolArray.uploadState) {
            if(ToolArray.uploadState)
                ToolArray.uploadState=false;
            finish();

        }




        super.onResume();

    }


    private void test() {
        ToolArray.needHelpeListArryV2 = new NeedHelpeListV2[1];

        position = 0;
        ToolArray.needHelpeListArryV2[position] = new NeedHelpeListV2(
                "2",
                "24.5059766",
                "120.986049350",
                "rferererggregergergerg",
                //"圖片",
                "影片",
                //"360°影片",
                "2017/11/7/ 下午 05:33:17",
                "1012",
                "Samsung",
                "",
                "",
                "Samsung@gmail.com",
                "True");
    }


    public void OnCheckHopeClick(View view) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (!checkAndRequestPermissions())
                return;


            if (type.equals("圖片"))
                showPictureDialog(picture);

            if (type.equals("影片") || type.equals("360°影片"))
                showVideoDialog(video);


        } else {

            if (type.equals("圖片"))
                showPictureDialog(picture);

            if (type.equals("影片") || type.equals("360°影片"))
                showVideoDialog(video);


        }


    }


    private boolean checkAndRequestPermissions() {

        int accountPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        int camerPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (accountPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.GET_ACCOUNTS);
        }
        if (camerPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {


            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);


            return false;


        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    if (type.equals("圖片")) {
                        showPictureDialog(picture);
                    }
                    if (type.equals("影片") || type.equals("360°影片")) {
                        showVideoDialog(video);
                    }
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.GET_ACCOUNTS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK("假如權限沒全開選擇取消的話，只能使用部分功能",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.

                                                Toast.makeText(InformationActivity.this, "不得使用此功能", Toast.LENGTH_SHORT).show();

                                                break;
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show();
                    }

                }


                break;


        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();


            switch (requestCode) {

                case RESULT_CAMERA_IMAGE:

//                        Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                uri);
//
//                        sendBroadcast(it);

                    Log.e("uri", uri.toString());

                    ContentResolver contentProvider = getContentResolver();
                    ParcelFileDescriptor mInputPFD;
                    try {
                        //获取contentProvider图片
                        mInputPFD = contentProvider.openFileDescriptor(uri, "r");
                        FileDescriptor fileDescriptor = mInputPFD.getFileDescriptor();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    intent.setClass(this, UploadPicActivity.class);
                    intent.putExtra("type", "1");//可放所有基本類別

                    intent.putExtra("imageUri", uri.toString());//可放所有基本類別
                    intent.putExtra("position", ""+position);

                    startActivity(intent);
                    break;
                case RESULT_PICK_IMAGE:
                    Uri imageUri = data.getData();

//                    Toast.makeText(InformationActivity.this, "你選的是" + imageUri.toString(), Toast.LENGTH_SHORT).show();


                    intent = new Intent();
                    intent.setClass(this, UploadPicActivity.class);
                    intent.putExtra("type", "2");//可放所有基本類別
                    intent.putExtra("imageUri", imageUri.toString());//可放所有基本類別
                    intent.putExtra("position", ""+position);


                    startActivity(intent);
                    break;


                case RESULT_CAMERA_VIDEO:
                    if (resultCode == RESULT_OK) {
                        mFileURI = data.getData();
                        if (mFileURI != null) {
                            intent = new Intent(this, UploadVideoActivity.class);
                            intent.setData(mFileURI);
                            intent.putExtra("position", ""+position);

                            startActivity(intent);
                        }
                    }
                    break;

                case RESULT_PICK_VIDEO:
                    if (resultCode == RESULT_OK) {
                        mFileURI = data.getData();
                        if (mFileURI != null) {
                            intent = new Intent(this, UploadVideoActivity.class);
                            intent.setData(mFileURI);
                            intent.putExtra("position", ""+position);

                            startActivity(intent);
                        }
                    }
                    break;


            }


        }
//        doSomething(bitmap);

    }


    private void showPictureDialog(String str[]) {


        AlertDialog.Builder dialog_list = new AlertDialog.Builder(InformationActivity.this);
        dialog_list.setTitle("選擇分享圖片方式");
        dialog_list.setItems(str, new DialogInterface.OnClickListener() {
            @Override

            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(InformationActivity.this, "你選的是" + picture[which], Toast.LENGTH_SHORT).show();
                if (which == 0) {
                    File imagePath = new File(Environment.getExternalStorageDirectory(), "Pictures/images");
                    if (!imagePath.exists()) imagePath.mkdirs();
                    File newFile = new File(imagePath, "p" + System.currentTimeMillis() + ".jpg");

                    //第二参数是在manifest.xml定义 provider的authorities属性
                    uri = FileProvider.getUriForFile(InformationActivity.this, "com.a3wish.android.fileprovider", newFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //兼容版本处理，因为 intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 只在5.0以上的版本有效
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip =
                                ClipData.newUri(getContentResolver(), "A photo", uri);
                        intent.setClipData(clip);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } else {
                        List<ResolveInfo> resInfoList =
                                getPackageManager()
                                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, uri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, RESULT_CAMERA_IMAGE);
                }


                if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_PICK_IMAGE);

//                    Intent intent = new Intent();
//                    intent.setType("image/*");      //開啟Pictures畫面Type設定為image
//                    intent.setAction(Intent.ACTION_GET_CONTENT);    //使用Intent.ACTION_GET_CONTENT
//                    startActivityForResult(intent, 1);      //取得相片後, 返回

                }
            }
        });
        dialog_list.show();


    }


    public void showVideoDialog(String str[]) {


        AlertDialog.Builder dialog_list = new AlertDialog.Builder(InformationActivity.this);
        dialog_list.setTitle("選擇分享影片方式");
        dialog_list.setItems(str, new DialogInterface.OnClickListener() {
            @Override

            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(InformationActivity.this, "你選的是" + picture[which], Toast.LENGTH_SHORT).show();
                if (which == 0) {

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    // Workaround for Nexus 7 Android 4.3 Intent Returning Null problem
                    // create a file to save the video in specific folder (this works for
                    // video only)
                    // mFileURI = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileURI);

                    // set the video image quality to high
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                    // start the Video Capture Intent
                    startActivityForResult(intent, RESULT_CAMERA_VIDEO);

                }


                if (which == 1) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                    startActivityForResult(intent, RESULT_PICK_VIDEO);

                }
            }
        });
        dialog_list.show();

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


}
