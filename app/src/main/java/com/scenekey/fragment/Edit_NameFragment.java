package com.scenekey.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Edit_NameFragment extends Fragment implements View.OnClickListener {


    public final String TAG = Edit_NameFragment.class.toString();
    private EditText et_firstName, et_lastName;
    private TextView txt_updateNAme;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private Animation shake;
    private ImageView img_f1_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit__name, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        shake = AnimationUtils.loadAnimation(context, R.anim.shakeanim);

        customProgressBar = new CustomProgressBar(context);
        et_firstName = view.findViewById(R.id.et_firstName);
        et_lastName = view.findViewById(R.id.et_lastName);
        txt_updateNAme = view.findViewById(R.id.txt_updateNAme);
        img_f1_back = view.findViewById(R.id.img_f1_back);
        txt_updateNAme.setOnClickListener(this);
        img_f1_back.setOnClickListener(this);

        String firstName = activity.userInfo().fullname;
        String lastName = activity.userInfo().lastName;

        et_firstName.setText(firstName);
        et_lastName.setText(lastName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_updateNAme:
                Validation validation = new Validation(context);
                if (validation.isFullNValid(et_firstName, shake) && validation.isLastNValid(et_lastName, shake)) {

                    String firstName = et_firstName.getText().toString().trim();
                    String lastName = et_lastName.getText().toString().trim();
                    updateName(firstName, lastName);
                } else {
                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.img_f1_back:
                activity.onBackPressed();
                break;
        }
    }

    private void updateName(final String firstName, final String lastName) {
        activity.showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.UPDATEPROFILE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Log.v("response", response);
                    // get response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        //String totalWallet = jsonObject.getString("totalWallet");

                        if (status.equals("1")) {
                            UserInfo userInfo = activity.userInfo();
                            userInfo.fullname = firstName;
                            userInfo.lastName = lastName;
                            activity.updateSession(userInfo);

                            CustomeClick.getmInctance().onTextChange(userInfo);

                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            activity.onBackPressed();

                        } else {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fullname", firstName);
                    params.put("lastName", lastName);
                    params.put("user_id", activity.userInfo().userid);
                    Utility.e("Update Profile params", params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
            Toast.makeText(context, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

    private void showProgDialog(boolean b) {
        customProgressBar.setCanceledOnTouchOutside(b);
        customProgressBar.setCancelable(b);
        customProgressBar.show();
    }

    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }
}
