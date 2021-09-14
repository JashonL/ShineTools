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
import com.growatt.shinetools.module.localbox.configtype.ConfigType1AndPFActivity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType6Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigTypeSelectActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TLXToolConfigActivity extends DemoBase {

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
        setContentView(R.layout.activity_tlxtool_config);
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
                ,getString(R.string.AFCI阈值) + "1" ,getString(R.string.AFCI阈值) + "2" ,getString(R.string.AFCI阈值) + "3" ,getString(R.string.FFT最大累计次数)
                , getString(R.string.m干接点状态),getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比)
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
                ,"(544)","(545)","(546)","(547)",
                "(3016)","(3017)","(3019)"
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
                        clazz = TLXConfigType1Activity.class;
                        break;
                    case 0:
//                    case 1:
//                        case 2:
                    case 8:case 9:
                    case 15:case 16:case 17:case 18:
                    case 19:case 20:case 21:case 31:
                        if (position == 8){ pos = 26;}
                        if (position == 9){ pos = 4;}
                        if (position == 15){ pos = 5;}
                        if (position == 16){ pos = 6;}
                        if (position == 17){ pos = 7;}
                        if (position == 18){ pos = 8;}
                        if (position == 19){ pos = 9;}
                        if (position == 20){ pos = 10;}
                        if (position == 21){ pos = 11;}
                        if (position == 31){pos = 16;}
                        clazz = TLXConfigTypeSelectActivity.class;
                        break;
//                    case 3: case 4:case 5:
                    case 7:case 10:case 11: case 12:case 13: case 14:case 32:case 33:
                        //增加四种
                    case 34: case 35: case 36: case 37:
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

                        if (position == 34){ pos = 32;}
                        if (position == 35){ pos = 33;}
                        if (position == 36){ pos = 34;}
                        if (position == 37){ pos = 35;}
                        clazz = TLXConfigType1Activity.class;
                        break;
                    case 22:case 23: case 24:case 25: case 26:case 27:
                        if (position>= 22 && position <= 27){ pos = position-22;}
                        clazz = TLXConfigType2Activity.class;
                        break;
                    case 28:
                        if (position == 28){ pos = 0;}
                        clazz = ConfigType6Activity.class;
                        break;
                    case 29:case 30:
                        if (position>= 29 && position <= 30){ pos = position-29;}
                        clazz = TLXConfigType4Activity.class;
                        break;
                    case 2:case 3:case 4:case 5:case 6:
                        pos = position-2;
                        clazz = ConfigType1AndPFActivity.class;
                        break;

                    case 38:
                        pos = 17;
                        clazz = ConfigTypeSelectActivity.class;
                        break;
                    case 39:
                        pos = 20;
                        clazz = TLXConfigType1Activity.class;
                        break;
                    case 40:
                        pos = 30;
                        clazz = TLXConfigType1Activity.class;
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
        });
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
//            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            bean.setTitle(String.format("%d.%s",i+1,titles[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
