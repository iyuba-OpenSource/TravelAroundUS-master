package com.iyuba.concept2.util;

import android.content.Context;

import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.configation.ConfigManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * 下一曲
 * 
 * @author chentong
 * 
 */
public class NextVideo {
	private ArrayList<Integer> AEID = new ArrayList<Integer>();
	private ArrayList<Integer> LOCALID = new ArrayList<Integer>();
	private int position;
	private int size;
	private final static int local = 100;
	private final static int all = 0;
	private ArrayList<Voa> voaList = new ArrayList<Voa>();
	private boolean justlocal = false;

	public NextVideo(int ID, int mode, Context mContext) {
		VoaOp voaOp = new VoaOp(mContext);
		justlocal = ConfigManager.Instance().loadBoolean("play_local_only");
		int curBook = ConfigManager.Instance().loadInt("curBook");
		if (mode == all) {
			voaList = (ArrayList<Voa>) voaOp.findDataByBook(curBook);
			size = voaList.size();
			for (int i = 0; i < size; i++) {
				AEID.add(voaList.get(i).voaId);
			}
		} else if (mode == local) {
			voaList = (ArrayList<Voa>) voaOp.findDataByBook(curBook);
			size = voaList.size();
			for (int i = 0; i < size; i++) {
				AEID.add(voaList.get(i).voaId);
			}
		}

		//不从收藏中取数据
//		voaList = (ArrayList<Voa>) voaOp.findDataFromCollection(curBook);
		voaList = (ArrayList<Voa>) voaOp.findDataByBook(curBook);

		if (voaList != null) {
			for (int i = 0; i < voaList.size(); i++) {
				LOCALID.add(voaList.get(i).voaId);
			}
		}

		if (justlocal && LOCALID != null) {
			position = LOCALID.indexOf(ID);
		} else {
			position = AEID.indexOf(ID);
		}

	}

	public int following() {
		if (justlocal) {
			if (position + 1 < voaList.size()&&LOCALID!=null&&position + 1 <LOCALID.size()) {
				return (Integer) LOCALID.get(position + 1);
			} else {
				if(LOCALID!=null&&LOCALID.size()>0)
					return (Integer) LOCALID.get(0);
				else
					return 0;
			}
		} else {
			if (position + 1 < size&&AEID!=null&&position + 1<AEID.size()) {
				return (Integer) AEID.get(position + 1);
			} else {
				if(AEID!=null&&AEID.size()>0)
					return (Integer) AEID.get(0);
				else
					return 0;
			}
		}

	}

	public int nextVideo() {
		if (justlocal) {
			Random rnd = new Random();
			int nextAEID = rnd.nextInt(LOCALID.size() * 10) / 10;
			while (nextAEID == position && LOCALID.size() != 1) {
				nextAEID = rnd.nextInt(LOCALID.size() * 10) / 10;
			}
			return (Integer) LOCALID.get(nextAEID);
		} else {
			Random rnd = new Random();
			int nextAEID = rnd.nextInt(AEID.size() * 10) / 10;
			while (nextAEID == position && AEID.size() != 1) {
				nextAEID = rnd.nextInt(AEID.size() * 10) / 10;
			}
			return (Integer) AEID.get(nextAEID);
		}

	}
}
