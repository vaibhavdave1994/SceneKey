package com.scenekey.lib_sources.SwipeCard;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.helper.WebServices;
import com.scenekey.util.CircleTransform;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardsAdapter extends ArrayAdapter<Card> {

    private  ArrayList<Card> cards;
    private  LayoutInflater layoutInflater;

    public CardsAdapter(Context context, ArrayList<Card> cards) {
        super(context, -1);
        this.cards = cards;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
         Card card = cards.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.z_cus_swipe_view_item, parent, false);
        }

         ImageView img=  convertView.findViewById(R.id.img_user);
        if (card.imageUrl != null) {
            try {
                Picasso.with(getContext()).load(WebServices.FEED_IMAGE+card.imageUrl).into((ImageView) convertView.findViewById(R.id.card_image));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (card.imageId != 0) {
            ((ImageView) convertView.findViewById(R.id.card_image)).setImageResource(card.imageId);
        }else if (card.bitmap != null) {
            ((ImageView) convertView.findViewById(R.id.card_image)).setImageBitmap(card.bitmap);
        }
        else {
             convertView.findViewById(R.id.card_image).setVisibility(View.GONE);
             convertView.findViewById(R.id.card_text).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.card_text)).setText(card.text);
        }
        if(card.userImage != null){
            try {
                Utility.e("Card Adapter image url",card.getUserImage());
                Picasso.with(getContext()).load(card.getUserImage()).transform(new CircleTransform()).into(img);
                ((TextView) convertView.findViewById(R.id.txt_time)).setText(getTimeInFormat(card.date));
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(card.imageint != 0){
            Picasso.with(getContext()).load(card.imageint).transform(new CircleTransform()).into(img);
        }

        return convertView;
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

   private String getTimeInFormat(String date) {
        String dateS = date.split(" ")[1];
        String dateArray[] = dateS.split(":");
        if (Integer.parseInt(dateArray[0]) > 12) {
            int hour = Integer.parseInt(dateArray[0]) - 12;
            return hour + ":" + dateArray[1] + " pm";
        }
        return dateArray[0] + ":" + dateArray[1] + " am ";
    }
}
