package a3wish.main.com.aims212.sam.a3wish;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a3wish.main.com.aims212.sam.a3wish.member.LoginActivty;
import a3wish.main.com.aims212.sam.a3wish.member.UpdateMemberActivityV2;
import a3wish.main.com.aims212.sam.a3wish.member.UpdatePasswordsActivity;
import a3wish.main.com.aims212.sam.a3wish.tab.HopeFragment;
import a3wish.main.com.aims212.sam.a3wish.tab.Pager;
import a3wish.main.com.aims212.sam.a3wish.tool.CircleTransform;
import a3wish.main.com.aims212.sam.a3wish.tool.NeedHelpeList;
import a3wish.main.com.aims212.sam.a3wish.tool.NeedHelpeListV2;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;
import a3wish.main.com.aims212.sam.a3wish.upload.HeadViewActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    //DrawerLayout這是為了宣告抽屜的物件
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String member_sn, token, email, password, name, sex, image, character, reasponStr123;

    private String[] str;

    private boolean mCharacter = false;

    private SharedPreferences sharedPreferences;

    private ProgressDialong progressDialong;

    private View headerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView toorbarTitle, headTxt;
    private static final int DEFAULT_OFFSCREEN_PAGES = 0;


    protected GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;


    private String reasponStr;


    private CircleImageView circleImageView;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;

    private final static int RESULT_CAMERA = 100;
    private final static int RESULT_PICK_IMAGE = 101;

    private final static int RESULT_PICKER_AND_CROP = 102;


    File newFile;

    Uri uri;
    final String[] pictureDiologString = {"拍攝圖片", "選擇圖片"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialMember();
        buildGoogleApiClient();


//        mService = new Drive.Builder(
//                transport, jsonFactory, credential)
//                .setApplicationName("Drive API Android Quickstart")
//                .build();


    }

    @Override
    protected void onResume() {

        if (ToolArray.refreshState) {
            ToolArray.refreshState = false;
            ToolArray.needHelpeListArryV2 = null;
            startActivity(new Intent(this, MainActivity.class));
            finish();


        }

        if (ToolArray.changeAccountState) {
            ToolArray.changeAccountState = false;
            startActivity(new Intent(this, UpdateMemberActivityV2.class));
        }


        super.onResume();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("onConnected", "1");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e("onConnected", "2");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e("onConnected", "3");

        if (mLastLocation != null) {
            Log.e("onConnected", "4");

            getJson();

            if (character.equals("True")) {
                setContentView(R.layout.activity_hope);
                initialDrawlayout();
                initialHope();


            } else {
                setContentView(R.layout.activity_achieve);
                initialDrawlayout();
                initialAchieve();

            }


        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e("onConnectionSuspended", "" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionSuspended", "" + connectionResult.toString());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_wish:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();

                break;
            case R.id.nav_achieve:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_sub_updatemember:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, UpdateMemberActivityV2.class));
//                finish();
                break;
            case R.id.nav_sub_updatepasswords:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, UpdatePasswordsActivity.class));
//                finish();
                break;
            //移除即可
            case R.id.nav_sub_achieve:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.nav_menu_hope);
//                navigationView.getMenu().findItem(2.id.nav_sub_text_achieve).setVisible(true);
//                navigationView.getMenu().findItem(R.id.nav_sub_text_hope).setVisible(false);
                changeCharacter("True");
                mCharacter = true;
                drawerLayout.closeDrawer(GravityCompat.START);

                break;
            //移除即可
            case R.id.nav_sub_hope:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.nav_menu_achieve);
//                navigationView.getMenu().findItem(R.id.nav_sub_text_achieve).setVisible(false);
//                navigationView.getMenu().findItem(R.id.nav_sub_text_hope).setVisible(true);

                changeCharacter("False");
                mCharacter = true;
                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.nav_sub_logout:
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.FCM_TOKEN), "0");

                editor.putString(getString(R.string.MEMBER_SN), "0");
                editor.putString(getString(R.string.MEMBER_EMAIL), "0");
                editor.putString(getString(R.string.MEMBER_PASSWORD), "0");
                editor.putString(getString(R.string.MEMBER_NAME), "0");
                editor.putString(getString(R.string.MEMBER_SEX), "0");
                editor.putString(getString(R.string.MEMBER_CHARACTER), "0");
                editor.putString(getString(R.string.MEMBER_IMAGE), "0");
                editor.commit();


                logout();

                ToolArray.needHelpeListArryV2 = null;

                startActivity(new Intent(MainActivity.this, LoginActivty.class));
                finish();

                break;
        }

        //最後當結束點選後，要關閉抽屜，所以要用抽屜物件所提供的方法
        if (!mCharacter)
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            mCharacter = false;

        //到這邊應該就可以執行了

        return false;

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    //返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            ToolArray.needHelpeListArryV2 = null;
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //相機選圖功能要求的驗證
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();


            switch (requestCode) {

                case RESULT_CAMERA:

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


                    cropPicture(this, getRealPathFromURI(getImageContentUri(newFile)));

                    break;
                case RESULT_PICK_IMAGE:
                    Uri imageUri = data.getData();

                    Log.e("RESULT_PICK_IMAGE", "" + imageUri);

                    Log.e("RESULT_PICK_IMAGE_getRealPathFromURI", "" + getRealPathFromURI(imageUri));

                    //Toast.makeText(MainActivity.this, "你選的是" + imageUri.toString(), Toast.LENGTH_SHORT).show();


                    cropPicture(this, getRealPathFromURI(imageUri));

                    break;


                case RESULT_PICKER_AND_CROP:
                    Uri imageUri1 = data.getData();

                    Log.e("RESULT_PICKER_AND_CROP", "" + imageUri1);

                    //getImageContentUri()
                    //getUriFromPath(imageUri1.toString());
                    //Log.e("RESULT_PICKER_AND_CROP",""+getUriFromPath(imageUri1.toString()));


                    boolean saveState = true;

                    ContentResolver contentProvider1 = getContentResolver();
                    ParcelFileDescriptor mInputPFD1;
                    try {
                        //获取contentProvider图片
                        mInputPFD1 = contentProvider1.openFileDescriptor(imageUri1, "r");
                        FileDescriptor fileDescriptor = mInputPFD1.getFileDescriptor();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        saveState = false;
                    }
                    if (saveState)
                        startActivity(new Intent(this, HeadViewActivity.class).putExtra("imageUri", imageUri1.toString()));//可放所有基本類別);

                    break;


            }


        }

    }


    //權限要求的驗證
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
                    showPictureDialog(pictureDiologString);
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.GET_ACCOUNTS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

                                                //finish();
                                                Toast.makeText(MainActivity.this, "不得使用此功能", Toast.LENGTH_SHORT).show();
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

    //拿取SharedPreferences的數值
    private void initialMember() {

        sharedPreferences = getApplicationContext().
                getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

        member_sn = sharedPreferences.getString(getString(R.string.MEMBER_SN), "0");
        Log.e("MainActivity_member_sn:", member_sn);
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "0");
        Log.e("MainActivity_myToken:", token);
        email = sharedPreferences.getString(getString(R.string.MEMBER_EMAIL), "0");
        Log.e("MainActivity_myEmail:", email);
        password = sharedPreferences.getString(getString(R.string.MEMBER_PASSWORD), "0");
        Log.e("MainActivity_myPassword:", password);
        name = sharedPreferences.getString(getString(R.string.MEMBER_NAME), "0");
        Log.e("MainActivity_myName:", name);
        sex = sharedPreferences.getString(getString(R.string.MEMBER_SEX), "0");
        Log.e("MainActivity_mySex:", sex);
        character = sharedPreferences.getString(getString(R.string.MEMBER_CHARACTER), "0");
        Log.e("MainActivity_myCharacter:", character);
        image = sharedPreferences.getString(getString(R.string.MEMBER_IMAGE), "");
        Log.e("MainActivity_image:", character);

        if (image.equals("")) {
            if (sex.equals("True"))
                image = "https://docs.google.com/uc?id=" + ToolArray.image_boy;
            else
                image = "https://docs.google.com/uc?id=" + ToolArray.image_girl;

        } else {
            image = "https://docs.google.com/uc?id=" + image;
        }
        Log.e("MainActivity_image:", image);


    }

    //要取地圖的權限
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //向server拿取
    private void getJson() {

        progressDialong = new ProgressDialong(this);
        progressDialong.DialongShow("載入中...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String str[] = new String[]{member_sn};

                try {
//                    reasponStr = Networks.post(ToolArray.NeedHelpeList, str);
                    reasponStr = Networks.post(ToolArray.NeedHelpeListV2, str);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    reasponStr = "upload fail";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("myServeLog", reasponStr);
                        progressDialong.DialongCancel();
//                        makeTable(reasponStr);
                        makeTableV2(reasponStr);
                        if (reasponStr.equals("[ ]")) {

                            Log.e("reasponStr.equals", "[ ]");
                        }


                    }
                });

            }
        }).start();
    }

    private void makeTable(String str) {

        try {

//            ToolArray.needHelpeListItem = new ArrayList<Map<String, Object>>();
            JSONArray a = new JSONArray(str);
            ToolArray.needHelpeListArry = new NeedHelpeList[a.length()];
//            otherMarker = new Marker[a.length()];
            String type = "", image = "";
            for (int i = 0; i < a.length(); i++) {
//                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject current = a.getJSONObject(i);
//                item.put("name", current.getString("name").toString());

                if (current.getString("Hope_Type").trim().toString().equals("1"))
                    type = "圖片";
                else if (current.getString("Hope_Type").trim().toString().equals("2"))
                    type = "影片";
                else
                    type = "360°影片";

                if (current.getString("Hope_Sex").trim().toString().equals("")) {
//                    if(current.getString("Hope_Sex").trim().toString().equals("Asus"))

                    if (current.getString("Hope_Sex").trim().toString().equals("Asus"))
                        image = "https://drive.google.com/uc?id=" + "0B-RR6-bb-4NANERVaEJlLVA3ek0";
                    else
                        image = "https://drive.google.com/uc?id=" + "0B-RR6-bb-4NAUEFsYW5taEhobDA";

                } else {
                    image = current.getString("Hope_Image").trim().toString();
                }

                ToolArray.needHelpeListArry[i] = new NeedHelpeList(
                        current.getString("Hope_Sn").trim().toString(),
                        current.getString("Hope_Email").trim().toString(),
                        current.getString("Hope_Name").trim().toString(),
                        current.getString("Hope_Sex").trim().toString(),
                        image,
                        current.getString("Hope_lat").trim().toString(),
                        current.getString("Hope_long").trim().toString(),
                        current.getString("Hope_Content").trim().toString(),
                        current.getString("Hope_Note").trim().toString(),
                        type,
                        current.getString("Hope_Date").trim().toString());
//                ToolArray.needHelpeListItem.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //將json拆解
    private void makeTableV2(String str) {
        //可能資料庫回傳的數據為[ ]，這時就return，看誰來
        if (str.equals("[ ]"))
            return;
        try {
            JSONArray a = new JSONArray(str);
            ToolArray.needHelpeListArryV2 = new NeedHelpeListV2[a.length()];
            ToolArray.allJsonString = a.length();
            for (int i = 0; i < a.length(); i++) {
                String typeOther = "";
                String imageOther = "";

                JSONObject current = a.getJSONObject(i);
                if (current.getString("Wish_Type").trim().toString().equals("1")) {
                    typeOther = "圖片";
                    ToolArray.imageJsonString = ToolArray.imageJsonString + 1;
                } else if (current.getString("Wish_Type").trim().toString().equals("2")) {
                    typeOther = "影片";
                    ToolArray.videoJsonString = ToolArray.videoJsonString + 1;
                } else {
                    typeOther = "360°影片";
                    ToolArray.panorameJsonString = ToolArray.panorameJsonString + 1;
                }


                if (current.getString("Wish_Image").trim().toString().equals("")) {
                    if (current.getString("Wish_Sex").trim().toString().equals("True"))
                        imageOther = "https://docs.google.com/uc?id=" + ToolArray.image_boy;
                    else
                        imageOther = "https://docs.google.com/uc?id=" + ToolArray.image_girl;
                } else {

                    imageOther = "https://docs.google.com/uc?id=" + current.getString("Wish_Image").trim().toString();
                }


                ToolArray.needHelpeListArryV2[i] = new NeedHelpeListV2(
                        current.getString("Wish_Sn").trim().toString(),
                        current.getString("Wish_lat").trim().toString(),
                        current.getString("Wish_long").trim().toString(),
                        current.getString("Wish_Content").trim().toString(),
                        current.getString("Wish_Note").trim().toString(),
                        typeOther,
                        current.getString("Wish_Date").trim().toString(),
                        current.getString("Member_Sn").trim().toString(),
                        current.getString("Wish_Name").trim().toString(),
                        imageOther,
                        current.getString("Wish_Email").trim().toString(),
                        current.getString("Wish_Sex").trim().toString());


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //初始化抽屜
    private void initialDrawlayout() {
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toorbarTitle = (TextView) findViewById(R.id.toolbar_title);
        if (character.equals("True")) {
            toorbarTitle.setText("許願地點");
        } else {
            toorbarTitle.setText("圓夢地點");
        }

        drawerLayout = (DrawerLayout) this.findViewById(R.id.draw_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.naviget);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();
        //精簡版
        navigationView.inflateMenu(R.menu.nav_menu_3wish_v2);
        // navigationView.inflateMenu(R.menu.nav_menu_3wish);


        View headerView = navigationView.getHeaderView(0);

        circleImageView = headerView.findViewById(R.id.headCir);


//        if (image.equals("")) {
//            if (sex.equals("True"))
//
//
//                Picasso.with(getApplicationContext()).load("https://docs.google.com/uc?id="+ ToolArray.image_boy)//download URL
//                        .placeholder(R.drawable.progress_animation)//loading
//                        .error(R.drawable.headerror)//error
//                        .into(circleImageView);//imageview
//
//            else
//                Picasso.with(getApplicationContext()).load(ToolArray.image_boy)//download URL
//                        .placeholder(R.drawable.progress_animation)//loading
//                        .error(R.drawable.headerror)//error
//                        .into(circleImageView);//imageview
//
//        }else

        Picasso.with(getApplicationContext()).load(image)//download URL
                .placeholder(R.drawable.progress_animation)//loading
                .error(R.drawable.headerror)//error
                .into(circleImageView);//imageview

        headTxt = headerView.findViewById(R.id.headTxt);
        headTxt.setText(name);

        //移除即可
        // checkCharacter();

    }



    //圓夢端
    private void initialAchieve() {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("圖片"));
        tabLayout.addTab(tabLayout.newTab().setText("影片"));
        tabLayout.addTab(tabLayout.newTab().setText("360°影片"));

        tabLayout.setTabTextColors(R.color.colorPrimary, R.color.colorWhite);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);


    }

    //許願端
    private void initialHope() {


        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.tab_content, new HopeFragment()).commit();

    }


    //確認角色為許願者或圓夢者
    private void checkCharacter() {

        if (character.equals("True")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.nav_menu_hope);
//            navigationView.getMenu().findItem(R.id.nav_sub_text_achieve).setVisible(true);
//            navigationView.getMenu().findItem(R.id.nav_sub_text_hope).setVisible(false);

        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.nav_menu_achieve);
//            navigationView.getMenu().findItem(R.id.nav_sub_text_achieve).setVisible(false);
//            navigationView.getMenu().findItem(R.id.nav_sub_text_hope).setVisible(true);

        }

    }


    //這邊不要管他可以刪
    private void changeCharacter(String character) {


        str = new String[]{email, character, token};

        progressDialong = new ProgressDialong(MainActivity.this);
        progressDialong.DialongShow("切換中...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reasponStr123 = Networks.post(ToolArray.UpdateCharacter, str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    reasponStr123 = "upload fail";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialong.DialongCancel();
                        if (reasponStr123.equals("no")) {
                            Toast.makeText(MainActivity.this, "切換失敗", Toast.LENGTH_LONG).show();
                            checkCharacter();
                        } else if (reasponStr123.equals("upload fail")) {

                            Toast.makeText(MainActivity.this, "網路異常", Toast.LENGTH_LONG).show();
                            checkCharacter();
                        } else {
                            progressDialong.DialongCancel();
                            //Toast.makeText(Main2Activity.this, "Test:"+reasponStr123, Toast.LENGTH_LONG).show();
                            Log.e("myServerCharacter", reasponStr123);
                            //String serverCharacter = reasponStr;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.MEMBER_CHARACTER), reasponStr123);
                            editor.commit();
                            ToolArray.needHelpeListArryV2 = null;
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();

                        }

                    }
                });
            }
        }).start();


    }

    //點擊大頭貼的地方
    public void onClickCircleImage(View view) {

//        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkAndRequestPermissions()) {
                showPictureDialog(new String[]{"拍攝圖片", "選擇圖片"});
            }

        } else {

            showPictureDialog(new String[]{"拍攝圖片", "選擇圖片"});

        }


    }

    //確認權限6.0後的要求
    private boolean checkAndRequestPermissions() {

        int accountPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        int camerPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (accountPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.GET_ACCOUNTS);
            Log.e("accountPermission", "true");
        }
        if (camerPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            Log.e("camerPermission", "true");

        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("writePermission", "true");

        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    //選擇拍照或是選照功能
    private void showPictureDialog(String str[]) {


        AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
        dialog_list.setTitle("上傳圖片");
        dialog_list.setItems(str, new DialogInterface.OnClickListener() {
            @Override

            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "你選的是" + pictureDiologString[which], Toast.LENGTH_SHORT).show();
                if (which == 0) {
                    File imagePath = new File(Environment.getExternalStorageDirectory(), "Pictures/images");
                    if (!imagePath.exists()) imagePath.mkdirs();
                    newFile = new File(imagePath, "p" + System.currentTimeMillis() + ".jpg");

                    //第二参数是在manifest.xml定义 provider的authorities属性
                    uri = FileProvider.getUriForFile(MainActivity.this, "com.a3wish.android.fileprovider", newFile);

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
                    startActivityForResult(intent, RESULT_CAMERA);
                    drawerLayout.closeDrawer(GravityCompat.START);

                }


                if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_PICK_IMAGE);
                    drawerLayout.closeDrawer(GravityCompat.START);

//                    Intent intent = new Intent();
//                    intent.setType("image/*");      //開啟Pictures畫面Type設定為image
//                    intent.setAction(Intent.ACTION_GET_CONTENT);    //使用Intent.ACTION_GET_CONTENT
//                    startActivityForResult(intent, 1);      //取得相片後, 返回

                }
            }
        });
        dialog_list.show();


    }


    //為權限所寫的提示Dialog
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("再次確認", okListener)
                .setNegativeButton("離開", okListener)
                .create()
                .show();
    }


    //截圖的功能
    public void cropPicture(Activity activity, String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;
        Uri outputUri;
        File imagePath = new File(Environment.getExternalStorageDirectory(), "Pictures/head");
        if (!imagePath.exists())
            imagePath.mkdirs();
        File newFile = new File(imagePath, "p" + System.currentTimeMillis() + ".jpg");


        Intent intent = new Intent("com.android.camera.action.CROP");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProvider.getUriForFile(activity, "com.a3wish.android.fileprovider", file);
            outputUri = Uri.fromFile(newFile);
            //outputUri = FileProvider.getUriForFile(activity, "com.solux.furniture.fileprovider", new File(crop_image));
        } else {
            imageUri = Uri.fromFile(file);
            outputUri = Uri.fromFile(newFile);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, RESULT_PICKER_AND_CROP);
    }


    //把uri轉絕對路徑
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    //把uri轉相對對路徑
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //登出後需要清除推播token id
    private void logout() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //call your activity where you want to land after log out
            }
        }.execute();

    }


}
