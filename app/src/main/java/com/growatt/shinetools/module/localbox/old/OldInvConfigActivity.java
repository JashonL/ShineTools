package com.growatt.shinetools.module.localbox.old;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.OldInvConfigTypeSelectActivity;
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OldInvConfigActivity extends DemoBase {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles;
    private String mTitle;
    private String[] registers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_config);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initString();
        initRecyclerView();
        initListener();
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
        }
    }
    private void initString() {
        titles = new String[]{
                getString(R.string.m396开关逆变器),getString(R.string.m397安规功能使能),getString(R.string.m398有功功率百分比),getString(R.string.m399感性载率),getString(R.string.m400容性载率),
                getString(R.string.m401容性PF),getString(R.string.m402感性PF),getString(R.string.m403PV电压),getString(R.string.m405运行PF为1),
                getString(R.string.m406过频降额起点),getString(R.string.m407频率负载限制率),getString(R.string.m408Qv无功延时),getString(R.string.m409过频降额延时),getString(R.string.m410Qv曲线Q最大值),
                getString(R.string.m411Island使能),getString(R.string.m412风扇检查),getString(R.string.m413电网N线使能),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能),
                getString(R.string.m416指定的规格设置使能),getString(R.string.m417MPPT使能),getString(R.string.m418电源启动重启斜率),getString(R.string.m419Qv切入切出高压),getString(R.string.m420Qv切入切出低压),
                getString(R.string.m421Qv切入切出功率),getString(R.string.m422无功曲线切入切出电压),getString(R.string.m389检查固件),getString(R.string.m390PF调整值),getString(R.string.m391PF限制负载百分比点),
                getString(R.string.m392PF限制功率因数),getString(R.string.m防逆流设置),getString(R.string.m防逆流功率百分比),getString(R.string.m防逆流失效后默认功率百分比)
        };
        registers = new String[]{
                "(0)","(1)" ,"(3)","(4)","(4)","(5)","(5)","(8)","(99)",
                "(81)","(100)" ,"(165)","(166)","(168)","(144)","(150)","(151)","(192)","(194)",
                "(193)","(163|83)" ,"(82/131)","(108/109)","(110/111)","(113/118)","(114/115)","153","(101~106)","1~4(90/92/94/96)",
                "1~4(91/93/95/97)","(202)","(201)","(203)"
        };
    }

    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
    }


    private void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(position == 18
            || position == 12
            || position == 20
            ){
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.reminder))
                        .setText(getString(R.string.m1256是否为单相逆变器) + "?")
                        .setGravity(Gravity.CENTER)
                        .setWidth(0.7f)
                        .setNegative(getString(R.string.all_no),view1 -> {
                            setPos(position, false);
                        })
                        .setPositive(getString(R.string.all_ok),view1 -> {
                            setPos(position, true);
                        })
                        .show(getSupportFragmentManager());
            }else {
                setPos(position, false);
            }
        });
    }

    /**
     *
     * @param position
     * @param isSingle 是否为单项机
     */
    private void setPos(int position,boolean isSingle) {
        String title = "";
        Class clazz = null;
        int type = -1;
        int pos = -1;
        switch (position){
            case 0:type = 0;pos = 0;break;
            case 1:type = 1;pos = 29;break;
            case 2:type = 9;pos = 0;break;
            case 3:type = 9;pos = 1;break;
            case 4:type = 9;pos = 2;break;
            case 5:type = 9;pos = 3;break;
            case 6:type = 9;pos = 4;break;
            case 7:type = 1;pos = 3;break;
            case 8:type = 0;pos = 4;break;
            case 9:type = 1;pos = 4;break;
            case 10:type = 1;pos = 5;break;
            case 11:type = 1;pos = 6;break;
            case 12:
                if (isSingle){
                    type = 1;
                    pos = 32;
                }else {
                    type = 1;
                    pos = 7;
                }

            break;
            case 13:type = 1;pos = 8;break;
            case 14:type = 0;pos = 5;break;
            case 15:type = 0;pos = 6;break;
            case 16:type = 0;pos = 7;break;
            case 17:type = 0;pos = 8;break;
            case 18:
                if (isSingle){
                    type = 0;
                    pos = 29;
                }else {
                    type = 0;
                    pos = 9;
                }
            break;
            case 19:type = 0;pos = 10;break;
            case 20:
                if (isSingle){
                    type = 0;
                    pos = 30;
                    title = "(83)";
                }else {
                    type = 0;
                    pos = 31;
                    title = "(163)";
                }
            break;
            case 21:type = 2;pos = 0;break;
            case 22:type = 2;pos = 1;break;
            case 23:type = 2;pos = 2;break;
            case 24:type = 2;pos = 3;break;
            case 25:type = 2;pos = 4;break;
            case 26:type = 1;pos = 31;break;
            case 27:type = 10;pos = 0;break;
            case 28:type = 7;pos = 2;break;
            case 29:type = 7;pos = 3;break;
            case 30:type = 0;pos = 16;break;
            case 31:type = 1;pos = 18;break;
            case 32:type = 1;pos = 19;break;
        }
        switch (type){
            case 0:
                clazz = OldInvConfigTypeSelectActivity.class;
                break;
            case 1:
                clazz = OldInvConfigType1Activity.class;
                break;
            case 2:
                clazz = OldInvConfigType2Activity.class;
                break;
            case 7:
                clazz = OldInvConfigType4Activity.class;
                break;
            case 9:
                clazz = OldInvConfigType1AndPFActivity.class;
                break;
            case 10:
                clazz = OldInvConfigType6Activity.class;
                break;
        }
        if (clazz != null) {
            Intent intent = new Intent(mContext, clazz);
            intent.putExtra("type", pos);
            intent.putExtra("title", String.format("%s%s",titles[position], TextUtils.isEmpty(title)?registers[position]:title));
            jumpTo(intent, false);
        }
    }

    /**
     * 多布局定义
     */
    private void initRecyclerView() {
        mList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MaxConfigMuiltAdapter(R.layout.item_maxconfig_type0,mList);
        mRecyclerView.setAdapter(mAdapter);
        initData(titles,mAdapter);
    }

    private void initData(String[] titles, MaxConfigMuiltAdapter adapter) {
        List<MaxConfigBean> newList = new ArrayList<>();
        for (int i = 0;i < titles.length ;i++){
            MaxConfigBean bean = new MaxConfigBean();
//            bean.setTitle(i + "." +titles[i]);
            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
