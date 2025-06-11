package com.iyuba.core.teacher.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.teacher.activity.ShowLargePicActivity;
import com.iyuba.core.teacher.protocol.AgreeAgainstRequest;
import com.iyuba.core.teacher.sqlite.mode.Question;
import com.iyuba.core.teacher.sqlite.op.CommentAgreeOp;
import com.iyuba.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class QuestionListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Question> mList = new ArrayList<Question>();
	private Player mediaPlayer;

	public QuestionListAdapter(Context context, ArrayList<Question> quesList) {
		mContext = context;
		mList = quesList;
	}
	
	public QuestionListAdapter(Context context) {
		mContext = context;

	}

	// 查看此用户是否已点赞
	private int checkAgree(String commentId, String uid) {
		return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
	}

	public void setData(ArrayList<Question> quesList) {
		mList = quesList;
	}

	public void addList(ArrayList<Question> quesList) {
		mList.addAll(quesList);
	}
	
	public void clearList(){
		mList.clear();
	}

	public void pausePlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Question getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Question ques = mList.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(
					R.layout.lib_list_item_question, null);
			viewHolder = new ViewHolder();
			viewHolder.quesIcon = (ImageView) convertView
					.findViewById(R.id.ques_icon);
			viewHolder.quesName = (TextView) convertView
					.findViewById(R.id.ques_name);
			viewHolder.quesInfo = (TextView) convertView
					.findViewById(R.id.ques_info);
			viewHolder.answerIcon = (ImageView) convertView
					.findViewById(R.id.answer_icon);
			viewHolder.isAnswer = (TextView) convertView
					.findViewById(R.id.is_answer);
			viewHolder.answerName = (TextView) convertView
					.findViewById(R.id.answer_name);
			viewHolder.quesDisc = (TextView) convertView
					.findViewById(R.id.ques_disc);
			viewHolder.quesPic = (ImageView) convertView
					.findViewById(R.id.ques_pic);
			viewHolder.quesSource = (TextView) convertView
					.findViewById(R.id.ques_source);
			viewHolder.answerNum = (TextView) convertView
					.findViewById(R.id.answer_num);
			viewHolder.agreeNum = (TextView) convertView
					.findViewById(R.id.agree_num);
			viewHolder.user_inf = convertView.findViewById(R.id.user_inf);
			viewHolder.agree = (ImageView) convertView.findViewById(R.id.agree);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		GitHubImageLoader.Instace(mContext).setCirclePic(
				mList.get(position).uid, viewHolder.quesIcon);

		if (checkAgree("q" + mList.get(position).qid,
				String.valueOf(UserInfoManager.getInstance().getUserId())) == 1) {
			viewHolder.agree.setBackgroundResource(R.drawable.agree_press);
		} else {

			viewHolder.agree.setBackgroundResource(R.drawable.agree_normal);

		}

		viewHolder.agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!UserInfoManager.getInstance().isLogin()) {
//					Intent intent = new Intent();
//					intent.setClass(mContext, Login.class);
//					mContext.startActivity(intent);
					LoginUtil.startToLogin(mContext);
					return;
				}

				if (checkAgree("q" + mList.get(position).qid,
						String.valueOf(UserInfoManager.getInstance().getUserId())) == 1) {
					handler.sendEmptyMessage(3);
				} else {

					ExeProtocol.exe(
							new AgreeAgainstRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
									UserInfoManager.getInstance().getUserName(),
									"questionid",
									String.valueOf(mList.get(position).qid)),
							new ProtocolResponse() {

								@Override
								public void finish(BaseHttpResponse bhr) {
									handler.sendEmptyMessage(4);
								}

								@Override
								public void error() {

								}
							});

					new CommentAgreeOp(mContext).saveData(
							"q" + mList.get(position).qid,
							String.valueOf(UserInfoManager.getInstance().getUserId()),
							"agree");
					mList.get(position).agree++;
					handler.sendEmptyMessage(0);
				}
			}
		});

		viewHolder.quesIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(!UserInfoManager.getInstance().isLogin()){
					
//					Intent intent = new Intent();
//					intent.setClass(mContext, Login.class);
//					mContext.startActivity(intent);
					LoginUtil.startToLogin(mContext);
					return;
				}
				mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
						Integer.valueOf(ques.uid),
						UserInfoManager.getInstance().getUserName(), 0));//点击其他人头像时也可以进入

//				Intent intent = new Intent();
//				SocialDataManager.Instance().userid = ques.uid;
//				intent.setClass(mContext, PersonalHome.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				mContext.startActivity(intent);
			}
		});
		if(ques.username==null||"".equals(ques.username)||"null".equals(ques.username))
			viewHolder.quesName.setText(ques.uid);
		else
			viewHolder.quesName.setText(ques.username);

		if (ques.time == null || "null".equals(ques.time)) {
			ques.time = "";
		}

		if (ques.location == null || "null".equals(ques.location)) {
			ques.location = "";
		}
		ques.time = ques.time.substring(0, 19);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long time = sdf.parse(ques.time).getTime();
			viewHolder.quesInfo.setText(formatTime(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// viewHolder.quesInfo.setText(ques.time);

		if (ques.ansCount == 0) {
			viewHolder.answerIcon.setVisibility(View.INVISIBLE);
			viewHolder.isAnswer.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.answerIcon.setVisibility(View.VISIBLE);
			viewHolder.isAnswer.setVisibility(View.VISIBLE);
		}

		if (ques.question != null && !ques.question.trim().equals("")) {
			viewHolder.quesDisc.setText(ques.question);
			viewHolder.quesDisc.setVisibility(View.VISIBLE);
		} else {
			viewHolder.quesDisc.setVisibility(View.INVISIBLE);
		}

		if (ques.img != null && !ques.img.trim().equals("")) {
			
			
//			int screenWidth = getScreenWidth(this);
//			ViewGroup.LayoutParams lp = testImage.getLayoutParams();
//			lp.width = screenWidth;
//			lp.height = LayoutParams.WRAP_CONTENT;
//			testImage.setLayoutParams(lp);
//
//			testImage.setMaxWidth(screenWidth);
//			testImage.setMaxHeight(screenWidth * 5); 这里其实可以根据需求而定，我这里测试为最大宽度的5倍
			
			viewHolder.quesPic.setVisibility(View.VISIBLE);
			viewHolder.quesPic.setAdjustViewBounds(true);
			viewHolder.quesPic.setMaxHeight(360);
			viewHolder.quesPic.setMaxWidth(240);
			// GitHubImageLoader.Instance(mContext).setPic("http://www."+Constant.IYBHttpHead+"/question/"+ques.img.replaceAll("_b.jpg",
			// "_s.jpg"),
			// viewHolder.quesPic, R.drawable.nearby_no_icon,0);

			ImageLoader.getInstance().displayImage("http://www."+Constant.IYBHttpHead()+"/question/"
					+ ques.img.replaceAll("_b.jpg", "_s.jpg"), viewHolder.quesPic);
			
//			ImageLoader.getInstance().displayImage("http://www."+Constant.IYBHttpHead+"/question/"
//					+ ques.img, viewHolder.quesPic);
			
//			ImageLoader.getInstance().loadImage(
//					"http://www."+Constant.IYBHttpHead+"/question/"
//							+ ques.img.replaceAll("_b.jpg", "_s.jpg"),
//					new SimpleImageLoadingListener() {
//								
//						
//
//						@Override
//						public void onLoadingStarted(String imageUri, View view) {
//							// TODO Auto-generated method stub
//							super.onLoadingStarted(imageUri, view);
//							
//							
//						}
//
//						@Override
//						public void onLoadingComplete(String imageUri,
//								View view, Bitmap loadedImage) {
//							super.onLoadingComplete(imageUri, view, loadedImage);
//
//							viewHolder.quesPic.setImageBitmap(loadedImage);
//							
//							LayoutParams para;
//							para = viewHolder.quesPic.getLayoutParams();
//							
//							int height = loadedImage.getHeight();
//							int width = loadedImage.getWidth();
//							
//							//获取手机屏幕密度
//							float f = mContext.getResources()
//									.getDisplayMetrics().density;
//							float bit = width / 80;
//							height = (int) (height / bit);
//							width = (int) (80 / 1.5 * f);
//							height = (int) (height / 1.5 * f);
//							
//							para.height = height;
//							para.width = width;
//							viewHolder.quesPic.setLayoutParams(para);
//
//						}
//
//					});
			
//			ImageLoader.getInstance().loadImage(
//					"http://www."+Constant.IYBHttpHead+"/question/"
//							+ ques.img,
//					new SimpleImageLoadingListener() {
//
//						@Override
//						public void onLoadingComplete(String imageUri,
//								View view, Bitmap loadedImage) {
//							super.onLoadingComplete(imageUri, view, loadedImage);
//
//							viewHolder.quesPic.setImageBitmap(loadedImage);
//							LayoutParams para;
//							para = viewHolder.quesPic.getLayoutParams();
//							int height = loadedImage.getHeight();
//							int width = loadedImage.getWidth();
//							// float
//							// f=mContext.getResources().getDisplayMetrics().density;
//							// int height = loadedImage.getHeight();
//							float f = mContext.getResources()
//									.getDisplayMetrics().density;
//							Log.e("2-----", "" + width);
//							float bit = width / 80;
//							height = (int) (height / bit);
//							width = (int) (80 / 1.5 * f);
//							height = (int) (height / 1.5 * f);
//							para.height = height;
//							para.width = width;
//							viewHolder.quesPic.setLayoutParams(para);
//
//						}
//
//					});

			viewHolder.quesPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, ShowLargePicActivity.class);
					intent.putExtra("pic", "http://www."+Constant.IYBHttpHead()+"/question/"
							+ ques.img);
					mContext.startActivity(intent);
				}
			});
		} else {
			viewHolder.quesPic.setVisibility(View.GONE);
		}

		viewHolder.quesSource.setText(ques.category2 + " " + ques.category1);

		viewHolder.answerNum.setText(ques.commentcount + "");
		viewHolder.agreeNum.setText(ques.agree + "");

		return convertView;
	}

	private String formatTime(long time) {
		Date date = new Date(time);
		Date date2 = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar.setTime(date);
		calendar2.setTime(date2);

		if(calendar2.get(Calendar.YEAR)
				- calendar.get(Calendar.YEAR) > 0){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}else if (calendar2.get(Calendar.DAY_OF_YEAR)
				- calendar.get(Calendar.DAY_OF_YEAR) > 0) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		} else if (System.currentTimeMillis() - time > 60 * 60 * 1000) {
			return (System.currentTimeMillis() - time) / (60 * 60 * 1000)
					+ "小时之前";
		} else if (System.currentTimeMillis() - time > 60 * 1000) {
			return (System.currentTimeMillis() - time) / (60 * 1000) + "分钟之前";
		} else if(System.currentTimeMillis() - time > 60){
			
			return (System.currentTimeMillis() - time) / (1000) + "秒之前";
			
		}else if(System.currentTimeMillis() - time == 0){
			return "1秒之前";
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}

	}

	public class ViewHolder {
		View user_inf;

		ImageView quesIcon;
		TextView quesName;
		TextView quesInfo;
		ImageView answerIcon;
		TextView isAnswer;
		TextView answerName;
		TextView quesDisc;
		ImageView quesPic;
		TextView quesSource;
		TextView answerNum;
		// TextView againestNum;
		TextView agreeNum;
		ImageView agree;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				notifyDataSetChanged();
				break;

			case 1:
				notifyDataSetChanged();
				break;
			case 3:
				CustomToast.showToast(mContext, R.string.agree_already);
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.comment_agree);
				break;

			}

		}
	};

}
