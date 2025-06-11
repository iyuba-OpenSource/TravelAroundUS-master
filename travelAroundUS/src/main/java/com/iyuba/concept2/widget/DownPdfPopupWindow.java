package com.iyuba.concept2.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iyuba.concept2.R;


public class DownPdfPopupWindow extends PopupWindow {

    public DownPdfPopupWindow(Context context) {
        setFocusable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_down_pdf, null, false);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setElevation(30);
    }

}
