package com.iyuba.concept2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.BookDetail;
import com.iyuba.concept2.sqlite.op.ShopCartOp;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.lil.user.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressLint("CommitPrefEdits")
public class BabyPopWindow implements OnDismissListener, OnClickListener {
    private TextView nowPrice, content, confirm;
    private ImageView img;
    private LinearLayout blackBg;

    private PopupWindow popupWindow;
    private OnItemClickListener listener;
    private Context context;
    private BookDetail bookDetail = new BookDetail();
private ShopCartOp shopCartOp;
    private String uid;


    public BabyPopWindow(final Context context, final String uid) {
        this.context = context;
        this.uid = uid;
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_popwindow, null);
        nowPrice = (TextView) view.findViewById(R.id.book_now_price);
        content = (TextView) view.findViewById(R.id.content_info);
        confirm = (TextView) view.findViewById(R.id.confirm);
        img = (ImageView) view.findViewById(R.id.book_img);

        confirm.setOnClickListener(this);

        shopCartOp = new ShopCartOp(context);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        popupWindow.setAnimationStyle(R.style.noAnimation);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                blackBg.setVisibility(View.GONE);
            }
        });

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String date = sdf.format(new Date());

                if (shopCartOp.dataNum(String.valueOf(UserInfoManager.getInstance().getUserId()), bookDetail.id) == 0) {
                    long result = shopCartOp.saveData(bookDetail,String.valueOf(UserInfoManager.getInstance().getUserId()));
                    if (result > 0) {
                        Toast.makeText(context, "已添加至购物车", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                } else {
                    shopCartOp.updateBookNum(bookDetail,String.valueOf(UserInfoManager.getInstance().getUserId()));
                    Toast.makeText(context, "已添加至购物车", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }


            }
        });
    }


    public interface OnItemClickListener {
        void onClickOKPop();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onDismiss() {

    }

    public void setValue(BookDetail bookDetail, LinearLayout blackBg) {
        this.bookDetail = bookDetail;
        GitHubImageLoader.Instace(context).setRawPic(bookDetail.editImg, img,
                R.drawable.failed_image);
        this.nowPrice.setText("¥" + bookDetail.totalPrice);
        this.content.setText(bookDetail.editInfo);
        this.blackBg = blackBg;
    }



    public void dissmiss() {
        popupWindow.dismiss();
    }


    public void showAsDropDown(View parent) {
//        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            int[] a = new int[2];
            parent.getLocationInWindow(a);

            int popHeight=popupWindow.getContentView().getMeasuredHeight();

            popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, 0, -popHeight+a[1] + parent.getHeight());
        } else {
            popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:


                break;
            default:
                break;
        }
    }

    private void setSaveData() {


    }

}
