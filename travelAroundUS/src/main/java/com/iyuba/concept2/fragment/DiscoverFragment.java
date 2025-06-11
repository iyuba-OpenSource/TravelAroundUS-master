///*
// * 文件名
// * 包含类名列表
// * 版本信息，版本号
// * 创建日期
// * 版权声明
// */
//package com.iyuba.concept2.fragment;
//
//import static com.youdao.sdk.common.YoudaoSDK.getApplicationContext;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.SpannableString;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//import com.facebook.stetho.common.LogUtil;
//import com.iyuba.concept2.R;
//import com.iyuba.concept2.activity.MyAbilityActivity;
//import com.iyuba.concept2.activity.SendBookActivity;
//import com.iyuba.concept2.activity.SendBookNovelActivity;
//import com.iyuba.concept2.lil.manager.AbilityControlManager;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Login;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.sqlite.db.Emotion;
//import com.iyuba.core.common.util.AdTimeUtils;
//import com.iyuba.core.common.util.Expression;
//import com.iyuba.core.common.util.MD5;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.discover.activity.FriendCircFreshListActivity;
//import com.iyuba.core.discover.activity.Saying;
//import com.iyuba.core.discover.activity.SearchFriend;
//import com.iyuba.core.discover.activity.WordCollection;
//import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
//import com.iyuba.core.me.activity.WriteState;
//import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
//import com.iyuba.core.search.activity.SearchActivity;
//import com.iyuba.core.teacher.activity.FindTeacherActivity;
//import com.iyuba.core.teacher.activity.TeacherActivity;
//import com.iyuba.core.teacher.activity.TeacherBaseInfo;
//import com.iyuba.headlinelibrary.HeadlineType;
//import com.iyuba.headlinelibrary.IHeadlineManager;
//import com.iyuba.headlinelibrary.data.model.Headline;
//import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
//import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
//import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
//import com.iyuba.imooclib.ui.content.ContentActivity;
//import com.iyuba.module.headlinesearch.event.HeadlineSearchItemEvent;
//import com.iyuba.module.headlinesearch.ui.MSearchActivity;
//import com.iyuba.module.movies.IMovies;
//import com.iyuba.module.movies.event.IMovieGoVipCenterEvent;
//import com.iyuba.module.movies.ui.movie.MovieActivity;
//import com.iyuba.module.user.IyuUserManager;
//import com.iyuba.module.user.User;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//
///**
// * 类名 发现 碎片  2019.3.25 正在使用
// *
// * @author 作者 <br/>
// *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
// */
//public class DiscoverFragment extends Fragment {
//    private Context mContext;
//    private View quesAns, headline, news, exam, mob, all, searchWord, findFriend,
//            vibrate, collectWord, saying, back, discover_search_teacher,
//            discover_search_certeacher, discover_search_circlefriend, latestAc,
//            exGiftAc, discover_gift, search_by_search, morelayout, watchwatch;
//    private RelativeLayout stateView, mRlMyAbility, me_send_book,me_send_book_novel;
//    private View root;
//    private TextView moretext;
//    private TextView mState;
//
//    private ImageView ivLine1;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = getActivity();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.discover_main, container, false);
//        initWidget();
//        initCommonData();
//        EventBus.getDefault().register(this);
//        return root;
//    }
//
//    private void initCommonData() {
//        //这里根据登录状态进行存储
//        if (AccountManager.getInstance().checkUserLogin()){
//            User user = new User();
//            AccountManager managerLib =AccountManager.Instance(mContext);
//            user.vipStatus = String.valueOf(managerLib.getVipStatus());
//            user.uid = managerLib.getUserId();
//            user.name = managerLib.userName;
//            if (AdTimeUtils.isTime())
//                user.vipStatus = "1";
//            IyuUserManager.getInstance().setCurrentUser(user);
//        }
//
//    }
//
//    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//    private String showadtime = "2018-02-13 12:00:00";
//
//    private void initWidget() {
//
//        me_send_book = root.findViewById(R.id.me_send_book);
//        me_send_book_novel = root.findViewById(R.id.me_send_book_novel);
//        me_send_book.setOnClickListener(ocl);
//        me_send_book_novel.setOnClickListener(ocl);
//        mRlMyAbility = root.findViewById(R.id.rl_my_ability);
//        mRlMyAbility.setOnClickListener(v -> {
//            if (AccountManager.Instance(mContext).isLogin()) {
//                startActivity(new Intent(getContext(), MyAbilityActivity.class));
//            } else {
//                ToastUtil.showToast(getContext(), "请先登录！");
//            }
//        });
//        back = root.findViewById(R.id.button_back);
//        back.setOnClickListener(v->{
//            getActivity().finish();
//        });
//        headline = root.findViewById(R.id.headline);
//        headline.setOnClickListener(ocl);
////        news = root.findViewById(R.id.news);
////        news.setOnClickListener(ocl);
////        exam = root.findViewById(R.id.exam);
////        exam.setOnClickListener(ocl);
//        mob = root.findViewById(R.id.mob);
//        mob.setOnClickListener(ocl);
//        quesAns = root.findViewById(R.id.ans_ques);
//        quesAns.setOnClickListener(ocl);
//        if (Constant.APPID.equals("238")) {
//            mob.setVisibility(View.GONE);
//        }
//        morelayout = root.findViewById(R.id.more_layout);
//        moretext = (TextView) root.findViewById(R.id.more_text);
//        morelayout.setVisibility(View.GONE);
//        moretext.setVisibility(View.GONE);
//        //long time = System.currentTimeMillis();
//       //
//        // Log.e("Tag", "--bgtime--" + time);
//        //       Date date = null;
////        try {
////            date = sdf2.parse(showadtime);
////            long thetime = date.getTime();
////            Log.e("show-time", date.getTime() + "");
////            if (time < thetime) {
////                moretext.setVisibility(View.GONE);
////                morelayout.setVisibility(View.GONE);
////            } else {
////                moretext.setVisibility(View.VISIBLE);
////                morelayout.setVisibility(View.VISIBLE);
////            }
////        } catch (ParseException e) {
////            e.printStackTrace();
////            moretext.setVisibility(View.VISIBLE);
////            morelayout.setVisibility(View.VISIBLE);
////        }
//        watchwatch = root.findViewById(R.id.watch_watch);
//        watchwatch.setOnClickListener(ocl);
//        search_by_search = root.findViewById(R.id.search_search);
//        search_by_search.setOnClickListener(ocl);
//        discover_gift = root.findViewById(R.id.discover_gift);
//        discover_gift.setOnClickListener(ocl);
//        all = root.findViewById(R.id.all);
//        all.setOnClickListener(ocl);
//        searchWord = root.findViewById(R.id.search_word);
//        searchWord.setOnClickListener(ocl);
//        saying = root.findViewById(R.id.saying);
//        saying.setOnClickListener(ocl);
//        collectWord = root.findViewById(R.id.collect_word);
//        collectWord.setOnClickListener(ocl);
//        findFriend = root.findViewById(R.id.discover_search_friend);
//        discover_search_certeacher = root
//                .findViewById(R.id.discover_search_certeacher);
//        discover_search_circlefriend = root
//                .findViewById(R.id.discover_search_circlefriend);
//        discover_search_teacher = root
//                .findViewById(R.id.discover_search_teacher);
//
//        stateView = root.findViewById(R.id.me_state_change);//个性签名
////        messageView = root.findViewById(R.id.me_message);//消息中心
//        stateView.setOnClickListener(ocl);
////        messageView.setOnClickListener(ocl);
//
//        mState = root.findViewById(R.id.me_state);
//
//        discover_search_circlefriend.setOnClickListener(ocl);
//        discover_search_certeacher.setOnClickListener(ocl);
//        discover_search_teacher.setOnClickListener(ocl);
//        findFriend.setOnClickListener(ocl);
//        vibrate = root.findViewById(R.id.discover_vibrate);
//        vibrate.setOnClickListener(ocl);
//
//        ivLine1 = root.findViewById(R.id.line_studys);
//
//        //根据接口判断是否出现入口
//        /*if (AccountManager.getInstance().isMocShow()){
//            if(getActivity().getPackageName().equals("com.iyuba.concept2")){
//                setMocShow(true);
//            }else {
//                setMocShow(false);
//            }
//        }else {
//            setMocShow(false);
//        }*/
//        //放出慕课（上面可以控制慕课）
//        setMocShow(true);
//
//        Log.e("bao",getActivity().getPackageName());
//        latestAc = root.findViewById(R.id.discover_latest_activity);
//        latestAc.setOnClickListener(ocl);
//        exGiftAc = root.findViewById(R.id.discover_exchange_gift);
//        exGiftAc.setOnClickListener(ocl);
//
//        if (AccountManager.Instance(mContext).isteacher.equals("1")) {
//            discover_search_certeacher.setVisibility(View.VISIBLE);
//
//        } else {
//            discover_search_certeacher.setVisibility(View.VISIBLE);
//
//        }
//
//    }
//
//    private OnClickListener ocl = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent;
//            int id = v.getId();
//            if (id == R.id.watch_watch) {
//
//                //看一看
//                IMovies.init(getApplicationContext(),Constant.APPID,Constant.APPType);
//                startActivity(new Intent(mContext, MovieActivity.class));
//            }
//            if (id == R.id.ans_ques) {  //问一问
////                HeadlinesRuntimeManager.setApplicationContext(this);
////                startActivity(MainHeadlinesActivity.getIntent2Me(context,uid,appId,appName));
//                intent = new Intent(mContext, TeacherActivity.class);
//                startActivity(intent);
//            }
//            if (id == R.id.headline) {
////                intent = new Intent(mContext, HeadlineActivity.class);
////                startActivity(intent);
//            } else if (id == R.id.me_send_book) {
//                startActivity(new Intent(mContext, SendBookActivity.class));
//            } else if (id == R.id.me_send_book_novel) {
//                startActivity(new Intent(mContext, SendBookNovelActivity.class));
//            } else if (id == R.id.mob) {
//                intent = new Intent(mContext, SimpleMobClassList.class);
//                intent.putExtra("title", R.string.discover_mobclass);
//                startActivity(intent);
//            } else if (id == R.id.all) {
//                intent = new Intent();
//                intent.setClass(mContext, Web.class);
//                intent.putExtra("url", "http://app."+Constant.IYBHttpHead()+"/android");
//                intent.putExtra("title",
//                        mContext.getString(R.string.discover_appall));
//                startActivity(intent);
//            } else if (id == R.id.search_word) {//查一查
//
//                intent = new Intent();
//                intent.setClass(mContext, SearchActivity.class);//SearchWord 查单词改版
//                startActivity(intent);
//
//            } else if (id == R.id.saying) {
//                intent = new Intent();
//                intent.setClass(mContext, Saying.class);
//                startActivity(intent);
//            } else if (id == R.id.collect_word) {
//                if (AccountManager.Instance(mContext).checkUserLogin()&&!AccountManager.Instance(mContext).islinshi) {
//                    intent = new Intent();
//                    intent.setClass(mContext, WordCollection.class);//单词本
//                    startActivity(intent);
//                }else {
//                    ToastUtil.showToast(mContext,"请先登录！");
//                }
//            } else if (id == R.id.discover_search_friend) {
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    intent = new Intent();
//                    intent.setClass(mContext, SearchFriend.class);
//                    startActivity(intent);
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//                }
//            } else if (id == R.id.discover_search_teacher) {
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    intent = new Intent();
//                    intent.setClass(mContext, FindTeacherActivity.class);
//                    startActivity(intent);
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, FindTeacherActivity.class);
//                    startActivity(intent);
//                }
//            } else if (id == R.id.discover_search_certeacher) {
//                if (AccountManager.Instance(mContext).checkUserLogin()
//                        && !AccountManager.Instance(mContext).islinshi) {
//                    intent = new Intent();
//                    intent.setClass(mContext, TeacherBaseInfo.class);
//                    startActivity(intent);
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//                }
//
//            } else if (id == R.id.discover_search_circlefriend) {
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    intent = new Intent();
//                    intent.setClass(mContext, FriendCircFreshListActivity.class);
//                    startActivity(intent);
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//                }
//
//            } else if (id == R.id.discover_latest_activity) {//礼物地址错误 已经被隐藏
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    intent = new Intent();
//                    intent.setClass(mContext, Web.class);
//                    intent.putExtra("url",
//                            "http://www."+Constant.IYBHttpHead()+"/book/book.jsp?uid="
//                                    + AccountManager.Instance(mContext).userId
//                                    + "&platform=android&appid="
//                                    + Constant.APPID);
//
//                    LogUtil.e("礼物错误地址：下面有正确的"+"http://www."+Constant.IYBHttpHead()+"/book/book.jsp?uid="
//                            + AccountManager.Instance(mContext).userId
//                            + "&platform=android&appid="
//                            + Constant.APPID);
//                    intent.putExtra("title",
//                            mContext.getString(R.string.discover_iyubaac));
//                    startActivity(intent);
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//                }
//            } else if (id == R.id.discover_exchange_gift) {//积分商城
//                if (AccountManager.Instance(mContext).checkUserLogin() && !AccountManager.Instance(mContext).islinshi) {
//                    intent = new Intent();
//                    intent.setClass(mContext, Web.class);
////					intent.putExtra("url", "http://class."+Constant.IYBHttpHead+"/ex.jsp?uid="
////							+AccountManager.Instance(mContext).userId+"&platform=android&appid="
////							+Constant.APPID);
//                    intent.putExtra("url", "http://m."+Constant.IYBHttpHead()+"/mall/index.jsp?"
//                            + "&uid=" + AccountManager.Instance(mContext).userId
//                            + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManager.Instance(mContext).userId + "camstory")
//                            + "&username=" + AccountManager.Instance(mContext).userName
//                            + "&platform=android&appid="
//                            + Constant.APPID
//                            + "&username=" + TextAttr.encode(AccountManager.Instance(mContext).userName)
//                    );
//                    intent.putExtra("title",
//                            mContext.getString(R.string.discover_iyubaac));
//                    startActivity(intent);
//
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//                }
//            } else if (id == R.id.discover_gift) {//礼物？？？
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    intent = new Intent();
//                    intent.setClass(mContext, Web.class);
//
//                    intent.putExtra("url", "http://vip."+Constant.IYBHttpHead()+"/mycode.jsp?"
//                            + "uid=" + AccountManager.Instance(mContext).userId
//                            + "&appid=" + Constant.APPID
//                            + "&sign=" + MD5.getMD5ofStr(AccountManager.Instance(mContext).userId
//                            + "iyuba" + Constant.APPID + date
//                            + "&username=" + TextAttr.encode(AccountManager.Instance(mContext).userName)));
//
//                    LogUtil.e("礼物请求地址："+"http://vip."+Constant.IYBHttpHead()+"/mycode.jsp?"
//                            + "uid=" + AccountManager.Instance(mContext).userId
//                            + "&appid=" + Constant.APPID
//                            + "&sign=" + MD5.getMD5ofStr(AccountManager.Instance(mContext).userId
//                            + "iyuba" + Constant.APPID + date
//                            + "&username=" + TextAttr.encode(AccountManager.Instance(mContext).userName)));
//                    intent.putExtra("title",
//                            mContext.getString(R.string.discover_gift));
//                    startActivity(intent);
//
//                } else {
//                    intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//
//                }
//            } else if (id == R.id.search_search) {
//
//                //搜一搜
////                String[] types = new String[]{
////                        HeadlineType.NEWS,
////                        HeadlineType.SONG,
////                        HeadlineType.VOA,
////                        HeadlineType.BBC,
////                        HeadlineType.TED,
////                        HeadlineType.VIDEO,
////                        HeadlineType.WORD,
////                        HeadlineType.QUESTION,
////                        HeadlineType.CLASS,
////                };
//                List<String> temp = new ArrayList<>();
//                temp.add(HeadlineType.NEWS);
//                temp.add(HeadlineType.SONG);
//                temp.add(HeadlineType.VOA);
//                temp.add(HeadlineType.BBC);
//                temp.add(HeadlineType.TED);
//                temp.add(HeadlineType.VIDEO);
//                temp.add(HeadlineType.WORD);
//                temp.add(HeadlineType.QUESTION);
//                if (!AbilityControlManager.getInstance().isLimitMoc()){
//                    temp.add(HeadlineType.CLASS);
//                }
//
//                String[] types = new String[temp.size()];
//                for (int i = 0; i < temp.size(); i++) {
//                    types[i] = temp.get(i);
//                }
//
//                startActivity(MSearchActivity.buildIntent(getContext(), types));//"noqw"
//            }else if (id == R.id.me_state_change) {
//                if (AccountManager.Instance(mContext).islinshi) {
//                    showNormalDialog("临时用户无法修改个性签名。");
//                } else {
//                    intent = new Intent(mContext, WriteState.class);
//                    startActivity(intent);
//                }
//            }
//
//            Log.v("zmm", "lbl<=>zmm");
//        }
//    };
//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//    private String date = sdf.format(new Date());
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        // MobclickAgent.onPause(mContext);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (AccountManager.Instance(mContext).isteacher.equals("1")) {
//            discover_search_certeacher.setVisibility(View.VISIBLE);
//        } else {
//            discover_search_certeacher.setVisibility(View.VISIBLE);
//        }
//
//
//        try {
//            AccountManager managerLib =AccountManager.Instance(mContext);
//            if (managerLib.userInfo!=null&&managerLib.userInfo.text == null) {
//                mState.setText(R.string.social_default_state);
//            } else {
//                String zhengze = "image[0-9]{2}|image[0-9]";
//                Emotion emotion = new Emotion();
//                managerLib.userInfo.text = emotion.replace(managerLib.userInfo.text);
//                SpannableString spannableString = Expression.getExpressionString(mContext, managerLib.userInfo.text, zhengze);
//                mState.setText(spannableString);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    /**
//     * 美剧-下载开通会员
//     *
//     * @param event
//     */
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(IMovieGoVipCenterEvent event) {
//
//        if (AccountManager.Instance(getActivity()).checkUserLogin() && !AccountManager.Instance(mContext).islinshi) {
//
//            startActivity(new Intent(mContext, VipCenterGoldActivity.class));
//        } else {
//
//            Intent intent = new Intent();
//            intent.setClass(mContext, Login.class);
//            mContext.startActivity(intent);
//
//        }
//    }
//
//
//    /**
//     * 搜一搜列表点击
//     *
//     * @param event
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(HeadlineSearchItemEvent event) {
//        Headline headline = event.headline;
//        IHeadlineManager.appId = Constant.APPID;
//        boolean isvip = false;
//
//        if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//            isvip = true;
//        }
//        switch (headline.type) {
//            case "news":
//
//                startActivity(TextContentActivity.getIntent2Me(getContext(), headline));
//
//                break;
//            case "voa":
//            case "csvoa":
//            case "bbc":
//
//                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
//                break;
//            case "song":
//                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
//                break;
//            case "voavideo":
//            case "meiyu":
//            case "ted":
//                startActivity(VideoContentActivity.getIntent2Me(getContext(), headline));
//                break;
//            case "bbcwordvideo":
//            case "topvideos":
//                startActivity(VideoContentActivity.getIntent2Me(getContext(),headline));
//                break;
//            case "class": {
//                int packId = Integer.parseInt(headline.id);
////                Intent intent = ContentActivity.buildIntent(getContext(), packId);
//                Intent intent = ContentActivity.buildIntent(getContext(),packId,headline.description);
//                startActivity(intent);
//                break;
//            }
//        }
//    }
//
//    private void showNormalDialog(String content) {
//        /* @setIcon 设置对话框图标
//         * @setTitle 设置对话框标题
//         * @setMessage 设置对话框消息提示
//         * setXXX方法返回Dialog对象，因此可以链式设置属性
//         */
//        final AlertDialog.Builder normalDialog =
//                new AlertDialog.Builder(mContext);
//        normalDialog.setIcon(com.iyuba.lib.R.drawable.iyubi_icon);
//        normalDialog.setTitle("提示");
//        normalDialog.setMessage(content);
//        normalDialog.setPositiveButton("登录",
//                (dialog, which) -> {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    startActivity(intent);
//
//                });
//        normalDialog.setNegativeButton("确定",
//                (dialog, which) -> {
//
//                });
//        // 显示
//        normalDialog.show();
//    }
//
//    //是否显示看一看
//    private void setMocShow(boolean isShow){
//        if (isShow){
//            ivLine1.setVisibility(View.VISIBLE);
//        }else {
//            ivLine1.setVisibility(View.GONE);
//        }
//    }
//}
