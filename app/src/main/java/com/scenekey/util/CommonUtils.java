package com.scenekey.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class CommonUtils {


    private static Toast toast;

    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;

    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;


    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static Dialog showLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setContentView(R.layout.custom_progress_dialog_layout);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

   /* public static void showToastAlert(final Activity activity, String title, String message) {

        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_dilog, null);
        TextView tv_title = layout.findViewById(R.id.tvTitle);
        TextView msgTv = layout.findViewById(R.id.tvMessages);

        TextView tvPopupOk = layout.findViewById(R.id.tvPopupOk);
        LinearLayout lnr_ok = layout.findViewById(R.id.lnr_ok);

        msgTv.setText(message);

        tv_title.setText(title);
        msgTv.setText(message);
        lnr_ok.setVisibility(View.GONE);
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }*/

   /* public static void showLogoutAlert(Activity activity, String title, String message) {

        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_dilog, null);
        TextView tv_title = layout.findViewById(R.id.tvTitle);
        TextView msgTv = layout.findViewById(R.id.tvMessages);
        tv_title.setText(title);
        msgTv.setText(message);
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
*/

    // New Code
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Activity inContext, Uri contentUri) {

        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = inContext.getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getTimestamp(String format) {
        return new java.text.SimpleDateFormat(format, java.util.Locale.US).format(new java.util.Date());
    }



    public static void showToast(Activity activity, String message, int len) {
        if (toast != null) toast.cancel();

        // Create the object once.
        toast = Toast.makeText(activity, message, len);
        toast.show();
    }

    public static void snackbar(Activity activity, @NonNull View coordinatorLayout, @NonNull String message) {
        if (toast != null) toast.cancel();
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#ffffff"));  //old color 1976d2
        textView.setGravity(Gravity.CENTER);
        snackbar.setActionTextColor(Color.parseColor("#1976d2"));
        sbView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));  //Color.WHITE
        snackbar.show();
    }


  /*  public static void showCustomAlertt(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.custom_dilog);

        TextView tvMessages = dialog.findViewById(R.id.tvMessages);
        tvMessages.setText(message);
        TextView tvPopupOk = dialog.findViewById(R.id.tvPopupOk);

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSessionCustomAlertt(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.custom_dilog);

        TextView tvMessages = dialog.findViewById(R.id.tvMessages);
        tvMessages.setText(message);
        TextView tvPopupOk = dialog.findViewById(R.id.tvPopupOk);

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OutLoud.getDataManager().logout(activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

   /* public static String getRealPathFromUri(Activity inContext, Uri contentUri) {

        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = inContext.getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/


    public static File persistImage(Context context, Bitmap bitmap, String name) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            return imageFile;
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing bitmap", e);
        }

        return imageFile;
    }

    @TargetApi(19)
    private static String generateFromKitkat(Uri uri, Context context) {
        String filePath = null;

        try {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String wholeID = DocumentsContract.getDocumentId(uri);

                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Video.Media.DATA};
                String sel = MediaStore.Video.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().
                        query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }

                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(context, "video file is corrupted, please try another file.", Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static Bitmap getVidioThumbnail(String path)
    {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if (bitmap != null) {
                return bitmap;
            }
        }
        // MediaMetadataRetriever is available on API Level 8 but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();
            final Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, path);
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
                bitmap = (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                final byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                if (bitmap == null) {
                    bitmap = (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
                }
            }
        } catch (Exception e) {
            bitmap = null;
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (final Exception ignored) {
            }
        }
        return bitmap;


    }


    public static String generatePath(Uri uri, Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }


    /*public static void parseError(Context context, ANError anError) {
        if (anError.getErrorBody() != null) {
            try {
                JSONObject jsonObject = new JSONObject(anError.getErrorBody());
                String status = jsonObject.getString("status");
                String message = "";
                if (jsonObject.has("message")) message = jsonObject.getString("message");

                // {"status":"fail","message":"Invalid token","authToken":"","responseCode":300,"isActive":0}
                if (message.equals("Invalid token")) {
                    //OutLoud.getDataManager().logout(activity);
                    showSessionCustomAlertt((Activity) context, "Session Expeired");
                    //showSessionAlert((Activity) context, "Session Expeired", "You have to login again");
                }

            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


   /*public static  void custom_dialog(String message) {
       final Dialog dialog = new Dialog(context, (context instanceof AuthActivity));
       Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       dialog.setContentView(R.layout.custom_dilog);

       TextView tvTitle = dialog.findViewById(R.id.tvTitle);
       TextView tvMessages = dialog.findViewById(R.id.tvMessages);
       TextView tvPopupOk = dialog.findViewById(R.id.tvPopupOk);

       tvMessages.setText(message);

       tvPopupOk.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.dismiss();
           }
       });

       dialog.show();
   }*/
}
