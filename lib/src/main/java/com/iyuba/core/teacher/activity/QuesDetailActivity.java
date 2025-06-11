package com.iyuba.core.teacher.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.manager.RecordManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.thread.UploadFile;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.CustomExpandableListView;
import com.iyuba.core.common.widget.MyScrollView;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.teacher.adapter.ChatAdapter;
import com.iyuba.core.teacher.adapter.CommentListAdapter;
import com.iyuba.core.teacher.protocol.AnswerFollowRequest;
import com.iyuba.core.teacher.protocol.AnswerFollowResponse;
import com.iyuba.core.teacher.protocol.AnswerQuesRequest;
import com.iyuba.core.teacher.protocol.AnswerQuesResponse;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesRequest;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesResponse;
import com.iyuba.core.teacher.protocol.GetChatListRequest;
import com.iyuba.core.teacher.protocol.GetChatListResponse;
import com.iyuba.core.teacher.protocol.GetCommentListRequest;
import com.iyuba.core.teacher.protocol.GetCommentListResponse;
import com.iyuba.core.teacher.sqlite.mode.AnswerInfo;
import com.iyuba.core.teacher.sqlite.mode.AnswerType;
import com.iyuba.core.teacher.sqlite.mode.Chat;
import com.iyuba.core.teacher.sqlite.mode.Question;
import com.iyuba.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

@RuntimePermissions
public class QuesDetailActivity extends Activity {
    private Context mContext;
    AnswerType answerType = new AnswerType();
    private ImageView btnBack;
    private ImageView userIcon;
    private TextView userName;
    private TextView questInfo;
    private TextView attention;
    private TextView quesDisc, questiondetail;
    private ImageView quesPic;
    private View chatView;
    private TextView teacherTag;
    private CustomExpandableListView chatListview;
    private View commentView;
    private TextView studentTag;
    private ListView commentListview;

    private int inputMode = 0;// 默认为文字输入，0文字，1语音
    private ImageView setMode;
    private View expressLayout;
    private EditText express;
    private ImageView holdSpeak;
    private Button sendBtn;
    private ImageView showMore;
    private TextView photoAlbum;
    private TextView takePhoto;
    private View bottomView;

    private Question question = new Question();
    private List<List<Chat>> childChatList;
    private List<AnswerInfo> groupChatList;
    private List<AnswerInfo> commentList;

    private ChatAdapter chatAdapter;
    private CommentListAdapter commentListAdapter;

    private CustomDialog waitDialog;

    private String uid;
    private String username;
    private int qid;

    private String tmpFilepath;
    private String tmpPhotoFilePath = Environment.getExternalStorageDirectory()
            + "/temp_photo.jpg";
    private File tmpPhotoFile = new File(tmpPhotoFilePath);
    private String tmpSoundFilePath = Environment.getExternalStorageDirectory()
            + "/temp_sound.jpg";
    private File tmpSoundFile = new File(tmpSoundFilePath);

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private View voiceView;
    private ImageView voiceValue;
    private Drawable amps[] = new Drawable[8];
    private RecordManager rManager;
    int show = 0;// ==2用于控制是否取消加载中的提示

    MyScrollView scrollView;
    View linear_express;

    private ImageView setCommentModeButton;
    private int commentMode = 0;// 0是文字评论，1是语音评论
    Button voiceExpressButton;
    private Player mPlayer;
    EditText expressEditText;
    Button voiceListenButton;
    Button button_express;
    ContextMenu contextMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_ques_detail);
        mContext = this;
        waitDialog = WaittingDialog.showDialog(mContext);
        initData();
        initWidget();
        init();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (commentListAdapter != null) {
            commentListAdapter.stopVoice();
        }
    }


    public void initData() {
        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        username = UserInfoManager.getInstance().getUserName();
        Intent intent = getIntent();
        String theQid = intent.getStringExtra("qid");
        if (theQid == null)
            theQid = "1";
        qid = Integer.parseInt(theQid);
    }

    public void initWidget() {
        mPlayer = new Player(mContext, null);
        scrollView = (MyScrollView) findViewById(R.id.scrollq);
        linear_express = findViewById(R.id.linear_express);
        linear_express.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
        contextMenu = (ContextMenu) findViewById(R.id.context_menu);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        userIcon = (ImageView) findViewById(R.id.user_icon);
        userName = (TextView) findViewById(R.id.user_name);
        questInfo = (TextView) findViewById(R.id.quest_info);
        attention = (TextView) findViewById(R.id.attention);
        quesDisc = (TextView) findViewById(R.id.ques_disc);
        questiondetail = (TextView) findViewById(R.id.questiondetail);
        quesPic = (ImageView) findViewById(R.id.ques_detail_pic);
        chatView = findViewById(R.id.answer_view);
        teacherTag = (TextView) findViewById(R.id.teacher_tag);
        chatListview = (CustomExpandableListView) findViewById(R.id.answer_listview);
        commentView = findViewById(R.id.comment_view);
        studentTag = (TextView) findViewById(R.id.student_tag);
        commentListview = (ListView) findViewById(R.id.comment_listviewq);
        OnChildClickListener occl = new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1,
                                        int arg2, int arg3, long arg4) {

                final int theaid = childChatList.get(arg2).get(arg3).answerid;
                final int num = arg2;
                final int num2 = arg3;
                final String auid = childChatList.get(arg2).get(arg3).fromid
                        + "";

                final String type = "2";

                contextMenu.setText(mContext.getResources().getStringArray(
                        R.array.choose_delete2));
                contextMenu.setCallback(new ResultIntCallBack() {

                    @Override
                    public void setResult(int result) {
                        // TODO Auto-generated method stub
                        switch (result) {
                            case 0:
                                if (UserInfoManager.getInstance().isLogin()) {
                                    delAlertDialog(theaid + "", num, num2, type);
                                } else {
//                                    Intent intent = new Intent();
//                                    intent.setClass(mContext, Login.class);
//                                    startActivity(intent);
                                    LoginUtil.startToLogin(mContext);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
                contextMenu.show();
                return false;
            }
        };

        chatListview.setOnChildClickListener(occl);

        setMode = (ImageView) findViewById(R.id.set_mode);
        express = (EditText) findViewById(R.id.express);
        expressLayout = findViewById(R.id.express_layout);
        holdSpeak = (ImageView) findViewById(R.id.hold_speak);
        sendBtn = (Button) findViewById(R.id.send);
        showMore = (ImageView) findViewById(R.id.show);
        photoAlbum = (TextView) findViewById(R.id.phone_album);
        takePhoto = (TextView) findViewById(R.id.take_photo);
        bottomView = findViewById(R.id.bottom_view);
        voiceView = findViewById(R.id.voice_view);
        voiceValue = (ImageView) findViewById(R.id.mic_value);
        button_express = (Button) findViewById(R.id.button_express);
        setCommentModeButton = (ImageButton) findViewById(R.id.setcommentmode);
        setCommentModeButton.setOnClickListener(ocl_comment);
        setCommentModeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (commentMode == 1) {
                    switchToText();
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        QuesDetailActivityPermissionsDispatcher.switchToVoiceWithPermissionCheck(QuesDetailActivity.this);
                    } else {
                        switchToVoice();
                    }
                }
            }
        });

        // 发表评论回复的功能
        button_express.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (UserInfoManager.getInstance().isLogin()) {

                    if (answerType.sub == 0) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入评论！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                answerQuestion(expressionInput.toString()
                                        .toString(), 2);
                            }
                        } else {
                            // 发送语音
                            new CommentThread().start();

                        }
                    } else if (answerType.sub == 1) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入回复！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                answerQuestion(expressionInput.toString()
                                        .toString(), 1);
                            }
                        } else {
                            // 发送语音
                            new answerThread().start();
                        }
                    } else if (answerType.sub == 2) {
                        if (commentMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    express.getWindowToken(), 0);
                            String expressionInput = expressEditText.getText()
                                    .toString();
                            if (expressionInput.toString().trim().equals("")) {
                                CustomToast.showToast(mContext, "请输入内容！");
                                expressEditText.setText("");
                            } else {
                                if (expressionInput.toString().trim().length() > 100) {
                                    CustomToast.showToast(mContext,
                                            "最多只能输入100字，已超啦!");
                                    return;
                                }
                                // answerQuestion(expressionInput.toString().toString());
                                answerFollowQuestion(expressionInput.toString()
                                        .trim());
                            }
                        } else {
                            // 发送语音
                            new UploadVoice().start();
                            // new UploadFollow().start();
                        }
                    }

                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
                    LoginUtil.startToLogin(mContext);
                }
            }
        });

        // 录音
        voiceExpressButton = (Button) findViewById(R.id.voice_express);
        voiceExpressButton.setOnTouchListener(voice_otl);

        expressEditText = (EditText) findViewById(R.id.editText_express);

        voiceListenButton = (Button) findViewById(R.id.voice_express_listen);

        voiceListenButton.setOnClickListener(ocl_comment);
        voiceListenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                show = 2;
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                mPlayer.reset();
                mPlayer.playUrl(Constant.voiceCommentAddr);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        userIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(PersonalHomeActivity.buildIntent (mContext,
                        Integer.valueOf(question.uid),
                        UserInfoManager.getInstance().getUserName(), 0));//点击其他人头像时也可以进入

//                Intent intent = new Intent();
//                SocialDataManager.Instance().userid = question.uid;
//                intent.setClass(mContext, PersonalHome.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                mContext.startActivity(intent);
            }
        });

        attention.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        quesPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mContext, ShowLargePicActivity.class);
                intent.putExtra("pic", "http://www."+Constant.IYBHttpHead()+"/question/"
                        + question.img);
                mContext.startActivity(intent);
            }
        });

        setMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                inputMode = 1 - inputMode;
                if (inputMode == 0) {
                    sendBtn.setVisibility(View.VISIBLE);
                    showMore.setVisibility(View.GONE);
                    expressLayout.setVisibility(View.VISIBLE);
                    holdSpeak.setVisibility(View.GONE);
                    setMode.setImageDrawable(getResources().getDrawable(
                            R.drawable.chatting_setmode_keyboard_btn));
                    bottomView.setVisibility(View.GONE);
                } else {

                    sendBtn.setVisibility(View.GONE);
                    showMore.setVisibility(View.VISIBLE);
                    expressLayout.setVisibility(View.GONE);
                    holdSpeak.setVisibility(View.VISIBLE);
                    setMode.setImageDrawable(getResources().getDrawable(
                            R.drawable.chatting_setmode_keyboard_btn));
                }
            }
        });

        holdSpeak.setOnTouchListener(voice_otl);

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (UserInfoManager.getInstance().isLogin()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(express.getWindowToken(), 0);
                    String expressStr = express.getText().toString();
                    if ("".equals(expressStr.trim())) {
                        handler_chat.sendEmptyMessage(0);

                        answerFollowQuestion(expressStr.trim());

                    } else {
                        Message msg = handler_chat.obtainMessage();
                        msg.obj = expressStr;
                        handler.sendEmptyMessage(3);
                        handler_chat.handleMessage(msg);
                    }
                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
                    LoginUtil.startToLogin(mContext);
                }
            }
        });

        showMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bottomView.getVisibility() == View.VISIBLE) {
                    bottomView.setVisibility(View.GONE);
                } else {
                    bottomView.setVisibility(View.VISIBLE);
                }
            }
        });

        photoAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        takePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tmpPhotoFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            }
        });

    }

    public static void setExpandableListViewHeight(Context context,
                                                   ExpandableListView expandableListView) {

        ExpandableListAdapter listAdapter = expandableListView.getExpandableListAdapter();

        ViewGroup.LayoutParams params = expandableListView
                .getLayoutParams();

        if (listAdapter == null) {
            // pre -condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {

            View listGroupItem = listAdapter.getGroupView(i, true, null, expandableListView);
            listGroupItem.measure(0, 0);
            totalHeight += listGroupItem.getMeasuredHeight();

            for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                View listChildItem = listAdapter.getChildView(i, j, false, null, expandableListView);
                listChildItem.measure(0, 0);
                totalHeight += listChildItem.getMeasuredHeight();
            }

        }

        float f = context.getResources().getDisplayMetrics().density;
        totalHeight = (int) (totalHeight / 1.5 * f);
        params.height = totalHeight;
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }


    private void setListViewHeight(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        float f = mContext.getResources().getDisplayMetrics().density;
        params.height = (int) (params.height / 1.5 * f);

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void answerQuestion(final String answer, final int userType) {

        username = UserInfoManager.getInstance().getUserName();
        show = 3;

        ExeProtocol.exe(
                new AnswerQuesRequest(
                        String.valueOf(UserInfoManager.getInstance().getUserId()),
                        UserInfoManager.getInstance().getUserName(),
                        userType, qid, answer), new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        AnswerQuesResponse tr = (AnswerQuesResponse) bhr;
                        if ("1".equals(tr.result) || "0".equals(tr.result)) {
                            handler.sendEmptyMessage(4);
                            Message msg = handler_chat.obtainMessage();
                            msg.what = 1;
                            msg.arg1 = Integer.parseInt(tr.jiFen);
                            handler_chat.sendMessage(msg);
                            if (userType == 1) {
                                new GetChatDataTask().execute();
                            } else {
                                Log.e("iyuba", "提交--------");
                                new GetCommentDataTask().execute();
                            }
                        } else {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(2);
                            Message msg = handler_chat.obtainMessage();
                            msg.obj = answer;
                            msg.what = 4;
                            handler_chat.sendMessage(msg);
                        }
                    }

                    @Override
                    public void error() {
                        handler.sendEmptyMessage(4);
                        handler_chat.sendEmptyMessage(2);
                        Message msg = handler_chat.obtainMessage();
                        msg.obj = answer;
                        msg.what = 4;
                        handler_chat.sendMessage(msg);
                    }

                });
    }

    // 提交追问或者老师的追问回答
    public void answerFollowQuestion(final String answer) {

        ExeProtocol.exe(
                new AnswerFollowRequest(
                        String.valueOf(UserInfoManager.getInstance().getUserId()),
                        String.valueOf(answerType.aid)
                        , answer), new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        AnswerFollowResponse tr = (AnswerFollowResponse) bhr;
                        if ("1".equals(tr.result)) {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(1);
                            new GetChatDataTask().execute();
                        } else {
                            handler.sendEmptyMessage(4);
                            handler_chat.sendEmptyMessage(2);
                            Message msg = handler_chat.obtainMessage();
                            msg.obj = answer;
                            msg.what = 4;
                            handler_chat.sendMessage(msg);
                        }
                    }

                    @Override
                    public void error() {

                    }
                });

    }

    private void delAlertDialog(final String id, final int num, final int num2,
                                final String type) {
        AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle(R.string.alert_title);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage(mContext.getString(R.string.alert_delete));
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        ExeProtocol.exe(new DeleteAnswerQuesRequest(type, id,
                                        String.valueOf(UserInfoManager.getInstance().getUserId())),
                                new ProtocolResponse() {

                                    @Override
                                    public void finish(BaseHttpResponse bhr) {
                                        // TODO Auto-generated method stub
                                        DeleteAnswerQuesResponse tr = (DeleteAnswerQuesResponse) bhr;
                                        if (tr.result.equals("1")) {
                                            childChatList.get(num).remove(num2);

                                            handler.sendEmptyMessage(1);
                                            handler.sendEmptyMessage(8);
                                        } else {
                                            childChatList.get(num).remove(num2);

                                            handler.sendEmptyMessage(1);
                                            handler.sendEmptyMessage(8);
                                        }
                                    }

                                    @Override
                                    public void error() {
                                    }
                                });

                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                    }
                });
        alert.show();
    }

    Handler handler_chat = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.input_content);
                    break;
                case 1:
                    if (msg.arg1 == 0)
                        CustomToast.showToast(mContext, R.string.send_success);
                    else
                        CustomToast.showToast(mContext, R.string.send_success + "!积分增加" + String.valueOf(msg.arg1) + "分");
                    expressEditText.setText("");
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.send_fail);
                    break;
                case 3:
                    express.setText("");
                    break;
                case 4:
                    String text = (String) msg.obj;
                    express.setText(text);
                    break;
                case 5:
                    String answer = (String) msg.obj;
                    // answerQuestion(answer);
                    break;
            }
        }
    };

    // 发送回复语音
    class answerThread extends Thread {
        File file = null;

        @Override
        public void run() {
            super.run();
            try {

                // 用户名转码
                try {
                    username = UserInfoManager.getInstance().getUserName();
                    username = new String(username.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                username = TextAttr.encode(TextAttr.encode(TextAttr
                        .encode(username)));

                String uri = "http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
                        + "&format=json"
                        + "&authorid="
                        + uid
                        + "&username="
                        + username + "&questionid=" + qid + "&authortype=" + 1;
                Map<String, String> textParams = new HashMap<String, String>();
                Map<String, File> fileParams = new HashMap<String, File>();
                Log.e("iyuba",
                        "http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
                                + "&format=json" + "&authorid=" + uid
                                + "&username=" + username + "&questionid=" + qid
                                + "&authortype=" + 1);
                file = new File(Constant.voiceCommentAddr);
                fileParams.put("file1", file);

                if (file != null && file.exists()) {
                    UploadFile.post(uri, textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetChatDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    // 发送评论语音
    class CommentThread extends Thread {
        File file = null;

        @Override
        public void run() {
            super.run();
            try {

                // 用户名转码
                try {
                    username = UserInfoManager.getInstance().getUserName();
                    username = new String(username.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                username = TextAttr.encode(TextAttr.encode(TextAttr
                        .encode(username)));

                String uri = "http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
                        + "&format=json"
                        + "&authorid="
                        + uid
                        + "&username="
                        + username + "&questionid=" + qid + "&authortype=" + 2;

                Map<String, String> textParams = new HashMap<String, String>();
                Map<String, File> fileParams = new HashMap<String, File>();
                Log.e("iyuba",
                        "http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
                                + "&format=json" + "&authorid=" + uid
                                + "&username=" + username + "&questionid=" + qid
                                + "&authortype=" + 2);
                file = new File(Constant.voiceCommentAddr);
                fileParams.put("file1", file);

                if (file != null && file.exists()) {
                    UploadFile.post(uri, textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetCommentDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    // 发送语音追问
    public class UploadVoice extends Thread {
        File file = null;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            StringBuffer sb = new StringBuffer(
                    "http://www."+Constant.IYBHttpHead()+"/question/zaskQuestion.jsp?"
                            + "format=json"
                            + "&answerid="
                            + answerType.aid
                            + "&fromid="
                            + UserInfoManager.getInstance().getUserId()
                            + "&zcontent="
                            + TextAttr.encode(TextAttr.encode(TextAttr
                            .encode(""))));
            Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileParams = new HashMap<String, File>();
            file = new File(Constant.voiceCommentAddr);
            fileParams.put("file1", file);
            Log.e("iyuba", sb.toString());
            if (file != null && file.exists()) {
                try {
                    UploadFile.post(sb.toString(), textParams, fileParams,
                            new OperateCallBack() {

                                @Override
                                public void success(String message) {

                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(1);
                                    handler_sound.sendEmptyMessage(3);
                                    new GetChatDataTask().execute();
                                }

                                @Override
                                public void fail(String message) {
                                    handler.sendEmptyMessage(4);
                                    handler_chat.sendEmptyMessage(2);
                                    Message msg = handler_chat.obtainMessage();
                                    msg.what = 4;
                                    handler_chat.sendMessage(msg);// TODO
                                    // Auto-generated
                                    // method
                                    // stub
                                }
                            });
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
        }
    }

    private OnTouchListener voice_otl = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            if (v.getId() == R.id.voice_express) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.comment_speak_pressed));

                    amps[1] = getResources().getDrawable(R.drawable.amp1);
                    amps[2] = getResources().getDrawable(R.drawable.amp2);
                    amps[3] = getResources().getDrawable(R.drawable.amp3);
                    amps[4] = getResources().getDrawable(R.drawable.amp4);
                    amps[5] = getResources().getDrawable(R.drawable.amp5);
                    amps[6] = getResources().getDrawable(R.drawable.amp6);
                    amps[7] = getResources().getDrawable(R.drawable.amp7);

                    handler_sound.sendEmptyMessageDelayed(1, 500);

                    File file = new File(Constant.voiceCommentAddr);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    rManager = new RecordManager(file, voiceValue);
                    rManager.startRecord();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    v.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.comment_speak_normal));

                    handler_sound.removeMessages(1);
                    handler_sound.sendEmptyMessage(0);
                    rManager.stopRecord();
                }
            } else {
                handler_sound.removeMessages(1);
                handler_sound.sendEmptyMessage(0);
                rManager.stopRecord();
            }
            return true;
        }
    };

    Handler handler_sound = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    voiceView.setVisibility(View.GONE);
                    voiceListenButton.setVisibility(View.VISIBLE);

                    break;
                case 1:
                    voiceView.setVisibility(View.VISIBLE);

                    break;
                case 3:
                    voiceListenButton.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void init() {
        new GetChatDataTask().execute();
        new GetCommentDataTask().execute();
    }

    public void initQuestion() {
        // question = QuestionManager.getInstance().question;
        GitHubImageLoader.Instace(mContext)
                .setCirclePic(question.uid, userIcon);
        userName.setText(question.username + "");
        // questInfo.setText(question.time.substring(0,19)+ " " );

        question.time = question.time.substring(0, 19);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = sdf.parse(question.time).getTime();
            questInfo.setText(formatTime(time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        quesDisc.setText(question.question + "");
        if (question.questiondetail != null) {
            question.questiondetail = question.questiondetail.replace("null",
                    "");
            if (!question.questiondetail.equals("")) {
                questiondetail.setText(question.questiondetail);
                questiondetail.setVisibility(View.VISIBLE);
            }

        }
        if (question.img.equals("")) {
            quesPic.setVisibility(View.GONE);
        } else {
            quesPic.setVisibility(View.VISIBLE);
            // GitHubImageLoader.Instance(mContext).setPic("http://www."+Constant.IYBHttpHead+"/question/"+question.img,
            // quesPic, R.drawable.nearby_no_icon,0);

            ImageLoader.getInstance().displayImage("http://www."+Constant.IYBHttpHead()+"/question/" + question.img,
                    quesPic);

//			ImageLoader.getInstance().loadImage(
//					"http://www."+Constant.IYBHttpHead+"/question/" + question.img,
//					new SimpleImageLoadingListener() {
//
//						@Override
//						public void onLoadingComplete(String imageUri,
//								View view, Bitmap loadedImage) {
//							super.onLoadingComplete(imageUri, view, loadedImage);
//
//							LayoutParams para;
//							para = quesPic.getLayoutParams();
//							int height = loadedImage.getHeight();
//							int width = loadedImage.getWidth();
//							float f = mContext.getResources()
//									.getDisplayMetrics().density;
//							Log.e("2-----", "" + width);
//							float bit = width / 200;
//							height = (int) (height / bit);
//							width = (int) (200 / 1.5 * f);
//							height = (int) (height / 1.5 * f);
//							para.height = height;
//							para.width = width;
//							quesPic.setLayoutParams(para);
//							quesPic.setImageBitmap(loadedImage);
//						}
//					});

        }
    }

    private String formatTime(long time) {
        Date date = new Date(time);
        Date date2 = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar.setTime(date);
        calendar2.setTime(date2);

        if (calendar2.get(Calendar.DAY_OF_YEAR)
                - calendar.get(Calendar.DAY_OF_YEAR) > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        } else if (System.currentTimeMillis() - time > 60 * 60 * 1000) {
            return (System.currentTimeMillis() - time) / (60 * 60 * 1000)
                    + "小时之前";
        } else if (System.currentTimeMillis() - time > 60 * 1000) {
            return (System.currentTimeMillis() - time) / (60 * 1000) + "分钟之前";
        } else if (System.currentTimeMillis() - time > 60) {

            return (System.currentTimeMillis() - time) / (1000) + "秒之前";

        } else if (System.currentTimeMillis() - time == 0) {
            return "1秒之前";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.send_success);
                    break;
                case 1:
                    // 判断此老师如果 没有回复此问题则他只能

                    // if(answerType.isanswer==0&&AccountManager.Instance(mContext).isteacher.equals("1")&&!question.uid.equals(AccountManager.Instance(mContext).userId)){
                    // expressEditText.setHint("回复问题");
                    // answerType.sub=1;
                    // }

                    if (chatAdapter != null) {
                        chatAdapter.notifyDataSetChanged();

                        setExpandableListViewHeight(mContext, chatListview);
                    }

                    Message msg2 = handler.obtainMessage();
                    msg2.what = 23;
                    handler.sendMessageDelayed(msg2, 100);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    break;
                case 2:
                    if (commentListAdapter != null) {
                        studentTag.setVisibility(View.VISIBLE);
                        studentTag.setText("讨论区(" + commentList.size() + ")");
                        commentListview.setAdapter(commentListAdapter);
                        commentListAdapter.notifyDataSetChanged();
                        ViewGroup.LayoutParams params = commentListview
                                .getLayoutParams();

                        int height = 0;
                        height = 160 * commentList.size();
                        float f = mContext.getResources().getDisplayMetrics().density;
                        height = (int) (height / 1.5 * f);
                        params.height = height;
                        commentListview.setLayoutParams(params);
                        Message msg1 = handler.obtainMessage();
                        msg1.what = 22;
                        handler.sendMessageDelayed(msg1, 100);

                    }
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    break;
                case 3:
                    waitDialog.show();
                    break;
                case 30:
                    commentListAdapter = new CommentListAdapter(mContext,
                            commentList);
                    break;
                case 31:
                    commentView.setVisibility(View.VISIBLE);
                    break;
                case 32:
                    commentView.setVisibility(View.GONE);
                    break;
                case 4:
                    if (show >= 2)
                        waitDialog.dismiss();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部
                    // scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.network_error);
                    break;
                case 35:
                    CustomToast.showToast(mContext, "问题已被删除!");
                    finish();
                    break;
                case 6:
                    // chatView.setVisibility(View.GONE);
                    break;
                case 7:
                    chatView.setVisibility(View.VISIBLE);
                    teacherTag.setVisibility(View.VISIBLE);
                    teacherTag.setText("老师(" + groupChatList.size() + ")");
                    chatAdapter = new ChatAdapter(mContext, groupChatList,
                            childChatList, expressEditText, answerType);
                    chatListview.setAdapter(chatAdapter);

//				setListViewHeightBasedOnChildren(chatListview);

                case 8:
                    initChatList();
                    break;
                case 21:
                    initQuestion();
                    break;
                case 22:
                    int ch = 0;
                    for (int cl = 0; cl < commentList.size(); cl++) {

                        if (commentListview.getChildAt(cl) != null) {
                            ch = ch + commentListview.getChildAt(cl).getHeight();
                        }
                    }
                    ViewGroup.LayoutParams params = commentListview
                            .getLayoutParams();

                    params.height = ch + 20;
                    commentListview.setLayoutParams(params);
                    break;

                case 23:
                    // CustomToast.showToast(mContext,
                    // "881---"+answerType.isanswer);
                    if (answerType.isanswer == 0
                            && UserInfoManager.getInstance().getOtherData().getIsteacher().equals("1")
                            && !question.uid.equals(String.valueOf(UserInfoManager.getInstance().getUserId()))) {
                        expressEditText.setHint("回复问题");
                        answerType.sub = 1;
                    } else {

                        if (expressEditText.getText().toString().trim().equals("")) {
                            expressEditText.setHint("评论");
                            answerType.sub = 0;
                        }
                    }
                    float f = mContext.getResources().getDisplayMetrics().density;
                    int it = (int) (250 / 1.5 * f);
                    int sh = 0;
                    for (int cl = 0; cl < childChatList.size(); cl++) {
                        sh = sh + chatListview.getChildAt(cl).getHeight() + it;
                        // CustomToast.showToast(mContext, sh);

                        Log.e("iyuba", "" + chatListview.getChildAt(cl).getHeight());

                    }
                    ViewGroup.LayoutParams paramss = chatListview.getLayoutParams();

                    break;
            }
        }
    };

    public void getChatList() {
        ExeProtocol.exe(new GetChatListRequest(qid), new ProtocolResponse() {

            @Override
            public void finish(BaseHttpResponse bhr) {
                // TODO Auto-generated method stub
                GetChatListResponse tr = (GetChatListResponse) bhr;
                show++;// 加载完毕值加1
                if ("1".equals(tr.result)) {
                    // 获取问题内容并使其显示
                    question = tr.item;
                    QuestionManager.getInstance().question = question;
                    handler.sendEmptyMessage(21);
                    childChatList = tr.chatList;
                    groupChatList = tr.infoList;

                    if (groupChatList.size() != 0) {
                        handler.sendEmptyMessage(7);
                        handler.sendEmptyMessage(8);
                    } else {
                        handler.sendEmptyMessage(6);
                    }

                    handler.sendEmptyMessage(1);
                    handler.sendEmptyMessage(4);
                } else {

                    handler.sendEmptyMessage(4);
                    handler.sendEmptyMessage(35);
                }
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessage(35);
            }
        });
    }

    public void getCommentList() {
        ExeProtocol.exe(new GetCommentListRequest(qid), new ProtocolResponse() {

            @Override
            public void finish(BaseHttpResponse bhr) {
                // TODO Auto-generated method stub

                GetCommentListResponse tr = (GetCommentListResponse) bhr;
                show++;// 加载完毕值加1

                if ("1".equals(tr.result)) {
                    commentList = tr.infoList;
                    if (commentList.size() != 0) {

                        handler.sendEmptyMessage(31);
                        handler.sendEmptyMessage(30);
                        // commentListAdapter = new CommentListAdapter(mContext,
                        // commentList);

                    } else {
                        handler.sendEmptyMessage(32);
                    }

                    handler.sendEmptyMessage(2);
                    handler.sendEmptyMessage(4);
                } else {
                    handler.sendEmptyMessage(4);
                    handler.sendEmptyMessage(35);
                }

            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessage(35);
            }
        });
    }

    // 获取老师回答和追问
    private class GetChatDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            handler.sendEmptyMessage(3);
            getChatList();
            return null;
        }
    }

    // 获取评论
    private class GetCommentDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            handler.sendEmptyMessage(3);
            getCommentList();
            return null;
        }
    }

    public void initChatList() {
        chatListview.setGroupIndicator(null);
        for (int i = 0; i < groupChatList.size(); i++) {
            chatListview.expandGroup(i);
            setExpandableListViewHeight(mContext, chatListview);
        }
    }

    private void switchToText() {
        findViewById(R.id.voicebutton).setVisibility(View.GONE);
        findViewById(R.id.edittext).setVisibility(View.VISIBLE);
        // 显示软键盘
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.showSoftInput(express, InputMethodManager.RESULT_SHOWN);

        setCommentModeButton
                .setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
        commentMode = 0;
    }
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void switchToVoice() {
        findViewById(R.id.voicebutton).setVisibility(View.VISIBLE);
        findViewById(R.id.edittext).setVisibility(View.GONE);
        // 关闭软键盘
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(express.getWindowToken(), 0);
        setCommentModeButton
                .setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
        commentMode = 1;
    }

    private OnClickListener ocl_comment = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            show = 2;
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case 2:
                    if (commentMode == 1) {
                        switchToText();
                    } else {

                        if (Build.VERSION.SDK_INT >= 23) {
                            QuesDetailActivityPermissionsDispatcher.switchToVoiceWithPermissionCheck(QuesDetailActivity.this);
                        } else {

                            switchToVoice();
                        }
                    }
                    break;
                case 4:
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                    }
                    mPlayer.reset();
                    mPlayer.playUrl(Constant.voiceCommentAddr);
                    break;

                case 1:
                    if (UserInfoManager.getInstance().isLogin()) {

                        if (answerType.sub == 0) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入评论！");
                                    expressEditText.setText("");
                                } else {

                                    answerQuestion(expressionInput.toString()
                                            .toString(), 2);
                                }
                            } else {
                                // 发送语音
                                // new UploadVoice().start();

                            }
                        } else if (answerType.sub == 1) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入回复！");
                                    expressEditText.setText("");
                                } else {

                                    answerQuestion(expressionInput.toString()
                                            .toString(), 1);
                                }
                            } else {
                                // 发送语音
                                new answerThread().start();
                            }
                        } else if (answerType.sub == 2) {
                            if (commentMode == 0) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(
                                        express.getWindowToken(), 0);
                                String expressionInput = expressEditText.getText()
                                        .toString();
                                if (expressionInput.toString().trim().equals("")) {
                                    CustomToast.showToast(mContext, "请输入内容！");
                                    expressEditText.setText("");
                                } else {
                                    if (expressionInput.toString().trim().length() > 100) {
                                        CustomToast.showToast(mContext,
                                                "最多只能输入100字，已超啦!");
                                        return;
                                    }
                                    // answerQuestion(expressionInput.toString().toString());
                                    answerFollowQuestion(expressionInput.toString()
                                            .trim());
                                }
                            } else {
                                // 发送语音
                                new UploadVoice().start();
                                // new UploadFollow().start();

                            }
                        }

                    } else {
//                        Intent intent = new Intent();
//                        intent.setClass(mContext, Login.class);
//                        startActivity(intent);
                        LoginUtil.startToLogin(mContext);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            // 触摸屏幕时候关闭软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(express.getWindowToken(), 0);

            // 判断此老师如果 没有回复此问题则他只能回复问题
            if (answerType.isanswer == 0
                    && UserInfoManager.getInstance().getOtherData().getIsteacher().equals("1")
                    && !question.uid.equals(String.valueOf(UserInfoManager.getInstance().getUserId()))) {
                expressEditText.setHint("回复问题");
                answerType.sub = 1;
            } else {

                if (expressEditText.getText().toString().trim().equals("")) {
                    expressEditText.setHint("评论");
                    answerType.sub = 0;
                    switchToText();
                }
            }
        }
        return true;
    }


    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    public void recordSoundDenied() {
        CustomToast.showToast(QuesDetailActivity.this, "录音权限未打开，打开后即可正常使用此功能");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        QuesDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
