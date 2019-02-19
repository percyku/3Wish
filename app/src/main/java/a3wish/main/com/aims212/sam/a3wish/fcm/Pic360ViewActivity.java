package a3wish.main.com.aims212.sam.a3wish.fcm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

import a3wish.main.com.aims212.sam.a3wish.R;

public class Pic360ViewActivity extends AppCompatActivity {


    private VrPanoramaView vrPanoramaView;


    private ImageTask imageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic360_view);
        vrPanoramaView = (VrPanoramaView) findViewById(R.id.vr_pano);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        imageTask = new ImageTask();

        imageTask.execute(url);
    }




    private class ImageTask extends AsyncTask<String, Void, Bitmap> {
        //2.4.读取资产目录下面的a.jpg图片
        @Override
        protected Bitmap doInBackground(String... params) {
            String urldisplay = params[0];
            try {
                //2.4.1.获取文件流
//                InputStream inputStream = getAssets().open(params[0]);
                InputStream inputStream = new java.net.URL(urldisplay).openStream();
                //2.4.2.使用BitmapFactory可以将inputStream,file,byte[]-->Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //2.5.在主线程展示位图
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                //加载bitmap到vr图片控件  参1.Bitmap
                VrPanoramaView.Options options = new VrPanoramaView.Options();
                //TYPE_STEREO_OVER_UNDER :立体图片：上半画面显示在左眼，下半画面显示在右眼
                //TYPE_MONO :普通图片
                options.inputType = VrPanoramaView.Options.TYPE_MONO;
                //3.1.监听加载过程
                VrPanoramaEventListener listener = new VrPanoramaEventListener() {
                    //3.1.1.加载bitmap异常
                    @Override
                    public void onLoadError(String errorMessage) {
                        super.onLoadError(errorMessage);
                        Toast.makeText(Pic360ViewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    //3.1.2.加载bitmap成功
                    @Override
                    public void onLoadSuccess() {
                        super.onLoadSuccess();
                        Toast.makeText(Pic360ViewActivity.this, "進入VR圖片顯示...", Toast.LENGTH_SHORT).show();
                    }
                };
                vrPanoramaView.setEventListener(listener);
                vrPanoramaView.loadImageFromBitmap(bitmap, options);
            }
        }
    }

    //步骤三。处理全景控件展示细节
    //3.2.页面停到后台,暂停画面显示
    @Override
    protected void onPause() {
        super.onPause();
        if (vrPanoramaView != null) {
            vrPanoramaView.pauseRendering();
        }
    }

    //3.3.页面回到屏幕，再继续显示
    @Override
    protected void onResume() {
        super.onResume();
        if (vrPanoramaView != null) {
            vrPanoramaView.resumeRendering();
        }
        //3.5.按钮控制
        //3.5.1隐藏info按钮
        vrPanoramaView.setInfoButtonEnabled(true);
        //3.5.2隐藏全屏按钮
        vrPanoramaView.setFullscreenButtonEnabled(false);
        //3.5.3.展示全屏
        //FULLSCREEN_MONO 全屏模式
        //FULLSCREEN_STEREO CardBoard 纸盒


        vrPanoramaView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);
    }

    //3.4.页面关闭，销毁图片
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vrPanoramaView != null) {
            vrPanoramaView.shutdown();
        }
        if (imageTask != null && !imageTask.isCancelled()) {
            //防止页面退出AsyncTask引用的异常
            imageTask.cancel(true);
            imageTask = null;
        }
    }
}
