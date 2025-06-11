package com.iyuba.concept2.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.AgreeAgainstRequest;
import com.iyuba.concept2.protocol.AgreeAgainstResponse;
import com.iyuba.concept2.sqlite.mode.Comment;
import com.iyuba.concept2.sqlite.op.CommentAgreeOp;
import com.iyuba.concept2.widget.CircleImageView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.ui.NewLoginActivity;
import com.iyuba.core.lil.user.util.LoginUtil;

import java.util.ArrayList;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class CommentListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Comment> mList = new ArrayList<Comment>();
    private boolean playingVoice = false;
    public Player mediaPlayer;
    private ImageView tempVoice;
    private int voiceCount;
    private String voiceId;
    private int type;

    public CommentListAdapter(Context context, ArrayList<Comment> Comments, int type) {
        mContext = context;
        mList = Comments;
        this.type = type;

    }
    public CommentListAdapter(Context context, ArrayList<Comment> Comments,
                              int type,MediaPlayer videoView) {
        mContext = context;
        mList = Comments;
        this.type = type;
    }

    public void setData(ArrayList<Comment> Comments) {
        mList = Comments;
    }

    public void addList(ArrayList<Comment> Comments) {
        // TODO Auto-generated method stub
        mList.addAll(Comments);
    }

    public void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Comment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Comment curItem = mList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.image =  convertView.findViewById(R.id.comment_image);
            viewHolder.reread = (ImageView) convertView.findViewById(R.id.reread_image);
            viewHolder.comment_body_voice = convertView.findViewById(R.id.comment_voice_view);
            viewHolder.body = (TextView) convertView.findViewById(R.id.comment_letter_view);
            viewHolder.name = (TextView) convertView.findViewById(R.id.comment_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.comment_time);
            viewHolder.comment_body_voice_icon = (ImageView) convertView.findViewById(R.id.comment_body_voice_icon);// 显示正在播放的
            viewHolder.reply = (Button) convertView.findViewById(R.id.reply);
            viewHolder.deleteBox = (ImageView) convertView.findViewById(R.id.checkBox_isDelete);
            viewHolder.agreeText = (TextView) convertView.findViewById(R.id.agree_text);
            viewHolder.againstText = (TextView) convertView.findViewById(R.id.against_text);
            viewHolder.agreeView = (ImageView) convertView.findViewById(R.id.agree);
            viewHolder.againstView = (ImageView) convertView.findViewById(R.id.against);
            viewHolder.tvVoiceStatus = convertView.findViewById(R.id.tv_voice_statues);
            viewHolder.mSenId = convertView.findViewById(R.id.tv_sentence);
            viewHolder.mSenScore = convertView.findViewById(R.id.tv_score);

            viewHolder.comment = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.reread.setVisibility(View.GONE);

        if (type==2){
            Log.e("类型type",type+"");
            viewHolder.tvVoiceStatus.setVisibility(View.VISIBLE);
            if (curItem.shuoshuoType==2){
                viewHolder.tvVoiceStatus.setText("单句");
                viewHolder.mSenId.setVisibility(View.VISIBLE);
            }else if(curItem.shuoshuoType==4){
                viewHolder.mSenId.setVisibility(View.GONE);
                viewHolder.tvVoiceStatus.setText("多句合成");
            }
        }else {
            viewHolder.tvVoiceStatus.setVisibility(View.GONE);
        }
        if (curItem.indexId!=null&&!curItem.indexId.equals("")&&!curItem.indexId.equals("0")) {
            viewHolder.mSenId.setText("第" + curItem.indexId + "句");
            viewHolder.mSenId.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mSenId.setVisibility(View.GONE);
        }
        if (curItem.score!=null&&!curItem.equals("")) {
            viewHolder.mSenScore.setText(curItem.score + "分");
            viewHolder.mSenScore.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mSenScore.setVisibility(View.GONE);
        }

        if (checkAgree(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId())) == 1) {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree_press);
        } else if (checkAgree(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId())) == 2) {
            viewHolder.againstView.setBackgroundResource(R.drawable.against_press);
        } else {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree_normal);
            viewHolder.againstView.setBackgroundResource(R.drawable.against_normal);
        }
        // 点赞部分
        viewHolder.agreeText.setText(String.valueOf(curItem.agreeCount));
        viewHolder.againstText.setText(String.valueOf(curItem.againstCount));
        viewHolder.agreeView.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(mContext);
                return;
            }

            if (checkAgree(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId())) == 0) {
                ClientSession.Instace().asynGetResponse(
                        new AgreeAgainstRequest("61001", curItem.id, type),
                        new IResponseReceiver() {
                            @Override
                            public void onResponse(
                                    BaseHttpResponse response,
                                    BaseHttpRequest request, int rspCookie) {
                                // TODO 自动生成的方法存根
                                AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                                if (rs.result.equals("001")) {
                                    handler.sendEmptyMessage(4);
                                    //设置点赞
                                    new CommentAgreeOp(mContext).saveData(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId()), "agree");
                                    mList.get(position).agreeCount = mList
                                            .get(position) == null ? null
                                            : (++mList.get(position).agreeCount);
                                    handler.sendEmptyMessage(0);
                                } else if (rs.result.equals("000")) {
                                    handler.sendEmptyMessage(2);
                                }
                            }
                        });
            } else {
                handler.sendEmptyMessage(3);
            }
        });
        viewHolder.againstView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(mContext);
                    return;
                }

                if (checkAgree(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId())) == 0) {
                    ClientSession.Instace().asynGetResponse(
                            new AgreeAgainstRequest("61002", curItem.id, type),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    // TODO 自动生成的方法存根
                                    AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                                    if (rs.result.equals("001")) {
                                        handler.sendEmptyMessage(5);
                                        new CommentAgreeOp(mContext).saveData(curItem.id, String.valueOf(UserInfoManager.getInstance().getUserId()), "against");
                                        mList.get(position).againstCount = mList
                                                .get(position) == null ? null
                                                : (++mList.get(position).againstCount);
                                        handler.sendEmptyMessage(0);
                                    } else if (rs.result.equals("000")) {
                                        handler.sendEmptyMessage(2);
                                    }
                                }
                            });
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        });
        // 是在播放，显示动画
        if (playingVoice && curItem.id.equals(voiceId)) {
            voiceAnimation(viewHolder.comment_body_voice_icon);
        } else {// 否则停止
            viewHolder.comment_body_voice_icon
                    .setImageResource(R.drawable.chatfrom_voice_playing);
            voiceStopAnimation(viewHolder.comment_body_voice_icon);
        }
        if(curItem.username!=null&&!"none".equals(curItem.username)&&!"".equals(curItem.username)&&!"null".equals(curItem.username))
            viewHolder.name.setText(curItem.username);
        else
            viewHolder.name.setText(curItem.userId);
        viewHolder.time.setText(curItem.createdate);
        viewHolder.body.setText(curItem.shuoshuo);
        viewHolder.reply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().isLogin()) {
                    Intent intent = new Intent("toreply");
                    intent.putExtra("username", curItem.username);
                    mContext.sendBroadcast(intent);
                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    mContext.startActivity(intent);
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        if (type != 0) {
            viewHolder.reply.setVisibility(View.GONE);
        }
        GitHubImageLoader.Instace(mContext).setPic(mList.get(position).userId,
                viewHolder.image);
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().isLogin()) {
                    mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
                            Integer.valueOf(curItem.userId),
                            curItem.username, 0));//点击其他人头像时也可以进入
                } else {
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        // 点击语音评论进行播放
        viewHolder.comment_body_voice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                playingVoice = true;//
                if (tempVoice != null) {// 播放之前先停止其他的播放
                    handler.removeMessages(1);
                    tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);
                }
                voiceId = curItem.id;
                if (type != 2)
                    playVoice("http://daxue."+Constant.IYBHttpHead()+"/appApi/" + curItem.shuoshuo);// 播放
                else
                    playVoice("http://voa."+Constant.IYBHttpHead()+"/voa/" + curItem.shuoshuo);// 播放
                voiceAnimation(v.findViewById(R.id.comment_body_voice_icon));// 播放的动画
            }
        });
        if (curItem.shuoshuoType == 0) {
            viewHolder.comment_body_voice.setVisibility(View.INVISIBLE);
            viewHolder.body.setVisibility(View.VISIBLE);
        } else {
            viewHolder.comment_body_voice.setVisibility(View.VISIBLE);
            viewHolder.body.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private int checkAgree(String commentId, String uid) {
        if (UserInfoManager.getInstance().isLogin()){
            return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
        }
        return 0;
    }

    // 播放语音
    private void playVoice(String url) {

        if (mediaPlayer == null) {
            mediaPlayer = new Player(mContext,
                    new OnPlayStateChangedListener() {

                        @Override
                        public void playFaild() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void playCompletion() {
                            // TODO Auto-generated method stub
                            playingVoice = false;
                            handler.removeMessages(1, tempVoice);
                            tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);
                        }
                    });
        }else {
            stopVoice();
        }
        mediaPlayer.playUrl(url);
    }
    private void playVoices(String url) {

    }
    // 播放语音评论之前先在这里reset播放器
    private void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }
    public void stopVoices(){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    // 播放动画，参数为要显示变化的imageview
    private void voiceAnimation(View v) {
        voiceStopAnimation(v);
        voiceCount = 0;
        tempVoice = (ImageView) v;
        handler.obtainMessage(1, tempVoice).sendToTarget();
    }

    // 停止播放动画
    private void voiceStopAnimation(View v) {
        handler.removeMessages(1, v);
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
                    // 通过不断切换图片来表示动画
                    if (voiceCount % 3 == 0) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.chatfrom_voice_playing_f1);
                    } else if (voiceCount % 3 == 1) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.chatfrom_voice_playing_f2);
                    } else if (voiceCount % 3 == 2) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.chatfrom_voice_playing_f3);
                    }
                    voiceCount++;
                    handler.sendMessageDelayed(handler.obtainMessage(1, msg.obj),
                            500);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.comment_already);
                    break;
                case 4:
                    CustomToast.showToast(mContext, R.string.comment_agree);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.comment_disagree);
                    break;
            }
        }
    };

    public class ViewHolder {
        CircleImageView image;// 头像图片
        ImageView reread;//跟读图片
        ImageView agreeView;// 点赞按钮
        ImageView againstView;// 点踩按钮
        TextView agreeText; // 多少赞
        TextView againstText; // 多少踩
        View comment_body_voice;// 语音评论
        ImageView comment_body_voice_icon;// 显示正在播放的
        TextView name; // 用户名
        TextView time; // 发布时间
        TextView body; // 评论体
        Button reply; // 回复按钮
        ImageView deleteBox;
        View comment; // 整体
        TextView tvVoiceStatus;
        TextView mSenScore,mSenId;

    }
}
