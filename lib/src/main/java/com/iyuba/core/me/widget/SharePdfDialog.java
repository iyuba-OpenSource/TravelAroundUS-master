package com.iyuba.core.me.widget;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.lib.R;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class SharePdfDialog extends Dialog {

    TextView mDescriptionTv;
    TextView mLinkTv;
    ImageView imageClose;
    TextView textCopy;
    TextView textSendQq;
    TextView textSendWexin;


    private String mLink;
    private String mTitle;
    private String mPicUrl;
    private Context mContext;

    public SharePdfDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme_personal);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mContext = context;

    }

    public SharePdfDialog setShareStuff(String link, String title, String picUrl) {
        mLink = link;
        mTitle = title;
        mPicUrl = picUrl;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share_pdf);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        initWidget();
        initListener();
        String description = mContext.getString(R.string.create_pdf_success);
        mDescriptionTv.setText(description);
        mLinkTv.setText(mLink);
    }

    private void initWidget() {
        mDescriptionTv = findViewById(R.id.text_description);
        mLinkTv = findViewById(R.id.text_link);
        imageClose = findViewById(R.id.image_close);
        textCopy = findViewById(R.id.text_copy);
        textSendQq = findViewById(R.id.text_send_qq);
        textSendWexin = findViewById(R.id.text_send_wexin);
    }

    private void initListener() {
        imageClose.setOnClickListener(v -> dismiss());
        textCopy.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mLink)) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mLink);
                ToastUtil.showToast(getContext(), "复制成功!");
            }
        });
        textSendQq.setOnClickListener(v ->{
            if (!TextUtils.isEmpty(mLink)) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(mTitle);
                sp.setTitleUrl(mLink);
                sp.setImageUrl(mPicUrl);
                sp.setText("爱语吧PDF");
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.share(sp);
            }
        });
        textSendWexin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mLink)) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(mTitle);
                sp.setTitleUrl(mLink);
                sp.setImageUrl(mPicUrl);
                sp.setUrl(mLink);
                sp.setText("爱语吧PDF");
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.share(sp);
            }
        });
    }
}
