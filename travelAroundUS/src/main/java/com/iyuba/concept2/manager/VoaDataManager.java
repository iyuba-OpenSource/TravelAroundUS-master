package com.iyuba.concept2.manager;

import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.concept2.widget.subtitle.Subtitle;
import com.iyuba.concept2.widget.subtitle.SubtitleSum;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频管理
 * 
 * @author chentong
 * 
 */
public class VoaDataManager {

	private static VoaDataManager instance;

	private VoaDataManager() {
		
	}

	public static synchronized VoaDataManager Instace() {
		if (instance == null) {
			instance = new VoaDataManager();
		}
		return instance;
	}

	public Voa voaTemp;
	public int showType=0;
	public List<Voa> voasTemp = new ArrayList<Voa>();
	public List<VoaDetail> voaDetailsTemp = new ArrayList<VoaDetail>();
	public SubtitleSum subtitleSum;

	public void setSubtitleSum(Voa voa, List<VoaDetail> textDetailsTemp) {
		if (textDetailsTemp == null) {
			return ;
		}
		
		this.voaDetailsTemp = textDetailsTemp;
		subtitleSum = new SubtitleSum();
		subtitleSum.voaId = voa.voaId;
		subtitleSum.articleTitle = voa.title;
		subtitleSum.isCollect = false; // 查询是否被收藏
		subtitleSum.mp3Url = voa.sound;
		
		if (subtitleSum.subtitles == null) {
			subtitleSum.subtitles = new ArrayList<Subtitle>();
			subtitleSum.subtitles.clear();
		}
		
		for (int i = 0; i < textDetailsTemp.size(); i++) {
			Subtitle st = new Subtitle();
			st.articleTitle = voa.title;
			
			if (textDetailsTemp.get(i).sentenceCn.equals("")) {
				st.content = textDetailsTemp.get(i).sentence;
			} else {
				st.content = textDetailsTemp.get(i).sentence + "\n"
						+ textDetailsTemp.get(i).sentenceCn;
			}

			// TODO: 2023/12/27 这里的时间不准确，暂时提前450ms测试下
			st.pointInTime = textDetailsTemp.get(i).startTime;
			if (st.pointInTime>1.0f){
				st.pointInTime -= 0.45f;
			}
			subtitleSum.subtitles.add(st);
		}
	}

	public void changeLanguage(boolean isOnlyEnglish) {
		if (voaDetailsTemp==null) {
			return ;
		}
		
		for (int i = 0; i < voaDetailsTemp.size(); i++) {
			Subtitle st = subtitleSum.subtitles.get(i);
			
			if (isOnlyEnglish){//只有英文
				st.content = voaDetailsTemp.get(i).sentence;
			} else {
				st.content = voaDetailsTemp.get(i).sentence + "\n"
						+ voaDetailsTemp.get(i).sentenceCn;
			}
		}
	}
	
	public static synchronized VoaDataManager getInstance() {
		if (instance == null) {
			instance = new VoaDataManager();
		}
		return instance;
	}
	
}
