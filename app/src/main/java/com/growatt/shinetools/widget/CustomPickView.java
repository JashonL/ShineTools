package com.growatt.shinetools.widget;

import android.app.Activity;
import android.content.Context;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2020/2/12.
 * 选择器弹框二次封装
 */

public class CustomPickView {

    public interface IOnPickViewSelectListener {
        void onTimeSelectedListener(Date date);

        void onSelectedListener(int options1, int options2, int options3);
    }

    /**
     * 弹出时间选择器
     */
    public static void showTimePickView(Context context, String title, Calendar selected, boolean yearShow,
                                        boolean monthShow, boolean dayShow, boolean hourShow, boolean minuteShow,
                                        boolean secondShow, String lableYear, String lableMonth, String lableDay, String lableHour,
                                        String lableMin, String lableSecond,
                                        IOnPickViewSelectListener listener) {
        if (selected == null) {
            selected = Calendar.getInstance();//系统当前时间
        }
        TimePickerView pvCustomTime = new TimePickerBuilder(context, (date, v) -> {//选中事件回调
            listener.onTimeSelectedListener(date);
        })
                .setType(new boolean[]{yearShow, monthShow, dayShow, hourShow, minuteShow, secondShow})// 默认全部显示
                .setCancelText(context.getString(R.string.all_no))//取消按钮文字
                .setSubmitText(context.getString(R.string.all_ok))//确认按钮文字
                .setContentTextSize(18)
                .setTitleSize(18)//标题文字大小
                .setTitleText(title)//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(0xff333333)//标题文字颜色
                .setSubmitColor(0xff009cff)//确定按钮文字颜色
                .setCancelColor(0xff666666)//取消按钮文字颜色
                .setTitleBgColor(0xffffffff)//标题背景颜色 Night mode
                .setDividerColor(0xff666666)
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
                .setTextColorCenter(0xff666666)
                .setDate(selected)// 如果不设置的话，默认是系统时间*/
                .setLabel(lableYear, lableMonth, lableDay,
                        lableHour, lableMin, lableSecond)//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvCustomTime.show();
    }


    /**
     * 普通选项
     */
    public static <T> void showPickView(Context context, String title, List<T> list1, List<T> list2, List<T> list3,
                                        int options1Selected1, int options1Selected2, int options1Selected3,
                                        IOnPickViewSelectListener listener) {
        OptionsPickerView<T> pvOptions = new OptionsPickerBuilder(context, (options1, options2, options3, v) -> listener.onSelectedListener(options1, options2, options3))
                .setTitleText(title)
                .setCancelText(context.getString(R.string.all_no))
                .setSubmitText(context.getString(R.string.all_ok))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff009cff)
                .setCancelColor(0xff666666)
                .setBgColor(0xffffffff)
                .setTitleSize(16)
                .setDividerColor(0xff666666)
                .setTextColorCenter(0xff666666)
                .setSelectOptions(options1Selected1, options1Selected2, options1Selected3)
                .build();
        pvOptions.setPicker(list1);
        pvOptions.show();
    }



    /**
     * 弹出滚动选择器
     *
     * @param data     数据源
     * @param title    选择器标题
     */
    public static void showPickView(final Activity context, final List<String> data, OnOptionsSelectListener listener, String title) {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(context,listener)
                .setTitleText(title)
                .setCancelText(ShineToosApplication.getContext().getString(R.string.m427语言))//取消按钮文字
                .setSubmitText(ShineToosApplication.getContext().getString(R.string.all_ok))//确认按钮文字
                .setTitleBgColor(ContextCompat.getColor(context,R.color.white_background))
                .setTitleColor(ContextCompat.getColor(context,R.color.title_2))
                .setSubmitColor(ContextCompat.getColor(context,R.color.headerView))
                .setCancelColor(ContextCompat.getColor(context,R.color.title_3))
                .setBgColor(ContextCompat.getColor(context,R.color.white_background))
                .setTitleSize(22)
                .setTextColorCenter(ContextCompat.getColor(context,R.color.title_1))
                .build();
        pvOptions.setPicker(data);
        pvOptions.show();
    }


}
