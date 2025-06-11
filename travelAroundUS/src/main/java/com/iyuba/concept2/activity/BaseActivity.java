package com.iyuba.concept2.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iyuba.concept2.R;
import com.iyuba.concept2.util.CheckNetWork;
import com.iyuba.concept2.widget.subtitle.TextPageSelectTextCallBack;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.WordCard;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutResId());
		// View rootView = findViewById(android.R.id.content); // 获取Activity的根布局
		// IeltsBibleUtil.findButtonSetOnClickListener(rootView, this);
		initCommons();
		initVariables();
		initViews(savedInstanceState);
		loadData();
	}

	private void initCommons() {
//		Button btn_nav = (Button) findViewById(R.id.btn_nav);
//		if (btn_nav != null) {
//			btn_nav.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					finish();
//
//				}
//			});
//		}
		ImageButton selector_btn_bg = findView(R.id.btn_nav_sub);
		if (selector_btn_bg != null) {
			selector_btn_bg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();

				}
			});
		}
	}

	/** 返回用于显示界面的id */
	protected abstract int getLayoutResId();

	/** 初始化变量,包括intent携带的数据和activity内的变量 */
	protected abstract void initVariables();

	/** 加载layout布局,初始化控件,为事件挂上事件的方法 */
	protected abstract void initViews(Bundle savedInstanceState);

	/** 调用mobileAPI */
	protected abstract void loadData();

	/**
	 * 查找View，省去强转的操作
	 *
	 * @param id 布局文件中的i控件id
	 * @return 返回对应的控件View
	 */
	protected <T> T findView(int id) {
		@SuppressWarnings("unchecked")
		T view = (T) findViewById(id);
		return view;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/** 选词选中后的回调函数 */
	public TextPageSelectTextCallBack textPageCallBack(final WordCard wordCard) {
		return new TextPageSelectTextCallBack() {

			@Override
			public void selectTextEvent(String selectText) {
				// WordCard wordCard=findView(R.id.wc_word_card);
				wordCard.setVisibility(View.GONE);
				if (selectText.matches("^[a-zA-Z]*")) {
					if (CheckNetWork
							.isNetworkAvailable(getApplicationContext())) {
						wordCard.setVisibility(View.VISIBLE);
						wordCard.searchWord(selectText);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.play_check_network, Toast.LENGTH_SHORT).show();
					}
				} else {
					ToastUtil.showToast(getApplicationContext(), "请取英文单词");
				}
			}

			@Override
			public void selectParagraph(int paragraph) {

			}
		};
	}

	/** headview 布局 */
	public View headView(int layoutId) {
		LayoutInflater layoutInflater = getLayoutInflater();
		return layoutInflater.inflate(layoutId, null);
	}

	/** 判断用户有没有登录的方法,没有登录去登录呀 */
	public boolean checkUserLoginAndLogin() {
		// 用户是否登录
		boolean isLogIn = UserInfoManager.getInstance().isLogin();
		if (isLogIn) {
			return true;
		} else {
//			Intent intent = new Intent();
//			intent.setClass(getApplicationContext(), Login.class);
//			startActivity(intent);
			LoginUtil.startToLogin(getApplicationContext());
			return false;
		}
	}

	/** 判断用户有没有登录的方法 */
	public boolean isUserLogin() {
		// 用户是否登录
		boolean isLogIn = UserInfoManager.getInstance().isLogin();
		if (isLogIn) {
			return true;
		} else {
			return false;
		}
	}

	/** 判断网络是否可用的方法*/
	public boolean isNetworkAvalible() {
		// 网络是否可用
		if (NetWorkState.isConnectingToInternet()) {
			return true;
		} else {
			return false;
		}
	}
}
