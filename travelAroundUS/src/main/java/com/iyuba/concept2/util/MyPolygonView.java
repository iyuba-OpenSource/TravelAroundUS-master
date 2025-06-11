package com.iyuba.concept2.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * =====作者=====
 * 多边形统计
 */
public class MyPolygonView extends View {

    //-------------我们必须给的模拟数据-------------
    //n边形
    private int n = 6;
    //文字
    private String[] text;
    //区域等级，值不能超过n边形的个数
    private int[] area;

    //-------------View相关-------------
    //View自身的宽和高
    private int mHeight;
    private int mWidth;

    //-------------画笔相关-------------
    //边框的画笔
    private Paint borderPaint;
    //文字的画笔
    private Paint textPaint;
    //区域的画笔
    private Paint areaPaint;

    //-------------多边形相关-------------
    //n边形个数
    private int num = 5;
    //两个多边形之间的半径
    private int r = 60;
    //n边形顶点坐标
    private float x, y;
    //n边形角度
    private float angle = (float) ((2 * Math.PI) / n);
    //文字与边框的边距等级，值越大边距越小
    private int textAlign = 5;

    //-------------颜色相关-------------
    //边框颜色
    private int mColor = 0xFF000000;
    //文字颜色
    private int textColor = 0xFFFF0000;
    //区域颜色
    private int areaColor = 0x800000ff;


    public MyPolygonView(Context context, String[] text, int[] area) {
        super(context);
        this.text = text;
        this.area = area;
    }

    public MyPolygonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPolygonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public int[] getArea() {
        return area;
    }

    public void setArea(int[] area) {
        this.area = area;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔
        initPaint();
        //画布移到中心点
        canvas.translate(mWidth / 2, mHeight / 2);
        //画n边形
        drawPolygon(canvas);
        //画n边形的中点到顶点的线
        drawLine(canvas);
        //画文字
        drawText(canvas);
        //画蓝色区域
        drawArea(canvas);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //边框画笔
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(mColor);
        borderPaint.setStrokeWidth(2);
        //文字画笔
        textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        //区域画笔
        areaPaint = new Paint();
        areaPaint.setColor(areaColor);
        areaPaint.setAntiAlias(true);
        areaPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * 绘制多边形
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //n边形数目
        for (int j = 1; j <= num; j++) {
            float r = j * this.r;
            path.reset();
            //画n边形
            for (int i = 1; i <= n; i++) {
                x = (float) (Math.cos(i * angle) * r);
                y = (float) (Math.sin(i * angle) * r);
                if (i == 1) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            //关闭当前轮廓。如果当前点不等于第一个点的轮廓,一条线段是自动添加的
            path.close();
            canvas.drawPath(path, borderPaint);
        }
    }

    /**
     * 画多边形线段
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        float r = num * this.r;
        for (int i = 1; i <= n; i++) {
            path.reset();
            x = (float) (Math.cos(i * angle) * r);
            y = (float) (Math.sin(i * angle) * r);
            path.lineTo(x, y);
            canvas.drawPath(path, borderPaint);

        }
    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        float r = num * this.r;
        for (int i = 1; i <= n; i++) {
            //测量文字的宽高
            Rect rect = new Rect();
            textPaint.getTextBounds(text[i - 1] + "(" + area[i - 1] + ")", 0, (text[i - 1] + "(" + area[i - 1] + ")").length(), rect);
            float textWidth = rect.width();
            float textHeight = rect.height();

            x = (float) (Math.cos(i * angle) * r);
            y = (float) (Math.sin(i * angle) * r);
            //位置微调
            if (x < 0) {
                x = x - textWidth;
            }
            if (y > 25) {
                y = y + textHeight;
            }
            //调文字与边框的边距
            float LastX = x + x / num / textAlign;
            float LastY = y + y / num / textAlign;
            canvas.drawText(text[i - 1] + "(" + area[i - 1] + ")", LastX, LastY, textPaint);
        }
    }

    /**
     * 画区域
     *
     * @param canvas
     */
    private void drawArea(Canvas canvas) {
        Path path = new Path();
        for (int i = 1; i <= n; i++) {
            float r = area[i - 1] * this.r * 5 / 100;
            x = (float) (Math.cos(i * angle) * r);
            y = (float) (Math.sin(i * angle) * r);
            if (i == 1) {
                path.moveTo(x, y);

            } else {
                path.lineTo(x, y);
            }
        }

        Path paths = new Path();


        if (getIsZeroNum() == 1) {

            for (int i = 1; i <= n; i++) {

                paths.reset();
                float r = area[i - 1] * this.r * 5 / 100;
                x = (float) (Math.cos(i * angle) * r);
                y = (float) (Math.sin(i * angle) * r);
                paths.lineTo(x, y);
                borderPaint.setColor(areaColor);
                borderPaint.setStrokeWidth(4);
                canvas.drawPath(paths, borderPaint);

            }


        }


        //关闭当前轮廓。如果当前点不等于第一个点的轮廓,一条线段是自动添加的
        path.close();
        canvas.drawPath(path, areaPaint);
    }

    /**
     * 获取数值非0的项目总数
     *
     * @return
     */
    private int getIsZeroNum() {
        int isZeroNum = 0;
        for (int i = 1; i <= n; i++) {
            if (area[i - 1] != 0) {

                isZeroNum++;
            }
        }
        return isZeroNum;
    }
}
