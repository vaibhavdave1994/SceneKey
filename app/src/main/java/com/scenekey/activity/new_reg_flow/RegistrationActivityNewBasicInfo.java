package com.scenekey.activity.new_reg_flow;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.activity.RegistrationActivity;
import com.scenekey.base.BaseActivity;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.helper.Constant;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.helper.WebServices;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.scenekey.helper.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class RegistrationActivityNewBasicInfo extends AppCompatActivity {

     EditText et_f_name,et_l_name;
     AppCompatImageView img_back;
     AppCompatButton btn_next;
     RelativeLayout rl_pic_selection;
     boolean isButtonClickable = false;
     Utility utility;
     String imageUri = "";
     CircleImageView civ_pp;
    protected Pop_Up_Option pop_up_option;
    private String  profileImageUrl = "", mCurrentPhotoPath;
    Context context = this;
    private Bitmap profileImageBitmap;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_basicinfo);
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
        et_f_name = findViewById(R.id.et_f_name);
        et_l_name = findViewById(R.id.et_l_name);
        btn_next = findViewById(R.id.btn_next);
        civ_pp = findViewById(R.id.civ_pp);
        img_back = findViewById(R.id.img_back);
        rl_pic_selection = findViewById(R.id.rl_pic_selection);
        textWatcher(et_f_name);
        textWatcher(et_l_name);
        email = getIntent().getStringExtra("email");
        utility = new Utility(this);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isButtonClickable){
                    if(!profileImageUrl.equalsIgnoreCase("")) {
                        Intent intent = new Intent(context, RegistrationActivityNewGender.class);
                        intent.putExtra("imageUri", profileImageUrl);
                        intent.putExtra("f_name", et_f_name.getText().toString().trim());
                        intent.putExtra("l_name", et_l_name.getText().toString().trim());
                        intent.putExtra("email", email);
                        intent.putExtra("from", "basic_info");
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "Please select Profile image", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegistrationActivityNewBasicInfo.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rl_pic_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop_up_option = initializePopup();
                pop_up_option.setObject(null);
                pop_up_option.show();
            }
        });
    }

    //------text watcher-----------
    public void textWatcher(EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = editable.toString();
                
                if(!et_f_name.getText().toString().trim().equalsIgnoreCase("")
                && !et_l_name.getText().toString().trim().equalsIgnoreCase("")){
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_reg_btn_back_primary));
                    btn_next.setTextColor(getResources().getColor(R.color.white));
                    isButtonClickable = true;
                    return;
                }
                else {
                    isButtonClickable = false;
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

    public Pop_Up_Option initializePopup() {
        pop_up_option = new Pop_Up_Option(RegistrationActivityNewBasicInfo.this) {
            @Override
            public void onGalleryClick(Pop_Up_Option dialog, Object object) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constant.MY_PERMISSIONS_REQUEST_EXTERNAL);
                    } else {
                        callIntent(Constant.RESULT_LOAD);
                    }
                } else {
                    callIntent(Constant.RESULT_LOAD);
                }
                dialog.dismiss();
            }

            @Override
            public void onCameraClick(Pop_Up_Option dialog, Object object) {
                // New Code
                dispatchTakePictureIntent();
                dialog.dismiss();
            }
        };
        return pop_up_option;
    }

    public void callIntent(int caseId) {

        switch (caseId) {
            case Constant.INTENT_CAMERA:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Constant.RESULT_LOAD:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, Constant.RESULT_LOAD);
                break;

            case Constant.REQUEST_CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                }
                break;

            case Constant.MY_PERMISSIONS_REQUEST_EXTERNAL:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.MY_PERMISSIONS_REQUEST_EXTERNAL);
                }
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constant.REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        ImageSessionManager.getInstance().createImageSession(mCurrentPhotoPath, false);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   Bitmap bitmap;

        if (resultCode != 0) {
            switch (requestCode) {
                case Constant.RESULT_LOAD:
                    Uri uri = data.getData();
                    // New Code
                    if (uri != null) {
                        // Calling Image Cropper
                        CropImage.activity(uri).setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setAspectRatio(4, 4).start(this);
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constant.REQUEST_CAMERA:
                    // New Code
                    Uri uri1 = Uri.fromFile(new File(mCurrentPhotoPath));
                    if (uri1 != null) {
                        // Calling Image Cropper
                        CropImage.activity(uri1).setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setAspectRatio(4, 4).start(this);
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    break;

                // New Code
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:// Image Cropper
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        if (result != null) {
                            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                            profileImageUrl = Utility.getImageUri(this, profileImageBitmap).toString();
                            String uri_path = Utility.getRealPathFromURI(this, Utility.getImageUri(this, profileImageBitmap));
                            ImageSessionManager.getInstance().createImageSession(uri_path, false);

                            Log.e("UPLOAD PATH", uri_path);
                            Picasso.with(context).load(profileImageUrl).into(civ_pp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.alertImageException), Toast.LENGTH_SHORT).show();
                    } catch (OutOfMemoryError error) {
                        Toast.makeText(context, getResources().getString(R.string.alertOutOfMemory), Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constant.RESULT_LOAD);
                } else {
                    Toast.makeText(this, "You denied permission , can't select image", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constant.INTENT_CAMERA);
                } else {
                    Toast.makeText(this, "permission denied by user ", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
