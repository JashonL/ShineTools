package com.growatt.shinetools.login;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.BaseView;
import com.growatt.shinetools.constant.Cons;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.RequestCallback;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OssLoginUtils {

    //已用于记录登录过的OSS服务器
    public static final List<String> ossLogedservers = new ArrayList<>();

    public static int looperNum = 0;


    public static void userLogin(final int loginType, final Context context, final String userName,
                                 final String password, final
                                 String isSelectServer,BaseView baseView) {
        OssLoginUtils.ossLogedservers.clear();
        DialogUtils.getInstance().closeLoadingDialog();
        ossLogin(loginType, context, userName, password, isSelectServer,baseView);

    }


    /**
     * oss登录接口:按钮添加禁止点击，然后开启
     *
     * @param context
     * @param userName
     * @param password
     * @param loginType      :1：代表oss超时重新登录；2：代表server超时重新登录；0和其他：代表正常登录
     * @param isSelectServer 0是轮询  1是选择
     */
    public static void ossLogin(final int loginType, final Context context, final String userName,
                                final String password, final
                                String isSelectServer,BaseView baseView) {
        //当前登录的服务器
        String url = ShineToolsApi.getUrl();
        //当前默认  这个一开始是空的 所以
        String ossRealUrl = url.replace("http://", "").replace("https://", "");
        ossLogedservers.add(ossRealUrl);


        ShineToolsApi.login(context,userName,password,new RequestCallback(new BaseView() {
            @Override
            public void startView() {

            }

            @Override
            public void loadingView() {

            }

            @Override
            public void successView(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int result = jsonObject.getInt("result");
                    String msg = jsonObject.optString("msg");
                    JSONObject obj = jsonObject.optJSONObject("obj");
                    List<String> urls = new ArrayList<>();
                    List<String> urlNames = new ArrayList<>();

                    if (obj != null) {
                        String ossServerUrl2 = obj.optString("ossServerUrl2");
                        String ossServerName = obj.optString("ossServerUrl2Name");
                        String[] servers = ossServerUrl2.split(",,");
                        String[] ossServerUrl2Name = ossServerName.split(",,");
                        Collections.addAll(urls, servers);
                        Collections.addAll(urlNames, ossServerUrl2Name);
                        boolean multiserverUser = obj.optBoolean("isMultiserverUser", false);
                        //如果结果不是1，进行轮询调用返回的列表进行登录
                        switch (result) {
                            case 1://正常
                                DialogUtils.getInstance().closeLoadingDialog();
                                looperNum = 0;
                                //用户类型
                                if (obj != null) {
                                    //把登录记录清空
                                    ossLogedservers.clear();
                                    //将这个OSS的UrL保存到本地数据库
                                    SqliteUtil.addUrl(ossRealUrl);
                                    //oss用户
                                    //oss登录超时
                                    if (loginType == 1) {
                                        return;
                                    }
                                    baseView.successView(json);

                                }
                                break;

                            case 6:
                                //服务器已经知道用户存在
                                // multiserverUser=true时用户存在多个服务器 弹框选择服务器ossServerUrl2
                                //multiserverUser=false时用户只存在多个服务器，因此需要自动轮询服务器ossServerUrl2
                                ossShowServerDialog(loginType, context, userName, password,
                                         urls, urlNames, msg, result, multiserverUser,baseView);
                                break;

                 /*       case 0://超时
                        case 2://服务器错误
                        case 3://用户名错误*/
                            default:
                                Cons.setOssRealUrl("");
                                ossLooperLogin(loginType, context, userName, password,
                                         isSelectServer, urls, msg, result,baseView);
                                break;
                        }
                    } else {
                        DialogUtils.getInstance().closeLoadingDialog();
                        ToastUtils.show(msg);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    jumpLoginActivity(context);
                }
            }

            @Override
            public void errorView(String errorMsg) {
                String[] servers = new String[]{
                        "oss-cn.growatt.com",
                        "oss-us.growatt.com",
                        "oss.growatt.com"
                };
                List<String> urls = new ArrayList<>();
                Collections.addAll(urls, servers);
                ossLooperLogin(loginType, context, userName, password,
                         isSelectServer, urls, errorMsg, -1,baseView);
            }
        }));




    }


    //Oss进行轮询请求
    public static void ossLooperLogin(final int loginType, final Context context, final String userName,
                                      final String password, final
                                      String isSelectServer, List<String> urls, String msg, int result,BaseView baseView) {


        DialogUtils.getInstance().closeLoadingDialog();

        int index = -1;
        for (int i = 0; i < urls.size(); i++) {
            String server = urls.get(i);
            if (!ossLogedservers.contains(server)) {
                index = i;
                break;
            }
        }

        if ("1".equals(isSelectServer) || index == -1 || looperNum > 3) {//说明已经轮询完了
            DialogUtils.getInstance().closeLoadingDialog();
            Cons.setOssRealUrl("");
            looperNum = 0;
            switch (result) {
                case -1:
                    jumpLoginActivity(context);
                    break;

                case 0:
                    ToastUtils.show(R.string.all_http_failed);
                    jumpLoginActivity(context);
                    break;
                case 2:
                    ToastUtils.show(R.string.服务器错误);
                    jumpLoginActivity(context);
                    break;

                default:
                    ToastUtils.show(R.string.all_login_error);
                    jumpLoginActivity(context);
                    break;
            }

        } else {
            //否则轮询登录
            looperNum++;
            String ossUrl = urls.get(index);
            Cons.setOssRealUrl(ossUrl);
            ossLogin(loginType, context, userName, password, String.valueOf(0),baseView);
        }

    }


    /**
     * 弹框登录
     *
     * @param multiserverUser 是否存在多个服务器
     * @param urls
     */
    public static void ossShowServerDialog(final int loginType, final Context context, final String userName,
                                           final String password, final
                                           List<String> urls, List<String> urlNames, String msg, int result,
                                           boolean multiserverUser,BaseView baseView) {
        if (multiserverUser) {//弹框让用户选择
            DialogUtils.getInstance().closeLoadingDialog();
            CircleDialogUtils.showCommentItemDialog2((FragmentActivity) context,
                    context.getString(R.string.choose_state),
                    context.getString(R.string.choose_tips),
                    urlNames, Gravity.CENTER, context.getString(R.string.all_no), new OnLvItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String ossUrl = urls.get(position);
                            Cons.setOssRealUrl(ossUrl);
                            looperNum = 0;
                            //请求登录
                            userLogin(loginType, context, userName, password,
                                     String.valueOf(1),baseView);
                            return true;
                        }
                    }, null);
        } else {
            //轮询
            ossLooperLogin(loginType, context, userName, password,
                     String.valueOf(0), urls, msg, result,baseView);
        }
    }


    /**
     * 跳转到登录界面
     *
     * @param context
     */
    public static void jumpLoginActivity(Context context) {
        if (!(context instanceof LoginActivity)) {
            jumpActivity(context, LoginActivity.class);
        }
    }


    /**
     * 跳转到指定界面
     *
     * @param context
     * @param clazz
     */
    public static void jumpActivity(Context context, Class<?> clazz) {
        try {
            if (context == null) {
                context = ShineToosApplication.getContext();
            }
            if (context instanceof Activity) {
                Activity act = (Activity) context;
                act.startActivity(new Intent(act, clazz));
                act.finish();
            } else {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
