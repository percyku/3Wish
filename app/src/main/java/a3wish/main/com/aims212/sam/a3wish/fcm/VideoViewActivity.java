package a3wish.main.com.aims212.sam.a3wish.fcm;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import a3wish.main.com.aims212.sam.a3wish.R;

public class VideoViewActivity extends AppCompatActivity implements
        YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.OnFullscreenListener {
    private YouTubePlayer mYouTubePlayer;
    private boolean mIsFullScreen = false;
    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";

    private YouTubePlayerFragment playerFragment;
    private TextView achieveTxt;
    private Button viewBt;

    String urlStr[] = new String[]{};
    String noteStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);


        // 主要是接收fcm資料的推播（主要手機完全沒開啟本app,透過androidManifest標記得頁面）
        // 或是透過MyMessageReceiveService 的PendingIntent 過來的資料，
        // 這邊已經統一了
        Intent intent = getIntent();
        //圓夢連接
        //例如：1/youtube 影片id(前面的1代表是一般影片，而2為360影片)
        //這邊會在下面進行split
        String url = intent.getStringExtra("url");
        //祝福話語
        noteStr = intent.getStringExtra("note");



        if (url != null) {
            Log.e("FCM", "url:" + url);
            urlStr = url.split(",");
        }

        if (noteStr != null)
            Log.e("FCM", "note:" + noteStr);

        achieveTxt = (TextView) this.findViewById(R.id.achieveTxt);
        achieveTxt.setText("給予祝福的話語:" + "\r\n" + noteStr);

        viewBt = (Button) this.findViewById(R.id.viewBt);

        if (urlStr[0].equals("1")) {
            viewBt.setVisibility(View.GONE);
            panToVideo(urlStr[1]);

        } else {
            viewBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+urlStr[1])));
                }
            });

        }


//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=wYFAtWbVhmE")));
//        finish();


    }


    public void panToVideo(final String youtubeId) {
        popPlayerFromBackStack();
        playerFragment = YouTubePlayerFragment
                .newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, playerFragment,
                        YOUTUBE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
        //google api金鑰，才能啟動youtube api
        playerFragment.initialize("AIzaSyBHuTmrJr2LRGGyIE7W9HGCsJJ72AJu4NI",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(youtubeId);
                        mYouTubePlayer = youTubePlayer;
                        youTubePlayer
                                .setPlayerStateChangeListener(VideoViewActivity.this);
                        youTubePlayer
                                .setOnFullscreenListener(VideoViewActivity.this);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult result) {
                        showErrorToast(result.toString());
                    }
                });
    }

    public boolean popPlayerFromBackStack() {
        if (mIsFullScreen) {
            mYouTubePlayer.setFullscreen(false);
            return false;
        }
        if (getFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG) != null) {
            getFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    @Override
    public void onFullscreen(boolean fullScreen) {
        mIsFullScreen = fullScreen;


    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {


    }

    private void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(new Intent(UpdateMemberActivity.this, MainActivity.class));
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
