package com.scenekey.activity.new_reg_flow;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.scenekey.R;
import com.scenekey.helper.Constant;

public class RegistrationActivityNewPassword extends AppCompatActivity{

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
