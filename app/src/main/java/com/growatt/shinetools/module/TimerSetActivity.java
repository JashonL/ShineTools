package com.growatt.shinetools.module;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.TLXHChargePriorityBean;
import com.growatt.shinetools.widget.NumberPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 时段设置
 */
public class TimerSetActivity extends DemoBase {
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.np_choose1)
    NumberPicker mNpChoose1;
    @BindView(R.id.np_choose2)
    NumberPicker mNpChoose2;
    @BindView(R.id.np_choose1_end)
    NumberPicker mNpChoose1End;
    @BindView(R.id.np_choose2_end)
    NumberPicker mNpChoose2End;
    private String[] hours;
    private String[] minutes;
    private TLXHChargePriorityBean mChargeBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_set);
        ButterKnife.bind(this);
        initResource();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 重要通知 来自极光接收器
     * @param eventBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventPriorityBean(@NonNull TLXHChargePriorityBean eventBean) {
        mChargeBean = eventBean;
        try {
            //给控件赋值
            String timePeriod = mChargeBean.getTimePeriod();
            if ((!TextUtils.isEmpty(timePeriod)) && timePeriod.contains("~")){
                String[] splitTimes = timePeriod.split("~");
                String startTimes = splitTimes[0];
                String endTimes = splitTimes[1];
                String[] splitStarts = startTimes.split(":");
                String[] splitEnds = endTimes.split(":");
                int startHour = Integer.valueOf(splitStarts[0]);
                int startMin = Integer.valueOf(splitStarts[1]);
                int endHour = Integer.valueOf(splitEnds[0]);
                int endMin = Integer.valueOf(splitEnds[1]);
                if (startHour>=0 && startHour <= 23){
                    mNpChoose1.setValue(startHour);
                }
                if (startMin>=0 && startMin <= 59){
                    mNpChoose2.setValue(startMin);
                }
                if (endHour>=0 && endHour <= 23){
                    mNpChoose1End.setValue(endHour);
                }
                if (endMin>=0 && endMin <= 59){
                    mNpChoose2End.setValue(endMin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().removeStickyEvent(eventBean);
    }
    private void initResource() {
        mTvTitle.setText(R.string.时间段设置);
        mTvRight.setText(R.string.m182保存);
        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours[i] = "0" + String.valueOf(i);
            } else {
                hours[i] = String.valueOf(i);
            }
        }
        minutes = new String[60];
        for (int i = 0; i < minutes.length; i++) {
            if (i < 10) {
                minutes[i] = "0" + String.valueOf(i);
            } else {
                minutes[i] = String.valueOf(i);
            }
        }
        initData(mNpChoose1, hours, "00");
        initData(mNpChoose2, minutes, "00");
        initData(mNpChoose1End, hours, "00");
        initData(mNpChoose2End, minutes, "00");
    }

    private static void initData(NumberPicker numberPicker, String[] values, String value) {
        //展示值，一个数组
        numberPicker.setDisplayedValues(values);
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i])) {
                index = i;
                break;
            }
        }
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        //设置当前值
        numberPicker.setValue(index);
    }

    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                if (mChargeBean != null){
                    mChargeBean.setTimePeriod(String.format("%s:%s~%s:%s"
                            , hours[mNpChoose1.getValue()]
                            , minutes[mNpChoose2.getValue()]
                            , hours[mNpChoose1End.getValue()]
                            , minutes[mNpChoose2End.getValue()]
                            ));
                }
                finish();
                break;
        }
    }
}
