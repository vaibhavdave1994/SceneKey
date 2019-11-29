package com.scenekey.activity.invite_friend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.tamir7.contacts.Contact;
import com.scenekey.R;
import com.scenekey.helper.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Ravi Birla on 09,September,2019
 */
public class Adapterinvite_friend extends RecyclerView.Adapter<Adapterinvite_friend.MyViewHolder> {

    private Context context;
    private List<ContactModel> list;
    private List<ContactModel> globlemCountry;
    private CheckListener checkListener;
    public int x = 0;
    private String[] itemNumber;

    interface CheckListener{
        void checkpos(int pos,int m);
    }


    Adapterinvite_friend(Context context,List<ContactModel> list,CheckListener checkListener,String[] itemNumber) {
        this.context = context;
        this.list = list;
        this.checkListener = checkListener;
        this.itemNumber = itemNumber;
        SessionManager sessionManager = new SessionManager(context);


        Set<ContactModel> unique = new LinkedHashSet<ContactModel>(list);
        list = new ArrayList<ContactModel>(unique);



        if (list.size() == 0)
        {
            sessionManager.putContactcheck(false);
        }


        this.globlemCountry = new ArrayList<>();
        globlemCountry.addAll(list);

       /* for (int i = 0; i < list.size(); i++) {
            globlemCountry.get(i).setFlag(flags[i]);
        }*/

        Collections.sort(list, new Comparator<ContactModel>()
        {
            @Override
            public int compare(ContactModel o1, ContactModel o2) {
                return o1.name.compareTo(o2.name);
            }

        });

    }


    public void filter(String charText) {
        charText = charText.toLowerCase();
        list.clear();
        if (charText.length() == 0) {
            list.addAll(globlemCountry);
            Collections.sort(list, new Comparator<ContactModel>()
            {
                @Override
                public int compare(ContactModel o1, ContactModel o2) {
                    return o1.name.compareTo(o2.name);
                }

            });

        } else {
            for (int i = 0; i < globlemCountry.size(); i++) {
                if (globlemCountry.get(i).name.toLowerCase().startsWith(charText)||globlemCountry.get(i).name.toLowerCase().contains(charText)) {
                    list.add(globlemCountry.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public Adapterinvite_friend.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.invitefriend_layout, parent, false);
        return new Adapterinvite_friend.MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final Adapterinvite_friend.MyViewHolder holder, final int position) {

        ContactModel contactModel = list.get(position);
        String test = contactModel.name;

        if (itemNumber != null){
        for (String item : itemNumber)
        {
            if (item.equals(contactModel.mobileNumber))
            {

                    holder.tv_invite.setText(R.string.resend);
                    holder.tv_friendstatus.setText(R.string.sent);
                    holder.tv_invite.setVisibility(View.VISIBLE);
                    holder.rlcheck_contact.setVisibility(View.GONE);


            }
        }}

        char firstL = contactModel.name.charAt(0);
        if (firstL == '0' || firstL == '1' || firstL == '2' || firstL == '3' || firstL == '4' || firstL == '5' ||
                firstL == '6' || firstL == '7' || firstL == '8' || firstL == '9' || firstL == '+'){
            holder.tv_friendname.setText(R.string.noname);
            holder.place_holder.setVisibility(View.VISIBLE);
            holder.iv_hoopimage.setVisibility(View.GONE);
            holder.tvFirstName.setVisibility(View.GONE);
        }else {
            holder.tv_friendname.setText(contactModel.name);
            holder.iv_hoopimage.setVisibility(View.VISIBLE);
            holder.tvFirstName.setVisibility(View.VISIBLE);



            if (contactModel.photo == null){
                char firstN;
                String[] item =test.split(" ");
                holder.place_holder.setVisibility(View.GONE);
                holder.iv_hoopimage.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_circle_friend));
                if (item.length >2)
                {
                    StringTokenizer tokens = new StringTokenizer(test, " ");
                    String first = tokens.nextToken();// this will contain "Fruit"
                    String second = tokens.nextToken();
                    String third = tokens.nextToken();
                    first=first.substring(0,1).toUpperCase()+first.substring(1, first.length());
                    firstN = first.charAt(0);
                    second=second.substring(0,1).toUpperCase()+second.substring(1, second.length());
                    char secondN = second.charAt(0);
                    third=third.substring(0,1).toUpperCase()+third.substring(1, third.length());
                    char thirdN = third.charAt(0);
                    holder.tvFirstName.setText(String.valueOf(firstN +""+ secondN+""+thirdN));
                }
                else if (test.contains(" ")){
                    StringTokenizer tokens = new StringTokenizer(test, " ");
                    String first = tokens.nextToken();// this will contain "Fruit"
                    String second = tokens.nextToken();
                    first=first.substring(0,1).toUpperCase()+first.substring(1, first.length());
                    firstN = first.charAt(0);
                    second=second.substring(0,1).toUpperCase()+second.substring(1, second.length());
                    char secondN = second.charAt(0);
                    holder.tvFirstName.setText(String.valueOf(firstN +""+ secondN));
                }else {
                    test=test.substring(0,1).toUpperCase()+test.substring(1, test.length());
                    firstN = test.charAt(0);
                    holder.tvFirstName.setText(String.valueOf(firstN));
                }
            }  else {
//                holder.iv_hoopimage.setImageBitmap(bitmap);
                Glide.with(context).load(contactModel.photo).into(holder.iv_hoopimage);
                holder.place_holder.setVisibility(View.GONE);
//                Glide.with(context).load(contactModel.photo).into(holder.iv_hoopimage);
                holder.tvFirstName.setVisibility(View.GONE);
            }
        }


        if (contactModel.isselect)
        {
            holder.tv_invite.setVisibility(View.GONE);
            holder.rlcheck_contact.setVisibility(View.VISIBLE);
        }
        else {
            holder.tv_invite.setVisibility(View.VISIBLE);
            holder.rlcheck_contact.setVisibility(View.GONE);
        }



    }


   /* @Override
    public long getItemId(int position) {
        return position;
    }*/

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_friendname,tvFirstName,tv_invite,tv_friendstatus;
        private ImageView iv_hoopimage,place_holder,ivcheck_contact;
        private RelativeLayout rlcheck_contact;
        MyViewHolder(final View itemView) {
            super(itemView);

            tv_friendname = itemView.findViewById(R.id.tv_friendname);
            iv_hoopimage = itemView.findViewById(R.id.iv_hoopimage);
            place_holder = itemView.findViewById(R.id.place_holder);
            tvFirstName= itemView.findViewById(R.id.tvFirstName);
            tv_invite= itemView.findViewById(R.id.tv_invite);
            ivcheck_contact= itemView.findViewById(R.id.ivcheck_contact);
            rlcheck_contact= itemView.findViewById(R.id.rlcheck_contact);
            tv_friendstatus= itemView.findViewById(R.id.tv_friendstatus);
            tv_invite.setOnClickListener(this);
            rlcheck_contact.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.tv_invite:
                {
                    x++;
                    ContactModel contactModel = list.get(getAdapterPosition());
                    if (contactModel.isselect)
                    {

                        contactModel.setIsselect(false);
                        checkListener.checkpos(getAdapterPosition(),x);
                        notifyDataSetChanged();
                    }
                    else {


                        contactModel.setIsselect(true);
                        checkListener.checkpos(getAdapterPosition(),x);
                        notifyDataSetChanged();

                    }
                }
                break;

                case R.id.rlcheck_contact:
                {
                    x--;
                    ContactModel contactModel = list.get(getAdapterPosition());
                    if (contactModel.isselect)
                    {

                        contactModel.setIsselect(false);
                        checkListener.checkpos(getAdapterPosition(),x);
                        notifyDataSetChanged();
                    }
                    else {

                        contactModel.setIsselect(true);
                        checkListener.checkpos(getAdapterPosition(),x);
                        notifyDataSetChanged();

                    }
                }
                break;
            }
        }
    }
}
