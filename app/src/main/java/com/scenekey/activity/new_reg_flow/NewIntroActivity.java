package com.scenekey.activity.new_reg_flow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;

public class NewIntroActivity extends RegistrationActivity {

    private TextView[] dots;
    private int[] layouts;
    private LinearLayout dotsLayout;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int position;
    AppCompatButton btn_continue,btn_google,btn_fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  StatusBarUtil.setTranslucent(this);

        setContentView(R.layout.activity_intro_new);

        initView();

    }

    public void initView(){
        btn_fb = findViewById(R.id.btn_fb);
        btn_google = findViewById(R.id.btn_google);
        btn_continue = findViewById(R.id.btn_continue);
        viewPager = findViewById(R.id.a6_viewpager);
        dotsLayout = findViewById(R.id.layoutDots);

        layouts = new int[]{
                R.layout.welcome_slide1_new,
                R.layout.welcome_slide2_new,
                R.layout.welcome_slide3_new,
                R.layout.welcome_slide4_new
        };

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginApi();
            }
        });

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmialLoginApi();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewIntroActivity.this, RegistrationActivityNewEmail.class));
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active_new);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(26);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            NewIntroActivity.this.position = position;

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.start));
//                btnSkip.setVisibility(View.VISIBLE);
            } else {
                // still pages are left
//                btnNext.setText(getString(R.string.next));
//                btnSkip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //Util.printLog("Scroll",position+" : "+arg0);
           /* if(position==layouts.length-1 && arg0==1){
                launchHomeScreen();
            }*/

        }


    };


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        View top_status = findViewById(R.id.top_status);

      if (!(SceneKey.sessionManager.isSoftKey())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                StatusBarUtil.setStatusBarTranslucent(this, true);

            } else {
                top_status.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                top_status.setBackgroundResource(R.color.white);
            } else {
                StatusBarUtil.setStatusBarColor(this, R.color.new_white_bg);
                top_status.setVisibility(View.VISIBLE);
            }
        }else{
            StatusBarUtil.setStatusBarColor(this,R.color.white);
            top_status.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                top_status.setBackgroundResource(R.color.white);
            } else {
                StatusBarUtil.setStatusBarColor(this, R.color.new_white_bg);
            }
        }*/

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}