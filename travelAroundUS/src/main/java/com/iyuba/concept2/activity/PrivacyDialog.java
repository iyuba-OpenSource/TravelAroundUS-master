package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.concept2.R;
import com.iyuba.core.common.util.CommonUtils;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.util.ToastUtil;

public class PrivacyDialog {
    private static boolean alreadyRefuse = false;

    public static void showDialog(Context context, CallBack callBack) {
        alreadyRefuse = false;
        String privacy1 = "1.为了更方便您使用我们的软件，我们回根据您使用的具体功能时申请必要的权限，如摄像头，存储权限，录音权限等。\n";
        String privacy2 = "2.使用本app需要您了解并同意";
        String privacy3 = "使用协议";
        String privacy4 = "及";
        String privacy5 = "隐私政策";
        String privacy6 = "，点击同意即代表您已阅读并同意该协议";

        ClickableSpan userAgreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, Web.class);
                intent.putExtra("url", CommonUtils.getUserAgreementUrl(context));
                intent.putExtra("title", "用户协议");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
        };
        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, com.iyuba.core.common.activity.Web.class);
                Log.e("TAG", "onClick: 点击隐私政策"+ CommonUtils.getPrivacyPolicyUrl(context));
                intent.putExtra("url", CommonUtils.getPrivacyPolicyUrl(context));
                intent.putExtra("title", "隐私政策");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
        };


        int userAgreementStart = privacy1.length() + privacy2.length();
        int userAgreementEnd = userAgreementStart + privacy3.length();
        int privacyStart = privacy1.length() + privacy2.length()
                + privacy3.length() + privacy4.length();
        int privacyEnd = privacyStart + privacy5.length();

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(privacy1);
        strBuilder.append(privacy2);
        strBuilder.append(privacy3);
        strBuilder.append(privacy4);
        strBuilder.append(privacy5);
        strBuilder.append(privacy6);
        strBuilder.setSpan(userAgreementClickableSpan, userAgreementStart, userAgreementEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(privacyClickableSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacy, null);
        dialog.setView(view);
        dialog.show();

        TextView textView = view.findViewById(R.id.text_link);

        textView.setText(strBuilder);

        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView agreeNo = view.findViewById(R.id.text_no_agree);
        TextView agree = view.findViewById(R.id.text_agree);

        agreeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyRefuse) {
                    System.exit(0);
                } else {
                    ToastUtil.showToast(context, "请同意，再次点击不同意，将会退出app");
                    alreadyRefuse = true;
                }
            }
        });

        agree.setOnClickListener(v -> {
            callBack.callback();
            dialog.dismiss();
        });

    }


    public interface CallBack {
        void callback();
    }
}
