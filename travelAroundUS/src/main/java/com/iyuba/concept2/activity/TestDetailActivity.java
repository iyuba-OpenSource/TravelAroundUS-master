package com.iyuba.concept2.activity;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.TestDetailAdapter;
import com.iyuba.concept2.protocol.TestDetailRequest;
import com.iyuba.concept2.protocol.TestDetailResponse;
import com.iyuba.concept2.sqlite.mode.TestResultDetail;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.lil.user.UserInfoManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class TestDetailActivity extends Activity{

	private Context mContext;
	private ListView TestDetailListView;
	private TestDetailAdapter testDetailAdapter;
	private List<TestResultDetail> mList = new ArrayList<TestResultDetail>();
//	private CustomDialog waitDialog;
	private String mode = "1";
	private Button backBtn;
	private int page = 1;
	private View testDetailFooter;
	private LayoutInflater inflater;
	private final String numPerPage = "20";
	private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

		@Override
		public void onStartConnect(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnected(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartSend(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSend(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSendFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartRecv(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetError(BaseHttpRequest request, int rspCookie,
				ErrorResponse errorInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_detail_record);
		mContext = this;
		Intent intent = getIntent();
		mode = intent.getStringExtra("testMode");
		inflater = getLayoutInflater();
		testDetailFooter = inflater.inflate(R.layout.comment_footer, null);
//		waitDialog = WaittingDialog.showDialog(TestDetailActivity.this);
		TestDetailListView = (ListView) findViewById(R.id.detail_list);
		testDetailAdapter = new TestDetailAdapter(mContext);
		TestDetailListView.addFooterView(testDetailFooter);
		TestDetailListView.setAdapter(testDetailAdapter);
		backBtn = (Button) findViewById(R.id.button_back);
//		waitDialog.show();
		new UpdateTestDetailThread().start();
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		TestDetailListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						// 当comment不为空且comment.size()不为0且没有完全加载
						mHandler.sendEmptyMessage(0);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}
	
	private class UpdateTestDetailThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			ClientSession.Instace().asynGetResponse(
					new TestDetailRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),mode,String.valueOf(page),
							numPerPage), new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {

							TestDetailResponse tr = (TestDetailResponse) response;

							if (tr != null && tr.result.equals("1")) {

								mList.clear();
								mList.addAll(tr.mList);
								binderAdapterDataHandler
										.post(binderAdapterDataRunnable);

							} else {

							}
						}
					}, null, mNetStateReceiver);

		}
	}
	private Handler binderAdapterDataHandler = new Handler();
	private Runnable binderAdapterDataRunnable = new Runnable() {
		public void run() {
//				testDetailAdapter.addList((ArrayList<TestResultDetail>) mList);
//				testDetailAdapter.notifyDataSetChanged();			
//				waitDialog.dismiss();
//				if(mList.size() == 0){
//					Toast.makeText(mContext, "没有数据记录哦~~", 2000).show();
//				}
				
				if (mList.size() == 0) {
					testDetailFooter.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(1);
				} else if (mList.size() < 20) {
					testDetailAdapter.addList((ArrayList<TestResultDetail>) mList);
					testDetailAdapter.notifyDataSetChanged();
					testDetailFooter.setVisibility(View.INVISIBLE);
				} else {
					testDetailAdapter.addList((ArrayList<TestResultDetail>) mList);
					testDetailAdapter.notifyDataSetChanged();
					testDetailFooter.setVisibility(View.VISIBLE);
				}
		}
	};
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				page = page + 1;
				new UpdateTestDetailThread().start();
				testDetailFooter.setVisibility(View.GONE);
				break;
			case 1:
				Toast.makeText(mContext, "已经到底啦~~", 500).show();
				break;
			default:
				break;
			}
		};
	};
	
}
