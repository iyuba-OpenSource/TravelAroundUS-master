package com.iyuba.concept2.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.iyuba.concept2.R;


public class RightMenuPopupWindow extends PopupWindow {

    public RightMenuPopupWindow(Context context) {
        setFocusable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_right_menu, null, false);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setElevation(30);
    }

}
