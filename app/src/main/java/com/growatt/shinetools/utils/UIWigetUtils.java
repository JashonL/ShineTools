package com.growatt.shinetools.utils;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import com.growatt.shinetools.R;

public class UIWigetUtils {


    public static void clickPasswordSwitch(ImageView imageView, EditText editText, boolean visible) {
        if (visible) {
            imageView.setImageResource(R.drawable.icon_signin_see);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            imageView.setImageResource(R.drawable.icon_signin_conceal);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (editText.getText().length() > 0) {
            editText.setSelection(editText.getText().length());
        }
    }

}
