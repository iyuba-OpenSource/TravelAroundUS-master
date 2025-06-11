//package com.iyuba.core.microclass.fragment;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import androidx.fragment.app.Fragment;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Spinner;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.listener.OnActivityGroupKeyDown;
//import com.iyuba.core.common.manager.DataManager;
//import com.iyuba.core.common.manager.MobManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IErrorReceiver;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.ErrorResponse;
//import com.iyuba.core.common.util.ExeRefreshTime;
//import com.iyuba.core.common.util.NetWorkState;
//import com.iyuba.core.common.widget.RollViewPager;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.microclass.activity.MobileClassActivity;
//import com.iyuba.core.microclass.adapter.MobClassListAdapter;
//import com.iyuba.core.microclass.adapter.MobClassListTypeAdapter;
//import com.iyuba.core.microclass.protocol.CourseListRequest;
//import com.iyuba.core.microclass.protocol.CourseListResponse;
//import com.iyuba.core.microclass.protocol.CourseTypeListRequest;
//import com.iyuba.core.microclass.protocol.CourseTypeListResponse;
//import com.iyuba.core.microclass.protocol.SlideShowCourseListRequest;
//import com.iyuba.core.microclass.protocol.SlideShowCourseListResponse;
//import com.iyuba.core.microclass.sqlite.mode.CoursePack;
//import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
//import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;
//import com.iyuba.core.microclass.sqlite.op.CoursePackOp;
//import com.iyuba.core.microclass.sqlite.op.CoursePackTypeOp;
//import com.iyuba.lib.R;
//import com.youdao.sdk.nativeads.RequestParameters;
//import com.youdao.sdk.nativeads.ViewBinder;
//import com.youdao.sdk.nativeads.YouDaoAdAdapter;
//import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
//import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.EnumSet;
//import java.util.List;
//
//public class MobClassListFragment extends Fragment implements
//        OnActivityGroupKeyDown {
//
//    private Context mContext;
//    private final static String PIC_BASE_URL = "http://app."+Constant.IYBHttpHead()+"/dev/";
//    //轮播图图片数量
//    private int IMAGE_COUNT;
//    private int curPackId;
//    private double curPackPrice;
//
//    private String reqPackId;
//    private String reqPackType;
//    private String reqPackDesc;
//
//    public String lastPage;
//    public int iLastPage = 0;
//    public int pageNum = 1;
//    boolean isLast = false;
//    private int classShowId = 0;
//
//    private GetSlidePicListTask getSlidePicListTask;
//    private StringBuilder sb;
//    // 课程信息的数据库操作帮助类
//    private CoursePackOp coursePackOp;
//    private CoursePackTypeOp coursePackTypeOp;
//
//    //有道广告的Adapter
//    private YouDaoAdAdapter mAdAdapter;
//
//    private MobClassListAdapter mobClassListAdapter;
//    private MobClassListTypeAdapter mobClassListTypeAdapter;
//
//    // 用于存放轮播图信息的集合
//    private List<SlideShowCourse> ssCourseList = new ArrayList<SlideShowCourse>();
//    // 用于存放图片地址的集合
//    private ArrayList<String> imageUrls = new ArrayList<String>();
//    // 用于存放滚动点的集合
//    private ArrayList<View> dot_list = new ArrayList<View>();
//    private ArrayList<CoursePack> coursePackArrayList = new ArrayList<CoursePack>();
//    private ArrayList<CoursePackType> coursePackTypes = new ArrayList<CoursePackType>();
//
//    /*
//     * View或者布局相关
//     */
//    private ProgressDialog wettingDialog;
//    //轮播图布局(图片+引导dots)
//    private RelativeLayout layout_roll_view;
//    // 轮播图片的布局
//    private LinearLayout top_news_viewpager;
//    // 轮播图片指引(圆点)的布局
//    private LinearLayout dots_ll;
//    private PullToRefreshListView mobClassListView;
//    private ProgressBar mobClassListWaitBar;
//    private Spinner coursePackSpinner;
//    private View backView;
//    private View view;
//    private ListView actualListView;
//    private RollViewPager rollViewPager;
//
//    final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
//            RequestParameters.NativeAdAsset.TITLE,
//            RequestParameters.NativeAdAsset.TEXT,
//            RequestParameters.NativeAdAsset.MAIN_IMAGE,
//            RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
//
//    //指定请求资源
////    RequestParameters mRequestParameters = new RequestParameters.Builder()
//    RequestParameters mRequestParameters = new RequestParameters.RequestParametersBuilder()
////		.location(location)
////		.keywords(keywords)
//            .desiredAssets(desiredAssets)
//            .build();
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//
//        mContext = getActivity();
//
//        coursePackOp = new CoursePackOp(mContext);
//        coursePackTypeOp = new CoursePackTypeOp(mContext);
//        wettingDialog = new ProgressDialog(mContext);
//
//        coursePackArrayList = coursePackOp.findDataByAll();
//        coursePackTypes = coursePackTypeOp.findDataByAll();
//
//        DataManager.Instance().courseList = coursePackArrayList;
//        DataManager.Instance().courseTypeList = coursePackTypes;
//
//        //  获取课程包的分类信息
//        //	当前课程的分类信息为空时，请求服务器；并制定默认显示全部课程的分类
//        if (coursePackTypes.size() == 0) {
//            handler.sendEmptyMessage(0);
////			new GetPackTypeDataTask().execute();
//            reqPackId = "-2";
//            reqPackType = "-2";
//            reqPackDesc = "class.all";
//        }
//
//        //当前课程的列表为空时，请求服务器
//        if (coursePackArrayList.size() == 0) {
//            new GetHeaderDataTask().execute();
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        view = inflater.inflate(R.layout.lib_microclass_course_list, container,
//                false);
//
//        //请求服务器，获取课程列表
//        new GetHeaderDataTask().execute();
//        initView(view);
//        handler.sendEmptyMessage(9);
//        return view;
//    }
//
//    @Override
//    public void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
////		coursePackSpinner.setSelection(2);
//    }
//
//    public String getAppId(int ownerid) {
//        // TODO Auto-generated method stub
//        String appId = "238";
//        return appId;
//    }
//
//    public void initView(View view) {
//        backView = view.findViewById(R.id.backlayout);
//        backView.setBackgroundColor(Color.WHITE);
//
//        coursePackSpinner = (Spinner) view.findViewById(R.id.titleSpinner);
//
//        mobClassListWaitBar = (ProgressBar) view
//                .findViewById(R.id.courselist_waitbar);
//
//        layout_roll_view = (RelativeLayout) View.inflate(mContext,
//                R.layout.layout_roll_view, null);
//        // 用于将轮播图添加进去
//        top_news_viewpager = (LinearLayout) layout_roll_view
//                .findViewById(R.id.top_sliding_viewpager);
//        dots_ll = (LinearLayout) layout_roll_view
//                .findViewById(R.id.dots_ll_ongoing);
//
//        mobClassListAdapter = new MobClassListAdapter(mContext,
//                coursePackArrayList);
//        mobClassListTypeAdapter = new MobClassListTypeAdapter(mContext,
//                coursePackTypes);
//
//        //定义信息流广告位置
//        mAdAdapter = new YouDaoAdAdapter(mContext, mobClassListAdapter,
//                YouDaoNativeAdPositioning.newBuilder()
//                        .addFixedPosition(2)
//                        .enableRepeatingPositions(10)
//                        .build());
//
//        //设定广告样式，代理listview的adapter
//        final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
//                new ViewBinder.Builder(R.layout.lib_native_ad_row)
//                        .titleId(R.id.native_title)
//                        .textId(R.id.native_text)
//                        .mainImageId(R.id.native_main_image)
//                        .build());
//
//        mAdAdapter.registerAdRenderer(adRenderer);
//
//        // AD_UNIT_ID为申请的广告位ID。
//        mAdAdapter.loadAds("44e16c0bd4cb49907163d8c4c8c6ad61", mRequestParameters);
//
//        mobClassListView = (PullToRefreshListView) view
//                .findViewById(R.id.ptr_course_list);
//
//        actualListView = mobClassListView.getRefreshableView();
//        registerForContextMenu(actualListView);
//
//        mobClassListView.setMode(Mode.BOTH);
//
//        actualListView.addHeaderView(layout_roll_view);
//
//        mobClassListView.setOnRefreshListener(orfl);
//        mobClassListView.setOnItemClickListener(oItemClickListener);
//
//        if (mobClassListTypeAdapter != null) {
//            coursePackSpinner.setAdapter(mobClassListTypeAdapter);
//        }
//
//        if (mobClassListAdapter != null) {
////			actualListView.setAdapter(mobClassListAdapter);
//            //绑定样式与广告数据的对应关系，然后代理当前listview的adapter
//            actualListView.setAdapter(mAdAdapter);
//
//            mAdAdapter.refreshAds(actualListView, "44e16c0bd4cb49907163d8c4c8c6ad61", mRequestParameters);
//
//        }
//        mobClassListWaitBar.setVisibility(View.GONE);
//
//        coursePackSpinner
//                .setOnItemSelectedListener(new OnItemSelectedListener() {
//
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent,
//                                               View view, int position, long id) {
//                        // TODO Auto-generated method stub
//                        reqPackId = coursePackTypes.get(position).id + "";
//                        reqPackType = coursePackTypes.get(position).type + "";
//                        reqPackDesc = coursePackTypes.get(position).desc; // 对应的是轮播图片对应的请求字段，如"class.all"
//
//                        pageNum = 1;
//
//                        handler.sendEmptyMessage(3);
//                        handler.sendEmptyMessage(6);
//
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                });
//
//    }
//
//    //*******************************************************************************
//    //***************************以下是下拉刷新和上拉加载部分的处理***************************
//
//    public void getPackTypeData() {
//
//        wettingDialog.show();
//        ClientSession.Instace().asynGetResponse(
//                // APPID pageNumber pageCounts
//
//                // 获取所有课程的列表
//                new CourseTypeListRequest(), new IResponseReceiver() {
//
//                    @Override
//                    public void onResponse(BaseHttpResponse response,
//                                           BaseHttpRequest request, int rspCookie) {
//                        CourseTypeListResponse res = (CourseTypeListResponse) response;
//                        if (res.result.equals("1")) {
//                            if (res.courseTypeList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
//                                // 则证明没有新的资讯类容，
//                                // 无需刷新
//                                // 以后可更改接口实现高效刷新
//                                int flag = 0;
//                                if (DataManager.Instance().courseTypeList
//                                        .size() == 0) {
//                                    flag = 1;
//                                } else {
//                                    if (res.courseTypeList.size() > DataManager
//                                            .Instance().courseTypeList
//                                            .size()) {
//                                        flag = 1;
//                                    }
//                                }
//                                if (flag == 1) {
//                                    coursePackTypes.clear();
//                                    coursePackTypes
//                                            .addAll(res.courseTypeList);
//                                    coursePackTypeOp
//                                            .deleteCoursePackTypeData();
//                                    coursePackTypeOp
//                                            .insertCoursePackType(coursePackTypes);
//                                    mobClassListTypeAdapter.clearList();// 清除原来的记录
//                                    mobClassListTypeAdapter
//                                            .addList(res.courseTypeList);
//                                    mobClassListTypeAdapter
//                                            .notifyDataSetChanged();
//                                    handler.sendEmptyMessage(8);
//                                    handlerRefreshList
//                                            .sendEmptyMessage(3);
//                                }
//                            }
//                        }
//                        wettingDialog.dismiss();
//                    }
//                }, new IErrorReceiver() {
//
//                    @Override
//                    public void onError(ErrorResponse errorResponse,
//                                        BaseHttpRequest request, int rspCookie) {
//                        // TODO Auto-generated method stub
//                    }
//
//                }, null);
//
//    }
//
//    public void getHeaderData() {
//
//        ClientSession.Instace().asynGetResponse(
//                // APPID pageNumber pageCounts
//
//                // 获取所有课程的列表
//                new CourseListRequest(reqPackId, reqPackType, "1"),
//                new IResponseReceiver() {
//
//                    @Override
//                    public void onResponse(BaseHttpResponse response,
//                                           BaseHttpRequest request, int rspCookie) {
//                        CourseListResponse res = (CourseListResponse) response;
//                        if (res.result.equals("1")) {
//
//                            iLastPage = Integer.parseInt(res.lastPage);
//
//                            if (iLastPage != pageNum) {
//                                isLast = false;
//                            } else if (iLastPage == pageNum || iLastPage == 0) {
//                                isLast = true;
//                            }
//
//                            pageNum = 2;
//
//                            if (res.courseList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
//                                // 则证明没有新的资讯类容，
//                                // 无需刷新
//                                // 以后可更改接口实现高效刷新
////								int flag = 0;
////								if (DataManager.Instance().courseList
////										.size() == 0) {
////									flag = 1;
////								} else {
////									if (res.courseList.size() > DataManager
////											.Instance().courseList
////											.size()
////											|| !(res.courseList.get(res.courseList
////													.size() - 1).id == DataManager
////													.Instance().courseList.get(DataManager
////													.Instance().courseList
////													.size() - 1).id)) {
////										flag = 1;
////									}
////								}
////								if (flag == 1) {
//                                coursePackArrayList.clear();
//                                coursePackArrayList
//                                        .addAll(res.courseList);
//                                mobClassListAdapter.clearList();// 清除原来的记录
//                                mobClassListAdapter
//                                        .addList(res.courseList);
//
////									mAdAdapter.notifyDataSetChanged();
//                                handlerRefreshList.sendEmptyMessage(3);
//
//                                if (reqPackId.equals("-2")) {
//                                    coursePackOp
//                                            .deleteCoursePackData();
//                                }
//
//                                coursePackOp
//                                        .insertCoursePacks(coursePackArrayList);
////								}
//                            }
//                        }
//                        handler.sendEmptyMessage(2);
//                        wettingDialog.dismiss();
//                    }
//                }, new IErrorReceiver() {
//
//                    @Override
//                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
//                                        int rspCookie) {
//                        // TODO Auto-generated method stub
//                        handlerRefreshList.sendEmptyMessage(4);
//                        handlerRefreshList.sendEmptyMessage(9);
//                    }
//                }, null);
//
//    }
//
//    public void getFooterData() {
//
//        if (isLast) {
//            handlerRefreshList.sendEmptyMessage(14);
//            handlerRefreshList.sendEmptyMessage(4);
//            return;
//        }
//
//        ClientSession.Instace().asynGetResponse(
//                // APPID pageNumber pageCounts
//
//                // 获取上拉加载课程的列表,将当前获取的课程列表与上拉加载返回的课程列表做比较，
//                new CourseListRequest(reqPackId, reqPackType,
//                        pageNum + ""), new IResponseReceiver() {
//
//                    @Override
//                    public void onResponse(BaseHttpResponse response,
//                                           BaseHttpRequest request, int rspCookie) {
//                        CourseListResponse res = (CourseListResponse) response;
//                        if (res.result.equals("1")) {
//                            if (res.courseList.size() > 0) {
//
//                                iLastPage = Integer.parseInt(res.lastPage);
//
//                                if (iLastPage != pageNum) {
//                                    isLast = false;
//                                } else if (iLastPage == pageNum || iLastPage == 0) {
//                                    isLast = true;
//                                }
//
//                                pageNum++;
//
//                                mobClassListAdapter.clearList();// 清除原来的记录
//                                coursePackArrayList.addAll(res.courseList);
//                                mobClassListAdapter
//                                        .addList(coursePackArrayList);
//
////								mAdAdapter.notifyDataSetChanged();
//                                handlerRefreshList.sendEmptyMessage(3);
//
//                                try {
////									coursePackOp
////											.insertCoursePacks(coursePackArrayList);
//                                } catch (Exception e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        handlerRefreshList.sendEmptyMessage(3);
//                        handler.sendEmptyMessage(2);
//                        wettingDialog.dismiss();
//                    }
//                }, new IErrorReceiver() {
//
//                    @Override
//                    public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
//                                        int rspCookie) {
//                        // TODO Auto-generated method stub
//                        handler.sendEmptyMessage(2);
//                        wettingDialog.dismiss();
//                    }
//                }, null);
//
//    }
//
//    private class GetPackTypeDataTask extends AsyncTask<Void, Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(Void... params) {
//            ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
//            getPackTypeData();
//            return null;
//        }
//    }
//
//    private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(Void... params) {
//            ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
//            getHeaderData();
//            return null;
//        }
//    }
//
//    private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(Void... params) {
//            ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
//            getFooterData();
//            return null;
//        }
//    }
//
//    private OnItemClickListener oItemClickListener = new OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            // TODO Auto-generated method stub
//            Intent intent = new Intent();
//
//            if (position > 1) {
//                mAdAdapter.getItem(position - 2);
//
//                // 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2
//
//                curPackId = ((CoursePack) mAdAdapter.getItem(position - 2)).id;
//                curPackPrice = ((CoursePack) mAdAdapter.getItem(position - 2)).price;
//
//                MobManager.Instance().packid = curPackId;
//                MobManager.Instance().ownerid = ((CoursePack) mAdAdapter.getItem(position - 2)).ownerid;
//                MobManager.Instance().appId = Constant.APPID;
//                MobManager.Instance().desc = ((CoursePack) mAdAdapter.getItem(position - 2)).desc;
//                MobManager.Instance().curPackPrice = curPackPrice;
//
//                MobManager.Instance().CourseNum = ((CoursePack) mAdAdapter.getItem(position - 2)).classNum;
//
//                intent.putExtra("packname",
//                        ((CoursePack) mAdAdapter.getItem(position - 2)).name);
//                intent.putExtra("position", position);
//                intent.putExtra("coursenum",
//                        ((CoursePack) mAdAdapter.getItem(position - 2)).classNum);
//
//                intent.setClass(mContext, MobileClassActivity.class);
//                startActivity(intent);
//            }
//
//        }
//    };
//
//
//    private OnRefreshListener2<ListView> orfl = new OnRefreshListener2<ListView>() {
//
//        @Override
//        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//            // TODO Auto-generated method stub
//
//            if (NetWorkState.isConnectingToInternet()) {// 开始刷新
//
//                handler.sendEmptyMessage(0);
//
//                new GetHeaderDataTask().execute();
//                handler.sendEmptyMessage(6);
//
//            } else {// 刷新失败
//                handlerRefreshList.sendEmptyMessage(4);
//                handlerRefreshList.sendEmptyMessage(9);
//                handlerRefreshList.sendEmptyMessage(13);
//            }
//
//        }
//
//        @Override
//        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//            // TODO Auto-generated method stub
//
//            if (NetWorkState.isConnectingToInternet()) {// 开始刷新
//
//                new GetFooterDataTask().execute();
//
//            } else {// 刷新失败
//                handlerRefreshList.sendEmptyMessage(4);
//                handlerRefreshList.sendEmptyMessage(9);
//                handlerRefreshList.sendEmptyMessage(13);
//            }
//
//        }
//    };
//
//
//    //***************************以上是下拉刷新和上拉加载部分的处理***************************
//    //*******************************************************************************
//
//    Handler handler = new Handler() {
//        int reqPageNumber;
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            switch (msg.what) {
//
//                case 0:
//                    wettingDialog.show();
//                    ClientSession.Instace().asynGetResponse(
//                            // APPID pageNumber pageCounts
//
//                            // 获取所有课程的列表
//                            new CourseTypeListRequest(), new IResponseReceiver() {
//
//                                @Override
//                                public void onResponse(BaseHttpResponse response,
//                                                       BaseHttpRequest request, int rspCookie) {
//                                    CourseTypeListResponse res = (CourseTypeListResponse) response;
//                                    if (res.result.equals("1")) {
//                                        if (res.courseTypeList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
//                                            // 则证明没有新的资讯类容，
//                                            // 无需刷新
//                                            // 以后可更改接口实现高效刷新
//                                            int flag = 0;
//                                            if (DataManager.Instance().courseTypeList
//                                                    .size() == 0) {
//                                                flag = 1;
//                                            } else {
//                                                if (res.courseTypeList.size() > DataManager
//                                                        .Instance().courseTypeList
//                                                        .size()) {
//                                                    flag = 1;
//                                                }
//                                            }
//                                            if (flag == 1) {
//                                                coursePackTypes.clear();
//                                                coursePackTypes
//                                                        .addAll(res.courseTypeList);
//                                                coursePackTypeOp
//                                                        .deleteCoursePackTypeData();
//                                                coursePackTypeOp
//                                                        .insertCoursePackType(coursePackTypes);
//                                                mobClassListTypeAdapter.clearList();// 清除原来的记录
//                                                mobClassListTypeAdapter
//                                                        .addList(res.courseTypeList);
////											mobClassListTypeAdapter
////													.notifyDataSetChanged();
//                                                handler.sendEmptyMessage(8);
//                                                handlerRefreshList
//                                                        .sendEmptyMessage(3);
//
//
//                                            }
//                                        }
//                                    }
//                                    wettingDialog.dismiss();
//                                }
//                            }, new IErrorReceiver() {
//
//                                @Override
//                                public void onError(ErrorResponse errorResponse,
//                                                    BaseHttpRequest request, int rspCookie) {
//                                    // TODO Auto-generated method stub
//                                }
//
//                            }, null);
//                    break;
//
//                case 1:
//                    wettingDialog.show();
//                    ClientSession.Instace().asynGetResponse(
//                            // APPID pageNumber pageCounts
//
//                            // 获取所有课程的列表
//                            new CourseListRequest(reqPackId, reqPackType, "1"),
//                            new IResponseReceiver() {
//
//                                @Override
//                                public void onResponse(BaseHttpResponse response,
//                                                       BaseHttpRequest request, int rspCookie) {
//                                    CourseListResponse res = (CourseListResponse) response;
//                                    if (res.result.equals("1")) {
//
//                                        iLastPage = Integer.parseInt(res.lastPage);
//
//                                        if (res.courseList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
//                                            // 则证明没有新的资讯类容，
//                                            // 无需刷新
//                                            // 以后可更改接口实现高效刷新
//                                            int flag = 0;
//                                            if (DataManager.Instance().courseList
//                                                    .size() == 0) {
//                                                flag = 1;
//                                            } else {
//                                                if (res.courseList.size() > DataManager
//                                                        .Instance().courseList
//                                                        .size()
//                                                        || !(res.courseList.get(res.courseList
//                                                        .size() - 1).id == DataManager
//                                                        .Instance().courseList.get(DataManager
//                                                                .Instance().courseList
//                                                                .size() - 1).id)) {
//                                                    flag = 1;
//                                                }
//                                            }
//                                            if (flag == 1) {
//                                                coursePackArrayList.clear();
//                                                coursePackArrayList
//                                                        .addAll(res.courseList);
//                                                mobClassListAdapter.clearList();// 清除原来的记录
//                                                mobClassListAdapter
//                                                        .addList(res.courseList);
//
////											mAdAdapter.notifyDataSetChanged();
//                                                handlerRefreshList.sendEmptyMessage(3);
//
//                                                if (reqPackId.equals("-2")) {
//                                                    coursePackOp
//                                                            .deleteCoursePackData();
//                                                }
//
//                                                coursePackOp
//                                                        .insertCoursePacks(coursePackArrayList);
//                                            }
//                                        }
//                                    }
//                                    handler.sendEmptyMessage(2);
//                                    wettingDialog.dismiss();
//                                }
//                            }, null, null);
//                    break;
//                case 2:
//                    mobClassListAdapter.notifyDataSetChanged();
//                    mobClassListView.onRefreshComplete();
//                    mobClassListWaitBar.setVisibility(View.GONE);
//                    break;
//
//                // 分类获取也联网获取
//                case 3:
//                    if ((!reqPackId.equals("-2")) && (!reqPackId.equals("-1"))) { // 按课程的分类获取包（日语一级、VOA应用、英语四级等）
//                        // 首先判断本地是否有该类别的课程对应的包
//                        coursePackArrayList.clear();
//                        coursePackArrayList = coursePackOp
//                                .findDataByOwnerId(reqPackId);
//
//                        // 如果当前课程对应的包在本地有记录，则加载本地的
//                        if (coursePackArrayList.size() != 0) {
//                            mobClassListAdapter.clearList();// 清除原来的记录
//                            mobClassListAdapter.addList(coursePackArrayList);
//                            handlerRefreshList.sendEmptyMessage(3);
//                        } else {// 否则，联网取数据，加载到本地
//
//                            handler.sendEmptyMessage(4);
//                        }
//                    } else if (reqPackId.equals("-1")) { // 获取最新课程包列表
//                        reqPackType = "1";
//                        reqPackId = "-1";
//                        handler.sendEmptyMessage(4);
//                        handlerRefreshList.sendEmptyMessage(3);
//                    } else if (reqPackId.equals("-2")) { // 获取全部课程包列表
//                        coursePackArrayList.clear();
//                        coursePackArrayList = coursePackOp.findDataByAll();
//
//                        if (coursePackArrayList.size() != 0) {
//                            mobClassListAdapter.clearList();// 清除原来的记录
//                            mobClassListAdapter.addList(coursePackArrayList);
//
//                            handlerRefreshList.sendEmptyMessage(3);
//
//                        } else {
//                            if (NetWorkState.isConnectingToInternet()) {// 开始刷新
//
////							handler.sendEmptyMessage(1);
//                                new GetHeaderDataTask().execute();
//                                handlerRefreshList.sendEmptyMessage(3);
//
//                            } else {// 刷新失败
//                                handlerRefreshList.sendEmptyMessage(4);
//                                handlerRefreshList.sendEmptyMessage(9);
//                                handlerRefreshList.sendEmptyMessage(13);
//                            }
//
//                            handlerRefreshList.sendEmptyMessage(3);
//                        }
//                    }
//
//
//                    break;
//
//                // 获取最新课程的请求
//                case 4:
//                    wettingDialog.show();
//                    Log.d("获取最新课程：", "此处为最新课程的返回结果");
//                    ClientSession.Instace().asynGetResponse(
//                            // APPID pageNumber pageCounts
//
//                            // 获取所有课程的列表
//                            new CourseListRequest(reqPackId, reqPackType, "1"),
//                            new IResponseReceiver() {
//
//                                @Override
//                                public void onResponse(BaseHttpResponse response,
//                                                       BaseHttpRequest request, int rspCookie) {
//                                    CourseListResponse res = (CourseListResponse) response;
//                                    if (res.result.equals("1")) {
//                                        if (res.courseList.size() > 0) {
//
//                                            coursePackArrayList.clear();
//                                            coursePackArrayList
//                                                    .addAll(res.courseList);
//                                            mobClassListAdapter.clearList();// 清除原来的记录
//                                            mobClassListAdapter
//                                                    .addList(res.courseList);
//
////										mAdAdapter.notifyDataSetChanged();
//                                            handlerRefreshList.sendEmptyMessage(3);
//
//                                            if (!reqPackId.equals("-1")) {
//                                                try {
//                                                    coursePackOp
//                                                            .insertCoursePacks(res.courseList);
//                                                } catch (Exception e) {
//                                                    // TODO Auto-generated catch
//                                                    // block
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    handler.sendEmptyMessage(2);
//                                    wettingDialog.dismiss();
//                                }
//                            }, null, null);
//                    break;
//
//                // 获取上拉加载课程的请求
//                case 5:
//
//                    reqPageNumber = msg.arg1;
//                    Log.d("请求列表的页码", reqPageNumber + "");
//                    wettingDialog.show();
//                    Log.d("获取上拉加载课程：", "此处为上拉加载课程的返回结果");
//
//                    ClientSession.Instace().asynGetResponse(
//                            // APPID pageNumber pageCounts
//
//                            // 获取上拉加载课程的列表,将当前获取的课程列表与上拉加载返回的课程列表做比较，
//                            new CourseListRequest(reqPackId, reqPackType,
//                                    pageNum + ""), new IResponseReceiver() {
//
//                                @Override
//                                public void onResponse(BaseHttpResponse response,
//                                                       BaseHttpRequest request, int rspCookie) {
//                                    CourseListResponse res = (CourseListResponse) response;
//                                    if (res.result.equals("1")) {
//                                        if (res.courseList.size() > 0) {
//
//                                            boolean hasCoursePack = false;
//                                            for (int i = 0; i < res.courseList
//                                                    .size(); i++) {
//                                                for (int j = 0; j < coursePackArrayList
//                                                        .size(); j++) {
//                                                    if (coursePackArrayList.get(j).id == res.courseList
//                                                            .get(i).id) {
//                                                        hasCoursePack = true;
//                                                    }
//                                                }
//                                                if (hasCoursePack == false) {
//                                                    coursePackArrayList
//                                                            .add(res.courseList
//                                                                    .get(i));
//                                                }
//                                            }
//
//                                            mobClassListAdapter.clearList();// 清除原来的记录
//                                            mobClassListAdapter
//                                                    .addList(coursePackArrayList);
////										mAdAdapter.notifyDataSetChanged();
//                                            handlerRefreshList.sendEmptyMessage(3);
//                                            try {
//                                                // coursePackOp.deleteCoursePackData();
//                                                coursePackOp
//                                                        .insertCoursePacks(coursePackArrayList);
//                                                // 这里保证插到数据库中的加载数据放到之前的数据后边
//                                                // coursePackOp.insertCoursePacks(res.courseList);
//                                            } catch (Exception e) {
//                                                // TODO Auto-generated catch block
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                    handlerRefreshList.sendEmptyMessage(3);
//                                    wettingDialog.dismiss();
//                                }
//                            }, null, null);
//                    break;
//                case 6:
//                    initSlideShowViewPicData();
//                    break;
//                case 7:
//                    break;
//                case 8:
//                    mobClassListTypeAdapter.notifyDataSetChanged();
//                    break;
//                case 9:
//                    if (coursePackTypes.size() != 0 && coursePackArrayList.size() != 0 && imageUrls.size() != 0) {
//                        if (classShowId == 0) {
//                            for (int i = 0; i < coursePackTypes.size(); i++) {
//                                CoursePackType cpt = coursePackTypes.get(i);
//                                if (cpt.id == 21) {
//                                    classShowId = i;
//                                }
//                            }
//                        }
//                        coursePackSpinner.setSelection(classShowId);
//                        handler.sendEmptyMessage(6);
//                        handlerRefreshList.sendEmptyMessage(9);
//                    } else {
//                        handler.sendEmptyMessageDelayed(9, 500);
//                    }
//
//                default:
//                    break;
//            }
//        }
//    };
//
//    Handler handlerRefreshList = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0: // 从网络获取最新数�?
//
//                    break;
//                case 1:
//                    break;
//                case 2:
//
//                    break;
//                case 3:
//                    if (mobClassListAdapter == null) {
//
//                        mobClassListAdapter = new MobClassListAdapter(mContext,
//                                coursePackArrayList);
//
//                        actualListView.setAdapter(mAdAdapter);
//                        mAdAdapter.refreshAds(actualListView, "44e16c0bd4cb49907163d8c4c8c6ad61", mRequestParameters);
//                    } else {
//                        mobClassListAdapter.notifyDataSetChanged();
//                        mAdAdapter.notifyDataSetChanged();
//                    }
//                    actualListView.setVisibility(View.VISIBLE);
//                    mobClassListWaitBar.setVisibility(View.GONE);
//                    break;
//                case 4:
//                    mobClassListView.onRefreshComplete();
//                    break;
//                case 5:
//
//                    break;
//                case 6: // 搜索执行
//                    break;
//                case 7:
//                    break;
//                case 8:
//                    if (isDetached()) {
//                        return;
//                    }
//                    wettingDialog.show();
//                    break;
//                case 9:
//                    wettingDialog.dismiss();
//                    break;
//                case 10: // 搜索listView适配
//                    break;
//                case 11:
//                    break;
//                case 12:
//                    break;
//                case 13:
//                    CustomToast.showToast(mContext, R.string.check_network, 1000);
//                    break;
//                case 14:
//                    CustomToast.showToast(mContext, "已经是最后一页！", 1000);
//                    break;
//                case 15:
//                    break;
//                case 16:
//                    break;
//                case 17:
//                    break;
//            }
//        }
//    };
//
//
//    //*******************************************************************************************
//    //**************************************以下为处理轮播图使用**************************************
//
//    /**
//     * 初始化相关Data
//     */
//    private void initSlideShowViewPicData() {
//        String url = ConfigManager.Instance().loadString(reqPackDesc + "weike");
//        Log.e("reqPackDesc", reqPackDesc);
//        if (url.length() == 0 || NetWorkState.isConnectingToInternet()) {
//            sb = new StringBuilder();
//            Log.e("准备发送一次轮播图片的请求：", "准备发送！！！");
//            getSlidePicListTask = new GetSlidePicListTask();
//            getSlidePicListTask.execute();
//        } else {
//            Log.e("爱与维克", url);
//            slideHandler.sendEmptyMessage(0);
//        }
//    }
//
//    /**
//     * 异步任务,获取数据
//     */
//    class GetSlidePicListTask extends AsyncTask<String, Integer, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            try {
//                // 这里一般调用服务端接口获取一组轮播图片，下面是从百度找的几个图片
//
//                ClientSession.Instace().asynGetResponse(
//
//                        // 获取所有课程轮播图片的列表
//                        new SlideShowCourseListRequest(reqPackDesc),
//                        new IResponseReceiver() {
//
//                            @Override
//                            public void onResponse(BaseHttpResponse response,
//                                                   BaseHttpRequest request, int rspCookie) {
//                                SlideShowCourseListResponse res = (SlideShowCourseListResponse) response;
//                                if (res.result.equals("1")) {
//                                    if (res.ssCourseList.size() > 0) {
//
//                                        IMAGE_COUNT = res.ssCourseList.size();
//
//                                        ssCourseList.clear();
//                                        ssCourseList.addAll(res.ssCourseList);
//
//                                        imageUrls.clear();
//
//                                        for (int i = 0; i < res.ssCourseList
//                                                .size(); i++) {
//                                            if (!imageUrls
//                                                    .contains(PIC_BASE_URL
//                                                            + res.ssCourseList
//                                                            .get(i).pic)) {
//                                                imageUrls.add(PIC_BASE_URL
//                                                        + res.ssCourseList
//                                                        .get(i).pic);
//                                                sb.append(PIC_BASE_URL
//                                                        + res.ssCourseList
//                                                        .get(i).pic + "-");
//                                                Log.e("SlideShowCourseList" + i,
//                                                        res.ssCourseList.get(i).name);
//                                            }
//
//                                        }
//                                        String allUrl = sb.toString();
//                                        ConfigManager.Instance().removeKey(reqPackDesc + "weike");
//                                        ConfigManager.Instance().putString(reqPackDesc + "weike", allUrl);
//                                        try {
//                                            ConfigManager.Instance().removeKey(reqPackDesc);
//                                            ConfigManager.Instance().putString(reqPackDesc, ssCourseList);
//                                            ArrayList o = (ArrayList) ConfigManager.Instance().loadObject(reqPackDesc);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                    //添加底部的点、为轮播图添加点击处理事件
//                                    slideHandler.sendEmptyMessage(0);
//                                }
//                            }
//                        }, new IErrorReceiver() {
//                            @Override
//                            public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
//                                slideHandler.sendEmptyMessage(0);
//                            }
//                        }, null);
//
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//
//    }
//
////	private void initDot() {
////		// 滚动的个数应该和图片的个数相等
////		// 清空点所在集合
////		String urls = ConfigManager.Instance().loadString(reqPackDesc + "weike");
////		String[] arrUrls = urls.split("-");
////		dot_list.clear();
////		dots_ll.removeAllViews();
////		top_news_viewpager.removeAllViews();
////		for (int i = 0; i < arrUrls.length; i++) {
////			View view = new View(mContext);
////			if (i == 0) {
////				// 红色
////				view.setBackgroundResource(R.drawable.dot_focus);
////			} else {
////				view.setBackgroundResource(R.drawable.dot_blur);
////			}
////			// 指定点的大小
////			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
////					10, 10);
////			// 指定点的间距
////			layoutParams.setMargins(8, 0, 8, 0);
////			// 添加到线性布局中
////			dots_ll.addView(view, layoutParams);
////			// 添加到集合中去
////			dot_list.add(view);
////		}
////	}
//
//    Handler slideHandler = new Handler() {
//
//        @Override
//        public void dispatchMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.dispatchMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    // 初始划滚动点
////				initDot();
//                    // 滚动的个数应该和图片的个数相等
//                    // 清空点所在集合
//                    String urls = ConfigManager.Instance().loadString(reqPackDesc + "weike");
//                    String[] arrUrls = urls.split("-");
//                    dot_list.clear();
//                    dots_ll.removeAllViews();
//                    top_news_viewpager.removeAllViews();
//                    for (int i = 0; i < arrUrls.length; i++) {
//                        View view = new View(mContext);
//                        if (i == 0) {
//                            // 红色
//                            view.setBackgroundResource(R.drawable.dot_focus);
//                        } else {
//                            view.setBackgroundResource(R.drawable.dot_blur);
//                        }
//                        // 指定点的大小
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                                10, 10);
//                        // 指定点的间距
//                        layoutParams.setMargins(8, 0, 8, 0);
//                        // 添加到线性布局中
//                        dots_ll.addView(view, layoutParams);
//                        // 添加到集合中去
//                        dot_list.add(view);
//                    }
//                    // 创建轮播图
//                        rollViewPager = new RollViewPager(mContext,
//                                dot_list, new RollViewPager.OnViewClickListener() {
//                            // 用于处理点击图片的逻辑
//                            public void viewClick(SlideShowCourse ssCourse) {
//                                Intent intent = new Intent();
//
//                                // 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2
//
//                                curPackId = ssCourse.id;
//                                curPackPrice = ssCourse.price;
//
//                                MobManager.Instance().packid = curPackId;
//                                MobManager.Instance().ownerid = ssCourse.ownerid;
//                                MobManager.Instance().appId = Constant.APPID;
//                                MobManager.Instance().desc = ssCourse.desc1;
//                                MobManager.Instance().curPackPrice = curPackPrice;
//
//                                // MobManager.Instance().CourseNum =
//                                // coursePackArrayList.get(position-2).classNum;
//
//                                intent.putExtra("packname", ssCourse.name);
//                                // intent.putExtra("position", position);
//                                // intent.putExtra("coursenum",
//                                // coursePackArrayList.get(position-2).classNum);
//
//                                intent.setClass(mContext,
//                                        MobileClassActivity.class);
//
//                                if (curPackId != 0) {
//                                    startActivity(intent);
//                                }
//
//                            }
//                        });
//                        // 将图片地址添加到轮播图中
//                        try {
//                            List<SlideShowCourse> ssCourseList = (List<SlideShowCourse>) ConfigManager.Instance().loadObject(reqPackDesc);
//                            rollViewPager.initSlideShowCourseList(ssCourseList);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        imageUrls.clear();
//                        imageUrls.addAll(Arrays.asList(arrUrls));
////					rollViewPager.initImgUrl(imageUrls);
////					rollViewPager.startRoll();
//
//                        Log.e("imageUrls",imageUrls.toString());
//                        binderAdapterDataHandler
//                                .post(binderAdapterDataRunnable);
//                        top_news_viewpager.removeAllViews();
//                        top_news_viewpager.addView(rollViewPager);
//
//
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//    };
//
//
//    @Override
//    public boolean onSubActivityKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    private Handler binderAdapterDataHandler = new Handler();
//    private Runnable binderAdapterDataRunnable = new Runnable() {
//        public void run() {
//            rollViewPager.initImgUrl(imageUrls);
//        }
//    };
//
//    //**************************************以上为处理轮播图使用**************************************
//    //*******************************************************************************************
//}
