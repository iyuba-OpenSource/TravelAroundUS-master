package com.iyuba.core.common.activity;

/**
 * 网页显示
 *
 * @author chentong
 * @version 1.0
 * @para 传入"url" 网址；"title"标题显示
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.lib.R;

import java.util.HashMap;
import java.util.Map;

public class Web extends BasisActivity {
    private ImageView backButton;
    private WebView web;
    private TextView textView;

    private ProgressBar proLoading;

	private String mReffer = "";

    public static void start(Context context, String url, String title) {
        Intent intent = new Intent(context, Web.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.lib_web);
        setProgressBarVisibility(true);
        CrashApplication.getInstance().addActivity(this);
        backButton = findViewById(R.id.lib_button_back);
        textView = findViewById(R.id.web_buyiyubi_title);
        web = findViewById(R.id.webView);
        proLoading = findViewById(R.id.pro_loading);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        web.loadUrl(this.getIntent().getStringExtra("url"));

        textView.setText(this.getIntent().getStringExtra("title"));
//		textView.setText("购买爱语币");
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setDomStorageEnabled(true);
        websettings.setBuiltInZoomControls(true);

        web.setWebViewClient(new WebViewClient() {
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
				///获取请求uir
				String url = request.getUrl().toString();
				///获取RequestHeader中的所有 key value
				Map<String, String> lRequestHeaders = request.getRequestHeaders();
				if (lRequestHeaders.containsKey("Referer")) {
					mReffer = lRequestHeaders.get("Referer");
				}
				return super.shouldInterceptRequest(view, request);
			}
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                try {
					if (url.startsWith("http:") || url.startsWith("https:")) {
						HashMap<String, String> lStringStringHashMap = new HashMap<>();
						if (!TextUtils.isEmpty(mReffer))
							lStringStringHashMap.put("referer", mReffer);
                        view.loadUrl(url, lStringStringHashMap);
                        return true;
                    }else if(url.startsWith("myscheme:")) {
                        finish();
                        return true;
                    }else{
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
                if (proLoading.getVisibility() == View.GONE) {
                    proLoading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (proLoading.getVisibility() == View.VISIBLE) {
                    proLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (proLoading.getVisibility() == View.VISIBLE) {
                    proLoading.setVisibility(View.GONE);
                }
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            public void onProgressChanged(WebView view, int progress) {
                Web.this.setProgress(progress * 100);
            }
        });
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
        if (web.canGoBack()) {
            web.goBack(); // goBack()表示返回webView的上一页面
        } else if (!web.canGoBack()) {
            finish();
        }
    }


}
