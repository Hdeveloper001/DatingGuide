package com.taxidriver.ubertips;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class SplashScreen extends Activity {
    private FrameLayout mLayoutSplash;
    private ImageView mImageIcon;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        mLayoutSplash = (FrameLayout) findViewById(R.id.layout_content);
        mImageIcon = (ImageView) findViewById(R.id.img_icon);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_logo);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        mLayoutSplash.startAnimation(animation);
        mImageIcon.startAnimation(animation);



        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                mLayoutSplash.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                new MyAsyncTask().execute(0);
            }
        };

        handler.postDelayed(r, 2000);
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        private int nPos;

        public MyAsyncTask() {
            nPos = 0;
        }
        @Override
        protected Integer doInBackground(Integer... params) {
            while (nPos < 100) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {}

                nPos += 5;

                publishProgress(nPos);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(nPos);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
