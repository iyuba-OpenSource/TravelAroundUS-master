package com.iyuba.concept2.widget;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class DictionEditText extends AppCompatEditText {
    OnSelectListener mListener;
    private boolean isCanSelect = false;
    private long initTime;
    private long endTime;
    float[] oldXY;

    public DictionEditText(Context context) {
        super(context);
        this.initialize();
    }

    public DictionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public DictionEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize();
    }

    private void initialize() {
        this.setGravity(48);
    }

    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
    }

    public boolean getDefaultEditable() {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
        Layout layout = this.getLayout();
        switch(action) {
            case 0:
                this.oldXY = new float[]{event.getX(), event.getY()};
                this.isCanSelect = true;
                this.initTime = System.currentTimeMillis();
                break;
            case 1:
                if (this.isCanSelect) {
                    this.endTime = System.currentTimeMillis();
                    if (this.endTime - this.initTime <= 500L) {
                        int line = layout.getLineForVertical(this.getScrollY() + (int)event.getY());
                        int off = layout.getOffsetForHorizontal(line, (float)((int)event.getX()));
                        String selectText = this.getSelectText(off);
                        if (selectText.length() > 0) {
                            this.setCursorVisible(true);
                            if (this.mListener != null) {
                                this.mListener.onSelect(selectText);
                            }
                        } else {
                            this.setCursorVisible(false);
                        }
                    }
                }
                break;
            case 2:
                if (Math.abs(event.getX() - this.oldXY[0]) > 8.0F && Math.abs(event.getY() - this.oldXY[1]) > 8.0F) {
                    this.isCanSelect = false;
                }
        }

        return true;
    }

    private String getSelectText(int currOff) {
        int leftOff = currOff;
        int rigthOff = currOff;
        int length = this.getText().toString().length();

        String endString;
        while(leftOff > 0) {
            if (leftOff != 0) {
                --leftOff;
                if (leftOff < 0) {
                    leftOff = 0;
                }
            }

            endString = this.getText().subSequence(leftOff, currOff).toString();
            if (!endString.matches("^[a-zA-Z'-]*")) {
                ++leftOff;
                break;
            }
        }

        while(rigthOff < length) {
            ++rigthOff;
            if (rigthOff > length) {
                rigthOff = length;
            }

            endString = this.getText().subSequence(currOff, rigthOff).toString();
            if (!endString.matches("^[a-zA-Z'-]*")) {
                --rigthOff;
                break;
            }
        }

        endString = "";

        try {
            endString = this.getText().subSequence(leftOff, rigthOff).toString();
            if (endString.trim().length() > 0) {
                Selection.setSelection(this.getEditableText(), leftOff, rigthOff);
            }
        } catch (Exception var7) {
        }

        return endString.trim();
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
    }

    public void clearSelect() {
        Selection.setSelection(this.getEditableText(), 0, 0);
    }

    public interface OnSelectListener {
        void onSelect(String var1);
    }
}
