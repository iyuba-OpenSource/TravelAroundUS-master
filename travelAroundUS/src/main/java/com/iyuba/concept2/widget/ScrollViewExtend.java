package com.iyuba.concept2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollViewExtend extends ScrollView {  
    // 滑动距离及坐标  
    private float xDistance, yDistance, xLast, yLast;  
  
    public ScrollViewExtend(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {
    	Log.e("onTouchEvent", "onTouchEvent");
    	return super.onTouchEvent(ev);
    }
    
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        switch (ev.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                xDistance = yDistance = 0f;  
                xLast = ev.getX();  
                yLast = ev.getY();  
                break;  
            case MotionEvent.ACTION_MOVE:  
                final float curX = ev.getX();  
                final float curY = ev.getY();  
                  
                xDistance += Math.abs(curX - xLast);  
                yDistance += Math.abs(curY - yLast);  
                xLast = curX;  
                yLast = curY;  
                  
                if(xDistance > yDistance){  
                    return false;  
                }    
        }  
        
        return super.onInterceptTouchEvent(ev);  
    }  
}   
