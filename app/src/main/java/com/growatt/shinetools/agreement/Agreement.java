package com.growatt.shinetools.agreement;


import static com.growatt.shinetools.constant.GlobalConstant.MAINACTIVITY_ALIVE;
import static com.growatt.shinetools.utils.AppSystemUtils.KEY_APP_FIRST_INSTALL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.growatt.shinetools.Html5Activity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.WebViewActivity;
import com.growatt.shinetools.bean.HtmlJumpBean;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.okhttp.callback.StringCallback;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import okhttp3.Call;

public class Agreement {

    public static String URL_USER_PROTOCAL = "";
    public static String URL_PRIVACY = "";
    public static String URL_MAINTENANCE = "";


    public static final String agree_zn = "尊敬的用户，您好：\n" +
            "为了向您提供更好的服务，Shinephone可能收集您的个人信息，并尊重您的意愿，遵守相关法律法规，严格保管谨慎使用您的个人信息，系统将弹框提示，建议您允许我们获取相关信息。\n" +
            "设备权限\n" +
            "读取您的手机状态基本信息，实现检测网络状态，实现设备配网\n" +
            "存储权限\n" +
            "启用SD卡读写数据功能，存储图片或下载的文档。\n" +
            "相机权限\n" +
            "拍照上传信息。\n" +
            "请您在使用前点击阅读《用户协议》和《隐私政策》如果同意，请点击下方同意按钮开始使用shinephone";


    public static final String agree_en = "Dear users,\n" +
            "In order to provide you with better services, Shinephone may collect your personal information, and under the premise of respecting your wishes and complying with relevant laws and regulations, strictly keep and use your personal information with caution. The system will pop up a prompt, suggesting that you allow us to obtain relevant information.\n" +
            "Device permissions\n" +
            "Read the basic information of your mobile phone status, realize the detection of the network status, and realize the network configuration of the device\n" +
            "Storage permissions\n" +
            "Enable the SD card data read and write function to store pictures or downloaded documents.\n" +
            "Camera permissions\n" +
            "Take pictures and upload information.\n" +
            "Please click to read the \"User Agreement\" & \"Privacy Policy\" before using it. If you agree, please click on the Agree button below to start using Shinephone";


    public static void showPrivateDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_procy_dialog, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvComfir = view.findViewById(R.id.tv_comfir);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        int language = CommenUtils.getLanguageNew1();
        SpannableStringBuilder spannableStringBuilder;
        if (language == 0) {
            spannableStringBuilder = updateTextStyle(context, agree_zn, true);
        } else {
            spannableStringBuilder = updateTextStyle(context, agree_en, false);
        }

        //显示弹框
        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setBodyView(view, view1 -> {
        });
        tvContent.setText(spannableStringBuilder);
        builder.setGravity(Gravity.CENTER);
        builder.setCancelable(false);
        FragmentManager supportFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        BaseCircleDialog show = builder.show(supportFragmentManager);

        tvCancel.setOnClickListener(v -> {
            SharedPreferencesUnit.getInstance(context).putBoolean(MAINACTIVITY_ALIVE, false);
            ShineToosApplication.getContext().exit();
            show.dialogDismiss();
        });

        tvComfir.setOnClickListener(v -> {
            SharedPreferencesUnit sharedPreferencesUnit = SharedPreferencesUnit.getInstance(ShineToosApplication.getContext());
            sharedPreferencesUnit.putBoolean(KEY_APP_FIRST_INSTALL, true);
            show.dialogDismiss();
        });

    }


    private static SpannableStringBuilder updateTextStyle(Context context, String content, boolean chinese) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);

        //使用ForegroundColorSpan添加点击事件会出现冲突
        UnderlineSpan colorSpan = new UnderlineSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#009cff"));//设置颜色
            }
        };


        //使用UnderlineSpan很好的兼容这个问题
        UnderlineSpan colorSpan1 = new UnderlineSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#009cff"));//设置颜色
                //   ds.setUnderlineText(false); //去掉下划线
            }
        };

        //隐私政策


        int protocolBeginIndex = content.indexOf("《");
        int protocolEndIndex = content.indexOf("》") + 1;


        int privacyBeginIndex = content.lastIndexOf("《");
        int privacyEndIndex = content.lastIndexOf("》") + 1;


        if (!chinese) {
            protocolBeginIndex = content.indexOf("\"");
            protocolEndIndex = content.indexOf("\"") + 1;

            privacyBeginIndex = content.lastIndexOf("\"");
            privacyEndIndex = content.lastIndexOf("\"") + 1;

        }


        ClickableSpan protocolClickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                jumpToAgreement(context, 1);
            }
        };

        ClickableSpan privacyClickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                jumpToAgreement(context, 2);
            }
        };


        spannableString.setSpan(protocolClickableSpan, protocolBeginIndex, protocolEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(privacyClickableSpan, privacyBeginIndex, privacyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //字体颜色一定要放在点击事件后面，不然部分手机不会修改颜色
        spannableString.setSpan(colorSpan, protocolBeginIndex, protocolEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan1, privacyBeginIndex, privacyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


        return spannableString;
    }


    public static void getPrivacy(Context context, int type) {
        String url = "";
        switch (type) {
            case 1://用户协议
                url = URL_USER_PROTOCAL;
                break;
            case 2:
                url = URL_PRIVACY;
                break;
            case 3:
                url = URL_MAINTENANCE;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            jumpToAgreement(context, type);
        } else {
            //跳转读取本地文件
            toWebView(url, context);
        }

    }

    private static void toLocalText(Context context, int type) {
        if (CommenUtils.getLanguageNew1() != 0) {
            String url="";
            switch (type) {
                case 1:
                    url= GlobalConstant.AGREEMENT_EN;
                    break;
                case 2:
                    url= GlobalConstant.PRIVACY_EN;
                    break;
                case 3:
                    url= GlobalConstant.DESCRIPTION_EN;
                    break;
            }
            if (!TextUtils.isEmpty(url)){
/*                HtmlJumpBean bean = new HtmlJumpBean();
                bean.setTitle("");
                bean.setUrl(url);
                Html5Activity.jumpAction(context, bean);*/

                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL, url);
                context.startActivity(intent);
            }
        } else {
            String url="";
            switch (type) {
                case 1:
                    url= GlobalConstant.AGREEMENT_CN;
                    break;
                case 2:
                    url= GlobalConstant.PRIVACY_CN;
                    break;
                case 3:
                    url= GlobalConstant.DESCRIPTION_CN;
                    break;
            }
            if (!TextUtils.isEmpty(url)){
            /*    HtmlJumpBean bean = new HtmlJumpBean();
                bean.setTitle("");
                bean.setUrl(url);
                Html5Activity.jumpAction(context, bean);*/


                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL, url);
                context.startActivity(intent);
            }

        }

    }


    private static void jumpToAgreement(Context context, int type) {
        int language = CommenUtils.getLanguageNew1();
        Mydialog.Show(context);
        ShineToolsApi.getPrivacyPolicyUrlShinetools(context, "2", String.valueOf(language), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Mydialog.Dismiss();
                toLocalText(context, type);
            }

            @Override
            public void onResponse(String response, int id) {
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int result = jsonObject.optInt("result");
                    if (result == 1) {

                        JSONObject jsonObject1 = jsonObject.optJSONObject("obj");
                        if (jsonObject1 != null) {
                            URL_USER_PROTOCAL = jsonObject1.optString("webUrl1");
                            URL_PRIVACY = jsonObject1.optString("webUrl2");
                            URL_MAINTENANCE = jsonObject1.optString("webUrl3");

                            String url = "";
                            switch (type) {
                                case 1:
                                    url = URL_USER_PROTOCAL;
                                    break;
                                case 2:
                                    url = URL_PRIVACY;
                                    break;
                                case 3:
                                    url = URL_MAINTENANCE;
                                    break;
                            }


                            if (TextUtils.isEmpty(url)) {
                                toLocalText(context, type);
                            } else {
                                toWebView(url, context);
                            }
                        }


                    } else {
                        toLocalText(context, type);
                    }
                } catch (Exception e) {
                    toLocalText(context, type);
                }
            }
        });



    }

    private static void toWebView(String url, Context context) {
/*        HtmlJumpBean bean = new HtmlJumpBean();
        bean.setTitle("");
        bean.setUrl(url);
        Html5Activity.jumpAction(context, bean);*/


        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.WEB_URL, url);
        context.startActivity(intent);

    }


}
