package com.scenekey.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.util.ImageUtil;
import com.scenekey.util.RoundedTransformation;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mindiii on 13/2/18.
 */

public class MapInfo_Adapter implements GoogleMap.InfoWindowAdapter {
    private Activity activity;
    private ArrayList<Events> eventArrayList;
    private int heightA,widthA;

    public MapInfo_Adapter(Activity activity, ArrayList<Events> eventArrayList) {
        this.activity = activity;
        this.eventArrayList = eventArrayList;
        heightA  = ((HomeActivity.ActivityWidth) * 3 / 4);
        widthA = HomeActivity.ActivityWidth;
        for (Events events : eventArrayList) {
            Picasso.with(activity).load(events.getEvent().getImage()).into(new ImageView(activity));
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        try {
            int position = Integer.parseInt(marker.getId().replace("m", ""));
            final Events events = eventArrayList.get(position);
            View myContentsView = activity.getLayoutInflater().inflate(R.layout.adapter_custom_map_info, null);
            ImageView img_event =  myContentsView.findViewById(R.id.img_event);
            int radius = (int) activity.getResources().getDimension(R.dimen._8sdp);

            try {
                Bitmap bitmap = ImageUtil.getBitmapByUrl(events.getEvent().getImage());

                //bitmap = (new RoundedTransformation(radius,1).transform(bitmap));
                img_event.setImageBitmap(bitmap);
                Picasso.with(activity).load(events.getEvent().getImage()).into(img_event);
            } catch (Exception e) {
                Picasso.with(activity).load(events.getEvent().getImage()).into(img_event);
                e.printStackTrace();
            }


            // Picasso.with(activity).load(events.getEvent().getImage()).transform(new RoundedTransformation(radius,1)).into(img_event);
            Utility.e("map", events.getEvent().event_name + " : " + events.getEvent().getImage());
            TextView txt_eventName =  myContentsView.findViewById(R.id.txt_eventName);
            TextView txt_eventAdress =  myContentsView.findViewById(R.id.txt_eventAdress);
            TextView txt_eventDate =  myContentsView.findViewById(R.id.txt_eventDate);
            TextView txt_like =  myContentsView.findViewById(R.id.txt_like);
            TextView txt_time =  myContentsView.findViewById(R.id.txt_time);
            LinearLayout hour =  myContentsView.findViewById(R.id.hour);
            LinearLayout like =  myContentsView.findViewById(R.id.like);

            try{
                Picasso.with(activity).load(events.getEvent().getImage()).transform(new RoundedTransformation(radius,0)).placeholder(R.drawable.scene1).into(img_event);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Event event = events.getEvent();

            if (events.isOngoing) {
                hour.setVisibility(View.GONE);
                like.setVisibility(View.VISIBLE);
                try{
                    if(Integer.parseInt(event.rating)==0)txt_like.setText("--");
                    else txt_like.setText(event.rating);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            else {
                hour.setVisibility(View.VISIBLE);
                like.setVisibility(View.GONE);
                txt_time.setText(events.remainingTime);
            }

            txt_eventName.setText(event.event_name);
            //holder.txt_eventAdress.setText(venue.getAddress()+" "+ activity.getDistanceMile(new Double[]{Double.valueOf(venue.getLatitude()), Double.valueOf(venue.getLongitude()), Double.valueOf(activity.getlatlong()[0]), Double.valueOf(activity.getlatlong()[1])})+" M");
            txt_eventAdress.setText(events.getVenue().getAddress());
            txt_eventDate.setText(events.timeFormat);
            Utility.e("Time and other",events.timeFormat+" : "+events.remainingTime);
            return myContentsView;
        } catch (Exception e) {

            return null;
        }

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public ArrayList<Events> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<Events> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }
}

