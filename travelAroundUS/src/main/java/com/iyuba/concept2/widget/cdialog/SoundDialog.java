//package com.iyuba.concept2.widget.cdialog;
//
//import android.content.Context;
//import android.graphics.drawable.AnimationDrawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.iyuba.concept2.R;
//import com.iyuba.core.search.adapter.SearchListAdapter;
//import com.iyuba.core.search.adapter.SentenceListAdapter;
//import com.iyuba.concept2.adapter.ValReadAdapter;
//
//public class SoundDialog{
//
//private static TextView tv;
//    /**
//     * 等待窗口
//     */
//    public static CustomDialog showDialog(final Context context, final ValReadAdapter adapter,String text) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View layout = inflater.inflate(R.layout.sound_dialog, null);
//        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//
//        final CustomDialog cDialog = customBuilder.setContentView(layout).create();
//
//
//        ImageView iv = (ImageView) layout.findViewById(R.id.im_sound);
//        tv=layout.findViewById(R.id.tv_sound);
//        tv.setText(text);
//
//        RelativeLayout re_pc = (RelativeLayout) layout.findViewById(R.id.re_pc);
//        AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
//        anim.start();
//        cDialog.setCanceledOnTouchOutside(false);
//
//        re_pc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//               adapter.dismissDia();
//
//            }
//        });
//        return cDialog;
//    }
//    public static CustomDialog showDialog(final Context context, final SearchListAdapter adapter, String text) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View layout = inflater.inflate(R.layout.sound_dialog, null);
//        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//
//        final CustomDialog cDialog = customBuilder.setContentView(layout).create();
//
//
//        ImageView iv = (ImageView) layout.findViewById(R.id.im_sound);
//        tv=layout.findViewById(R.id.tv_sound);
//        tv.setText(text);
//
//        RelativeLayout re_pc = (RelativeLayout) layout.findViewById(R.id.re_pc);
//        AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
//        anim.start();
//        cDialog.setCanceledOnTouchOutside(false);
//
//        re_pc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                adapter.dismissDia();
//
//            }
//        });
//        return cDialog;
//    }
//    public static CustomDialog showDialog(final Context context, final SentenceListAdapter adapter, String text) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View layout = inflater.inflate(R.layout.sound_dialog, null);
//        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//
//        final CustomDialog cDialog = customBuilder.setContentView(layout).create();
//
//
//        ImageView iv = (ImageView) layout.findViewById(R.id.im_sound);
//        tv=layout.findViewById(R.id.tv_sound);
//        tv.setText(text);
//
//        RelativeLayout re_pc = (RelativeLayout) layout.findViewById(R.id.re_pc);
//        AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
//        anim.start();
//        cDialog.setCanceledOnTouchOutside(false);
//
//        re_pc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                adapter.dismissDia();
//
//            }
//        });
//        return cDialog;
//    }
//
//    public static void setText(String text){
//        tv.setText(text);
//    }
//}