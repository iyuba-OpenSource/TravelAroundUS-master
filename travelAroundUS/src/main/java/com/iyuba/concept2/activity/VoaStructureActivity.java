package com.iyuba.concept2.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.sqlite.mode.VoaStructure;
import com.iyuba.concept2.sqlite.op.VoaStructureOp;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;


public class VoaStructureActivity extends BasisActivity {
	
	private VoaStructure voaStructure;
	private Context mContext;
	private VoaStructureOp structureOp = new VoaStructureOp(mContext);
	
	private RelativeLayout noStructureLayout;
	private LinearLayout structureLayout;
	private LinearLayout tagLayout;
	
	private TextView englishTagText;
	private TextView chineseTagText;
	private TextView noteText;
	
	private int voaId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voa_structure);
		
		mContext = this;
		
		initVoaStructure();
	}

	private void initVoaStructure() {
		voaId = VoaDataManager.Instace().voaTemp.voaId;
		
		voaStructure = structureOp.findData(voaId);
		
		noStructureLayout = (RelativeLayout) findViewById(R.id.no_structure_view);
		structureLayout = (LinearLayout) findViewById(R.id.structure_view);
		tagLayout = (LinearLayout) findViewById(R.id.tag);
		
		englishTagText = (TextView) findViewById(R.id.english_tag);
		chineseTagText = (TextView) findViewById(R.id.chinese_tag);
		noteText = (TextView) findViewById(R.id.note);
		
		if(voaStructure == null) {
			noStructureLayout.setVisibility(View.VISIBLE);
			structureLayout.setVisibility(View.GONE);
		} else {
			setVoaStructure();
		}
	}	
	
	public void setVoaStructure() {
		if((voaStructure.descEN == null || ("").equals(voaStructure.descEN.trim()))
				&&(voaStructure.descCH == null || ("").equals(voaStructure.descCH.trim()))) {
			tagLayout.setVisibility(View.GONE);
		} else {
			tagLayout.setVisibility(View.VISIBLE);
			
			if((voaStructure.descEN == null) || ("").equals(voaStructure.descEN.trim())) {
				englishTagText.setVisibility(View.GONE);
			} else {
//				englishTagText.setTextColor(Constant.NORMAL_COLOR);
				englishTagText.setTextSize(Constant.textSize);
				englishTagText.setText(voaStructure.descEN);
			}
			
			if((voaStructure.descCH == null) || ("").equals(voaStructure.descCH.trim())) {
				chineseTagText.setVisibility(View.GONE);
			} else {
//				chineseTagText.setTextColor(Constant.NORMAL_COLOR);
				chineseTagText.setTextSize(Constant.textSize);
				chineseTagText.setText(voaStructure.descCH);
			}
		}
		
		noteText.setTextColor(Constant.normalColor);
		noteText.setTextSize(Constant.textSize);
		
		String note = voaStructure.note;
		LogUtil.e("note:"+note);
		SpannableStringBuilder style = transformString(note);
		noteText.setText(style);
	}
	
	public SpannableStringBuilder transformString(String str) {
		String[] strs = str.split("\\+\\+\\+");
		str = str.replaceAll("\\+\\+\\+", "");
		int from = 0;
		int to = 0;

		SpannableStringBuilder style = new SpannableStringBuilder(str);
		
		if (strs.length > 1) {
			for (int i = 0; i < strs.length - 2; i = i + 2) {
				from += strs[i].length();
				to = from + strs[i + 1].length();
				style.setSpan(new StyleSpan(
						android.graphics.Typeface.BOLD_ITALIC), from, to,
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				from += strs[i + 1].length();
			}
		} 
		
		return style;
	}
	
	public void onResume() {
		super.onResume();
	}
}
