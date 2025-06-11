package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.CommentListAdapter;
import com.iyuba.concept2.protocol.UserCommentRequest;
import com.iyuba.concept2.protocol.UserCommentResponse;
import com.iyuba.concept2.sqlite.mode.Comment;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.lil.event.RefreshEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 评测排行榜的详情界面
 * Created by ivotsm on 2017/3/10.
 */

public class CommentActivity extends BasisActivity {
    private Context mContext;
    private Button backBtn,synchoBtn,editBtn;
    private TextView title;
    private ListView listComment;
    private String uid, voaId,userName,userPic,type;
    private boolean commentAll = false;
//    private LayoutInflater inflater;
//    private View commentFooter;
    private ArrayList<Comment> comments = new ArrayList<>();
//    private MediaPlayer voiceMediaPlayer;
    private CommentListAdapter commentAdapter;
    private CustomDialog waitDialog;
    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {

        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {

        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {
        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {

        }
    };

    //跳转数据
    public static void start(Context context,String searchUserId,String searchUserName,String searchUserPic,String voaId,String type){
        Intent intent = new Intent();
        intent.setClass(context,CommentActivity.class);
        intent.putExtra("uid",searchUserId);
        intent.putExtra("userName",searchUserName);
        intent.putExtra("userPic",searchUserPic);
        intent.putExtra("voaId",voaId);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_comment);

        mContext = this;
        //inflater = getLayoutInflater();
        waitDialog = new CustomDialog(mContext);

        backBtn = (Button) findViewById(R.id.button_back);
        title = (TextView) findViewById(R.id.title);
        synchoBtn = (Button) findViewById(R.id.button_syncho);
        editBtn = (Button) findViewById(R.id.button_edit);
        listComment = (ListView) findViewById(R.id.voa_list);

        synchoBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.GONE);

        //获取的数据
        uid = getIntent().getStringExtra("uid");
        voaId = getIntent().getStringExtra("voaId");
        userName = getIntent().getStringExtra("userName");
        userPic = getIntent().getStringExtra("userPic");
        type=getIntent().getStringExtra("type");

        title.setText(type);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        waitDialog.show();
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ClientSession.Instace().asynGetResponse(
                            new UserCommentRequest(uid, "familyalbum", voaId), new IResponseReceiver() {
                                @Override
                                public void onResponse(BaseHttpResponse response,
                                                       BaseHttpRequest request, int rspCookie) {//concept
                                    UserCommentResponse tr = (UserCommentResponse) response;
                                    if (tr.result.equals("true")) {
                                        comments.clear();
                                        comments.addAll(tr.comments);
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }, new IErrorReceiver() {
                                @Override
                                public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                                    LogUtils.e("CommentActivity.网络请求错误", "aaa");
                                }
                            }, mNetStateReceiver);
                    break;
                case 1:
                    for(int i = 0;i<comments.size();i++){
                        comments.get(i).username = userName;
                        comments.get(i).imgsrc = userPic;
                        comments.get(i).userId = uid;
                    }

                    //数据排序
                    //选择排序
                    // 遍历  然后每次遍历到一个元素之后  继续遍历该元素之后的所有元素 然后找到最小的元素 和其换位置
                    for (int i=0;i<comments.size();i++){
                        int minPos=i;
                        for (int j=i+1;j<comments.size();j++){
                            if (Integer.valueOf(comments.get(j).indexId)<Integer.valueOf(comments.get(minPos).indexId)){
                                minPos=j;
                            }
                        }
                        //交换数据
                        if (Integer.valueOf(comments.get(i).indexId)>Integer.valueOf(comments.get(minPos).indexId)){
                            Comment com = comments.get(i);
                            comments.set(i,comments.get(minPos));
                            comments.set(minPos,com);
                        }
                    }

                    waitDialog.dismiss();
                    commentAdapter = new CommentListAdapter(mContext, comments, 2);
                    listComment.setAdapter(commentAdapter);
                    break;default:
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        waitDialog.dismiss();
        if (commentAdapter!=null&&commentAdapter.mediaPlayer!=null) {
            commentAdapter.mediaPlayer.stop();
        }
    }

    //接受登录回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.USER_INFO)){
            //用户信息刷新
            //这里刷新适配器，主要显示是否已经点赞
            commentAdapter.notifyDataSetChanged();
        }
    }
}
