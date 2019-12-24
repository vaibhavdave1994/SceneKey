package com.scenekey.activity.trending_summery;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scenekey.R;
import com.scenekey.activity.trending_summery.Model.SummeryModel;
import com.scenekey.activity.trending_summery.adapter.MultiGallery_Adapter;

import java.util.List;

public class GalleryMultiViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_multi_view);
        setStatusBarColor();
        getIntentData();
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

    public void getIntentData() {

        List<SummeryModel.EventBean.FeedPostBean> feedPostlist = (List<SummeryModel.EventBean.FeedPostBean>) getIntent().getSerializableExtra("feedPostlist");

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        ImageView img_back = findViewById(R.id.img_back);
        MultiGallery_Adapter adapter = new MultiGallery_Adapter(GalleryMultiViewActivity.this, feedPostlist);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        img_back.setOnClickListener(view -> onBackPressed());
    }


}
