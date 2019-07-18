package com.scenekey.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.EditText;

import com.scenekey.R;
import com.scenekey.util.Utility;

/**
 * Created by mindiii on 5/2/18.
 */

public class Validation {

    private Context context;
    private Utility utility;

    public Validation(Context context) {
        this.context = context;
        utility = new Utility(context);
    }

    private String getString(EditText editText) {
        return editText.getText().toString();
    }

    /*public boolean isFullNameValid(EditText editText) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.fullNameEmptyError));
            editText.requestFocus();
            return false;
        } else if (!(editText.length() >= 3)) {
            editText.setError(context.getString(R.string.fullNameLengthError));
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }*/

    //New Code
    public boolean isFirstNameValid(EditText editText) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.firstNameEmptyError));
            editText.requestFocus();
            return false;
        } else if (!(editText.length() >= 3)) {
            editText.setError(context.getString(R.string.firstNameLengthError));
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    // New Code
    public boolean isLastNameValid(EditText editText) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.lastNameEmptyError));
            editText.requestFocus();
            return false;
        } else if (!(editText.length() >= 3)) {
            editText.setError(context.getString(R.string.lastNameLengthError));
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public boolean isFullNValid(EditText editText, Animation shake) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.fullNameEmptyError));
            //editText.requestFocus();
            editText.startAnimation(shake);
            return false;
        } else if (!(editText.length() >= 3)) {
            editText.setError(context.getString(R.string.fullNameLengthError));
            //editText.requestFocus();
            editText.startAnimation(shake);
            return false;
        } else {
            return true;
        }

    }


    public boolean isLastNValid(EditText editText, Animation shake) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.lastNameEmptyError));
            //editText.requestFocus();
            editText.startAnimation(shake);
            return false;
        } else if (!(editText.length() >= 3)) {
            editText.setError(context.getString(R.string.lastNameLengthError));
            //editText.requestFocus();
            editText.startAnimation(shake);
            return false;
        } else {
            return true;
        }

    }

    public boolean isEmailValid(EditText editText) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.emailEmptyError));
            editText.requestFocus();
            return false;
        } else {
            boolean bool = android.util.Patterns.EMAIL_ADDRESS.matcher(getString(editText)).matches();
            if (!bool) {
                editText.setError(context.getString(R.string.emailInvalidError));
                editText.requestFocus();
            }
            return bool;
        }
    }


    public boolean isPasswordValid(EditText editText) {
        if (getString(editText).isEmpty()) {
            editText.setError(context.getString(R.string.passEmptyError));
            editText.requestFocus();
            return false;
        } else if (editText.getText().length() >= 6) {
            editText.requestFocus();
            return true;
        } else {
            utility.showCustomPopup(context.getString(R.string.passLengthError), String.valueOf(R.font.montserrat_medium));
           // editText.setError(context.getString(R.string.passLengthError));
            editText.requestFocus();
            return false;
        }
    }

    public boolean isImageUpload(Bitmap profileImageBitmap) {
        if (profileImageBitmap == null) {
//            Utility.showToast(context,"Please select profile picture",0);
            utility.showCustomPopup("Please select profile picture.", String.valueOf(R.font.montserrat_medium));
            return false;
        } else return true;
    }


    /*.................................isEmpty......................................*/
    public boolean isEmpty(String textView) {
        if (textView.equals("") || textView.length() == 0) {
            return true;
        }
        return false;
    }


}
