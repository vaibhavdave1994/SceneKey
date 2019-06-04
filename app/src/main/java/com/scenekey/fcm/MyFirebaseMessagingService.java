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
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.LoginActivity;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.fragment.Key_In_Event_Fragment;
import com.scenekey.fragment.OfferSFragment;
import com.scenekey.fragment.Reward_Fragment;
import com.scenekey.fragment.WalletsFragment;
import com.scenekey.helper.SessionManager;
import com.scenekey.model.NotificationModal;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

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
    private String bag_admin = "";
    private String notificationMsg = "";
    private NotificationModal notificationModal;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Utility.e(TAG, "Body: " + remoteMessage.getData().toString());
        Utility.e(TAG, "Body1: " + remoteMessage);


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


       /* Bundle[{google.delivered_priority=normal,
                google.sent_time=1546499418854,
                google.ttl=2419200,
                google.original_priority=normal,
                from=65254831562, title=scenekey,
                google.message_id=0:1546499418859764%0234bc20f9fd7ecd,
                message=post,
                notificationType={"eventId":"1063741","bag":null,"notificationType":20}}]*/

        if (remoteMessage.getData().containsKey("message")) {
            String notificationMsg = remoteMessage.getData().get("message").toString();
            String titleMsg = remoteMessage.getData().get("title").toString();
            Object notificationType = remoteMessage.getData().get("notificationType").toString();

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
            } catch (Exception e) {

                e.printStackTrace();
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
                Log.e(TAG, "notificationType20: " + notificationType1);
                break;

            case "3":
                if (isAppOnForeground(this)) {
                    Activity activity = SceneKey.getInstance().getActiveActivity();
                    if (activity instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        Fragment fragment = homeActivity.getCurrentFragment();

                        if (fragment instanceof Reward_Fragment || fragment instanceof WalletsFragment || fragment instanceof OfferSFragment) {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "RewardScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);
                                sendBroadcast(intent);
                            } else {
                                new Intent(this, LoginActivity.class);
                            }

                        } else {
                            if (sessionManager.isLoggedIn()) {
                                intent = new Intent("BroadcastNotification");
                                notificationModal.notificationCurrentScreen = "HomeScreen";
                                notificationModal.isBroadCast = "isBroadCast";
                                intent.putExtra("notificationModalReward", notificationModal);
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
                        intent.putExtra("notificationModalReward", notificationModal);
                    } else {
                        new Intent(this, LoginActivity.class);
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
        NotificationCompat.Builder notificationBuilder;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        CharSequence name = "MyChannal";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        if (!send) {

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(notificationModal.titleMsg)
                    .setContentText(notificationModal.message)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationModal.message))
                    .setAutoCancel(true)
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
