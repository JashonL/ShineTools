package com.growatt.shinetools.module.localbox.mix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;

public class MIXToolConfigActivity extends DemoBase {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles;
    private String mTitle;
    private String[] registers;

    private int user_type = END_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixtool_config);
        ButterKnife.bind(this);
        user_type = ShineToosApplication.getContext().getUser_type();
        initIntent();
        initHeaderView();
        initString();
        initRecyclerView();
        initListener();
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }

    private void initString() {

        if (user_type == END_USER) {
        //11,19,20,
            titles = new String[]{
                    getString(R.string.m396开关逆变器),
                    getString(R.string.m398有功功率百分比),
                    getString(R.string.m405运行PF为1),
                    getString(R.string.m402感性PF),
                    getString(R.string.m399感性载率)
                    , getString(R.string.m400容性载率),
                    getString(R.string.m401容性PF),
                    getString(R.string.m默认PF曲线),
                    getString(R.string.m422无功曲线切入切出电压),
                    getString(R.string.m391PF限制负载百分比点)
                    , getString(R.string.m392PF限制功率因数),
                    getString(R.string.m425启动时间),
                    getString(R.string.m426故障恢复后重启延迟时间),
                    getString(R.string.m418电源启动重启斜率),
                    getString(R.string.m防逆流设置),
                    getString(R.string.m防逆流功率百分比)
//                ,getString(R.string.m防逆流失效后默认功率百分比),getString(R.string.m功率计) ,getString(R.string.m干接点状态)
//                ,getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能)
                    , getString(R.string.m406过频降额起点)
//                ,getString(R.string.m407频率负载限制率)
                    , getString(R.string.m409过频降额延时)

//                , getString(R.string.m416指定的规格设置使能),getString(R.string.m411Island使能)
                    , getString(R.string.m408Qv无功延时), getString(R.string.m410Qv曲线Q最大值), getString(R.string.m419Qv切入切出高压), getString(R.string.m420Qv切入切出低压), getString(R.string.m421Qv切入切出功率)
//                ,getString(R.string.m手动离网使能)
                    , getString(R.string.m离网使能), getString(R.string.m离网电压), getString(R.string.m离网频率)
                    , getString(R.string.mCT种类选择), getString(R.string.m电池种类)
                    , getString(R.string.使能SPI), getString(R.string.使能低电压穿越)
                    , getString(R.string.防逆流失效标志)
//                ,getString(R.string.降载点设置)


            };
            registers = new String[]{
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
            };
        } else {
            titles = new String[]{
                    getString(R.string.m396开关逆变器), getString(R.string.m398有功功率百分比), getString(R.string.m405运行PF为1), getString(R.string.m402感性PF), getString(R.string.m399感性载率)
                    , getString(R.string.m400容性载率), getString(R.string.m401容性PF), getString(R.string.m默认PF曲线), getString(R.string.m422无功曲线切入切出电压), getString(R.string.m391PF限制负载百分比点)
                    , getString(R.string.m392PF限制功率因数), getString(R.string.m417MPPT使能), getString(R.string.m425启动时间), getString(R.string.m426故障恢复后重启延迟时间), getString(R.string.m418电源启动重启斜率), getString(R.string.m防逆流设置), getString(R.string.m防逆流功率百分比)
//                ,getString(R.string.m防逆流失效后默认功率百分比),getString(R.string.m功率计) ,getString(R.string.m干接点状态)
//                ,getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能)
                    , getString(R.string.m406过频降额起点)
//                ,getString(R.string.m407频率负载限制率)
                    , getString(R.string.m409过频降额延时), getString(R.string.m397安规功能使能), getString(R.string.m413电网N线使能)
//                , getString(R.string.m416指定的规格设置使能),getString(R.string.m411Island使能)
                    , getString(R.string.m408Qv无功延时), getString(R.string.m410Qv曲线Q最大值), getString(R.string.m419Qv切入切出高压), getString(R.string.m420Qv切入切出低压), getString(R.string.m421Qv切入切出功率)
//                ,getString(R.string.m手动离网使能)
                    , getString(R.string.m离网使能), getString(R.string.m离网电压), getString(R.string.m离网频率)
                    , getString(R.string.mCT种类选择), getString(R.string.m电池种类)
                    , getString(R.string.使能SPI), getString(R.string.使能低电压穿越)
                    , getString(R.string.防逆流失效标志)
//                ,getString(R.string.降载点设置)


            };
            registers = new String[]{
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "",
            };
        }


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

        if (user_type==END_USER){
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Class clazz = null;
                    int type = -1;
                    int pos = -1;
                    switch (position) {
                        case 0:
                            type = 0;
                            pos = 0;
                            break;
                        case 1:
                            type = 9;
                            pos = 0;
                            break;
                        case 2:
                            type = 0;
                            pos = 4;
                            break;
                        case 3:
                            type = 9;
                            pos = 4;
                            break;
                        case 4:
                            type = 9;
                            pos = 1;
                            break;

                        case 5:
                            type = 9;
                            pos = 2;
                            break;
                        case 6:
                            type = 9;
                            pos = 3;
                            break;
                        case 7:
                            type = 0;
                            pos = 27;
                            break;
                        case 8:
                            type = 2;
                            pos = 4;
                            break;
                        case 9:
                            type = 7;
                            pos = 0;
                            break;

                        case 10:
                            type = 7;
                            pos = 1;
                            break;

                        case 11:
                            type = 1;
                            pos = 10;
                            break;
                        case 12:
                            type = 1;
                            pos = 11;
                            break;
                        case 13:
                            type = 2;
                            pos = 0;
                            break;

                        case 14:
                            type = 0;
                            pos = 37;
                            break;
                        case 15:
                            type = 1;
                            pos = 18;
                            break;


//                    case 17:type = 1;pos = 19;break;
//                    case 18:type = 0;pos = 22;break;
//                    case 19:type = 0;pos = 17;break;
//                    case 20:type = 1;pos = 20;break;
//                    case 21:type = 1;pos = 30;break;
//                    case 22:type = 0;pos = 8;break;
//                    case 23:type = 0;pos = 9;break;

                        case 16:
                            type = 1;
                            pos = 4;
                            break;

//                    case 25:type = 1;pos = 5;break;
                        case 17:
                            type = 1;
                            pos = 7;
                            break;
//                    case 29:type = 0;pos = 10;break;
//                    case 30:type = 0;pos = 5;break;
                        case 18:
                            type = 1;
                            pos = 6;
                            break;
                        case 19:
                            type = 1;
                            pos = 8;
                            break;
                        case 20:
                            type = 2;
                            pos = 1;
                            break;
                        case 21:
                            type = 2;
                            pos = 2;
                            break;

                        case 22:
                            type = 2;
                            pos = 3;
                            break;
//                    case 36:type = 0;pos = 23;break;
                        //修改部分
                        case 23:
                            type = 0;
                            pos = 29;
                            break;
                        case 24:
                            type = 0;
                            pos = 30;
                            break;
                        case 25:
                            type = 0;
                            pos = 31;
                            break;

//                    case 40:type = 1;pos = 23;break;
//                    case 41:type = 1;pos = 24;break;
//                    case 42:type = 0;pos = 18;break;
//                    case 43:type = 0;pos = 19;break;
                        case 26:
                            type = 0;
                            pos = 32;
                            break;
                        case 27:
                            type = 0;
                            pos = 33;
                            break;

                        case 28:
                            type = 10;
                            pos = 0;
                            break;
                        case 29:
                            type = 10;
                            pos = 1;
                            break;
                        case 30:
                            type = 0;
                            pos = 34;
                            break;
//                    case 45:type = 1;pos = 31;break;
                    }
                    switch (type) {
                        case 0:
//                        clazz = TLXConfigTypeSelectActivity.class;
                            clazz = MixConfigTypeSelectActivity.class;
                            break;
                        case 1:
//                        clazz = TLXConfigType1Activity.class;
                            clazz = MixConfigType1Activity.class;
                            break;
                        case 2:
//                        clazz = TLXConfigType2Activity.class;
                            clazz = MixConfigType2Activity.class;
                            break;
                        case 7:
//                        clazz = TLXConfigType4Activity.class;
                            clazz = MixConfigType4Activity.class;
                            break;
                        case 9:
//                        clazz = TLXConfigType1AndPFActivity.class;
                            clazz = MixConfigType1AndPFActivity.class;
                            break;
                        case 10:
                            clazz = MixSPIActivity.class;
                            break;
                    }
                    if (clazz != null) {
                        Intent intent = new Intent(mContext, clazz);
                        intent.putExtra("type", pos);
                        intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                        jumpTo(intent, false);
                    }
                }
            });
        }else {
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Class clazz = null;
                    int type = -1;
                    int pos = -1;
                    switch (position) {
                        case 0:
                            type = 0;
                            pos = 0;
                            break;
                        case 1:
                            type = 9;
                            pos = 0;
                            break;
                        case 2:
                            type = 0;
                            pos = 4;
                            break;
                        case 3:
                            type = 9;
                            pos = 4;
                            break;
                        case 4:
                            type = 9;
                            pos = 1;
                            break;

                        case 5:
                            type = 9;
                            pos = 2;
                            break;
                        case 6:
                            type = 9;
                            pos = 3;
                            break;
                        case 7:
                            type = 0;
                            pos = 27;
                            break;
                        case 8:
                            type = 2;
                            pos = 4;
                            break;
                        case 9:
                            type = 7;
                            pos = 0;
                            break;

                        case 10:
                            type = 7;
                            pos = 1;
                            break;
                        case 11:
                            type = 0;
                            pos = 36;
                            break;
                        case 12:
                            type = 1;
                            pos = 10;
                            break;
                        case 13:
                            type = 1;
                            pos = 11;
                            break;
                        case 14:
                            type = 2;
                            pos = 0;
                            break;

                        case 15:
                            type = 0;
                            pos = 37;
                            break;
                        case 16:
                            type = 1;
                            pos = 18;
                            break;


//                    case 17:type = 1;pos = 19;break;
//                    case 18:type = 0;pos = 22;break;
//                    case 19:type = 0;pos = 17;break;
//                    case 20:type = 1;pos = 20;break;
//                    case 21:type = 1;pos = 30;break;
//                    case 22:type = 0;pos = 8;break;
//                    case 23:type = 0;pos = 9;break;

                        case 17:
                            type = 1;
                            pos = 4;
                            break;

//                    case 25:type = 1;pos = 5;break;
                        case 18:
                            type = 1;
                            pos = 7;
                            break;
                        case 19:
                            type = 1;
                            pos = 29;
                            break;
                        case 20:
                            type = 0;
                            pos = 7;
                            break;
//                    case 29:type = 0;pos = 10;break;
//                    case 30:type = 0;pos = 5;break;
                        case 21:
                            type = 1;
                            pos = 6;
                            break;
                        case 22:
                            type = 1;
                            pos = 8;
                            break;
                        case 23:
                            type = 2;
                            pos = 1;
                            break;
                        case 24:
                            type = 2;
                            pos = 2;
                            break;

                        case 25:
                            type = 2;
                            pos = 3;
                            break;
//                    case 36:type = 0;pos = 23;break;
                        //修改部分
                        case 26:
                            type = 0;
                            pos = 29;
                            break;
                        case 27:
                            type = 0;
                            pos = 30;
                            break;
                        case 28:
                            type = 0;
                            pos = 31;
                            break;

//                    case 40:type = 1;pos = 23;break;
//                    case 41:type = 1;pos = 24;break;
//                    case 42:type = 0;pos = 18;break;
//                    case 43:type = 0;pos = 19;break;
                        case 29:
                            type = 0;
                            pos = 32;
                            break;
                        case 30:
                            type = 0;
                            pos = 33;
                            break;

                        case 31:
                            type = 10;
                            pos = 0;
                            break;
                        case 32:
                            type = 10;
                            pos = 1;
                            break;
                        case 33:
                            type = 0;
                            pos = 34;
                            break;
//                    case 45:type = 1;pos = 31;break;
                    }
                    switch (type) {
                        case 0:
//                        clazz = TLXConfigTypeSelectActivity.class;
                            clazz = MixConfigTypeSelectActivity.class;
                            break;
                        case 1:
//                        clazz = TLXConfigType1Activity.class;
                            clazz = MixConfigType1Activity.class;
                            break;
                        case 2:
//                        clazz = TLXConfigType2Activity.class;
                            clazz = MixConfigType2Activity.class;
                            break;
                        case 7:
//                        clazz = TLXConfigType4Activity.class;
                            clazz = MixConfigType4Activity.class;
                            break;
                        case 9:
//                        clazz = TLXConfigType1AndPFActivity.class;
                            clazz = MixConfigType1AndPFActivity.class;
                            break;
                        case 10:
                            clazz = MixSPIActivity.class;
                            break;
                    }
                    if (clazz != null) {
                        Intent intent = new Intent(mContext, clazz);
                        intent.putExtra("type", pos);
                        intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                        jumpTo(intent, false);
                    }
                }
            });
        }


    }

    /**
     * 多布局定义
     */
    private void initRecyclerView() {
        mList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MaxConfigMuiltAdapter(R.layout.item_maxconfig_type0, mList);
        mRecyclerView.setAdapter(mAdapter);
        initData(titles, mAdapter);
    }

    private void initData(String[] titles, MaxConfigMuiltAdapter adapter) {
        List<MaxConfigBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MaxConfigBean bean = new MaxConfigBean();
//            bean.setTitle(i + "." +titles[i]);
            bean.setTitle(String.format("%d.%s%s", i + 1, titles[i], registers[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
