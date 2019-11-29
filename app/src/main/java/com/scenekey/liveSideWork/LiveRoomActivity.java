package com.scenekey.liveSideWork;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.scenekey.R;
import com.scenekey.lib_sources.SwipeCard.Card;

import java.util.ArrayList;

public class LiveRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Card> roomLiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        inItView();
    }

    private void inItView() {

       /* if (getIntent().getSerializableExtra("liveComminList") != null) {
            roomLiveList = (ArrayList<Card>) getIntent().getSerializableExtra("liveComminList");
        }

        RecyclerView theRoomRecyclerView_live = findViewById(R.id.theRoomRecyclerView_live);
        ImageView img_live_room_back = findViewById(R.id.img_live_room_back);
        img_live_room_back.setOnClickListener(this);

        if (roomLiveList != null){
            TheLiveRoomAdapter theLiveRoomAdapter = new TheLiveRoomAdapter(roomLiveList, this);
            theRoomRecyclerView_live.setLayoutManager(new GridLayoutManager(LiveRoomActivity.this, 3));
            theRoomRecyclerView_live.setAdapter(theLiveRoomAdapter);
        }*/
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.img_live_room_back:
                onBackPressed();
                break;
        }
    }
}
