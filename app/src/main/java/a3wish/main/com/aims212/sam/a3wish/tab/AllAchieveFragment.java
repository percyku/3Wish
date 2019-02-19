package a3wish.main.com.aims212.sam.a3wish.tab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import a3wish.main.com.aims212.sam.a3wish.HopePageActivity;
import a3wish.main.com.aims212.sam.a3wish.InformationActivity;
import a3wish.main.com.aims212.sam.a3wish.MainActivity;
import a3wish.main.com.aims212.sam.a3wish.R;
import a3wish.main.com.aims212.sam.a3wish.member.RegisterActivity;
import a3wish.main.com.aims212.sam.a3wish.tool.CustomInfoWindowAdapter;
import a3wish.main.com.aims212.sam.a3wish.tool.NeedHelpeList;
import a3wish.main.com.aims212.sam.a3wish.tool.NeedHelpeListV2;
import a3wish.main.com.aims212.sam.a3wish.tool.Networks;
import a3wish.main.com.aims212.sam.a3wish.tool.ProgressDialong;
import a3wish.main.com.aims212.sam.a3wish.tool.ToolArray;
import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by percyku on 2017/9/26.
 */

public class AllAchieveFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener, View.OnClickListener {


    private View mView;
    protected static final String TAG = "Tab1";


    private GoogleMap mMap;
    private Marker mMarker, otherMarker[];


    private SupportMapFragment mapFragment;
    private ImageView markCenter, checkPosition, mHead;
    private TextView txt_name, txt_type, txt_context, txt_address;
    private Button bt_informatiom;
    private LinearLayout markCenterInfor,buttonInfor;


    protected Location mLastLocation;

    private boolean inforCheck = false;

    private static int position = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("tab1");

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.allAchieve_Fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.allAchieve_Fragment, mapFragment).commit();
            mapFragment.getMapAsync(this);
        } else {
            mapFragment.getMapAsync(this);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }

        try {

            mView = inflater.inflate(R.layout.fragment_all_achieve, container, false);
            markCenterInfor = mView.findViewById(R.id.markerInfor);
            markCenterInfor.setVisibility(View.INVISIBLE);
            markCenter = mView.findViewById(R.id.mark);
            markCenter.setVisibility(View.INVISIBLE);
//            markCenter.setVisibility(View.VISIBLE);
//            otherMarker=new Marker[ToolArray.allJsonString];

            checkPosition = mView.findViewById(R.id.check_position);
            checkPosition.setOnClickListener(this);

            bt_informatiom = mView.findViewById(R.id.bt_information);
            bt_informatiom.setOnClickListener(this);

            buttonInfor=mView.findViewById(R.id.infor);

            mHead = mView.findViewById(R.id.ic_head);


            txt_name = mView.findViewById(R.id.txt_name);

            txt_type = mView.findViewById(R.id.txt_type);

            txt_context = mView.findViewById(R.id.txt_context);

            txt_address = mView.findViewById(R.id.txt_address);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        otherMarker = new Marker[ToolArray.allJsonString];


        initialMarkerPosition();


        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);


    }


    @Override
    public void onMapClick(LatLng latLng) {
        markCenterInfor.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Toast.makeText(getActivity(), "移動中...", 100).show();
            if (!inforCheck) {
//                markCenter.setVisibility(View.VISIBLE);
//                inforCheck = true;
            }

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
//            Toast.makeText(getActivity(), "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(getActivity(), "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
//        Toast.makeText(this, "The camera is moving.",
//                Toast.LENGTH_SHORT).show();

//        markCenterInfor.setVisibility(View.GONE);

    }

    @Override
    public void onCameraMoveCanceled() {
//        Toast.makeText(this, "Camera movement canceled.",
//                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCameraIdle() {

//        if (inforCheck)
//            markCenterInfor.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        position = (int) (marker.getTag());

        Log.e("marker Onclick", "Onclick:"+position);


       if((int)(mMarker.getTag())!=(int) (marker.getTag())) {
           otherMarker[position].hideInfoWindow();


           Picasso.with(getActivity().getApplicationContext()).load(ToolArray.needHelpeListArryV2[position].getWish_Image().toString())//download URL
                   .placeholder(R.drawable.progress_animation)//use defaul image
                   .error(R.drawable.heads)//if failed
                   .into(mHead);//imageview

           bt_informatiom.setClickable(true);


           txt_name.setText(ToolArray.needHelpeListArryV2[position].getWish_Name().toString());
           txt_type.setText(ToolArray.needHelpeListArryV2[position].getWish_Type().toString());
           txt_context.setText(ToolArray.needHelpeListArryV2[position].getWish_Content().toString());
           txt_address.setText(getAddressByLocation(position));

       }else{
           bt_informatiom.setClickable(false);
       }

        return false;
    }

    //主要是下方詳細的按鈕與右下黑白棋的按鈕，觸發的動作
    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.bt_information:


                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                startActivity(new Intent(getActivity(), InformationActivity.class).putExtras(bundle));

                break;

            case R.id.check_position:
                initialMarkerPosition();


                break;


        }

    }


    private void initialMarkerPosition() {


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mLastLocation = MainActivity.mLastLocation;

        if (mLastLocation != null) {

            position = 0;
//            Log.i("Latitude:", "" + mLastLocation.getLatitude());
//            Log.i("Longitude:", "" + mLastLocation.getLongitude());


            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
            mMap.clear();

            mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("目前位置").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_infor)));
            if (ToolArray.needHelpeListArryV2 == null){
                buttonInfor.setVisibility(View.INVISIBLE);
                return;
            }
            mMarker.setTag(ToolArray.needHelpeListArryV2.length);
            mMarker.showInfoWindow();


            for (int i = 0; i < ToolArray.needHelpeListArryV2.length; i++) {


//                Log.e("count",""+i);
//
//                Log.e("count",""+ToolArray.needHelpeListArryV2[i].getWish_lat().toString());
//                Log.e("count",""+ToolArray.needHelpeListArryV2[i].getWish_long().toString());

                Double lat = Double.parseDouble(ToolArray.needHelpeListArryV2[i].getWish_lat().toString());
                Double lon = Double.parseDouble(ToolArray.needHelpeListArryV2[i].getWish_long().toString());
                otherMarker[i] = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maker_others)));
                otherMarker[i].setTag(i);

                position = i;
            }


//                        new DownloadImageTask((ImageView) mView.findViewById(R.id.ic_head))
//                                .execute(ToolArray.needHelpeListArry[position].getHope_Image().toString());
//
//                        txt_name.setText(ToolArray.needHelpeListArry[position].getHope_Name().toString());
//
//                        txt_type.setText(ToolArray.needHelpeListArry[position].getHope_Type().toString());
//
//                        txt_context.setText(ToolArray.needHelpeListArry[position].getHope_Content().toString());
//
//                        txt_address.setText(getAddressByLocation(position));


//            new DownloadImageTask((ImageView) mView.findViewById(R.id.ic_head))
//                    .execute(ToolArray.needHelpeListArryV2[position].getWish_Image().toString());


            Picasso.with(getActivity().getApplicationContext()).load(ToolArray.needHelpeListArryV2[position].getWish_Image().toString())//download URL
                    .placeholder(R.drawable.progress_animation)//loading
                    .error(R.drawable.headerror)//error
                    .into(mHead);//imageview


            txt_name.setText(ToolArray.needHelpeListArryV2[position].getWish_Name().toString());

            txt_type.setText(ToolArray.needHelpeListArryV2[position].getWish_Type().toString());

            txt_context.setText(ToolArray.needHelpeListArryV2[position].getWish_Content().toString());

            txt_address.setText(getAddressByLocation(position));


        } else {
            Toast.makeText(getActivity(), "偵測不到定位，請確認定位功能已開啟。", Toast.LENGTH_LONG).show();
        }
        markCenterInfor.setVisibility(View.INVISIBLE);
        markCenter.setVisibility(View.INVISIBLE);

        inforCheck = false;

    }


    public String getAddressByLocation(int i) {
        String returnAddress = "";
        try {

//            Double longitude = Double.parseDouble(ToolArray.needHelpeListArry[i].getHope_long().toString());       //取得經度
//            Double latitude = Double.parseDouble(ToolArray.needHelpeListArry[i].getHope_lat().toString());        //取得緯度

            Double longitude = Double.parseDouble(ToolArray.needHelpeListArryV2[i].getWish_long().toString());
            Double latitude = Double.parseDouble(ToolArray.needHelpeListArryV2[i].getWish_lat().toString());
            Geocoder gc = new Geocoder(getActivity(), Locale.TRADITIONAL_CHINESE);        //地區:台灣
            //自經緯度取得地址
            List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);

            if (!Geocoder.isPresent()) { //Since: API Level 9
                returnAddress = "Sorry! Geocoder service not Present.";
            }
            returnAddress = lstAddress.get(0).getAddressLine(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
    }




}
