package com.iyuba.concept2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.ConfigManager;
import com.iyuba.core.common.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendBookNovelActivity extends Activity{


    @BindView(R.id.button_back)
    Button button_back;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.commit)
    TextView commit;
    @BindView(R.id.cancel)
    TextView cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendbook_novel);
        ButterKnife.bind(this);




        SpannableString spanText=new SpannableString("\u3000\u3000送英文名著电子书啦!" +
                "\n\u3000\u3000只要在应用商店中对本应用进行五星好评，并截图发给QQ：2111356785，" +
                "即可获得3天的会员试用资格或者本页面全套的英文名著电子书。\n\u3000\u3000" +
                "机会难得，不容错过，小伙伴们赶快行动吧!");
        spanText.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);       //设置文件颜色
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View view) {
                if(isQQClientAvailable(SendBookNovelActivity.this)) {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                   startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url + "3099007489")));
                }else{

                    ToastUtil.showToast(SendBookNovelActivity.this,"未安装qq客户端");
                }

            }
        }, 42, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件




        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //去评价后标记，再也不弹出好评送书弹框
                    ConfigManager.Instance(SendBookNovelActivity.this).putBoolean("firstSendbook",true);


                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(SendBookNovelActivity.this).create();
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setTitle(getResources().getString(R.string.alert_title));
                    dialog.setMessage(getResources().getString(R.string.about_market_error));
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_btn_ok),
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
             finish();
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
