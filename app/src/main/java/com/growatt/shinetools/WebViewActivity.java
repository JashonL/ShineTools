package com.growatt.shinetools;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.growatt.shinetools.base.BaseActivity;

import butterknife.BindView;



public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;


    private boolean isDownload = true;
    private String url;
    public static final String WEB_URL="web_url";

    @Override
    protected int getContentView() {
        return R.layout.activity_webview;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initViews() {
        url= getIntent().getStringExtra(WEB_URL);
        webview.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webview.setWebChromeClient(webChromeClient);
        webview.setWebViewClient(webViewClient);

        WebSettings webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js




        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        //链接中有需要跳转下载的链接时跳转浏览器下载
        webview.setDownloadListener(new DownloadListener() {
            @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                                  String mimetype, long contentLength) {
                if (isDownload) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(url);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(uri);
                    startActivity(intent);
                }
                isDownload = true;//重置为初始状态
            }
        });


        openWebView();
    }

    @Override
    protected void initData() {

    }


    public void openWebView() {
//        if (url.startsWith("http")){
        webview.loadUrl(url);//加载url
//        }
    }




    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith("http")) {
                try {
                    // 以下固定写法,表示跳转到第三方应用
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    isDownload = false;  //该字段是用于判断是否需要跳转浏览器下载
                } catch (Exception e) {
                    // 防止没有安装的情况
                    e.printStackTrace();
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };


    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton(getString(R.string.android_key429),null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
            webview.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        webview.destroy();
        webview=null;
    }

}
