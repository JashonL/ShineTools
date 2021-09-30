package com.growatt.shinetools.module.localbox.ustool;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.widget.NumberPicker;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 时段设置
 */
public class DryTimeSelectActivity extends BaseActivity{


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



    private int position=0;

    @Override
    protected int getContentView() {
        return R.layout.activity_dry_time_select;
    }

    @Override
    protected void initViews() {
        mTvTitle.setText(R.string.时间段设置);
        mTvRight.setText(R.string.m182保存);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initResource();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }




    private void initResource() {

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
                minutes[i] = "0" + i;
            } else {
                minutes[i] = String.valueOf(i);
            }
        }
        initData(mNpChoose1, hours, "00");
        initData(mNpChoose2, minutes, "00");
        initData(mNpChoose1End, hours, "00");
        initData(mNpChoose2End, minutes, "00");


        timeParser();
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





    //初始化数据
    public void timeParser() {
        String time = getIntent().getStringExtra(GlobalConstant.KEY_JSON);
        if (!TextUtils.isEmpty(time)){
            TimeBean bean = new Gson().fromJson(time,TimeBean.class);
            int startHour = bean.getStartHour();
            int startMin = bean.getStartMin();
            int endHour = bean.getEndHour();
            int endMin = bean.getEndMin();
            if (startHour >= 0 && startHour <= 23) {
                mNpChoose1.setValue(startHour);
            }
            if (startMin >= 0 && startMin <= 59) {
                mNpChoose2.setValue(startMin);
            }
            if (endHour >= 0 && endHour <= 23) {
                mNpChoose1End.setValue(endHour);
            }
            if (endMin >= 0 && endMin <= 59) {
                mNpChoose2End.setValue(endMin);
            }
        }
    }




    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                TimeBean timeBean = new TimeBean();
                timeBean.setStartHour(mNpChoose1.getValue());
                timeBean.setStartMin(mNpChoose2.getValue());

                timeBean.setEndHour(mNpChoose1End.getValue());
                timeBean.setEndMin(mNpChoose2End.getValue());

                timeBean.setPosition(position);
                EventBus.getDefault().post(timeBean);
                finish();
                break;
        }
    }




    public static class TimeBean{

        private int position;

        private int startHour;
        private int startMin;

        private int endHour;
        private int endMin;

        public int getStartHour() {
            return startHour;
        }

        public void setStartHour(int startHour) {
            this.startHour = startHour;
        }

        public int getStartMin() {
            return startMin;
        }

        public void setStartMin(int startMin) {
            this.startMin = startMin;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMin() {
            return endMin;
        }

        public void setEndMin(int endMin) {
            this.endMin = endMin;
        }


        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

}
