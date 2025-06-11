package com.iyuba.concept2.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.DictRequest;
import com.iyuba.concept2.protocol.DictResponse;
import com.iyuba.concept2.protocol.WordUpdateRequest;
import com.iyuba.concept2.sqlite.mode.NewWord;
import com.iyuba.concept2.sqlite.op.MainWordOp;
import com.iyuba.concept2.sqlite.op.NewWordOp;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibResUtil;

public class WordCard extends LinearLayout {
	private Context mContext;
	private Button add_word, close_word;
	private ProgressBar progressBar_translate;
	private String selectText;
	private TextView key, pron, def, examples;
	private Typeface mFace;
	private NewWord selectCurrWordTemp;
	private ImageView speaker;
	private MainWordOp wordOp;
	private Handler wordHandler;
	private Player player;

	public WordCard(Context context) {
		super(context);
		mContext = context;
		wordOp = new MainWordOp(context);
		if (player==null) {
			player = new Player(mContext, null);
		}

		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard, this);
		initGetWordMenu();
	}

	public WordCard(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		if (player==null) {
			player = new Player(mContext, null);
		}

		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
				this);
		initGetWordMenu();
	}

	private void initGetWordMenu() {
		progressBar_translate = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
		key = (TextView) findViewById(R.id.word_key);
		pron = (TextView) findViewById(R.id.word_pron);
		def = (TextView) findViewById(R.id.word_def);
		examples = (TextView) findViewById(R.id.examples);
		speaker=(ImageView) findViewById(R.id.word_speaker);
		
		add_word = (Button) findViewById(R.id.word_add);
		add_word.setVisibility(GONE);
		// 添加到生词本
		add_word.setOnClickListener(v -> {
			saveNewWords(selectCurrWordTemp);
		});
		close_word = (Button) findViewById(R.id.word_close);
		close_word.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WordCard.this.setVisibility(View.GONE);

				//增加关闭的回调
				if (onWindowCloseListener!=null){
					onWindowCloseListener.closeWindow();
				}
			}
		});
	}

	/**
	 * 获取单词释义
	 */
	private void getNetworkInterpretation() {
		if (selectText != null && selectText.length() != 0) {
			ClientSession.Instace().asynGetResponse(
					new DictRequest(selectText), new IResponseReceiver() {

						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {
							DictResponse dictResponse = (DictResponse) response;
							
							selectCurrWordTemp = dictResponse.newWord;
							selectCurrWordTemp.id = String.valueOf(UserInfoManager.getInstance().getUserId());
							
							if (selectCurrWordTemp != null) {
								if (selectCurrWordTemp.def != null && selectCurrWordTemp.def.length() != 0) {
									handler.sendEmptyMessage(1);
								} else {
									handler.sendEmptyMessage(2);
								}
							} else {
							}
						}
					}, null, null);
		} else {
			CustomToast.showToast(mContext, R.string.play_please_take_the_word,
					1000);
		}
	}

	public void showWordDefInfo(Handler handler) {
		if(selectCurrWordTemp != null) {
			key.setText(selectCurrWordTemp.word);
			
			if (selectCurrWordTemp.pron != null
					&& selectCurrWordTemp.pron.length() != 0) {
				pron.setText(Html.fromHtml("[" + selectCurrWordTemp.pron + "]"));
			}
			
			def.setText(selectCurrWordTemp.def);
			
			examples.setText(Html.fromHtml(selectCurrWordTemp.examples));
			examples.setMovementMethod(ScrollingMovementMethod.getInstance());
			examples.setText(Html.fromHtml(selectCurrWordTemp.examples));
			
			if (selectCurrWordTemp.audio != null && selectCurrWordTemp.audio.length() != 0) {
				speaker.setVisibility(View.VISIBLE);
			} else {
				speaker.setVisibility(View.GONE);
			}
			
			speaker.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String url = selectCurrWordTemp.audio;
					player.playUrl(url);
				}
			});

			//这里判断下，如果存在就别显示了
			NewWord newWord = new NewWordOp(LibResUtil.getInstance().getContext()).findWordDataByWord(selectCurrWordTemp.word,UserInfoManager.getInstance().getUserId());
			if (newWord!=null){
				add_word.setVisibility(GONE);
			}else {
				add_word.setVisibility(VISIBLE);
			}

			handler.sendEmptyMessage(0);
		} else {
			CustomToast.showToast(mContext,
					R.string.no_search_word, 1000);
		}
		
		progressBar_translate.setVisibility(View.GONE); // 显示等待
	}

	private void saveNewWords(NewWord wordTemp) {
		if (!UserInfoManager.getInstance().isLogin()) {
			LoginUtil.startToLogin(mContext);
		} else {
			try {
				NewWordOp newWordOp = new NewWordOp(mContext);
				newWordOp.saveData(wordTemp);
				ToastUtil.showToast(CrashApplication.getInstance(), "单词已加入生词本");
				WordCard.this.setVisibility(View.GONE);
				addNetwordWord(wordTemp.word);

				//增加关闭的回调
				if (onWindowCloseListener!=null){
					onWindowCloseListener.closeWindow();
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	public void searchWord(String word, Context mContext, Handler handler) {
		selectText = word;
		wordHandler = handler;
		
		int isConnect = NetWorkState.getAPNType();
		
		if (isConnect == 0) {
			if (UserInfoManager.getInstance().isVip())  {
				if(wordOp == null) {
					wordOp = new MainWordOp(mContext);
				}
				
				selectCurrWordTemp = wordOp.findData(selectText);
				showWordDefInfo(wordHandler);
			} 
			
			// 请检查网络
			CustomToast.showToast(CrashApplication.getInstance(), R.string.category_check_network, 1000);
		} else {
			getNetworkInterpretation();
		}
	}
	
	public void setWordCard(Context context) {
		mContext = context;
		wordOp = new MainWordOp(mContext);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				showWordDefInfo(wordHandler);
				break;
			case 2:
				CustomToast.showToast(CrashApplication.getInstance(),
						R.string.play_no_word_interpretation, 1000);
				WordCard.this.setVisibility(View.GONE);
				break;
			}
		}
	};

	//增加一个回调接口，关闭之后可以恢复播放
	public interface OnWindowCloseListener{
		void closeWindow();
	}

	public OnWindowCloseListener onWindowCloseListener;

	public void setOnWindowCloseListener(OnWindowCloseListener onWindowCloseListener) {
		this.onWindowCloseListener = onWindowCloseListener;
	}
}
