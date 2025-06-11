package com.iyuba.concept2.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.AnnotationAdapter;
import com.iyuba.concept2.adapter.LifePointAdapter;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.sqlite.mode.VoaAnnotation;
import com.iyuba.concept2.sqlite.mode.VoaLifePointBean;
import com.iyuba.concept2.sqlite.op.AnnotationOp;
import com.iyuba.concept2.sqlite.op.LifeDataOp;

import java.util.ArrayList;
import java.util.List;

/**
 * 课文注释
 */
public class VoaAnnotationActivity extends BasisActivity {
	
	private ListView annoListView;
	private View noAnnotationView;
	private List<VoaAnnotation> voaAnnos;
	private List<VoaLifePointBean> voaLifePointBeans;

	private AnnotationAdapter annosAdapter;
	private LifePointAdapter lifePointAdapter;
	private Context mContext;
	private AnnotationOp annotationOp = new AnnotationOp(mContext);
	private LifeDataOp lifeDataOp = new LifeDataOp(mContext);
	private int voaId;
	private int showType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voa_annotations);
		
		mContext = this;
		
		init();
	}
	
	public void init() {
		noAnnotationView = findViewById(R.id.no_annotation_view);
		annoListView = (ListView) findViewById(R.id.annotation_list);
	}

	private void initVoaAnnotation() {
		voaId = VoaDataManager.Instace().voaTemp.voaId;
		showType=VoaDataManager.Instace().showType;
		if (showType==0) {//课文注解
			voaAnnos = annotationOp.findDataByVoaId(voaId);
			if (voaAnnos.size() == 0) {
				noAnnotationView.setVisibility(View.VISIBLE);
				annoListView.setVisibility(View.GONE);
			} else {
				annoListView.setVisibility(View.VISIBLE);
				noAnnotationView.setVisibility(View.GONE);
				annosAdapter = new AnnotationAdapter(mContext, (ArrayList<VoaAnnotation>) voaAnnos);
				annoListView.setAdapter(annosAdapter);
			}
		}else {
			//Toast.makeText(mContext,"文化点滴",Toast.LENGTH_SHORT).show();
			int lesson=VoaDataManager.Instace().voaTemp.lesson;
			int art=VoaDataManager.Instace().voaTemp.art;
			voaLifePointBeans=lifeDataOp.findDataByVoaId(lesson,art);
			if (voaLifePointBeans.size() == 0) {
				noAnnotationView.setVisibility(View.VISIBLE);
				annoListView.setVisibility(View.GONE);
			} else {
				annoListView.setVisibility(View.VISIBLE);
				noAnnotationView.setVisibility(View.GONE);
				lifePointAdapter = new LifePointAdapter(mContext, (ArrayList<VoaLifePointBean>) voaLifePointBeans);
				annoListView.setAdapter(lifePointAdapter);
			}
		}
		
	}	
	
	public void onResume() {
		initVoaAnnotation();
		super.onResume();
	}
}
