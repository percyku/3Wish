package a3wish.main.com.aims212.sam.a3wish.tab;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import a3wish.main.com.aims212.sam.a3wish.HopePageActivity;
import a3wish.main.com.aims212.sam.a3wish.R;

/**
 * Created by percyku on 2017/9/26.
 */

public class HopeFragment extends Fragment implements
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener {

    private View mView;
    private ImageView markCenter, checkPosition;
    private LinearLayout markCenterInfor;

    private GoogleMap mMap, otherMap;


    protected GoogleApiClient mGoogleApiClient;


    protected Location mLastLocation;

    private boolean inforCheck = false;


    private GoogleMap map;
    SupportMapFragment mapFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("VideoAchieve_Fragment");


        initialMap();


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
            mView = inflater.inflate(R.layout.fragment_hope, container, false);
            initial();
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.hope_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.hope_fragment, mapFragment).commit();
        } else {
            mapFragment.getMapAsync(this);
        }


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

    }


    private void initial() {

        markCenterInfor = (LinearLayout) mView.findViewById(R.id.markerInfor);
        markCenterInfor.setVisibility(View.INVISIBLE);
        markCenterInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("許願池")
                        .setMessage("要在這許下一個願望嗎？")
                        .setNegativeButton("許願", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "跳轉到許願頁面", Toast.LENGTH_SHORT).show();
                                double screemLatitude=mMap.getCameraPosition().target.latitude;
                                double screemLongitude=mMap.getCameraPosition().target.longitude;
                                Log.i("screemLatitude:", "" + ""+screemLatitude);
                                Log.i("screemLongitude:", "" + ""+screemLongitude);


                                if(screemLatitude!=0.0&&screemLongitude!=0.0){
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Latitude",""+screemLatitude);
                                    bundle.putString("Longitude",""+screemLongitude);
                                    startActivity(new Intent(getActivity(), HopePageActivity.class).putExtras(bundle));
                                }

                            }
                        }).setPositiveButton("現在還不需要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                    }


                }).show();
            }
        });
        markCenter = (ImageView) mView.findViewById(R.id.mark);
        markCenter.setVisibility(View.INVISIBLE);
        checkPosition = (ImageView) mView.findViewById(R.id.check_position);

        checkPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {

                    Log.i("Latitude:", "" + mLastLocation.getLatitude());
                    Log.i("Longitude:", "" + mLastLocation.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));


                } else {
                    Toast.makeText(getActivity(), "偵測不到定位，請確認定位功能已開啟。", Toast.LENGTH_LONG).show();
                }
                markCenterInfor.setVisibility(View.INVISIBLE);
//                    markCenter.setVisibility(View.INVISIBLE);
                markCenter.setVisibility(View.INVISIBLE);

                inforCheck = false;
            }
        });


    }

    private void initialMap() {
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.hope_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.hope_fragment, mapFragment).commit();
            mapFragment.getMapAsync(this);
            buildGoogleApiClient();
        } else {
            mapFragment.getMapAsync(this);
            buildGoogleApiClient();
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            Log.i("Latitude:", "" + mLastLocation.getLatitude());
            Log.i("Longitude:", "" + mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));


        } else {
            Toast.makeText(getActivity(), "偵測不到定位，請確認定位功能已開啟。", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraIdle() {
        if (inforCheck)
            markCenterInfor.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {
        markCenterInfor.setVisibility(View.GONE);

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Toast.makeText(getActivity(), "移動中...",
                    Toast.LENGTH_SHORT).show();
            if (!inforCheck) {
                markCenter.setVisibility(View.VISIBLE);
                inforCheck = true;
            }

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

        Log.i("OnClickLatitude:", "" + ""+latLng.latitude);
        Log.i("OnClickLongitude:", "" + ""+latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}

