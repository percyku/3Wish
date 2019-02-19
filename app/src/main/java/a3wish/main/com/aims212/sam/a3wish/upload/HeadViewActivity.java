package a3wish.main.com.aims212.sam.a3wish.upload;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.member.UpdateMemberActivity;
import a3wish.main.com.aims212.sam.a3wish.member.UpdateMemberActivityV2;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;

public class HeadViewActivity extends AppCompatActivity {


    private Toolbar toolbar;

    ProgressDialog mProgress;
    ProgressDialog mUploadProgress;
    private ProgressDialog testProgress;


    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    Drive mService;


    private static final java.util.Collection<String> SCOPES =
            DriveScopes.all();

    //google drive資料id,這已經在ToolArray寫了，請參考那邊
    String googleDriveFolderName = "1cK0aSbVm0E3vfJ0sWRJ5Z7my1NYha2k1";


    //上傳的名稱
    String fileName = "";


    //路徑位置
    String path_fileName2 = "";

    String reasponDrive;
    String reasponStr;


    private Button uploadBT;

    private ImageView imageView;

    boolean checkDriveServer = false;


    private SharedPreferences sharedPreferences;


    private Uri uri;


    private String member_sn, token, email, password, name, sex, image, character, reasponStr123;


//    private String metori="file:///storage/emulated/0/Pictures/head/p1510727014550.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_view);

        //主要是要得到sn,email,image(image是為了判斷有沒有已經上傳，下面會說明)
        initialMember();

        //layout物件實體化
        initial();


        //先給予credential實體，並確認google drive透過網路要開哪些權限
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), SCOPES)
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(email);



        //主要是開通google drive服務功能
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Drive API Android Quickstart")
                .build();


        //先判斷google 帳號是否是經過目前手機紀錄的帳密
        //
        checkState();


    }


    public void uploadPicOnclick(View view) {

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

    private void initial() {

        Intent intent = this.getIntent();
        String str = intent.getStringExtra("imageUri");
        Log.e("imageUri", str);
        Uri myUri = Uri.parse(str);
        for (String s : str.split("/")) {
            fileName = s;
        }
        Log.e("fileName", fileName);

        path_fileName2 = "/storage/emulated/0/Pictures/head/" + fileName;
        //path_fileName2 = getRealPathFromURI(myUri);

        Log.e("path_fileName2", path_fileName2);


        fileName = email + name + fileName;

        imageView = (ImageView) this.findViewById(R.id.achieveImage);

        uploadBT = (Button) findViewById(R.id.upload_button);
        ContentResolver cr = this.getContentResolver(); //抽象資料的接口

        try {
                /* 由抽象資料接口轉換圖檔路徑為Bitmap */
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(myUri));

            imageView.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            Log.e("Exception", e.getMessage(), e);
        }


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Drive API ...");
        testProgress = new ProgressDialog(this);
        mUploadProgress = new ProgressDialog(this);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void checkState() {
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
        //當帳號無效的話...
        if (credential.getSelectedAccountName() == null) {
            showDialogOK("您的Google帳號無效，需更換，否則無法使用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
//                                    ToolArray.changeAccountState = true;
                                    startActivity(new Intent(HeadViewActivity.this, UpdateMemberActivityV2.class));
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                                    uploadBT.setText("Google帳號有問題，需要更換");

                                    break;
                            }
                        }
                    });
        } else {
            //判斷網路
            if (isDeviceOnline()) {
                //當在ApiAsyncTaskHead裡面服務的同意，checkDriveServer會在裡面給予true這時即可直下上傳的函示

                if (checkDriveServer) {


                    upLoadData();

                } else {

                    mProgress.show();
                    //為了確認有同一本服務
                    new ApiAsyncTaskHead(HeadViewActivity.this).execute();
                }


            } else {
//                mStatusText.setText("No network connection available.");
                uploadBT.setTextColor(Color.parseColor("#FF4081"));
                uploadBT.setText("沒有網路！");

            }
        }
    }

    //判斷網路
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


    String description = "Upload from " + getDeviceName();

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
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


    String imageStr;

    private void upLoadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("upLoadData", "1");


                //進入MainAcitiviy，會進行initialMember()進行成員的判別
                //假如image是空值，就會塞入ToolArray.image_boy和ToolArray.image_girl
                //下面判別式成立表示，要先刪掉（deleteFile）原先google drive原先的圖，在進行更新（insertFile）新的圖片資料
                if (!image.equals("")) {
                    deleteFile(mService, image);
                    Log.e("upLoadData", "2");
                }

                Log.e("upLoadData", "3");


                image = insertFile(mService, fileName, description, ToolArray.fold_person_pic, null, path_fileName2).getId().toString();

                Log.e("upLoadData", "4");

                Log.d("insertFile", image);
                //假設
                if (image == "") {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("upLoadData", "5");

                            Toast.makeText(HeadViewActivity.this, "UpLaod Fail", Toast.LENGTH_SHORT).show();
                            testProgress.cancel();
                        }
                    });
                } else {

                    try {
                        //存入212資料庫
                        reasponStr = Networks.post(ToolArray.UpdateMemberPic, new String[]{member_sn, image});
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        reasponStr = "upload fail";
                        testProgress.cancel();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (reasponStr.equals("ok")) {
                                testProgress.cancel();
                                Log.e("testProgress.cancel()", image);
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.MEMBER_IMAGE), image);
                                editor.commit();
                                ToolArray.refreshState = true;
                                finish();
                            } else {

                            }


                        }
                    });

                }


            }
        }).start();
    }


    //上傳到google drive的方法
    public File insertFile(com.google.api.services.drive.Drive service, String title, String description,
                           String parentId, String mimeType, final String filename) {
        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);


        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }


        // File's content.
        java.io.File fileContent = new java.io.File(filename);

        if (!fileContent.canRead()) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialog wrong = new Dialog(HeadViewActivity.this);
                    wrong.setTitle("file (" + filename + ") is not readable");
                    wrong.show();

                }
            });
            Log.e("UploadPicActivity", "!fileContent.canRead()");
            return null;
        }


        FileContent mediaContent = new FileContent(mimeType, fileContent);


        try {
            Drive.Files.Insert insert = service.files().insert(body, mediaContent);


            MediaHttpUploader uploader = insert.getMediaHttpUploader();
            uploader.setChunkSize(1 * 1024 * 1024);
            uploader.setProgressListener(new FileUploadProgressListener(filename));


            File file = insert.execute();

            return file;
        } catch (IOException e) {
            Log.e("UploadPicActivity", "ERROR OCCURED : " + e);
            return null;
        }

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

    //類似背景執行傳值，並控制progressdialog
    public class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

        private String mFileUploadedName;

        public FileUploadProgressListener(String name) {
            mFileUploadedName = name;
        }

        @Override
        public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
            if (mediaHttpUploader == null) return;
            switch (mediaHttpUploader.getUploadState()) {
                case INITIATION_STARTED:
                    Log.d("UploadPicActivity", "Initiation has started");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mUploadProgress.setTitle("Upload file : " + mFileUploadedName);
//                            mUploadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                            mUploadProgress.setMax(100);
//                            mUploadProgress.show();
                            testProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            testProgress.setMessage("上傳中...");
                            testProgress.show();
                        }
                    });
                    //System.out.println("Initiation has started!");

                    break;
                case INITIATION_COMPLETE:
                    //System.out.println("Initiation is complete!");
                    break;
                case MEDIA_IN_PROGRESS:
                    final double percent = mediaHttpUploader.getProgress() * 100;
                    // if (Ln.DEBUG) {
                    Log.d("HANK", "Upload to google drive: " + mFileUploadedName + " - " + String.valueOf(percent) + "%");
//                    PictureActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mUploadProgress.setProgress((int) percent);
//                        }
//                    });

                    break;
                case MEDIA_COMPLETE:
                    //System.out.println("Upload is complete!");

//                    PictureActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mUploadProgress.dismiss();
//                        }
//                    });

                    break;
            }
        }
    }


    public void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("更換密碼", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
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

            //google drive＆yotube 服務同意
            case REQUEST_AUTHORIZATION:
                //有同意
                if (resultCode != RESULT_OK) {

                    Log.e("REQUEST_AUTHORIZATION", "chooseAccount()");
                    checkDriveServer = false;
                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                    uploadBT.setText("尚未獲得上傳權限");
                    //有不同意
                } else {
                    Log.e("REQUEST_AUTHORIZATION", "Sussessful");
                    checkDriveServer = true;
                    uploadBT.setTextColor(Color.parseColor("#FF4081"));
                    uploadBT.setText("可上傳圖片");

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
