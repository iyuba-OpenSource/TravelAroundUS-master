package com.iyuba.concept2.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.DictRequest;
import com.iyuba.concept2.protocol.DictResponse;
import com.iyuba.concept2.sqlite.mode.VoaWord;
import com.iyuba.concept2.util.ExeProtocol;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;

import java.util.ArrayList;
import java.util.List;

/**
 * 修复没有单词表时的异常
 * 
 * @author ct
 * @time 12.9.27
 * 
 */

public class VoaWordAdapter extends BaseAdapter {
	private Context mContext;
	private List<VoaWord> mList = new ArrayList<VoaWord>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;

	public VoaWordAdapter(Context context, List<VoaWord> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final VoaWord word = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = vi.inflate(R.layout.listitem_voa_word, null);
			
			viewHolder = new ViewHolder();
			viewHolder.key = (TextView) convertView.findViewById(R.id.word_key);
			viewHolder.key.setTextColor(Constant.normalColor);
			viewHolder.key.setTextSize(Constant.textSize);
			viewHolder.pron = (TextView) convertView.findViewById(R.id.word_pron);
			viewHolder.pron.setTextColor(Constant.normalColor);
			viewHolder.pron.setTextSize(Constant.textSize);
			viewHolder.def = (TextView) convertView.findViewById(R.id.word_def);
			viewHolder.def.setTextSize(Constant.textSize - 1);
			viewHolder.speaker = (ImageView) convertView.findViewById(R.id.word_speaker);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.key.setText(word.word);
		
		if (word.pron != null && word.pron.length() != 0
				&& !word.pron.equals("null")) {
			viewHolder.pron.setText(Html.fromHtml("[" + word.pron + "]"));
		} else {
			viewHolder.pron.setText("");
		}
		
		viewHolder.def.setText(word.def);
		
		if (word.audio != null && word.audio.length() != 0) {
			viewHolder.speaker.setVisibility(View.VISIBLE);
		} else {
			viewHolder.speaker.setVisibility(View.GONE);
		}
		
		viewHolder.speaker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExeProtocol.exe(new DictRequest(word.word), new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						DictResponse response = (DictResponse) bhr;
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.obj = response.newWord.audio;
						handler.sendMessage(msg);
					}

					@Override
					public void error() {
						//CustomToast.showToast(mContext, R.string.please_check_network, 1000);
					}
				});
			}
		});
		return convertView;
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String url = (String) msg.obj;
				Player player = new Player(mContext, null);
				player.playUrl(url);
				break;
			case 1:
//				CustomToast.showToast(mContext, R.string.please_check_network, 1000);
				break;
			default:
				break;
			}
		}
	};

	public class ViewHolder {
		TextView key;
		TextView pron;
		public TextView def;
		ImageView speaker;
	}

}
