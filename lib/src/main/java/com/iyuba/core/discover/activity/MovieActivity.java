package com.iyuba.core.discover.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.view.Window;




import com.iyuba.lib.R;

/**
 * 视频
 */

public class MovieActivity extends FragmentActivity {


	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.movie_activity);
		mContext = this;








		/*HeadlinesConstantManager.USERID = AccountManager.Instance(mContext).userId == null || "".equals(AccountManager.Instance(mContext).userId) ? "0" :
				AccountManager.Instance(mContext).userId;
		HeadlinesConstantManager.VIPSTATUS = ConfigManager.Instance().loadInt("isvip") + "";
		FragmentManager manager =  getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.root, new DropdownMenuFragment().newInstance(
				HeadlinesConstantManager.USERID,
				HeadlinesConstantManager.VIPSTATUS,
				HeadlinesConstantManager.ALL_CATEGORY,
				HeadlinesConstantManager.ALL_CATEGORY, true,
				HeadlinesConstantManager.REQUEST_PIECES_MIX, HeadlinesConstantManager.buildTypeStr(
						HeadlinesConstantManager.Type.ALL,
						HeadlinesConstantManager.Type.VOAVIDEO,
						HeadlinesConstantManager.Type.BBCWORDVIDEO,
						HeadlinesConstantManager.Type.MEIYU,
						HeadlinesConstantManager.Type.TED,
						HeadlinesConstantManager.Type.TOPVIDEOS)));
//        transaction.add(R.id.content_video,fragment);
		transaction.commit();*/

	}


	
	
}
