package com.iyuba.concept2.widget.subtitle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iyuba.configation.Constant;

/**
 * 字幕同步View
 * 
 * @author lijingwei
 * 
 */
public class SubtitleSynView extends ScrollView implements
		TextPageSelectTextCallBack {
	public boolean syncho;
	private Context context;
	private LinearLayout subtitleLayout;
	private SubtitleSum subtitleSum;
	private List<View> subtitleViews;
	private int currParagraph;
	private TextPageSelectTextCallBack tpstcb;
	private boolean enableSelectText = true;

	public void setTpstcb(TextPageSelectTextCallBack tpstcb) {
		if (enableSelectText)
			this.tpstcb = tpstcb;
	}

	public SubtitleSynView(Context context) {
		super(context);
		initWidget(context);
	}

	public SubtitleSynView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVerticalScrollBarEnabled(false);
		initWidget(context);
	}

	public SubtitleSynView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setVerticalScrollBarEnabled(false);
		initWidget(context);
	}

	private void initWidget(Context context) {
		this.context = context;
		subtitleLayout = new LinearLayout(this.context);
		subtitleLayout.setOrientation(LinearLayout.VERTICAL);
	}

	public void setSubtitleSum(SubtitleSum subtitleSum) {
		this.subtitleSum = subtitleSum;
		subtitleLayout.removeAllViews();
		removeAllViews();
		initSubtitleSum();
	}

	public void initSubtitleSum() {
		if (subtitleSum != null && subtitleSum.subtitles.size() != 0) {
			subtitleViews = new ArrayList<View>();
			for (int i = 0; i < subtitleSum.subtitles.size(); i++) {
				TextPage tp = new TextPage(this.context);
				tp.setBackgroundColor(Color.TRANSPARENT);
				tp.setTextColor(Constant.normalColor);
				tp.setTextSize(Constant.textSize);
				tp.setLineSpacing(3f, 1.3f);
				
				
				if (currParagraph == i) {
					tp.setTextColor(Constant.textColor);
				}
				
				tp.setText(subtitleSum.subtitles.get(i).content);
				tp.setTextpageSelectTextCallBack(this);
				final int current = i;
				tp.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						tpstcb.selectParagraph(current);
						return false;
					}
				});
				subtitleViews.add(tp);
				subtitleLayout.addView(tp);
			}
		}
		addView(subtitleLayout);
	}

	@Override
	public void selectTextEvent(String selectText) {
		tpstcb.selectTextEvent(selectText);
	}

	/**
	 * 段落高亮跳转
	 * 
	 * @param paragraph
	 */
	public void snyParagraph(int paragraph) {
		currParagraph = paragraph;
		handler.sendEmptyMessage(0);
	}

	public void unsnyParagraph() {
		handler.removeMessages(0);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				if (subtitleViews != null && subtitleViews.size() != 0) {
					int center = 0;
					for (int i = 0; i < subtitleViews.size(); i++) {
						TextView textView = (TextView) subtitleViews.get(i);
						if (currParagraph == i + 1) {
							textView.setTextColor(Constant.textColor);
							center = textView.getTop() + textView.getHeight()
									/ 2;
						} else {
							textView.setTextColor(0xff47220b);
						}
					}
					
					if (syncho) {
						center -= getHeight() / 2;
						if (center > 0) {
							smoothScrollTo(0, center);
						} else {
							smoothScrollTo(0, 0);
						}
					}
				}
				break;
			case 1:
				break;
			}
		}

	};

	public void updateSubtitleView() {
		if (subtitleSum != null && subtitleSum.subtitles.size() != 0
				&& subtitleViews != null && subtitleViews.size() != 0) {
			for (int i = 0; i < subtitleSum.subtitles.size(); i++) {
				TextPage tp = (TextPage) subtitleViews.get(i);
				tp.setText(subtitleSum.subtitles.get(i).content);
			}
			snyParagraph(currParagraph);
		}
	}

	public int getCurrParagraph() {
		return currParagraph;
	}

	@Override
	public void selectParagraph(int paragraph) {
		tpstcb.selectParagraph(paragraph);
	}
}
