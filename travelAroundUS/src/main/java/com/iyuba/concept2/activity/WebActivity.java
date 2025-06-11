package com.iyuba.concept2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.core.common.base.BasisActivity;

public class WebActivity extends BasisActivity {  //实现程序启动广告链接

    private Button backButton;
    private WebView web;
    private TextView textView;

    private ProgressBar proLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web);
        setProgressBarVisibility(true);
        backButton = (Button) findViewById(R.id.button_back);
        textView = (TextView) findViewById(R.id.play_title_info);
        web = (WebView) findViewById(R.id.webView);
        proLoading = findViewById(R.id.pro_loading);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        web.loadUrl(this.getIntent().getStringExtra("url"));
        textView.setText("爱语吧精品应用");
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setDomStorageEnabled(true);

        // TODO: 2022/7/6 这里因为加载使用协议和隐私协议的时间太长，增加加载弹窗
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if(url == null) return false;

                try {
                    if (url.startsWith("http:") || url.startsWith("https:"))
                    {
                        view.loadUrl(url);
                        return true;
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (proLoading!=null){
                    proLoading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (proLoading!=null){
                    proLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (proLoading!=null){
                    proLoading.setVisibility(View.GONE);
                }
            }
        });

//			web.setWebChromeClient(new WebChromeClient() {
//				// Set progress bar during loading
//				public void onProgressChanged(WebView view, int progress) {
//					web.this.setProgress(progress * 100);
//				}
//			});
        web.setDownloadListener(new DownloadListener() {

            @Override
            // TODO Auto-generated method stub
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
//			if (web.canGoBack()) {
//				web.goBack(); // goBack()表示返回webView的上一页面
//			} else if (!web.canGoBack()) {
        Intent intent3 = new Intent(WebActivity.this,
                MainFragmentActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("isFirstInfo", 0);
        startActivity(intent3);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//			}
    }


}
