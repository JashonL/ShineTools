package com.growatt.shinetools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.HtmlJumpBean;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 所有以网页形式跳转的总入口
 */
public class Html5Activity extends BaseActivity {
    private static final String HTML_BEAN = "html_bean";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.ivLeft)
    ImageView mIvLeft;
    @BindView(R.id.progressBar1)
    ProgressBar pg1;
    @BindView(R.id.llContainer)
    LinearLayout mll;
    @BindView(R.id.tvContent)
    TextView tvContent;
    private WebView mWebView;
    private String url;
    private HtmlJumpBean mHtmlBean;
    private static final String TAG = "Html5Activity";
    private boolean isDownload = true;
    private boolean isProduct=false;

    @Override
    protected int getContentView() {
        return R.layout.activity_html5;
    }

    @Override
    protected void initViews() {
        mIvLeft.setImageResource(R.drawable.ov_back);
        mIvLeft.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        mHtmlBean = intent.getParcelableExtra(HTML_BEAN);
        mTvTitle.setText(mHtmlBean.getTitle());


    }

    @Override
    protected void initData() {
        initView();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void init() {

    }

    /**
     * 跳转总入口
     * @param act
     * @param bean
     */
    public static void jumpAction(Context act, HtmlJumpBean bean){
        Intent intent = new Intent(act,Html5Activity.class);
        intent.putExtra(HTML_BEAN,bean);
        act.startActivity(intent);
    }
    @SuppressLint("JavascriptInterface")
    private void initView() {
        pg1=(ProgressBar) findViewById(R.id.progressBar1);
        url = mHtmlBean.getUrl();
        isProduct=mHtmlBean.isFlag();

        mWebView = new WebView(this.getApplicationContext());
//        mWebView = new PdfWebView(this.getApplicationContext());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(mWebViewLP);
        mWebView.setInitialScale(0);
        mll  = (LinearLayout) findViewById(R.id.llContainer);
        mll.addView(mWebView);

        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setTextZoom(100);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);//有可能是DOM储存API没有打开
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if(newProgress==100){
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }
        });
        if (!url.startsWith("http")&&!url.startsWith("file:///")){
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(url);
        }else {
            mWebView.loadUrl(url);
            tvContent.setVisibility(View.INVISIBLE);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api >= 19
//
//            mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + url);
//
//        } else {
//
//            if (!TextUtils.isEmpty(url)) {
//
//                byte[] bytes = null;
//
//                try {// 获取以字符编码为utf-8的字符
//
//                    bytes = url.getBytes("UTF-8");
//
//                } catch (UnsupportedEncodingException e) {
//
//                    e.printStackTrace();
//
//                }
//
//                if (bytes != null) {
//
////                    url = new BASE64Encoder().encode(bytes);// BASE64转码
//
//                }
//
//            }
//            mWebView.loadUrl("file:///android_asset/pdfjs_compatibility/web/viewer.html?file=" + url);
//
//        }


//链接中有需要跳转下载的链接时跳转浏览器下载
        mWebView.setDownloadListener(new DownloadListener() {
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

    }

    private void addImageClickListner() {
        if(mWebView!=null){
            mWebView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistner.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "})()");
        }
    }

    public class JavascriptInterface {

        private Context context;
        public JavascriptInterface(Context context) {
            this.context = context;
        }


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            try {
//                if(url.endsWith(".pdf")){
//                    //进行下载等相关操作
//                    return false;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            view.loadUrl(url);
//            return true;
            Log.i(TAG, "shouldOverrideUrlLoading: " +url);
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
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            addImageClickListner();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mll.removeView(mWebView);
        if(mWebView!= null) {
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView=null;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()){
                mWebView.goBack();// 返回前一个页面
                return true;
            }else {
                this.finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
