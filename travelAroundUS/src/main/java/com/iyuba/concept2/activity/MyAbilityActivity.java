//package com.iyuba.concept2.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import androidx.appcompat.app.AppCompatActivity;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.iyuba.concept2.R;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.GradeRequest;
//import com.iyuba.core.common.protocol.base.GradeResponse;
//import com.iyuba.core.common.protocol.message.RequestBasicUserInfo;
//import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
//import com.iyuba.core.common.protocol.message.ResponseBasicUserInfo;
//import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
//import com.iyuba.core.common.sqlite.mode.UserInfo;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.me.sqlite.mode.EditUserInfo;
//import com.iyuba.core.teacher.activity.QuesListActivity;
//import com.iyuba.core.teacher.activity.QuestionNotice;
//import com.iyuba.core.teacher.activity.TheQuesListActivity;
//import com.iyuba.module.intelligence.ui.LearnResultActivity;
//import com.iyuba.module.intelligence.ui.LearningGoalActivity;
//import com.iyuba.module.intelligence.ui.TestResultActivity;
//import com.iyuba.module.intelligence.ui.WordResultActivity;
//import com.umeng.analytics.MobclickAgent;
//
//public class MyAbilityActivity extends AppCompatActivity {
//
//    private RelativeLayout discover_rqlist, discover_qnotice, discover_myq, discover_mysub;
//    private RelativeLayout intel_learn_goal, intel_userinfo, intel_result;
//    private RelativeLayout intel_word_result, intel_test_result, intel_ability_test;
//    private ImageButton mIBBack;
//    private Context mContext;
//    private boolean infoFlag = false, levelFlag = false;
//    private ResponseUserDetailInfo userDetailInfo;
//    private UserInfo userInfo;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_ability);
//
//        mContext = this;
//        initView();
//        initClick();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(mContext);
//        handler.sendEmptyMessage(6);
//        viewChange();
//    }
//
//    private void initView() {
//        discover_rqlist = findViewById(R.id.discover_rqlist);
//        discover_qnotice = findViewById(R.id.discover_qnotice);
//        discover_myq = findViewById(R.id.discover_myq);
//        discover_mysub = findViewById(R.id.discover_mysub);
//
//        if (AccountManager.Instance(mContext).isteacher.equals("1")) {
//            discover_myq.setVisibility(View.GONE);
//            discover_mysub.setVisibility(View.VISIBLE);
//        } else {
//            discover_myq.setVisibility(View.VISIBLE);
//            discover_mysub.setVisibility(View.GONE);
//        }
//
//        intel_userinfo = findViewById(R.id.intel_userinfo);
//        intel_learn_goal = findViewById(R.id.intel_goal);
//        intel_result = findViewById(R.id.intel_result);
//        intel_word_result = findViewById(R.id.intel_word_result);
//        intel_test_result = findViewById(R.id.intel_test_result);
//        intel_ability_test = findViewById(R.id.intel_ability_test);
//        mIBBack = findViewById(R.id.titlebar_back_button);
//    }
//
//    private void initClick() {
//        discover_rqlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext, QuesListActivity.class));
//            }
//        });
//        discover_qnotice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext, QuestionNotice.class));
//            }
//        });
//
//        discover_myq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                LogUtils.e("medugo", "iasigb");
//                intent.setClass(mContext, TheQuesListActivity.class);
//                intent.putExtra("utype", "4");
//                startActivity(intent);
//            }
//        });
//
//        discover_mysub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, TheQuesListActivity.class);
//                intent.putExtra("utype", "2");
//                startActivity(intent);
//            }
//        });
//
//        intel_userinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext, InfoFullFillActivity.class));
//            }
//        });
//
//        intel_learn_goal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = LearningGoalActivity.buildIntent(mContext);
//                startActivity(intent);
//            }
//        });
//
//
//        intel_result.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check()) {
//                    Intent intent = LearnResultActivity.buildIntent(mContext);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        intel_word_result.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check()) {
//                    Intent intent = WordResultActivity.buildIntent(mContext);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        intel_test_result.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check()) {
//                    Intent intent = TestResultActivity.buildIntent(mContext);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        intel_ability_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, AbilityMapActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(mContext, "成为VIP用户即可使用智慧化评测功能！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        mIBBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    private void viewChange() {
//        if (checkLogin()) {
//            loadData();
//        }
//    }
//
//    private void loadData() {
//        final String id = AccountManager.Instance(mContext).userId;
//        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
//                new ProtocolResponse() {
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
//                        userInfo = response.userInfo;
//                        AccountManager.Instance(mContext).userInfo = userInfo;
//                        handler.sendEmptyMessage(3);
//                        Looper.prepare();
//                        ExeProtocol.exe(new GradeRequest(id),
//                                new ProtocolResponse() {
//                                    @Override
//                                    public void finish(BaseHttpResponse bhr) {
//                                        GradeResponse response = (GradeResponse) bhr;
//                                        userInfo.studytime = Integer
//                                                .parseInt(response.totalTime);
//                                        userInfo.position = response.positionByTime;
//                                    }
//
//                                    @Override
//                                    public void error() {
//                                        handler.sendEmptyMessage(0);
//                                    }
//                                });
//                        Looper.loop();
//                    }
//
//                    @Override
//                    public void error() {
//                    }
//                });
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(final Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    CustomToast.showToast(mContext, R.string.check_network);
//                    break;
//                case 6:
//                    infoFlag = false;
//                    levelFlag = false;
//                    ExeProtocol.exe(
//                            new RequestUserDetailInfo(AccountManager
//                                    .Instance(mContext).userId),
//                            new ProtocolResponse() {
//                                @Override
//                                public void finish(BaseHttpResponse bhr) {
//                                    ResponseUserDetailInfo responseUserDetailInfo =
//                                            (ResponseUserDetailInfo) bhr;
//                                    if (responseUserDetailInfo.result.equals("211")) {
//                                        userDetailInfo = responseUserDetailInfo;
//                                        if (userDetailInfo.gender.isEmpty()
//                                                || userDetailInfo.birthday
//                                                .isEmpty()
//                                                || userDetailInfo.resideLocation
//                                                .isEmpty()
//                                                || userDetailInfo.occupation
//                                                .isEmpty()
//                                                || userDetailInfo.education
//                                                .isEmpty()
//                                                || userDetailInfo.graduateschool
//                                                .isEmpty()) {
//                                            infoFlag = true;// 有为空的用户信息
//
//                                        }
//                                        EditUserInfo editUserInfo = userDetailInfo.editUserInfo;
//                                        if (editUserInfo.getPlevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getPlevel()) <= 0
//                                                || editUserInfo.getPtalklevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getPtalklevel()) <= 0
//                                                || editUserInfo.getPreadlevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getPreadlevel()) <= 0
//                                                || editUserInfo.getGlevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getGlevel()) <= 0
//                                                || editUserInfo.getGtalklevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getGtalklevel()) <= 0
//                                                || editUserInfo.getGreadlevel().isEmpty()
//                                                || Integer
//                                                .valueOf(editUserInfo.getGreadlevel()) <= 0) {
//                                            levelFlag = true;// 有为空的用户等级信息
//                                            LogUtils.e("数据错误"+editUserInfo.getPlevel()
//                                                    +" "+editUserInfo.getPtalklevel()
//                                                    +" "+editUserInfo.getPreadlevel()
//                                                    +" "+editUserInfo.getGlevel()
//                                                    +" "+editUserInfo.getGtalklevel()
//                                                    +" "+editUserInfo.getGreadlevel());
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void error() {
//                                }
//                            });
//                    break;
//            }
//        }
//    };
//
//    private boolean check() {
//        if (infoFlag) {
//            Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        } else if (levelFlag) {
//            Toast toast = Toast.makeText(mContext, "请先完善学习目标信息", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        } else {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkLogin() {
//        if ("".equals(AccountManager.Instance(mContext).userName) ||
//                AccountManager.Instance(mContext).userId == null || "".equals(AccountManager.Instance(mContext).userId)
//                || "0".equals(AccountManager.Instance(mContext).userId))
//            AccountManager.Instance(mContext).islinshi = false;
//        return AccountManager.Instance(mContext).checkUserLogin();
//    }
//}
