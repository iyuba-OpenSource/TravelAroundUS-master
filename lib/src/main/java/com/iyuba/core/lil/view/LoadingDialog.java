package com.iyuba.core.lil.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.core.lil.util.LibResUtil;
import com.iyuba.core.lil.util.LibScreenUtil;
import com.iyuba.lib.R;

/**
 * @title: 加载弹窗
 * @date: 2023/4/26 18:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoadingDialog extends AlertDialog {

    private Context context;
    private TextView progressMsg;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = LayoutInflater.from(LibResUtil.getInstance().getContext()).inflate(R.layout.dialog_loading,null);
        setContentView(rootView);

        progressMsg = rootView.findViewById(R.id.progressMsg);
        progressMsg.setText("正在加载数据中～");
        setCanceledOnTouchOutside(false);
    }

    public void setMsg(String msg){
        progressMsg.setText(msg);
    }

    @Override
    public void show() {
        super.show();

        int size = (int) (LibScreenUtil.getScreenW(context)*0.6);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = size;
        lp.height = size;
        getWindow().setAttributes(lp);
    }
}
