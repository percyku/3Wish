package a3wish.main.com.aims212.sam.a3wish.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import a3wish.main.com.aims212.sam.a3wish.R;


/**
 * Created by percyku on 2017/9/13.
 */

public class MyMessageReceiveService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //手機執行本app時，背景接收到的數值，依據傳來的數值，給予廣播適當的訊息，再intent到PicViewAcvtivity,Pic360ViewAcvtivity,VideoViewActivity
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            final NotificationManager notifyManager =
                    (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplication());


            final int notifyID = 1000;
            final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
            long[] vibrate_effect = {1000, 500};
            Intent notificationIntent;
            Log.e("myLog", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            if (remoteMessage.getData().get("type").toString().equals("1"))
                notificationIntent = new Intent(getApplicationContext(), PicViewActivity.class);
            else
                notificationIntent = new Intent(getApplicationContext(), VideoViewActivity.class);


            notificationIntent.putExtra("url", remoteMessage.getData().get("url").toString());
            notificationIntent.putExtra("note", remoteMessage.getData().get("note").toString());


            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(soundUri)
                    .setVibrate(vibrate_effect)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);

            notifyManager.notify(notifyID, builder.build());


        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


}
