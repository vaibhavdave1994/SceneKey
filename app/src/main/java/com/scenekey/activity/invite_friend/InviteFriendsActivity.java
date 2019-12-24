package com.scenekey.activity.invite_friend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.scenekey.R;
import com.scenekey.Retrofitprocess.RetrofitClient;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.SessionManager;
import com.scenekey.model.UserInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener {

    public final static int MY_PERMISSIONS_READ_CONTACTS = 0x1;
    private RecyclerView recyclerView;
    private Adapterinvite_friend adapter;
    private List<ContactModel> contectList;
    private List<ContactRoomModel> roomContectList;
    private CustomProgressBar progressBar;
    private Button btn_done;
    private SessionManager sessionManager;
    private String phonenumber = "";
    private String[] itemNumber;
    private List<Contact> contactsLibraryList;
    private TextView tv_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        inItView();
        Contacts.initialize(this);
        getContactList();
        }

    private void inItView() {
        contectList = new ArrayList<>();
        contactsLibraryList = new ArrayList<>();
        roomContectList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        EditText et_serch_post = findViewById(R.id.et_serch_post);
        btn_done = findViewById(R.id.btn_done);
        progressBar = new CustomProgressBar(this);
        ImageView ivback = findViewById(R.id.ivback);
        tv_found = findViewById(R.id.tv_found);
        ivback.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        textWatcher(et_serch_post);
    }


    private void getContactList() {


        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InviteFriendsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 9000);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getContactList();
                }
            },2000);

        } else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    contactsLibraryList = Contacts.getQuery().find();


                                    for (int i = 0; i < contactsLibraryList.size(); i++) {
                                            if (contactsLibraryList.get(i).getPhoneNumbers().size() !=0) {
                                                ContactModel model = new ContactModel(String.valueOf(contactsLibraryList.get(i).getId()), contactsLibraryList.get(i).getDisplayName(), contactsLibraryList.get(i).getPhotoUri()
                                                        , contactsLibraryList.get(i).getPhoneNumbers().get(0).getNumber());
                                                contectList.add(model);
                                            }
                                        }

                                    getContactListApiData();
                                    if(contactsLibraryList.size() == 0)
                                    {
                                        tv_found.setVisibility(View.VISIBLE);

                                    }
                                    else {
                                        tv_found.setVisibility(View.GONE);

                                    }
                                }




                            });


                        }catch (Exception e){
                        Log.e("error",e.getMessage());
                    }
                    }
            };
            thread.start();
        }


    }


    public void setAdapterView() {

        adapter = new Adapterinvite_friend(this, contectList, new Adapterinvite_friend.CheckListener() {
            @Override
            public void checkpos(int pos, int x) {
                StringBuilder commaSepValueBuilder = new StringBuilder();
                for (int i = 0; i < contectList.size(); i++) {
                    if (contectList.get(i).isselect) {
                        commaSepValueBuilder.append(contectList.get(i).mobileNumber);
                        commaSepValueBuilder.append(",");
                        Log.v("id", commaSepValueBuilder.toString());
                    }
                }

                if (commaSepValueBuilder.length() > 0) {
                    String finalStr = commaSepValueBuilder.toString().substring(0, commaSepValueBuilder.toString().length() - 1).concat("");
                    Log.e("phonenumber", finalStr);
                    phonenumber = finalStr;


                } else {
                    phonenumber = "";

                }

                if (x > 0) {
                    btn_done.setVisibility(View.VISIBLE);
                } else {
                    btn_done.setVisibility(View.GONE);
                }
            }
        }, itemNumber);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivback: {
                onBackPressed();
            }
            break;
            case R.id.btn_done: {


                for (int i = 0; i < roomContectList.size(); i++) {

                    if (roomContectList.get(i).isselect) {
                        roomContectList.get(i).setIsselect(false);
                    }
                }
                contactListApiData();
            }
            break;
        }
    }


    private void textWatcher(EditText et_serch_post) {

        et_serch_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.filter(charSequence.toString());
                    //  customList1.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void contactListApiData() {
        progressBar.show();
        String userid = sessionManager.getUserInfo().userid;
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getAnotherApi().contactList(phonenumber, userid);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                try {
                    progressBar.dismiss();
                    switch (response.code()) {
                        case 200: {
                            String stresult = Objects.requireNonNull(response.body()).string();
                            Log.d("response", stresult);
                            JSONObject jsonObject = new JSONObject(stresult);
                            String statusCode = jsonObject.optString("status");
                            String msg = jsonObject.optString("message");
                            int key_points1 = jsonObject.optInt("key_points");
                            if (statusCode.equals("success")) {
                                UserInfo userInfo = sessionManager.getUserInfo();
                                userInfo.key_points = String.valueOf(Integer.parseInt(sessionManager.getUserInfo().key_points) + key_points1);
                                showKeyPoints(String.valueOf(key_points1), false);
                                HomeActivity.updateSession(userInfo);

                            }
                            break;
                        }
                        case 400: {
                            String result = Objects.requireNonNull(response.errorBody()).string();
                            Log.d("response400", result);
                            JSONObject jsonObject = new JSONObject(result);
                            String statusCode = jsonObject.optString("status");
                            String msg = jsonObject.optString("message");
                            if (statusCode.equals("true")) {
                                Toast.makeText(InviteFriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 401:
                            try {
                                Log.d("ResponseInvalid", Objects.requireNonNull(response.errorBody()).string());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressBar.dismiss();
            }
        });

    }

    public void getContactListApiData() {
        progressBar.show();
        String userid = sessionManager.getUserInfo().userid;
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getAnotherApi().getContactnumber(userid);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                try {
                    progressBar.dismiss();
                    switch (response.code()) {
                        case 200: {
                            String stresult = Objects.requireNonNull(response.body()).string();
                            Log.d("response", stresult);
                            JSONObject jsonObject = new JSONObject(stresult);
                            String statusCode = jsonObject.optString("status");
                            String contactList1 = jsonObject.optString("contactList");
                            itemNumber = contactList1.split(",");
                            setAdapterView();


                            break;
                        }
                        case 400: {
                            String result = Objects.requireNonNull(response.errorBody()).string();
                            Log.d("response400", result);
                            JSONObject jsonObject = new JSONObject(result);
                            String statusCode = jsonObject.optString("status");
                            String msg = jsonObject.optString("message");
                            if (statusCode.equals("true")) {
                                Toast.makeText(InviteFriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 401:
                            try {
                                Log.d("ResponseInvalid", Objects.requireNonNull(response.errorBody()).string());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressBar.dismiss();
            }
        });

    }

    private void showKeyPoints(String s, final boolean shouldMsgDialogShow) {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_keypoint_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        TextView tvKeyPoint;
        tvKeyPoint = dialog.findViewById(R.id.tvKeyPoint);
        tvKeyPoint.setText(String.format("+%s", s));


        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                50);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(animate);
        Handler handler = new Handler();

        handler.postDelayed((Runnable) () -> {
            TranslateAnimation animate1 = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    50,  // fromYDelta
                    0);                // toYDelta
            animate1.setDuration(500);
            animate1.setFillAfter(true);
            ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(animate1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    onBackPressed();

                }
            }, 500);


        }, 1400);


        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }






    }


}







































  /* if (sessionManager.getContactcheck()) {
            progressBar.show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    roomContectList.addAll(dataManager().getContactList());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setAdapterView();
                            progressBar.dismiss();

                        }
                    });

                }
            }).start();

        } else {
            new AsyncTaskRunner().execute();
        }
*/


   /* private void startContactLookService() {
        try {
            if (ActivityCompat.checkSelfPermission(InviteFriendsActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {//Checking permission
                //Starting service for registering ContactObserver
                Intent intent = new Intent(InviteFriendsActivity.this, ContactWatchService.class);
                startService(intent);
            } else {
                //Ask for READ_CONTACTS permission
                ActivityCompat.requestPermissions(InviteFriendsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_READ_CONTACTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

   /* private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 10001);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {

                        synchronized (this) {
                            sleep(1000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    sessionManager.putContactcheck(true);
                                    String lastnumber = "0";
                                    contectList = getContacts(InviteFriendsActivity.this);
                                    Set<ContactModel> unique = new LinkedHashSet<>(contectList);
                                    contectList = new ArrayList<>(unique);

                                    Log.e("contectList", contectList.toString());


                                    for (int i = 0; i < contectList.size(); i++) {
                                        String strphoto = "";
                                        ContactRoomModel contactRoomModel = new ContactRoomModel(contectList.get(i).id, contectList.get(i).name
                                                , contectList.get(i).mobileNumber, strphoto, contectList.get(i).flag);


                                        roomContectList.add(contactRoomModel);
                                    }
                                    setAdapterView();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dataManager().contactData(roomContectList);

                                        }
                                    }).start();


                                }
                            });


                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
//                        progressBar.dismiss();
                    }

                }

                ;
            };
            thread.start();

        }
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startContactLookService();
        }
        if (requestCode == 10001) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new AsyncTaskRunner().execute();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }


    }*/

    /*public List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);


                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    assert cursorInfo != null;
                    while (cursorInfo.moveToNext()) {
                        String number = cursorInfo
                                .getString(cursorInfo
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        ContactModel info = new ContactModel();
//                        info.id = id;
//                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
////                        info.photo = photo;
//                        info.photoURI = pURI;
//                        list.add(info);
                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }
        return list;
    }*/


     /* @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    });

                }
            };
            handler.postDelayed(runnable, 3000);


        }


        @Override
        protected String doInBackground(Void... voids) {
            try {
                showContacts();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }


    }*/
