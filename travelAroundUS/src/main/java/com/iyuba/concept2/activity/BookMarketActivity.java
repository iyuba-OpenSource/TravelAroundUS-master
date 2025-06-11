package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.BookListAdapter;
import com.iyuba.concept2.adapter.TestArrayAdapter;
import com.iyuba.concept2.protocol.BookMarketRequest;
import com.iyuba.concept2.protocol.BookMarketResponse;
import com.iyuba.concept2.sqlite.mode.MarketBook;
import com.iyuba.concept2.sqlite.op.ShopCartOp;
import com.iyuba.concept2.util.ExeProtocol;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivotsm on 2017/2/16.
 */

public class BookMarketActivity extends BasisActivity {

    private Context mContext;
    private BookListAdapter bookListAdapter = null;
    private int pageSize;
    private int pageCount;
    private List<MarketBook> books = new ArrayList<>();
    private List<String> types = new ArrayList<>();
    private TestArrayAdapter adapter;
    private int type = 0;
    private ShopCartOp shopCartOp;

    @BindView(R.id.titlebar_title)
    TextView title;
    @BindView(R.id.book_type)
    Spinner bookType;
    @BindView(R.id.titlebar_back_button)
    ImageButton backButton;
    @BindView(R.id.book_list)
    PullToRefreshListView bookList;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,BookMarketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_market);
        ButterKnife.bind(this);
        mContext = this;

        shopCartOp = new ShopCartOp(mContext);

        pageSize = 10;
        pageCount = 1;

        title.setText("图书网城");
        types.add("全部");
        types.add("英语四级");
        types.add("英语六级");
        types.add("Voa系列");
        types.add("考研英语(一)");
        types.add("考研英语(二)");
        types.add("日语N1");
        types.add("日语N2");
        types.add("日语N3");
        types.add("托福");
        types.add("雅思");
        types.add("中职英语");
        types.add("新概念");
        types.add("走遍美国");
        types.add("剑桥小说");
        types.add("学位英语");

        // 适配器
        adapter = new TestArrayAdapter(mContext,
                types);

        // 设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 加载适配器
        bookType.setAdapter(adapter);


        bookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                switch (position) {
                    case 0:
                        type = 0;
                        break;
                    case 1:
                        type = 2;
                        break;
                    case 2:
                        type = 4;
                        break;
                    case 3:
                        type = 3;
                        break;
                    case 4:
                        type = 8;
                        break;
                    case 5:
                        type = 52;
                        break;
                    case 6:
                        type = 1;
                        break;
                    case 7:
                        type = 5;
                        break;
                    case 8:
                        type = 6;
                        break;
                    case 9:
                        type = 7;
                        break;
                    case 10:
                        type = 61;
                        break;
                    case 11:
                        type = 91;
                        break;
                    case 12:
                        type = 21;
                        break;
                    case 13:
                        type = 22;
                        break;
                    case 14:
                        type = 23;
                        break;
                    case 15:
                        type = 28;
                        break;
                }
                ConfigManager.Instance().putInt("bookType", type);
                handler.sendEmptyMessage(0);

            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        handler.sendEmptyMessage(0);

    }

    private void setView() {

        bookList.getLoadingLayoutProxy(true, false).setPullLabel(
                mContext.getString(R.string.pulldown));
        bookList.getLoadingLayoutProxy(true, false).setRefreshingLabel(
                mContext.getString(R.string.pulldown_refreshing));
        bookList.getLoadingLayoutProxy(true, false).setReleaseLabel(
                mContext.getString(R.string.pulldown_release));
        bookList.getLoadingLayoutProxy(false, true).setPullLabel(
                mContext.getString(R.string.pullup));
        bookList.getLoadingLayoutProxy(false, true).setRefreshingLabel(
                mContext.getString(R.string.pullup_loading));
        bookList.getLoadingLayoutProxy(false, true).setReleaseLabel(
                mContext.getString(R.string.pullup_release));
        bookList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (NetWorkState.isConnectingToInternet()) {
                    handler.sendEmptyMessage(0);
                } else {

                }
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (NetWorkState.isConnectingToInternet()) {
                    handler.sendEmptyMessage(2);
                } else {

                }
            }
        });
        bookList.getRefreshableView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if(!UserInfoManager.getInstance().isLogin()){
                            showNormalDialog("请登录后购买图书。");
                        }else {
                            Intent intent = new Intent();
                            MarketBook marketBook = (MarketBook) bookListAdapter.getItem(position - 1);
                            intent.putExtra("bookId", marketBook.getBookId());
                            intent.setClass(mContext, BookDetailActivity.class);
                            startActivity(intent);
                        }
                    }
                });


        shopCartOp.saveDatas(books);

    }

    private void showNormalDialog(String content){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(com.iyuba.lib.R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        Intent intent = new Intent();
//                        intent.setClass(mContext, Login.class);
//                        startActivity(intent);
                        LoginUtil.startToLogin(mContext);
                    }
                });
        normalDialog.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        normalDialog.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pageCount = 1;
                    ExeProtocol.exe(
                            new BookMarketRequest(String.valueOf(pageSize), String.valueOf(pageCount), String.valueOf(type)),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    BookMarketResponse response = (BookMarketResponse) bhr;
                                    if (response.result.equals("1")) {
                                        books.clear();
                                        books.addAll(response.books);
                                        handler.sendEmptyMessage(1);
                                    }
                                }

                                @Override
                                public void error() {

                                }
                            });
                    break;
                case 1:
                    if (bookListAdapter == null) {
                        bookListAdapter = new BookListAdapter(mContext, books);
                        bookList.getRefreshableView().setAdapter(bookListAdapter);
                    } else
                        bookListAdapter.replaceBooks(books);
                    bookList.onRefreshComplete();
                    setView();
                    break;
                case 2:
                    pageCount++;
                    ExeProtocol.exe(
                            new BookMarketRequest(String.valueOf(pageSize), String.valueOf(pageCount), String.valueOf(type)),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    BookMarketResponse response = (BookMarketResponse) bhr;
                                    if (response.result.equals("1")) {
                                        pageCount++;
                                        books.clear();
                                        books.addAll(response.books);
                                        handler.sendEmptyMessage(3);
                                    }
                                }

                                @Override
                                public void error() {

                                }
                            });
                    break;
                case 3:
                    bookListAdapter.addBooks(books);
                    bookList.onRefreshComplete();
                    setView();
                    break;

            }
        }
    };
}
