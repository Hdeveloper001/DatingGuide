package com.taxidriver.ubertips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;


enum PageType {PAGE_START, PAGE_MENU, PAGE_MENU_BACK, PAGE_DETAIL};

public class MainActivity extends Activity implements View.OnClickListener {
    private InterstitialAd mInterstitialAd;
    private Button mBtnStart;
    private ImageView mBtnBack;
    private TextView mTextTitle;
    private TextView mTextContent;
    private TextView mTextTopbar;
    private ImageView mImageContent;
    private LinearLayout mLayoutTopBar;
    private LinearLayout mLayoutStart;
    private LinearLayout mLayoutLogo;
    private ListView mListMenu;
    private ScrollView mScrollDetail;
    private PageType mPageType;

    private ArrayList<String> mArrTopic;
    private ArrayList<String> mArrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = (Button) findViewById(R.id.button_start);
        mBtnBack = (ImageView) findViewById(R.id.button_back);
        mTextTitle = (TextView) findViewById(R.id.txt_title);
        mTextContent = (TextView) findViewById(R.id.txt_content);
        mTextTopbar = (TextView) findViewById(R.id.txt_topbar);
        mImageContent = (ImageView) findViewById(R.id.img_content);
        mLayoutTopBar = (LinearLayout) findViewById(R.id.layout_topmenu);
        mLayoutLogo = (LinearLayout) findViewById(R.id.layout_logo);
        mLayoutStart = (LinearLayout) findViewById(R.id.layout_start);
        mScrollDetail = (ScrollView) findViewById(R.id.sv_detail);
        mListMenu = (ListView) findViewById(R.id.lv_menu);
        Utils mUtils = new Utils(this);

        mArrTopic = mUtils.txtToArray("menu.txt");
        mArrImage = mUtils.txtToArray("images.txt");

        mListMenu.setAdapter(new MenuListAdapter());

        mBtnStart.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });

        gotoPage(PageType.PAGE_START);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                gotoPage(PageType.PAGE_MENU);
                break;
            case R.id.button_back:
                if (mPageType == PageType.PAGE_DETAIL) {
                    gotoPage(PageType.PAGE_MENU_BACK);
                    return;
                }

                if (mPageType == PageType.PAGE_MENU || mPageType == PageType.PAGE_MENU_BACK) {
                    gotoPage(PageType.PAGE_START);
                }
                break;
        }
    }

    static class ViewHolder {
        TextView mTextView;
    }
    class MenuListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mArrImage.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;

            if(view != null) {
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = getLayoutInflater().inflate(R.layout.list_items, null);
                viewHolder = new ViewHolder();
                view.setTag(viewHolder);
            }

            viewHolder.mTextView = (TextView) view.findViewById(R.id.textView1);
            viewHolder.mTextView.setText(mArrTopic.get(position).replace(".txt",""));

            viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Utils mUtils = new Utils(MainActivity.this);

                    String filename = mArrTopic.get(position);
                    String imagename =  mArrImage.get(position);
                    String sContent = mUtils.loadAssetTxtAsString(filename);
                    Bitmap bImage = mUtils.getAssetsImage(imagename);

                    mTextTitle.setText(filename.replace(".txt", ""));
                    mTextContent.setText(Html.fromHtml(sContent));
                    mImageContent.setImageBitmap(bImage);
                    mTextTopbar.setText(filename.replace(".txt", ""));
                    gotoPage(PageType.PAGE_DETAIL);
                }
            });

            return view;
        }
    }

    private void gotoPage(PageType nPageType) {
        Animation animation;
        mPageType = nPageType;

        switch (nPageType) {
            case PAGE_START:
                mLayoutLogo.setVisibility(View.VISIBLE);
                mLayoutStart.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_right_half);
                mLayoutStart.startAnimation(animation);
                mListMenu.setVisibility(View.GONE);
                mScrollDetail.setVisibility(View.GONE);
                mLayoutTopBar.setVisibility(View.GONE);
                break;
            case PAGE_MENU:
                if (mInterstitialAd.isLoaded()) {
                    new MyAsyncTask().execute(0);
                    return;
                }

                mLayoutLogo.setVisibility(View.GONE);
                mScrollDetail.setVisibility(View.GONE);
                mListMenu.setVisibility(View.VISIBLE);
                mLayoutTopBar.setVisibility(View.VISIBLE);
                mTextTopbar.setText(R.string.app_name);
                break;
            case PAGE_MENU_BACK:
                if (mInterstitialAd.isLoaded()) {
                    new MyAsyncTask().execute(0);
                    return;
                }

                mLayoutLogo.setVisibility(View.GONE);
                mScrollDetail.setVisibility(View.GONE);
                mListMenu.setVisibility(View.VISIBLE);
                mLayoutTopBar.setVisibility(View.VISIBLE);
                mTextTopbar.setText(R.string.app_name);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_topbar);
                mLayoutTopBar.startAnimation(animation);
                break;
            case PAGE_DETAIL:
                mLayoutLogo.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_left);
                mListMenu.startAnimation(animation);
                mListMenu.setVisibility(View.GONE);

                mScrollDetail.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_left_detail);
                mScrollDetail.startAnimation(animation);

                mLayoutTopBar.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_topbar);
                mLayoutTopBar.startAnimation(animation);
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        public MyAsyncTask() {
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            publishProgress(0);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mInterstitialAd.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mLayoutLogo.setVisibility(View.GONE);
            mScrollDetail.setVisibility(View.GONE);
            mListMenu.setVisibility(View.VISIBLE);
            mLayoutTopBar.setVisibility(View.VISIBLE);
            mTextTopbar.setText(R.string.app_name);
        }
    }
}



