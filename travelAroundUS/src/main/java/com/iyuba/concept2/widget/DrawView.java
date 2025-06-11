package com.iyuba.concept2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.iyuba.core.common.util.CommonUtils;


/**
 * 绘制圆形图谱的工具类
 *
 * @author liuzhenli
 * @version 1.0.0
 */

public class DrawView extends View {
    private static int[] result;
    private static String[] mAbilityType;
    private int centerX;//圆心x的坐标
    private int centerY;//圆心y的坐标
    private static int mCorners;
    private Context mContext;
    //用户分数几个顶点的坐标 r为半径
    private int r;
    /****
     * 实心小圆点的半径
     */
    private int r_small = 5;
    private float rate;

    public DrawView(Context context) {
        super(context);
        mContext = context;
        float dens = context.getResources().getDisplayMetrics().density;//屏幕的密度
        rate = dens / 2.0f;//测试手机是在密度2.0的手机上  其他手机上适配在此基础上等比例缩放
        r = (int) (200 * rate);
        centerX = CommonUtils.getScreenWidth(context) / 2;
        centerY = (int) (300 * rate);

    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置数据
     *
     * @param abilityType 包含的能力
     * @param res         答题记录
     */
    public static void setData(String[] abilityType, int[] res) {
        result = res;
        mAbilityType = abilityType;
        mCorners = abilityType.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /** 方法 说明 drawRect 绘制矩形 drawCircle 绘制圆形 drawOval 绘制椭圆 drawPath 绘制任意多边形
         *  drawLine 绘制直线 drawPoin 绘制点
         */
        // 创建画笔
        Paint p = new Paint();
        // p.setColor(Color.RED);// 设置红色
        p.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
        PathEffect effects = new DashPathEffect(new float[]{20, 1, 1, 1}, 2);
        p.setPathEffect(effects);
        p.setStyle(Paint.Style.STROKE);//设置空心
        p.setStrokeWidth(1);

        p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除
        canvas.drawCircle(centerX, centerY, 20 * rate, p);// 大圆 float cx, float cy, float radius, Paint paint
        canvas.drawCircle(centerX, centerY, 40 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 60 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 80 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 100 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 120 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 140 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 160 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 180 * rate, p);// 大圆
        canvas.drawCircle(centerX, centerY, 200 * rate, p);// 大圆

        drawMap(canvas, p, mCorners);//自定义边数
    }

    /**
     * 绘制能力图谱
     *
     * @param cvs   画布
     * @param paint 画笔
     * @param cor   边的条数
     */
    private void drawMap(Canvas cvs, Paint paint, int cor) {
        int textSize = (int) (14 * mContext.getResources().getDisplayMetrics().scaledDensity);
        paint.reset();
        paint.setTextSize(textSize);


        float f = (float) Math.toRadians(360 / cor);//相邻两条线的夹角
        float tempf = (float) (Math.toRadians(90));//转化为弧度
        float[] x = new float[cor];
        float[] y = new float[cor];


        float[] xText = new float[cor];
        float[] yText = new float[cor];


        Path path = new Path();
        for (int i = 0; i < cor; i++) {
            //从圆心向顶点画直线
            paint.setARGB(0xff, 0xff, 0xff, 0xff);
            x[i] = (float) (centerX + r * Math.cos(f * i + tempf));
            y[i] = (float) (centerY - r * Math.sin(f * i + tempf));
            cvs.drawLine(centerX, centerY, x[i], y[i], paint);

            //写文字的时候根据文字位置需要重新定义起始位置  顶为0 逆时针
            String text2Draw = mAbilityType[i] + "(" + result[i] + ")";//写的文字内容
            xText[i] = x[i];//文字x坐标
            yText[i] = y[i];//文字y坐标

            float textHeight = getTextHeight(text2Draw, textSize);//文字高度
            float textWidth = getTextWidth(text2Draw, textSize);//文字宽度


            if (yText[i] > centerY) {//圆心下侧
                yText[i] = y[i] + textHeight / 2;
            } else if (yText[i] < centerY) {//圆心上侧
                yText[i] = y[i];
            } else {//圆心正左/右方
                yText[i] = y[i] + textHeight / 2;
            }


            if (xText[i] > centerX) { //圆心右侧
                xText[i] = x[i];
            } else if (xText[i] < centerX) {//圆心左侧
                xText[i] = x[i] - textWidth;
            } else {//圆心正上/下侧
                xText[i] = x[i] - textWidth / 2;
                // 正上方/正下方
                yText[i] = yText[i] < centerY ? y[i] - textHeight / 3 : y[i] + textHeight;
            }

            switch (cor) {
                case 4:
                    if (i == 1 || i == 3) yText[i] = y[i] + textHeight / 4;
                    break;

            }

            paint.setARGB(0xff, 0x14, 0xA9, 0x96);
            cvs.drawText(text2Draw, xText[i], yText[i], paint);

            //与得分关联,1.画顶点小圆   2.画封闭面积
            x[i] = (float) (centerX + r * Math.cos(f * i + tempf) * result[i] / 100);
            y[i] = (float) (centerY - r * Math.sin(f * i + tempf) * result[i] / 100);
            paint.setARGB(0x4D, 0x6C, 0xC5, 0xD9);
            if (i == 0) {
                path.moveTo(x[i], y[i]);
            } else if (i < cor - 1) {
                path.lineTo(x[i], y[i]);
            } else {
                path.lineTo(x[i], y[i]);
                path.close();//封闭
                cvs.drawPath(path, paint);
            }
            paint.setARGB(0xff, 0x6C, 0xC5, 0xD9);
            cvs.drawCircle(x[i], y[i], r_small, paint);//顶点小圆
        }
    }

    /***
     * 获取文字的宽度
     */
    private float getTextWidth(String text, int texSize) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(texSize);
        return paint.measureText(text);
    }

    /**
     * 获取文字的高度
     */

    private float getTextHeight(String text, int textsize) {
        TextPaint p = new TextPaint();
        p.setTextSize(textsize);
        Paint.FontMetrics fm = p.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent) + 2;
    }

}
