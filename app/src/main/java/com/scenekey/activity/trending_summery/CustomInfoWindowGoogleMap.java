package com.scenekey.activity.trending_summery;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.scenekey.R;
import com.scenekey.activity.trending_summery.Model.MapModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    CircleImageView img;
    boolean not_first_time_showing_info_window = false;
    private Timer timerHttp;
    private Context context;
    private MapModel mapModel;

    public CustomInfoWindowGoogleMap(Context ctx, MapModel mapModel) {
        context = ctx;
        this.mapModel = mapModel;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.mapinfowindow, null);

        TextView name_tv = view.findViewById(R.id.txt_eventName);
        img = view.findViewById(R.id.iv_circulerImage);

        if (not_first_time_showing_info_window) {
            Picasso.with(context).load(mapModel.getImg()).into(img);
        } else { // if it's the first time, load the image with the callback set
            not_first_time_showing_info_window = true;
            Picasso.with(context).load(mapModel.getImg()).into(img, new InfoWindowRefresher(marker));
        }

       /* assert mapModel != null;
        Glide.with(context).load(mapModel.getImg())
                .thumbnail(0.5f)
                .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sk_logo_image)
                .error(R.drawable.sk_logo_image)
                .into(img);*/

        name_tv.setText(mapModel.getName());
        return view;
    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {
        }
    }

}