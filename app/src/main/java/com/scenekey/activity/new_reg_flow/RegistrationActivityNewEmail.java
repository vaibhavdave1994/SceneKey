package com.scenekey.activity.new_reg_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.IntroActivity;
import com.scenekey.activity.LoginActivity;
import com.scenekey.aws_service.AWSImage;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Permission;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.scenekey.helper.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class RegistrationActivityNewEmail extends AppCompatActivity{

     EditText et_email;
     AppCompatButton btn_next;
     boolean isValidEmail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_email);
        setStatusBarColor();
        initView();

    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    private void initView() {
        et_email = findViewById(R.id.et_email);
        btn_next = findViewById(R.id.btn_next);
        textWatcher(et_email);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(RegistrationActivityNewEmail.this,);

            }
        });
    }

    //------text watcher-----------
    private void textWatcher(EditText et_serch_post) {

        et_serch_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = editable.toString();

                if (searchText.toLowerCase().matches(Constant.emailPattern)){
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_reg_btn_back_primary));
                    btn_next.setTextColor(getResources().getColor(R.color.white));
                    isValidEmail = true;
                    return;
                }
                else {
                    isValidEmail = false;
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_next_btn_desable));
                    btn_next.setTextColor(getResources().getColor(R.color.button_text_new_reg));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
