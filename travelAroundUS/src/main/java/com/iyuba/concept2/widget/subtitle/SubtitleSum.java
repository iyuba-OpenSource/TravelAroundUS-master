package com.iyuba.concept2.widget.subtitle;

import java.util.ArrayList;
import java.util.List;

public class SubtitleSum {
	public String articleTitle; // 文章标题
	public int voaId;
	public String mp3Url;
	public boolean isCollect; // 是否收藏
	public List<Subtitle> subtitles = new ArrayList<Subtitle>(); // 字幕
	
	public int getParagraph(double second){
		int step =0;
		if(subtitles != null && subtitles.size() != 0){
			for(int i = 0;i < subtitles.size(); i++){
				if(second >= subtitles.get(i).pointInTime){
					step = i + 1;
				}else{
					break;
				}
			}
		}
		return step;
	}
}
