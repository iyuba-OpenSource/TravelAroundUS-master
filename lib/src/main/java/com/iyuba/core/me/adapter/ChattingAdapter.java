package com.iyuba.core.me.adapter;

/**
 * 私信内容适配器
 * 
 * @author 陈彤
 * @version 1.0
 */

import android.content.Context;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.Expression;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.sqlite.mode.ChatMessage;
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.me.sqlite.mode.MessageLetterContent;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class ChattingAdapter extends BaseAdapter {
	private Context context;
	private List<MessageLetterContent> mList = new ArrayList<MessageLetterContent>();
	private String uid;

	public ChattingAdapter(Context context,
			List<MessageLetterContent> messages, String uid) {
		this.context = context;
		this.mList = messages;
		this.uid = uid;
	}

	public ChattingAdapter(Context context, String uid) {
		this.context = context;
		this.uid = uid;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public MessageLetterContent getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setList(ArrayList<MessageLetterContent> list) {
		mList = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MessageLetterContent message = mList.get(position);
		if (message.authorid.equals(uid)) {
			message.setDirection(1);
		} else {
			message.setDirection(0);
		}
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != message.direction) {
			holder = new ViewHolder();
			if (message.direction == ChatMessage.MESSAGE_FROM) {
				holder.flag = ChatMessage.MESSAGE_FROM;
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chatting_item_from, null);
			} else {
				holder.flag = ChatMessage.MESSAGE_TO;
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chatting_item_to, null);
			}
			holder.text = (TextView) convertView
					.findViewById(R.id.chatting_content_itv);
			holder.time = (TextView) convertView
					.findViewById(R.id.chatting_time_tv);
			holder.userImageView = (ImageView) convertView
					.findViewById(R.id.chatting_content_iv);
			convertView.setTag(holder);
		}
		String zhengze = "image[0-9]{2}|image[0-9]";
		Emotion emotion = new Emotion();
		message.message = emotion.replace(message.message);
		try {
			SpannableString spannableString = Expression.getExpressionString(
					context, message.message, zhengze);
			holder.text.setText(spannableString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		holder.time.setText(DateFormat.format("MM-dd kk:mm",
				Long.parseLong(message.dateline) * 1000));
		GitHubImageLoader.Instace(context).setCirclePic(message.authorid,
				holder.userImageView);
		holder.userImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				context.startActivity(PersonalHomeActivity.buildIntent (context,
						Integer.valueOf(mList.get(position).authorid),
						UserInfoManager.getInstance().getUserName(), 0));//点击其他人头像时也可以进入

			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView text;
		ImageView userImageView;
		TextView time;
		int flag;
	}

}
