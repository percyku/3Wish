package a3wish.main.com.aims212.sam.a3wish.fcm;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import a3wish.main.com.aims212.sam.a3wish.R;
import it.sephiroth.android.library.picasso.Picasso;


public class PicViewActivity extends AppCompatActivity {

    private TextView achieveTxt;
    private ImageView achieveImage;
    private Button viewBt;


    String urlStr[] = new String[]{};
    String noteStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_view);

        // 主要是接收fcm資料的推播（主要手機完全沒開啟本app,透過androidManifest標記得頁面）
        // 或是透過MyMessageReceiveService 的PendingIntent 過來的資料，
        // 這邊已經統一了
        Intent intent = getIntent();
        //圓夢連接
        //例如：1/google drive 檔案分享連結（這邊的檔案為圖片分享）
        // (前面的1代表是一般圖片，而2為360圖片)
        //這邊會在下面進行split
        String url = intent.getStringExtra("url");
        //圓夢連接
        noteStr = intent.getStringExtra("note");

        if (url != null) {
            Log.e("FCM", "url:" + url);
            urlStr = url.split(",");
        }
        if (noteStr != null)
            Log.e("FCM", "note:" + noteStr);


        initial();

    }

    private void initial() {
        achieveImage = (ImageView) this.findViewById(R.id.achieveImage);
        Picasso.with(getApplicationContext()).load(urlStr[1])//download URL
                .placeholder(R.drawable.progress_animation)//use defaul image
                .error(R.drawable.heads)//if failed
                .into(achieveImage);//imageview


        achieveTxt = (TextView) this.findViewById(R.id.achieveTxt);
        achieveTxt.setText("給予祝福的話語:" + "\r\n" + noteStr);

        viewBt = (Button) this.findViewById(R.id.viewBt);
        viewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //這邊是一般圖片與360圖片都可以開啟
                //因為圓夢者上傳圖片時，無法判別圖片是否為360圖片，只能透過radio button進行選擇
                //因此怕圓夢者忘記點擊碩360圖片
                //這邊才採取警告，並未禁止
                if (urlStr[0].equals("1")) {
                    new AlertDialog.Builder(PicViewActivity.this)
                            .setMessage("建議不要點，開效果不好")
                            .setPositiveButton("硬要", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(PicViewActivity.this, Pic360ViewActivity.class).putExtra("url", urlStr[1]));
                                }
                            })
                            .setNegativeButton("好我乖乖", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create()
                            .show();

                    return;
                }
                startActivity(new Intent(PicViewActivity.this, Pic360ViewActivity.class).putExtra("url", urlStr[1]));

            }
        });

    }


}
