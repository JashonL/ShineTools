package com.growatt.shinetools.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.modbusbox.SocketClientUtil;


/**
 * Created by dg on 2017/11/4.
 */

public class BtnDelayUtil {
    //延时禁用按钮
    public static final int NO_CLICK_DELAY = 301;
    //恢复按钮
    public static final int YES_CLICK = 302;
    //接收超时设置
    public static final int TIMEOUT_RECEIVE = 303;
    //马上只禁用按钮
    public static final int NO_CLICK_NOW = 304;
    //断开tcp
    public static final int NO_TCP = 305;
    /**
     * 禁用按钮:读取
     */
    public static void isNoClick(Handler handler, int type , View... views) {
       isNoClick(handler,type,1000,views);
    }
    /**
     * 禁用按钮:写
     */
    public static void isNoClickW(Handler handler, int type , View... views) {
        isNoClick(handler,type,1500,views);
    }
    /**
     * 禁用按钮：延迟禁用
     */
    public static void isNoClick(Handler handler, int type , int time, View... views) {
       isNoClickOnly(views);
        if (time != -1) {
            handler.removeMessages(type);
            handler.sendEmptyMessageDelayed(type, time);
        }
    }
    public static void isNoClickWhite(Handler handler, int type , int time, View... views) {
       isNoClickOnlyWhite(views);
        if (time != -1) {
            handler.removeMessages(type);
            handler.sendEmptyMessageDelayed(type, time);
        }
    }
    /**
     * 禁用按钮:仅仅禁用
     */
    public static void isNoClickOnly(View... views) {
        if (views == null) return;
        for(View view : views){
            view.setEnabled(false);
            if (view instanceof TextView && !(view instanceof RadioButton)){
                ((TextView) view).setTextColor(Color.parseColor("#80ffffff"));
            }
        }
    }
    public static void isNoClickOnlyWhite(View... views) {
        if (views == null) return;
        for(View view : views){
            view.setEnabled(false);
            if (view instanceof TextView && !(view instanceof RadioButton)){
                ((TextView) view).setTextColor(Color.parseColor("#80484848"));
            }
        }
    }
    /**
     * 恢复按钮
     */
    public static void isClick(View... views) {
        DialogUtils.getInstance().closeLoadingDialog();
        if (views == null) return;
        for(View view : views){
            view.setEnabled(true);
            if (view instanceof TextView && !(view instanceof RadioButton)){
                ((TextView) view).setTextColor(Color.WHITE);
            }
        }
    }
    /**
     * 恢复按钮
     */
    public static void isClickWhite(View... views) {
        DialogUtils.getInstance().closeLoadingDialog();
        if (views == null) return;
        for(View view : views){
            view.setEnabled(true);
            if (view instanceof TextView && !(view instanceof RadioButton)){
                ((TextView) view).setTextColor(Color.parseColor("#484848"));
            }
        }
    }

    /**
     * 刷新完成
     */
    public static void refreshFinish() {
//        Mydialog.Dismiss();
    }

    /**
     * max button  通用管理：用于读取
     * @param handler
     * @param what
     * @param act
     * @param views
     */
    public static void dealMaxBtn(Handler handler, int what, Context act, View... views) {
//        dealMaxBtn(handler,what,1000,act,views);
        dealTLXBtn(handler,what,1000,act,views);
    }
    public static void dealTLXBtn(Handler handler, int what, Context act, View... views) {
        dealTLXBtn(handler,what,1000,act,views);
    }
    /**
     * max button  通用管理：用于写入:时间不同
     * @param handler
     * @param what
     * @param act
     * @param views
     */
    public static void dealMaxBtnWrite(Handler handler, int what, Context act, View... views) {
//        dealMaxBtn(handler,what,1500,act,views);
        dealTLXBtn(handler,what,1500,act,views);
    }
    public static void dealTLXBtnWrite(Handler handler, int what, Context act, View... views) {
        dealTLXBtn(handler,what,1500,act,views);
    }
    /**
     * max button  通用管理：自定义时间
     * @param handler
     * @param what
     * @param act
     * @param views
     */
    public static void dealMaxBtn(Handler handler, int what, int time, Context act, View... views) {
        dealTLXBtn(handler,what,time,act,views);
//        switch (what) {
//            case SocketClientUtil.SOCKET_SERVER_SET://跳转到wifi列表
//                if (act instanceof FragmentActivity){
//                    MyControl.showJumpWifiSet((FragmentActivity) act);
//                }
//                refreshFinish();
//                //断开tcp
//                handler.sendEmptyMessage(NO_TCP);
//                return;
//            case BtnDelayUtil.NO_CLICK_DELAY://禁用按钮:延迟禁用
//                BtnDelayUtil.isNoClick(handler, BtnDelayUtil.YES_CLICK,time,views);
//                break;
//            case BtnDelayUtil.YES_CLICK://恢复按钮
//                BtnDelayUtil.isClick(views);
//                break;
//            case TIMEOUT_RECEIVE://接收超时
//                //弹框显示
//                if (act instanceof FragmentActivity){
//                    MyControl.showJumpWifiSet((FragmentActivity) act);
////                    MyControl.showJumpWifiSet((FragmentActivity) act,"接收数据失败,查看网络连接");
////                    T.dialog(act,"接收失败");
//                }
//                //恢复按钮以及隐藏dialog
//                isClick(views);
//                //断开tcp
//                handler.sendEmptyMessage(NO_TCP);
//                break;
//            case BtnDelayUtil.NO_CLICK_NOW://禁用按钮:马上禁用
//                BtnDelayUtil.isNoClickOnly(views);
//                break;
//            case NO_TCP://断开tcp
//                SocketClientUtil.close(SocketClientUtil.newInstance());
//                break;
//        }
    }
    /**
     * max button  通用管理：自定义时间
     * @param handler
     * @param what
     * @param act
     * @param views
     */
    public static void dealTLXBtn(Handler handler, int what, int time, Context act, View... views) {
        switch (what) {
            case SocketClientUtil.SOCKET_SERVER_SET://跳转到wifi列表
                if (act instanceof FragmentActivity){
                    MyControl.showJumpWifiSet((FragmentActivity) act);
                }
                refreshFinish();
                //断开tcp
                handler.sendEmptyMessage(NO_TCP);
                return;
            case BtnDelayUtil.NO_CLICK_DELAY://禁用按钮:延迟禁用
                BtnDelayUtil.isNoClickWhite(handler, BtnDelayUtil.YES_CLICK,time,views);
                break;
            case BtnDelayUtil.YES_CLICK://恢复按钮
                BtnDelayUtil.isClickWhite(views);
                break;
            case TIMEOUT_RECEIVE://接收超时
                //弹框显示
                if (act instanceof FragmentActivity){
                    MyControl.showJumpWifiSet((FragmentActivity) act);
//                    MyControl.showJumpWifiSet((FragmentActivity) act,"接收数据失败,查看网络连接");
//                    T.dialog(act,"接收失败");
                }
                //恢复按钮以及隐藏dialog
                isClickWhite(views);
                //断开tcp
                handler.sendEmptyMessage(NO_TCP);
                break;
            case BtnDelayUtil.NO_CLICK_NOW://禁用按钮:马上禁用
                BtnDelayUtil.isNoClickOnlyWhite(views);
                break;
            case NO_TCP://断开tcp
                SocketClientUtil.close(SocketClientUtil.newInstance());
                break;
        }
    }
    /**
     * 发送消息处理器:用于读取handler
     * @param handler
     */
    public static void sendMessage(Handler handler){
        sendMessage(handler,3500);
    }
    /**
     * 发送消息处理器:用于写入handler
     * @param handler
     */
    public static void sendMessageWrite(Handler handler){
        sendMessage(handler,3500);
    }
    /**
     * 发送消息处理器
     * @param handler
     */
    public static void sendMessage(Handler handler, int timeout){
        //马上禁用按钮
        handler.sendEmptyMessage(BtnDelayUtil.NO_CLICK_NOW);
        //接收消息超时设置
        handler.sendEmptyMessageDelayed(TIMEOUT_RECEIVE,timeout);
    }
    /**
     * 接收消息处理器
     * @param handler
     */
    public static void receiveMessage(Handler handler){
        //移除接收超时
        handler.removeMessages(TIMEOUT_RECEIVE);
        //禁用按钮
        handler.sendEmptyMessage(BtnDelayUtil.NO_CLICK_DELAY);
    }
    /**
     * 发送消息处理器
     * @param handler
     */
    public static void sendMessageNoClick(Handler handler, int timeout){
        //马上禁用按钮
//        handler.sendEmptyMessage(BtnDelayUtil.NO_CLICK_NOW);
        //接收消息超时设置
        handler.sendEmptyMessageDelayed(TIMEOUT_RECEIVE,timeout);
    }
    /**
     * 接收消息处理器
     * @param handler
     */
    public static void receiveMessageNoClick(Handler handler){
        //移除接收超时
        handler.removeMessages(TIMEOUT_RECEIVE);
        //禁用按钮
//        handler.sendEmptyMessage(BtnDelayUtil.NO_CLICK_DELAY);
    }
}
