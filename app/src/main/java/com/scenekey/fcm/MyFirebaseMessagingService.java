package com.scenekey.fcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.LoginActivity;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.fragment.OfferSFragment;
import com.scenekey.fragment.WalletsFragment;
import com.scenekey.helper.SessionManager;
import com.scenekey.model.NotificationModal;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    //    String title = "Notification!";
    String title = "";
    boolean toActivity;
    String CHANNEL_ID = "com.scenekey";// The id of the channel.
    private Notification notification;
    private String message = "";
    private String notficationType = "";
    private String first = "";
    private String second = "";
    private String eventId = "";
    private String fullAddress = "";
    private String notificationType1 = "";
    private String frequency = "";
    private String venue_id = "";
    private String bag_admin = "";
    private String notificationMsg = "";
    private NotificationModal notificationModal;
    private DatabaseReference mDatabase;





    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Utility.e(TAG, "Body: " + remoteMessage.getData().toString());
        Utility.e(TAG, "Body1: " + remoteMessage);


        Log.e("remoteMessage",remoteMessage.getData().toString());

       /* Bundle[{google.delivered_priority=normal,
                google.sent_time=1575965402034,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562,
                title={"notificationType":30},
        google.message_id=0:1575965402042871%0234bc20f9fd7ecd,
            message=Invite your friends to join SceneKey and earn key points,
                notificationType=[]}]*/

//   Bundle[{google.delivered_priority=normal,
//   google.sent_time=1574924166045,
//   google.ttl=2419200,
//   google.original_priority=normal,
//   from=65254831562,
//   title=scenekey,
//   google.message_id=0:1574924166053281%0234bc20f9fd7ecd,
//   message=Sunilmindiii has JOB on thursdays,
//   notificationType={"frequency_all":"4","bag":["336","161","30","3"],
//   "notificationType":"tag",
//   "venue_id":"159816",
//   "frequency":"4"}}]



        /* Bundle[{google.delivered_priority=normal,
                google.sent_time=1573712298754,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562,
                title=scenekey,
                google.message_id=0:1573712298759235%0234bc20f9fd7ecd,
            message=Sunilmindiii sent you a reward!,
                notificationType={"bag":"4","notificationType":3}}]*/

      /*  Bundle[{google.delivered_priority=normal,
                google.sent_time=1574075340829,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562,
                title=scenekey,
                google.message_id=0:1574075340838931%0234bc20f9fd7ecd,
            message=test venue 15-06-17 has A @ Miindiii on mondays, tuesdays, wednesdays, and thursdays,
                notificationType={"frequency_all":"1,2,3,4","bag":["67"],"notificationType":"tag","venue_id":"131419","frequency":"1"}}]
*/
        /* "notificationType":20 = (Like, comment, Update stats)
           notificationType":3 = rewards
           notificationType":22 = nudge
           notificationType":1 = make admin*/



       /* Bundle[{google.delivered_priority=normal,
                google.sent_time=1546497523245,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562,
                title=scenekey,
                google.message_id=0:1546497523249548%0234bc20f9fd7ecd,
                message=You have been messaged.,
                notificationType={"eventId":"1063741","nudgeId":3022,"bag":"13","notificationType":22}}]*/

      /*  Bundle[{google.delivered_priority=normal,
                google.sent_time=1546432515094,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562, title=scenekey,
                google.message_id=0:1546432515099535%0234bc20f9fd7ecd,
                message=sunilmindiii sent you an offer!,
                notificationType={"bag":["6"],"notificationType":3}}]*/


       /* Bundle[{trgoogle.delivered_priority=normal,
                google.sent_time=1546499418854,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562, title=scenekey,
                google.message_id=0:1546499418859764%0234bc20f9fd7ecd,
                message=post,
                notificationType={"eventId":"1063741","bag":null,"notificationType":20}}]*/

        if (remoteMessage.getData().containsKey("message")) {
            notificationMsg = remoteMessage.getData().get("message");
            String titleMsg = remoteMessage.getData().get("title");
            Object notificationType = remoteMessage.getData().get("notificationType");
            Object title = remoteMessage.getData().get("title");



            if (!notificationType.toString().equals("[]")) {
                try {
                    JSONObject obj = new JSONObject(notificationType.toString());

                    if (obj.has("fullAddress")) {
                        fullAddress = obj.getString("fullAddress");
                    }

                    if (obj.has("eventId")) {
                        eventId = obj.getString("eventId");
                    }

                    if (obj.has("notificationType")) {
                        notificationType1 = obj.getString("notificationType");
                    }

                    if (obj.has("frequency_all")) {
                        frequency = obj.getString("frequency_all");
                    }

                    if (obj.has("venue_id")) {
                        venue_id = obj.getString("venue_id");
                    }

                    Object bag = obj.getString("bag");

                    if (bag != null && bag instanceof String) {

                        Log.e(TAG, "bag: " + "String");


                        if (obj.has("bag")) {
                            bag_admin = obj.getString("bag");
                        }

                    } else if (bag != null && bag instanceof Array) {
                        Log.e(TAG, "bag: " + "Array");

                        JSONArray jsonArray = obj.getJSONArray("bag");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String bag_value = jsonArray.getString(i);
                            Log.e("bag_value", bag_value);
                        }
                    }

                    notificationModal = new NotificationModal();
                    notificationModal.eventId = eventId;
                    notificationModal.fullAddress = fullAddress;
                    notificationModal.bag_admin = bag_admin;
                    notificationModal.message = notificationMsg;
                    notificationModal.titleMsg = titleMsg;
                    notificationModal.notificationType1 = notificationType1;
                    notificationModal.frequency = frequency;
                    notificationModal.venue_id = venue_id;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    JSONObject objtitle = new JSONObject(title.toString());
                    if (objtitle.has("notificationType")) {
                        notificationType1 = objtitle.getString("notificationType");
                    }
                        notificationModal = new NotificationModal();
                        notificationModal.eventId = eventId;
                        notificationModal.fullAddress = fullAddress;
                        notificationModal.bag_admin = bag_admin;
                        notificationModal.message = notificationMsg;
                        notificationModal.titleMsg = "scenekey";
                        notificationModal.notificationType1 = notificationType1;
                        notificationModal.frequency = frequency;
                        notificationModal.venue_id = venue_id;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Intent intent = null;
        SessionManager sessionManager = new SessionManager(this);

        switch (notificationType1) {
            case "1":
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(this, HomeActivity.class);
                    notificationModal.notificationCurrentScreen = "HomeScreen";
                    notificationModal.isBroadCast = "noBroadCast";
                    intent.putExtra("notificationModalHome", notificationModal);

                    intent.putExtra("currentScreen", "HomeScreen");
                    intent.putExtra("isBroadCast", "noBroadCast");
                    intent.putExtra("eventId", notificationModal.eventId);
                    intent.putExtra("notificationType1", notificationModal.notificationType1);
                    intent.putExtra("frequency", notificationModal.frequency);
                    intent.putExtra("venue_id", notificationModal.venue_id);
                    intent.putExtra("message", notificationModal.message);
                } else {
                    new Intent(this, LoginActivity.class);
                }
                sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);

                break;

            case "2":
                if (isAppOnForeground(this)) {
                    Activity activity = SceneKey.getInstance().getActiveActivity();
                    if (activity instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        Fragment fragment = homeActivity.getCurrentFragment();

                        if (fragment instanceof Event_Fragment) {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");

                                notificationModal.notificationCurrentScreen = "EventScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalEvent", notificationModal);

                                intent.putExtra("currentScreen", "EventScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);

                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        } else {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "HomeScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalHome", notificationModal);

                                intent.putExtra("currentScreen", "HomeScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);

                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }
                        }
                    }
                } else {
                    if (sessionManager.isLoggedIn()) {
                        intent = new Intent(this, HomeActivity.class);
                        notificationModal.notificationCurrentScreen = "HomeScreen";
                        notificationModal.isBroadCast = "noBroadCast";
                        intent.putExtra("notificationModalHome", notificationModal);

                        intent.putExtra("currentScreen", "HomeScreen");
                        intent.putExtra("isBroadCast", "noBroadCast");
                        intent.putExtra("eventId", notificationModal.eventId);
                        intent.putExtra("notificationType1", notificationModal.notificationType1);
                        intent.putExtra("frequency", notificationModal.frequency);
                        intent.putExtra("venue_id", notificationModal.venue_id);
                        intent.putExtra("message", notificationModal.message);

                    } else {
                        new Intent(this, LoginActivity.class);
                    }
                    sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
                }

                break;

            case "20":
                if (sessionManager.isLoggedIn()) {
                    if (isAppOnForeground(this)) {
                        Activity activity = SceneKey.getInstance().getActiveActivity();
                        if (activity instanceof EventDetailsActivity) {
                            intent = new Intent("BroadcastNotification");
                            notificationModal.notificationCurrentScreen = "EventScreen";
                            notificationModal.isBroadCast = "isBroadCast";
                            intent.putExtra("notificationModalEvent", notificationModal);

                            intent.putExtra("currentScreen", "EventScreen");
                            intent.putExtra("isBroadCast", "isBroadCast");
                            intent.putExtra("eventId", notificationModal.eventId);
                            intent.putExtra("notificationType1", notificationModal.notificationType1);
                            intent.putExtra("frequency", notificationModal.frequency);
                            intent.putExtra("venue_id", notificationModal.venue_id);
                            intent.putExtra("message", notificationModal.message);

                            sendBroadcast(intent);
                        }
                    }
//                    else {
//                            intent = new Intent(this, EventDetailsActivity.class);
//                            notificationModal.notificationCurrentScreen = "EventScreen";
//                            notificationModal.isBroadCast = "noBroadCast";
//                            intent.putExtra("notificationModalHome", notificationModal);
//
//                        sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
//                    }

                }
//                else {
//                    new Intent(this, LoginActivity.class);
//                }
                /* Log.e(TAG, "notificationType20: " + notificationType1);*/
                break;

            case "3":
                if (isAppOnForeground(this)) {
                    Activity activity = SceneKey.getInstance().getActiveActivity();
                    if (activity instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        Fragment fragment = homeActivity.getCurrentFragment();

                        if (!(fragment instanceof OfferSFragment)) {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
                        }

//                        if (fragment instanceof Reward_Fragment || fragment instanceof WalletsFragment || fragment instanceof OfferSFragment) {
                        if (fragment instanceof OfferSFragment || fragment instanceof WalletsFragment) {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "RewardScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);

                                intent.putExtra("currentScreen", "RewardScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);


                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        } else {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "HomeScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);

                                intent.putExtra("currentScreen", "HomeScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);

                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        }
                    }
                } else {
                    if (sessionManager.isLoggedIn()) {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
                        intent = new Intent(this, HomeActivity.class);
                        notificationModal.notificationCurrentScreen = "HomeScreen";
                        notificationModal.isBroadCast = "noBroadCast";
                        intent.putExtra("notificationModalReward", notificationModal);

                        intent.putExtra("currentScreen", "HomeScreen");
                        intent.putExtra("isBroadCast", "noBroadCast");
                        intent.putExtra("eventId", notificationModal.eventId);
                        intent.putExtra("notificationType1", notificationModal.notificationType1);
                        intent.putExtra("frequency", notificationModal.frequency);
                        intent.putExtra("venue_id", notificationModal.venue_id);
                        intent.putExtra("message", notificationModal.message);
                    } else {
                        new Intent(this, LoginActivity.class);
                    }
                    sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
                }
                break;

            case "tag":

                if (isAppOnForeground(this)) {
                    Activity activity = SceneKey.getInstance().getActiveActivity();
                    if (activity instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        Fragment fragment = homeActivity.getCurrentFragment();
                            if (!(fragment instanceof OfferSFragment)) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
                            }

//                        if (fragment instanceof Reward_Fragment || fragment instanceof WalletsFragment || fragment instanceof OfferSFragment) {
                        if (fragment instanceof OfferSFragment || fragment instanceof WalletsFragment) {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "RewardScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);

                                intent.putExtra("currentScreen", "RewardScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);
                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        } else {
                            if (sessionManager.isLoggedIn()) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
                                intent = new Intent(this, HomeActivity.class);
                                notificationModal.notificationCurrentScreen = "HomeScreen";
                                notificationModal.isBroadCast = "noBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);

                                intent.putExtra("currentScreen", "HomeScreen");
                                intent.putExtra("isBroadCast", "noBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        }
                    }
                } else {
                    if (sessionManager.isLoggedIn()) {

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");

                        intent = new Intent(this, HomeActivity.class);
                        notificationModal.notificationCurrentScreen = "HomeScreen";
                        notificationModal.isBroadCast = "noBroadCast";

                        intent.putExtra("currentScreen", "HomeScreen");
                        intent.putExtra("isBroadCast", "noBroadCast");
                        intent.putExtra("eventId", notificationModal.eventId);
                        intent.putExtra("notificationType1", notificationModal.notificationType1);
                        intent.putExtra("frequency", notificationModal.frequency);
                        intent.putExtra("venue_id", notificationModal.venue_id);
                        intent.putExtra("message", notificationModal.message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        new Intent(this, LoginActivity.class);
                    }
                    sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
                }
              /*  if (sessionManager.isLoggedIn()) {
                    intent = new Intent(this, HomeActivity.class);
                    notificationModal.notificationCurrentScreen = "HomeScreen";
                    notificationModal.isBroadCast = "noBroadCast";
                    intent.putExtra("notificationModalReward", notificationModal);
                } else {
                    new Intent(this, LoginActivity.class);
                }
                sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
     */
                break;

            case "8":
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(this, HomeActivity.class);
                    notificationModal.notificationCurrentScreen = "HomeScreen";
                    notificationModal.isBroadCast = "noBroadCast";
                    intent.putExtra("notificationModalReward", notificationModal);

                    intent.putExtra("currentScreen", "HomeScreen");
                    intent.putExtra("isBroadCast", "noBroadCast");
                    intent.putExtra("eventId", notificationModal.eventId);
                    intent.putExtra("notificationType1", notificationModal.notificationType1);
                    intent.putExtra("frequency", notificationModal.frequency);
                    intent.putExtra("venue_id", notificationModal.venue_id);
                    intent.putExtra("message", notificationModal.message);
                } else {
                    new Intent(this, LoginActivity.class);
                }
                sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
                break;

            case "30": {
                if (sessionManager.isLoggedIn()) {


                    intent = new Intent(this, HomeActivity.class);
                    notificationModal.notificationCurrentScreen = "HomeScreen";
                    notificationModal.isBroadCast = "noBroadCast";
                    intent.putExtra("notificationModalReward", notificationModal);

                    intent.putExtra("currentScreen", "HomeScreen");
                    intent.putExtra("isBroadCast", "noBroadCast");
                    intent.putExtra("eventId", notificationModal.eventId);
                    intent.putExtra("notificationType1", notificationModal.notificationType1);
                    intent.putExtra("frequency", notificationModal.frequency);
                    intent.putExtra("venue_id", notificationModal.venue_id);
                    intent.putExtra("message", notificationModal.message);

                } else {
                    new Intent(this, LoginActivity.class);
                }
                Activity activity = SceneKey.getInstance().getActiveActivity();
                if (activity instanceof HomeActivity) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    Fragment fragment = homeActivity.getCurrentFragment();
                    if (!(fragment instanceof OfferSFragment)) {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");

                    }
                }
                sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);


            }
                break;


            case "22":
                if (isAppOnForeground(this)) {
                    Activity activity = SceneKey.getInstance().getActiveActivity();
                    if (activity instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        Fragment fragment = homeActivity.getCurrentFragment();

                        if (fragment instanceof Event_Fragment) {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "EventScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalEvent", notificationModal);


                                intent.putExtra("currentScreen", "EventScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);
                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        } else {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "HomeScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalHome", notificationModal);

                                intent.putExtra("currentScreen", "HomeScreen");
                                intent.putExtra("isBroadCast", "isBroadCast");
                                intent.putExtra("eventId", notificationModal.eventId);
                                intent.putExtra("notificationType1", notificationModal.notificationType1);
                                intent.putExtra("frequency", notificationModal.frequency);
                                intent.putExtra("venue_id", notificationModal.venue_id);
                                intent.putExtra("message", notificationModal.message);
                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }
                        }
                    }
                } else {
                    if (sessionManager.isLoggedIn()) {
                        intent = new Intent(this, HomeActivity.class);
                        notificationModal.notificationCurrentScreen = "HomeScreen";
                        notificationModal.isBroadCast = "noBroadCast";
                        intent.putExtra("notificationModalHome", notificationModal);

                        intent.putExtra("currentScreen", "HomeScreen");
                        intent.putExtra("isBroadCast", "noBroadCast");
                        intent.putExtra("eventId", notificationModal.eventId);
                        intent.putExtra("notificationType1", notificationModal.notificationType1);
                        intent.putExtra("frequency", notificationModal.frequency);
                        intent.putExtra("venue_id", notificationModal.venue_id);
                        intent.putExtra("message", notificationModal.message);


                    } else {
                        new Intent(this, LoginActivity.class);
                    }
                    sendNotification(remoteMessage.getTtl(), title, intent, message, false, notificationModal);
                }
                break;
        }
    }

    //    27/dec/2018 Changes
    private void sendNotification(int id, String title, Intent intent, String messageBody, boolean send, NotificationModal notificationModal) {
        PendingIntent pendingIntent = null;
        int badge = SceneKey.sessionManager.getbadgecount()+1;
        SceneKey.sessionManager.putbadgecount(badge);
        NotificationCompat.Builder notificationBuilder;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        NotificationModal NM = (NotificationModal) bundle.getSerializable("CON");
        CharSequence name = "MyChannal";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;


        if (!send) {

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(notificationModal.titleMsg)
                    .setContentText(notificationModal.message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationModal.message))
                    .setAutoCancel(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(SceneKey.sessionManager.getbadgecount())
                    .setContentIntent(pendingIntent);

        } else {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    // .setSmallIcon(R.drawable.icon_app_192_white)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(notificationModal.titleMsg)
                    .setContentText(notificationModal.message)
                    .setSmallIcon(R.drawable.app_icon)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationModal.message))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(SceneKey.sessionManager.getbadgecount())
                    .setContentIntent(pendingIntent);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());


    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void CustomNotification(int id, String title, Intent intent, String messageBody, boolean send, NotificationModal notificationModal) {
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setImageViewResource(R.id.image, R.drawable.app_icon);
        contentView.setTextViewText(R.id.title_name, notificationModal.titleMsg);
        contentView.setTextViewText(R.id.text_name, notificationModal.message);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContent(contentView)
                .setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(0, notification);





    }
}






