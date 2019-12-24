package com.scenekey.activity.new_reg_flow;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.helper.Constant;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivityNewBasicInfo extends AppCompatActivity {

    protected Pop_Up_Option pop_up_option;
    EditText et_f_name, et_l_name;
    AppCompatImageView img_back;
    AppCompatButton btn_next;
    RelativeLayout rl_pic_selection;
    boolean isButtonClickable = false;
    Utility utility;
    String imageUri = "";
    CircleImageView civ_pp;
    Context context = this;
    String email;
    UserInfo userInfo = null;
    Uri uriforbitmap;
    private String profileImageUrl = "", mCurrentPhotoPath;
    private Bitmap profileImageBitmap;

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_part_one_basicinfo);
        setStatusBarColor();
        initView();

    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        if (userInfo != null) {
            if (!userInfo.userEmail.isEmpty() || userInfo.userEmail != null)
                email = userInfo.userEmail;

            if (!userInfo.fullname.equalsIgnoreCase("") || userInfo.fullname != null)
                et_f_name.setText(userInfo.fullname);

            if (!userInfo.lastName.equalsIgnoreCase("") || userInfo.lastName != null)
                et_l_name.setText(userInfo.lastName);

            if (!userInfo.userImage.equalsIgnoreCase("") && userInfo.userImage != null) {
                Picasso.with(context).load(userInfo.userImage).placeholder(R.drawable.app_icon)
                        .error(R.drawable.app_icon).into(civ_pp);

                getBitmapFromURL(userInfo.userImage);
            }
        }

        utility = new Utility(this);

        btn_next.setOnClickListener(v -> {
            if (isButtonClickable) {
                if (userInfo != null) {
                    Intent intent = new Intent(context, RegistrationActivityNewGender.class);
                    if (userInfo.byteArray == null) {
                        intent.putExtra("imageUri", profileImageUrl);
                    }
                    userInfo.fullname = et_f_name.getText().toString().trim();
                    userInfo.lastName = et_l_name.getText().toString().trim();
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                } else {

                    if (!profileImageUrl.equalsIgnoreCase("")) {
                        Intent intent = new Intent(context, RegistrationActivityNewGender.class);
                        intent.putExtra("imageUri", profileImageUrl);
                        intent.putExtra("f_name", et_f_name.getText().toString().trim());
                        intent.putExtra("l_name", et_l_name.getText().toString().trim());
                        intent.putExtra("email", email);
                        intent.putExtra("from", "basic_info");
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Please select Profile image", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(RegistrationActivityNewBasicInfo.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            }
        });

        img_back.setOnClickListener(v -> onBackPressed());

        rl_pic_selection.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    pop_up_option = initializePopup();
                    pop_up_option.setObject(null);
                    pop_up_option.show();
                }
            } else {
                pop_up_option = initializePopup();
                pop_up_option.setObject(null);
                pop_up_option.show();
            }


        });
    }

    //------------text watcher-----------
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

                if (!et_f_name.getText().toString().trim().equalsIgnoreCase("")
                        && !et_l_name.getText().toString().trim().equalsIgnoreCase("")) {
                    btn_next.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_reg_btn_back_primary));
                    btn_next.setTextColor(getResources().getColor(R.color.white));
                    isButtonClickable = true;
                } else {
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
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        dispatchTakePictureIntent();

                    }
                } else {
                    dispatchTakePictureIntent();

                }
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

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            Constant.MY_PERMISSIONS_REQUEST_CAMERA);
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
//        File storageDir = getCacheDir();
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
                    uriforbitmap = uri;
                    long time = System.currentTimeMillis();
                    String str = String.valueOf(time);
                    String destinatiomPath = str + ".jpg";

                    UCrop.Options options = new UCrop.Options();
                    options.setHideBottomControls(true);
                    assert uri != null;
                    UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinatiomPath)))
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(450, 450)
                            .withOptions(options)
                            .start(this);
                    break;

                case Constant.REQUEST_CAMERA:
                    // New Code
                    UCrop.Options options1 = new UCrop.Options();
                    options1.setHideBottomControls(true);
                    Uri uri1 = Uri.fromFile(new File(mCurrentPhotoPath));
                    UCrop.of(uri1, Uri.fromFile(new File(mCurrentPhotoPath)))
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(450, 450)
                            .withOptions(options1)
                            .start(this);
                    break;

                // New Code
                case UCrop.REQUEST_CROP:// Image Cropper
                    if (data != null) {
                        handleCropResult(data);
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
                    //callIntent(Constant.RESULT_LOAD);
                    pop_up_option = initializePopup();
                    pop_up_option.setObject(null);
                    pop_up_option.show();
                } else {
                    Toast.makeText(this, "You denied permission , can't select image", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "permission denied by user ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void getBitmapFromURL(String image) {
        if (image != null && !image.equals("")) {
            Picasso.with(RegistrationActivityNewBasicInfo.this).load(image).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {
                        if (bitmap != null) {
                            profileImageBitmap = bitmap;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            userInfo.byteArray = stream.toByteArray();
                        }

                    } catch (Exception e) {
                        // some action
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        }
    }

    private void handleCropResult(Intent data) {
        final Uri result = UCrop.getOutput(data);
        try {
            if (result != null) {
                civ_pp.setImageURI(result);


                profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);

                if (profileImageBitmap == null) {
                    Picasso.with(this)
                            .load(result)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                    profileImageBitmap = bitmap;
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                }

                int value = 0;
                if (profileImageBitmap.getHeight() <= profileImageBitmap.getWidth()) {
                    value = profileImageBitmap.getHeight();
                } else {
                    value = profileImageBitmap.getWidth();
                }
                Bitmap finalBitmap = Bitmap.createBitmap(profileImageBitmap, 0, 0, value, value);
                if (userInfo != null) {
                    userInfo.socialImageChanged = true;

                }
                profileImageUrl = result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(context, getResources().getString(R.string.alertImageException), Toast.LENGTH_SHORT).show();
        } catch (OutOfMemoryError error) {
            Toast.makeText(context, getResources().getString(R.string.alertOutOfMemory), Toast.LENGTH_SHORT).show();
        }
    }

}
