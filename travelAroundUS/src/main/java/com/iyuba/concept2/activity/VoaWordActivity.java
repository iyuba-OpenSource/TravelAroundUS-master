package com.iyuba.concept2.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.VoaWordAdapter;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.protocol.DictRequest;
import com.iyuba.concept2.protocol.DictResponse;
import com.iyuba.concept2.protocol.WordUpdateRequest;
import com.iyuba.concept2.sqlite.mode.NewWord;
import com.iyuba.concept2.sqlite.mode.VoaWord;
import com.iyuba.concept2.sqlite.op.NewWordOp;
import com.iyuba.concept2.sqlite.op.VoaWordOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class VoaWordActivity extends BasisActivity {
	public static VoaWordActivity instance;
	
	private CustomDialog waittingDialog;
	private TextView wordSelf, wordDef, wordPron, wordExamples;
	private ImageView wordSpeaker, wordCollect;
	private Button backTolist, formerWord, nextWord;
	private RelativeLayout wordDetail;
	
	private ListView wordsListView = null;
	private List<VoaWord> voaWords;
	private VoaWordAdapter wordsAdapter;
	
	private int curWord = 0;
	private MediaPlayer voiceMediaPlayer;
	private Context mContext;
	private int voaId;
	
	private VoaWordOp voaWordOp = new VoaWordOp(mContext);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voa_word_list);
		
		mContext = this;
		instance = this;
		waittingDialog = WaittingDialog.showDialog(mContext);
		
		initVoaWords();
	}

	// 初始化单词生词
	private void initVoaWords() {
		voaId = VoaDataManager.Instace().voaTemp.voaId;
		
		voaWords = voaWordOp.findDataByVoaId(voaId);
		
		voiceMediaPlayer = new MediaPlayer();
		voiceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		voiceMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				voiceMediaPlayer.start();
			}
		});
		
		wordDetail = (RelativeLayout) findViewById(R.id.word_detail);
		
		wordSelf = (TextView) findViewById(R.id.word_self);
		wordDef = (TextView) findViewById(R.id.word_def);
		wordPron = (TextView) findViewById(R.id.word_pron);
		wordExamples = (TextView) findViewById(R.id.word_example);

		wordSpeaker = (ImageView) findViewById(R.id.word_speak);
		wordCollect = (ImageView) findViewById(R.id.word_collect);

		backTolist = (Button) findViewById(R.id.back_tolist);
		formerWord = (Button) findViewById(R.id.former_voaword);
		nextWord = (Button) findViewById(R.id.next_voaword);

		wordsListView = (ListView) findViewById(R.id.wordslist);

		List<Map<String, String>> liMaps = new ArrayList<Map<String, String>>();

		if (voaWords != null) {

			wordsAdapter = new VoaWordAdapter(mContext, voaWords);

			wordsListView.setAdapter(wordsAdapter);

			wordsListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					wordsListView.setVisibility(View.GONE);
					wordDetail.setVisibility(View.VISIBLE);
					curWord = position;
					showVoaWordById(position);
				}
			});

			wordSpeaker.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						VoaWord word = voaWords.get(curWord);
						if(word==null||"".equals(word.audio)||word.audio==null)
							return;
						voiceMediaPlayer.reset();
						// 可以是网址
						voiceMediaPlayer.setDataSource(word.audio);
						voiceMediaPlayer.prepareAsync();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			});
            //添加单词
			wordCollect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					CustomToast.showToast(mContext, "正在添加", 1000);
					
					VoaWord tempVoaWord = voaWords.get(curWord);
					NewWord word = new NewWord();
					word.word = tempVoaWord.word;
					word.audio = tempVoaWord.audio;
					word.pron = tempVoaWord.pron;
					word.def = tempVoaWord.def;
					
					saveNewWords(word);
				}
			});

			//TODO
			backTolist.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					changeWordState();
				}
			});

			formerWord.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (curWord == 0) {
						CustomToast.showToast(mContext, "已是第一个单词", 1000);
					} else {
						curWord--;
						showVoaWordById(curWord);
					}
				}
			});

			nextWord.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (curWord == voaWords.size() - 1) {
						CustomToast.showToast(mContext, "已是最后一个单词", 1000);
					} else {
						curWord++;
						showVoaWordById(curWord);
					}
				}
			});

		}
	}
	
	public boolean changeWordState() {
		if(wordDetail.getVisibility() == View.VISIBLE) {
			wordsListView.setVisibility(View.VISIBLE);
			wordDetail.setVisibility(View.GONE);
			return true;
		} else {
			return false;
		}
	}
	//保存新单词
	private void saveNewWords(NewWord wordTemp) {
		if (!UserInfoManager.getInstance().isLogin()) {
//			Intent intent = new Intent();
//			intent.setClass(mContext, Login.class);
//			mContext.startActivity(intent);
			LoginUtil.startToLogin(mContext);
		} else {
			try {
				wordTemp.id = String.valueOf(UserInfoManager.getInstance().getUserId());
				NewWordOp newWordOp = new NewWordOp(mContext);
				newWordOp.saveData(wordTemp);
				
				CustomToast.showToast(mContext, R.string.play_ins_new_word_success, 1000);
				
				addNetwordWord(wordTemp.word);
			} catch (Exception e) {
				e.printStackTrace();
				CustomToast.showToast(mContext, "加入单词本失败"+e, 1000);
			}
		}
	}
	
	public void addNetwordWord(String wordTemp) {
		ClientSession.Instace().asynGetResponse(
				new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
						WordUpdateRequest.MODE_INSERT, wordTemp),
				new IResponseReceiver() {
					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
					}
				}, null, null);
	}
	
	private final void showVoaWordById(int order) {
		waittingDialog.show();
		
		wordSelf.setTextSize(Constant.textSize + 1);
		wordPron.setTextSize(Constant.textSize);
		wordDef.setTextSize(Constant.textSize);
		wordExamples.setTextSize(Constant.textSize);
		
		try {
			ClientSession.Instace().asynGetResponse(
					new DictRequest(URLEncoder.encode(
							voaWords.get(curWord).word,
							"UTF-8")), new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {
							waittingDialog.dismiss();
							
							DictResponse rs = (DictResponse) response;
							if (rs.result != null && rs.result.equals("1")) {
								voaWords.get(curWord).audio = rs.newWord.audio;
								voaWords.get(curWord).pron = rs.newWord.pron;
								voaWords.get(curWord).examples = rs.newWord.examples;
								
								handler.sendEmptyMessage(0);
							}
						}
					}, null, null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				show(curWord);
				break;
			default:
				break;
			}
		}
	};
	
	public final void show(int order) {
		VoaWord voaWord = voaWords.get(curWord);

		if (voaWord.pron != null) {
			wordPron.setText(Html.fromHtml("[" + voaWord.pron + "]"));
		}

		if (voaWord.word != null) {
			wordSelf.setText(voaWord.word);
		}

		if (voaWord.def != null) {
			wordDef.setText(voaWord.def);
		}
		
		if (voaWord.examples != null) {
			wordExamples.setText(Html.fromHtml(voaWord.examples));
			wordExamples.setMovementMethod(ScrollingMovementMethod.getInstance());
			wordExamples.setText(Html.fromHtml(voaWord.examples));
		}

		if (order == 0) {
			formerWord.setBackgroundResource(R.drawable.former_word_none);
		} else {
			formerWord.setBackgroundResource(R.drawable.former_word);
		}

		if (order == voaWords.size() - 1) {
			nextWord.setBackgroundResource(R.drawable.next_word_none);
		} else {
			nextWord.setBackgroundResource(R.drawable.next_word);
		}
	}
	
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
}
