package com.scenekey.activity.trending_summery;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.scenekey.R;
import com.scenekey.activity.trending_summery.Model.SummeryModel;
import com.scenekey.activity.trending_summery.adapter.GallerySlider_pager;

import java.util.List;

public class GallerySlideActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private ImageView iv_left_arrow;
    private ImageView iv_right_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_slide);
        setStatusBarColor();
        inItView();
        setViewPager();
    }

    private void inItView() {
        viewPager = findViewById(R.id.image_slider_pager);
        iv_left_arrow = findViewById(R.id.iv_left_arrow);
        iv_right_arrow = findViewById(R.id.iv_rigth_arrow);
        ImageView img_back = findViewById(R.id.img_back);
        setClicks(iv_left_arrow, iv_right_arrow, img_back);
    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void setClicks(View... views) {
        for (View view : views) view.setOnClickListener(this);
    }


    private void setViewPager() {
        List<SummeryModel.EventBean.FeedPostBean> feedPostlist = (List<SummeryModel.EventBean.FeedPostBean>) getIntent().getSerializableExtra("feedPostlist");
        int feedListPostion = getIntent().getIntExtra("feedListPostion", 0);
        GallerySlider_pager gallerySlider_pager = new GallerySlider_pager(GallerySlideActivity.this, feedPostlist);
        viewPager.setAdapter(gallerySlider_pager);
        viewPager.setCurrentItem(feedListPostion);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    iv_left_arrow.setVisibility(View.GONE);
                    iv_right_arrow.setVisibility(View.VISIBLE);
                } else if (feedPostlist.size() - 1 == position) {
                    iv_right_arrow.setVisibility(View.GONE);
                    iv_left_arrow.setVisibility(View.VISIBLE);
                } else {
                    iv_left_arrow.setVisibility(View.VISIBLE);
                    iv_right_arrow.setVisibility(View.VISIBLE);
                }

                if (feedPostlist.size() == 1) {
                    iv_left_arrow.setVisibility(View.GONE);
                    iv_right_arrow.setVisibility(View.GONE);
                }

            }


            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow: {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }
            }
            break;
            case R.id.iv_rigth_arrow: {
                int tab = viewPager.getCurrentItem();
                tab++;
                viewPager.setCurrentItem(tab);
            }
            break;

            case R.id.img_back: {
                onBackPressed();
            }
            break;
        }
    }
}
