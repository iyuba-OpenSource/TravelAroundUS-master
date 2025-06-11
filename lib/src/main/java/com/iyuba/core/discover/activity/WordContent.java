package com.iyuba.core.discover.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.DictRequest;
import com.iyuba.core.common.protocol.base.DictResponse;
import com.iyuba.core.common.protocol.news.WordUpdateRequest;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.sqlite.op.EGDBOp;
import com.iyuba.core.common.sqlite.op.WordDBOp;
import com.iyuba.core.common.sqlite.op.WordOp;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.lib.R;

/**
 * 单词内容界面
 *
 * @author chentong
 * @version 1.0
 * @para "word"传入单词值
 */
public class WordContent extends BasisActivity {
    private Context mContext;
    private Button backBtn, saveBtn;
    private String appointWord;
    private boolean isAddCollect;
    private TextView key, pron, def, example;
    private Word curWord;
    private ImageView speaker;
    private boolean fromHtml;
    private CustomDialog waittingDialog;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        mContext = this;
        CrashApplication.getInstance().addActivity(this);
        waittingDialog = WaittingDialog.showDialog(mContext);
        player = new Player(mContext, null);

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        saveBtn = (Button) findViewById(R.id.word_add);

        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                saveNewWords();
            }
        });
        appointWord = this.getIntent().getStringExtra("word");
        isAddCollect = this.getIntent().getBooleanExtra("isCollect", true);
        if (!isAddCollect) {
            saveBtn.setVisibility(View.GONE);
        }
        initGetWordMenu();
        handler.sendEmptyMessage(0);
    }

    private void initGetWordMenu() {
        key = (TextView) findViewById(R.id.word_key);
        pron = (TextView) findViewById(R.id.word_pron);
        def = (TextView) findViewById(R.id.word_def);
        example = (TextView) findViewById(R.id.example);
        speaker = (ImageView) findViewById(R.id.word_speaker);
        curWord = new WordDBOp(mContext).findDataByKey(appointWord);
        if (curWord != null) {
            curWord.examples = new EGDBOp(mContext).findData(curWord.key);
            handler.sendEmptyMessage(2);
        }

        Log.e("mdg", "mnjnhkcnmlgbqnmlgbcnmlgbmdgismydoge");
        getNetworkInterpretation();
    }

    /**
     * 获取单词释义
     */
    private void getNetworkInterpretation() {
        if (appointWord != null && appointWord.length() != 0) {
            ExeProtocol.exe(new DictRequest(appointWord),
                    new ProtocolResponse() {
                        @Override
                        public void finish(BaseHttpResponse bhr) {
                            // TODO Auto-generated method stub
                            DictResponse dictResponse = (DictResponse) bhr;
                            fromHtml = true;
                            curWord = dictResponse.word;
                            handler.sendEmptyMessage(2);
                        }

                        @Override
                        public void error() {
                            // TODO Auto-generated method stub
                            handler.sendEmptyMessage(5);
                        }
                    });
        } else {
            handler.sendEmptyMessage(4);
        }
        Log.e("qnmlgb", "cnm-mdg");
    }

    /**
     * 显示单词释义
     */
    private void showWordDefInfo() {
        key.setText(curWord.key);
        if (curWord.pron != null && curWord.pron.length() != 0) {
            if (fromHtml) {
                pron.setText(Html.fromHtml("[" + curWord.pron + "]"));
            } else {
                pron.setText(TextAttr.decode("[" + curWord.pron + "]"));
            }
            Log.e("mgdg", "omg,mdg.imdg.cnmlgbwcnmlgb");
        }
        def.setText(curWord.def);
        if (curWord.examples != null && curWord.examples.length() != 0) {
            example.setText(Html.fromHtml(curWord.examples));
        } else {
            example.setText(R.string.no_word_example);
        }
        if (curWord.audioUrl != null && curWord.audioUrl.length() != 0) {
            speaker.setVisibility(View.VISIBLE);
        } else {
            speaker.setVisibility(View.GONE);
        }
        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //player = new Player(mContext, null);
                String url = curWord.audioUrl;
                player.playUrl(url);
            }
        });
        handler.sendEmptyMessage(1);
    }

    /**
     * 保存单词值单词本
     */
    private void saveNewWords() {
        if (!UserInfoManager.getInstance().isLogin()) {
//            Intent intent = new Intent();
//            intent.setClass(mContext, Login.class);
//            mContext.startActivity(intent);
            LoginUtil.startToLogin(mContext);
        } else {
            curWord.userId = String.valueOf(UserInfoManager.getInstance().getUserId());
            handler.sendEmptyMessage(3);
            addNetwordWord(curWord.key);
        }
    }

    /**
     * 同步网络生词库
     */
    private void addNetwordWord(String wordTemp) {
        ExeProtocol.exe(new WordUpdateRequest(
                        String.valueOf(UserInfoManager.getInstance().getUserId()),
                        WordUpdateRequest.MODE_INSERT, wordTemp),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        // TODO Auto-generated method stub
                        onBackPressed();
                    }

                    @Override
                    public void error() {
                        // TODO Auto-generated method stub
                        handler.sendEmptyMessage(5);
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    waittingDialog.show();
                    break;
                case 1:
                    waittingDialog.dismiss();
                    break;
                case 2:
                    showWordDefInfo();
                    break;
                case 3:
                    new WordOp(mContext).saveData(curWord);
                    CustomToast.showToast(mContext,
                            R.string.play_ins_new_word_success);
                    break;
                case 4:
                    CustomToast.showToast(mContext,
                            R.string.play_please_take_the_word);
                    break;
                case 5:
                    handler.sendEmptyMessage(1);
                    CustomToast.showToast(mContext,
                            mContext.getString(R.string.action_fail) + "\n"
                                    + mContext.getString(R.string.check_network));
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        fromHtml = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();//release();
    }
}
