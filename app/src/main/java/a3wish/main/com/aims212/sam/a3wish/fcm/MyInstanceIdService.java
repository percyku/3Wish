package a3wish.main.com.aims212.sam.a3wish.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import a3wish.main.com.aims212.sam.a3wish.R;


/**
 * Created by percyku on 2017/9/13.
 */

public class MyInstanceIdService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        // 第一次開啟得到的Token id
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("myLog", "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),refreshedToken);
        editor.commit();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
    }


}
