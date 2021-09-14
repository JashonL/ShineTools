package com.growatt.shinetools.module.localbox.mintool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.afci.AFCIChartActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TLXHToolConfigActivity extends DemoBase {

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
        setContentView(R.layout.activity_tlxhtool_config);
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
                getString(R.string.m401容性PF),getString(R.string.m402感性PF),getString(R.string.m403PV电压),getString(R.string.m404选择通信波特率),getString(R.string.m405运行PF为1),
                getString(R.string.m406过频降额起点),getString(R.string.m407频率负载限制率),getString(R.string.m408Qv无功延时),getString(R.string.m409过频降额延时),getString(R.string.m410Qv曲线Q最大值),
                getString(R.string.m411Island使能),getString(R.string.m412风扇检查),getString(R.string.m413电网N线使能),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能),
                getString(R.string.m416指定的规格设置使能),getString(R.string.m417MPPT使能),getString(R.string.m418电源启动重启斜率),getString(R.string.m419Qv切入切出高压),getString(R.string.m420Qv切入切出低压),
                getString(R.string.m421Qv切入切出功率),getString(R.string.m422无功曲线切入切出电压),getString(R.string.m389检查固件),getString(R.string.m390PF调整值),getString(R.string.m391PF限制负载百分比点),
                getString(R.string.m392PF限制功率因数),getString(R.string.m防逆流设置),getString(R.string.m防逆流功率百分比),getString(R.string.m防逆流失效后默认功率百分比)
//
//                        "开关逆变器(0)","安规功能使能(1)","有功功率百分比(3)","感性载率(4)","容性载率(4)","容性PF(5)","感性PF(5)",
//                        "PV电压(8)","选择通信波特率(22)","运行PF为1(89)","过频降额起点(91)",
//                        "频率-负载限制率(92)","Q(v)无功延时(107)","过频降额延时(108)","Q(v)曲线Q最大值(109)","Island使能(230)",
//                        "风扇检查(231)","电网N线使能(232)","N至GND监测功能使能(235)","非标准电网电压范围使能(236)","指定的规格设置使能(237)",
//                        "MPPT使能(238)","电源启动/重启斜率(20/21)","Q(v)切入/切出高压(93/94)","Q(v)切入/切出低压(95/96)","Q(v)切入/切出功率(97/98)",
//                        "无功曲线切入/切出电压(99/100)","检查固件1/2(233/234)","PF调整值(101~106)","PF限制负载百分比点1~4(110/112/114/116)","PF限制功率因数1~4(111/113/115/117)",
        };
        registers = new String[]{
                "(0)","(1)" ,"(3)","(4)","(4)","(5)","(5)","(8)","(22)","(89)",
                "(91)","(92)" ,"(107)","(108)","(109)","(230)","(231)","(232)","(235)","(236)",
                "(237)","(399)" ,"(20/21)","(93/94)","(95/96)","(97/98)","(99/100)","1/2(233/234)","(101~106)","1~4(110/112/114/116)",
                "1~4(111/113/115/117)","(122)","(123)","(3000)"
        };
        titles = new String[]{
                getString(R.string.m396开关逆变器),getString(R.string.m398有功功率百分比),getString(R.string.m405运行PF为1),getString(R.string.m402感性PF),getString(R.string.m399感性载率)
                ,getString(R.string.m400容性载率), getString(R.string.m401容性PF),getString(R.string.m默认PF曲线),getString(R.string.m422无功曲线切入切出电压),getString(R.string.m391PF限制负载百分比点)
                , getString(R.string.m392PF限制功率因数),getString(R.string.m417MPPT使能),getString(R.string.m425启动时间), getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m418电源启动重启斜率)
                ,getString(R.string.m防逆流设置),getString(R.string.m防逆流功率百分比),getString(R.string.m防逆流失效后默认功率百分比),getString(R.string.m功率计) ,getString(R.string.m干接点状态)
                ,getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能), getString(R.string.m406过频降额起点)
                ,getString(R.string.m407频率负载限制率),getString(R.string.m409过频降额延时),getString(R.string.m397安规功能使能),getString(R.string.m413电网N线使能), getString(R.string.m416指定的规格设置使能)
                ,getString(R.string.m411Island使能),getString(R.string.m408Qv无功延时),getString(R.string.m410Qv曲线Q最大值),getString(R.string.m419Qv切入切出高压),getString(R.string.m420Qv切入切出低压)
                , getString(R.string.m421Qv切入切出功率),getString(R.string.m手动离网使能),getString(R.string.m离网使能)  ,getString(R.string.m离网频率),getString(R.string.m离网电压)
                ,getString(R.string.mCV电压),getString(R.string.mCC电流),getString(R.string.mCT种类选择) ,getString(R.string.m电池种类)
                ,getString(R.string.AFCI阈值) + "1" ,getString(R.string.AFCI阈值) + "2" ,getString(R.string.AFCI阈值) + "3" ,getString(R.string.FFT最大累计次数)


//                ,getString(R.string.m390PF调整值)
        };
        registers = new String[]{
                "(0)" ,"(3)","(89)","(5)","(4)"
                ,"(4)","(5)","(89)","(99/100)","1~4(110/112/114/116)"
                , "1~4(111/113/115/117)","(399)", "(18)", "(19)","(20/21)"
                ,"(122)","(123)","(3000)","(533)","(3016)"
                ,"(3017)","(3019)","(235)","(236)","(91)"
                ,"(92)","(108)","(1)","(232)", "(237)"
                ,"(230)","(107)","(109)","(93/94)","(95/96)"
                ,"(97/98)","(3021)","(3079)" ,"(3081)" ,"(3080)"
                ,"(3030)" ,"(3024)" ,"(3068)" ,"(3070)"
                ,"(544)","(545)","(546)","(547)"

//                 ,"(101~106)"
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
        setHeaderTvRight(headerView,"AFCI",view -> {
            jumpTo(AFCIChartActivity.class,false);
        },R.color.blue_1);
    }


    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Class clazz = null;
                int type = -1;
                int pos = -1;
                switch (position){
                    case 0:type = 0;pos = 0;break;
                    case 1:type = 9;pos = 0;break;
                    case 2:type = 0;pos = 4;break;
                    case 3:type = 9;pos = 4;break;
                    case 4:type = 9;pos = 1;break;

                    case 5:type = 9;pos = 2;break;
                    case 6:type = 9;pos = 3;break;
                    case 7:type = 0;pos = 27;break;
                    case 8:type = 2;pos = 4;break;
                    case 9:type = 7;pos = 0;break;

                    case 10:type = 7;pos = 1;break;
                    case 11:type = 0;pos = 11;break;
                    case 12:type = 1;pos = 10;break;
                    case 13:type = 1;pos = 11;break;
                    case 14:type = 2;pos = 0;break;

                    case 15:type = 0;pos = 28;break;
                    case 16:type = 1;pos = 18;break;
                    case 17:type = 1;pos = 19;break;
                    case 18:type = 0;pos = 22;break;
                    case 19:type = 0;pos = 17;break;

                    case 20:type = 1;pos = 20;break;
                    case 21:type = 1;pos = 30;break;
                    case 22:type = 0;pos = 8;break;
                    case 23:type = 0;pos = 9;break;
                    case 24:type = 1;pos = 4;break;

                    case 25:type = 1;pos = 5;break;
                    case 26:type = 1;pos = 7;break;
                    case 27:type = 1;pos = 29;break;
                    case 28:type = 0;pos = 7;break;
                    case 29:type = 0;pos = 10;break;

                    case 30:type = 0;pos = 5;break;
                    case 31:type = 1;pos = 6;break;
                    case 32:type = 1;pos = 8;break;
                    case 33:type = 2;pos = 1;break;
                    case 34:type = 2;pos = 2;break;

                    case 35:type = 2;pos = 3;break;
                    case 36:type = 0;pos = 23;break;
                    case 37:type = 0;pos = 20;break;
                    case 38:type = 0;pos = 24;break;
                    case 39:type = 0;pos = 25;break;

                    case 40:type = 1;pos = 23;break;
                    case 41:type = 1;pos = 24;break;
                    case 42:type = 0;pos = 18;break;
                    case 43:type = 0;pos = 19;break;
                    case 44:type = 1;pos = 32;break;
                    case 45:type = 1;pos = 33;break;
                    case 46:type = 1;pos = 34;break;
                    case 47:type = 1;pos = 35;break;
                }
                switch (type){
                    case 0:
                        clazz = TLXConfigTypeSelectActivity.class;
                        break;
                    case 1:
                        clazz = TLXConfigType1Activity.class;
                        break;
                    case 2:
                        clazz = TLXConfigType2Activity.class;
                        break;
                    case 7:
                        clazz = TLXConfigType4Activity.class;
                        break;
                    case 9:
                        clazz = TLXConfigType1AndPFActivity.class;
                        break;
                }
                if (clazz != null) {
                    Intent intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                    jumpTo(intent, false);
                }
            }
        });
/*        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = null;
                Class clazz = null;
                int pos = position;
                switch (position){
//                    case 1:
//                        pos = 0;
//                        clazz = MaxConfigSafeyActivity.class;
//                        break;
                    case 1:
                        pos = 29;
                        clazz = ConfigType1Activity.class;
                        break;
                    case 0:
//                    case 1:
//                        case 2:
                    case 8:case 9:
                    case 15:case 16:case 17:case 18:
                    case 19:case 20:case 21:case 31:
                        if (position == 8){ pos = 3;}
                        if (position == 9){ pos = 4;}
                        if (position == 15){ pos = 5;}
                        if (position == 16){ pos = 6;}
                        if (position == 17){ pos = 7;}
                        if (position == 18){ pos = 8;}
                        if (position == 19){ pos = 9;}
                        if (position == 20){ pos = 10;}
                        if (position == 21){ pos = 11;}
                        if (position == 31){pos = 16;}
                        clazz = ConfigTypeSelectActivity.class;
                        break;
//                    case 3: case 4:case 5:
                    case 7:case 10:case 11: case 12:case 13: case 14:case 32:case 33:
                        if (position == 3){ pos = 0;}
                        if (position == 4){ pos = 1;}
                        if (position == 5){ pos = 2;}
                        if (position == 7){ pos = 3;}
                        if (position == 10){ pos = 4;}
                        if (position == 11){ pos = 5;}
                        if (position == 12){ pos = 6;}
                        if (position == 13){ pos = 7;}
                        if (position == 14){ pos = 8;}
                        if (position == 32){ pos = 18;}
                        if (position == 33){ pos = 19;}
                        clazz = ConfigType1Activity.class;
                        break;
                    case 22:case 23: case 24:case 25: case 26:case 27:
                        if (position>= 22 && position <= 27){ pos = position-22;}
                        clazz = ConfigType2Activity.class;
                        break;
                    case 28:
                        if (position == 28){ pos = 0;}
                        clazz = ConfigType6Activity.class;
                        break;
                    case 29:case 30:
                        if (position>= 29 && position <= 30){ pos = position-29;}
                        clazz = ConfigType4Activity.class;
                        break;
                    case 2:case 3:case 4:case 5:case 6:
                        pos = position-2;
                        clazz = ConfigType1AndPFActivity.class;
                        break;
                    default:
                        break;
                }
                if (clazz != null) {
                    intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                    jumpTo(intent, false);
                }
            }
        });*/
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
            bean.setTitle(String.format("%d.%s",i+1,titles[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
