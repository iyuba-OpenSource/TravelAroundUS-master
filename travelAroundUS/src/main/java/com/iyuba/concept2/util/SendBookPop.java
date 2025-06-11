package com.iyuba.concept2.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.ConfigManager;
import com.iyuba.core.common.util.ToastUtil;

import java.util.List;

/**
 * Created by iyuba on 2017/8/22.
 */

public class SendBookPop extends PopupWindow {


    private TextView text;


    private Activity context;


    private View conentView;

    public SendBookPop(final Activity context, View root) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.good_dlalog, null);

        int h = context.getWindowManager().getDefaultDisplay().getHeight() / 100 * 32;
        int w = context.getWindowManager().getDefaultDisplay().getWidth() / 100 * 85;
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
//        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(h);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();

        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.send_book_pop_bg));
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimationPreview);

        //产生背景变暗效果

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.4f;

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug

        context.getWindow().setAttributes(lp);

        this.showAtLocation(root, Gravity.CENTER, 0, 0);

        TextView commit = (TextView) conentView.findViewById(R.id.commit);
        TextView cancel = (TextView) conentView.findViewById(R.id.cancel);

        text = (TextView) conentView.findViewById(R.id.text);


        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e("调整屏幕", "透明度");
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug 3099007489
            }
        });

        SpannableString spanText = new SpannableString("\u3000\u3000送书啦！\n\u3000\u3000只要在应用商店中对本应用进行五星好评，并截图发给QQ：3099007489，即可获得3天的会员试用资格以及赠送一本由爱语吧名师团队编写的电子书哦。\n\u3000\u3000机会难得，不容错过，小伙伴们赶快行动吧!");
        spanText.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);       //设置文件颜色
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View view) {
                if (isQQClientAvailable(context)) {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url + "3099007489")));
                } else {

                    ToastUtil.showToast(context, "未安装qq客户端");
                }

            }
        }, 36, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //去评价后标记，再也不弹出好评送书弹框
                    ConfigManager.Instance(context).putBoolean("firstSendbook",true);

                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    Log.e("------","pppppp");


                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(context).create();
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setTitle(context.getResources().getString(R.string.alert_title));
                    dialog.setMessage(context.getResources().getString(R.string.about_market_error));
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.alert_btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

    }


    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


}
