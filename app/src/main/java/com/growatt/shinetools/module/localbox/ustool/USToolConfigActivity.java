package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.afci.AFCIChartActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1AndPFActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType2Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType4Activity;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class USToolConfigActivity extends DemoBase {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles;
    private String mTitle;
    private String[] registers;

    private String[]exportLimitSetting;
    private String[]exportLimitRegister;



    private String[]afciSetting;
    private String[]afciRegister;


    private String[]drySetting;
    private String[]dryRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustool_config);
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
                getString(R.string.m396开关逆变器),getString(R.string.m398有功功率百分比),getString(R.string.m405运行PF为1),getString(R.string.m402感性PF),getString(R.string.m399感性载率)
                ,getString(R.string.m400容性载率), getString(R.string.m401容性PF),getString(R.string.m默认PF曲线),getString(R.string.m422无功曲线切入切出电压),getString(R.string.m391PF限制负载百分比点)
                , getString(R.string.m392PF限制功率因数),getString(R.string.m417MPPT使能),getString(R.string.m425启动时间), getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m418电源启动重启斜率)
                ,getString(R.string.m防逆流设置),getString(R.string.m防逆流功率百分比),getString(R.string.m防逆流失效后默认功率百分比),getString(R.string.m功率计) ,getString(R.string.m干接点状态)
                ,getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比),getString(R.string.m414N至GND监测功能使能),getString(R.string.m415非标准电网电压范围使能), getString(R.string.m406过频降额起点)
                ,getString(R.string.m407频率负载限制率),getString(R.string.m409过频降额延时),getString(R.string.m397安规功能使能),getString(R.string.m413电网N线使能), getString(R.string.m416指定的规格设置使能)
                ,getString(R.string.m411Island使能),getString(R.string.m408Qv无功延时),getString(R.string.m410Qv曲线Q最大值),getString(R.string.m419Qv切入切出高压),getString(R.string.m420Qv切入切出低压)
                , getString(R.string.m421Qv切入切出功率),getString(R.string.m手动离网使能),getString(R.string.m离网使能)  ,getString(R.string.m离网频率),getString(R.string.m离网电压)
                ,getString(R.string.mCV电压),getString(R.string.mCC电流),getString(R.string.mCT种类选择) ,getString(R.string.m电池种类)
                ,getString(R.string.AFCI使能),getString(R.string.AFCI自检),getString(R.string.AFCI复位)

        };
        registers = new String[]{
                "(0)" ,"(3)",

                "(89)","(5)","(4)"
                ,"(4)","(5)","(89)","(99/100)","1~4(110/112/114/116)"
                , "1~4(111/113/115/117)",

                "(399)", "(18)", "(19)","(20/21)"
                ,"(122)","(123)","(3000)",
                 "(533)","(3016)"
                ,"(3017)","(3019)","(235)","(236)","(91)"
                ,"(92)","(108)","(1)","(232)", "(237)"
                ,"(230)","(107)","(109)","(93/94)","(95/96)"
                ,"(97/98)","(3021)","(3079)" ,"(3081)" ,"(3080)"
                ,"(3030)" ,"(3024)" ,"(3068)" ,"(3070)"
                ,"(178)","(541)","(542)"
        };




        titles = new String[]{
                getString(R.string.m396开关逆变器),getString(R.string.m398有功功率百分比),

                getString(R.string.m417MPPT使能)
//                ,getString(R.string.m425启动时间), getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m418电源启动重启斜率)
                ,getString(R.string.export_limit_setting),//Export Limit Setting

                getString(R.string.dry_setting),//drysetting

                getString(R.string.m功率计),

                getString(R.string.m414N至GND监测功能使能)

//                getString(R.string.m415非标准电网电压范围使能)
////                , getString(R.string.m406过频降额起点),getString(R.string.m407频率负载限制率),getString(R.string.m409过频降额延时)
//                ,getString(R.string.m397安规功能使能),getString(R.string.m413电网N线使能), getString(R.string.m416指定的规格设置使能)
//                ,getString(R.string.m411Island使能)
//                ,getString(R.string.m408Qv无功延时),getString(R.string.m410Qv曲线Q最大值),getString(R.string.m419Qv切入切出高压),getString(R.string.m420Qv切入切出低压), getString(R.string.m421Qv切入切出功率)
               /* ,getString(R.string.m手动离网使能)*/,

                getString(R.string.m离网使能)  ,getString(R.string.m离网频率),getString(R.string.m离网电压)
//                ,getString(R.string.mCV电压),getString(R.string.mCC电流)
//                ,getString(R.string.mCT种类选择) ,getString(R.string.m电池种类)
//                ,getString(R.string.AFCI使能),getString(R.string.AFCI自检),getString(R.string.AFCI复位)
                ,getString(R.string.AFCI功能)//AFCI功能

        };



        registers = new String[]{
                "(0)" ,"(3)",


                "(399)"
//                , "(18)", "(19)","(20/21)"
                ,"",


                "",

                "(533)",

                "(235)"

//                "(236)"
////                ,"(91)","(92)","(108)"
//                ,"(1)","(232)", "(237)"
//                ,"(230)"


//                ,"(107)","(109)","(93/94)","(95/96)","(97/98)"
                /*,"(3021)"*/,"(3079)" ,"(3081)" ,"(3080)"
//                ,"(3030)" ,"(3024)"
//                ,"(3068)" ,"(3070)"
//                ,"(178)","(541)","(542)"
                ,""
        };



        exportLimitSetting=new String[]{getString(R.string.m防逆流设置),getString(R.string.m防逆流功率百分比),getString(R.string.m防逆流失效后默认功率百分比)};



        exportLimitRegister=new String[]{"(122)","(123)","(3000)"};




        afciSetting=new String[]{getString(R.string.AFCI阈值) + "1" ,getString(R.string.AFCI阈值) + "2" ,getString(R.string.AFCI阈值) + "3" ,getString(R.string.FFT最大累计次数),getString(R.string.AFCI曲线扫描)};
        afciRegister=new String[]{"(544)","(545)","(546)","(547)",""};


        drySetting=new String[]{getString(R.string.m干接点状态)
                ,getString(R.string.m干接点开通的功率百分比),getString(R.string.m干接点关闭功率百分比)};
        dryRegister=new String[]{"(3016)","(3017)","(3019)"};

    }

    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
 /*       setHeaderTvRight(headerView,getString(R.string.AFCI曲线扫描),view -> {
            jumpTo(AFCIChartActivity.class,false);
        },R.color.blue_1);*/
    }


    private void initListener0() {
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

                    case 44:type = 0;pos = 29;break;
                    case 45:type = 0;pos = 30;break;
                    case 46:type = 0;pos = 31;break;
                }
                switch (type){
                    case 0:
                        clazz = USConfigTypeSelectActivity.class;
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


                    case 2:type = 0;pos = 11;break;

                    case 3://防逆流设置
                        dialogShow(Arrays.asList(exportLimitSetting), new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Class clazz = null;
                                int type = -1;
                                int pos = -1;
                                switch (position){
                                    case 0:type = 0;pos = 28;break;
                                    case 1:type = 1;pos = 18;break;
                                    case 2:type = 1;pos = 19;break;
                                }
                                switch (type){
                                    case 0:
                                        clazz = USConfigTypeSelectActivity.class;
                                        break;
                                    case 1:
                                        clazz = TLXConfigType1Activity.class;
                                        break;
                                }
                                if (clazz != null) {
                                    Intent intent = new Intent(mContext, clazz);
                                    intent.putExtra("type", pos);
                                    intent.putExtra("title", String.format("%s%s",exportLimitSetting[position],exportLimitRegister[position]));
                                    jumpTo(intent, false);
                                }
                                return true;
                            }
                        });
                        break;

                    case 4:
                        dialogShow(Arrays.asList(drySetting), new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Class clazz = null;
                                int type = -1;
                                int pos = -1;
                                switch (position){
                                    case 0:type = 0;pos = 17;break;
                                    case 1:type = 1;pos = 20;break;
                                    case 2:type = 1;pos = 30;break;
                                }
                                switch (type){
                                    case 0:
                                        clazz = USConfigTypeSelectActivity.class;
                                        break;
                                    case 1:
                                        clazz = TLXConfigType1Activity.class;
                                        break;
                                }
                                if (clazz != null) {
                                    Intent intent = new Intent(mContext, clazz);
                                    intent.putExtra("type", pos);
                                    intent.putExtra("title", String.format("%s%s",drySetting[position],dryRegister[position]));
                                    jumpTo(intent, false);
                                }
                                return true;
                            }
                        });
                        break;

                    case 5://功率计
                         type = 0;pos = 22;
                         break;


                    case 6:type = 0;pos = 8;break;


                    case 7:
                        type = 0;pos = 20;
                        break;
                    case 8:
                        type = 0;pos = 24;
                        break;
                    case 9://离网电压
                        type = 0;pos = 25;
                        break;

                    case 10:
                        dialogShow(Arrays.asList(afciSetting), new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Class clazz = null;
                                int type = -1;
                                int pos = -1;
                                switch (position){
                                    case 0:type = 1;pos = 32;break;
                                    case 1:type = 1;pos = 33;break;
                                    case 2:type = 1;pos = 34;break;
                                    case 3:type = 1;pos = 35;break;
                                    case 4:type=2;pos =36;break;
                                }
                                if (type == 1) {
                                    clazz = TLXConfigType1Activity.class;
                                }else if (type==2){
                                    clazz = AFCIChartActivity.class;
                                }
                                if (clazz != null) {
                                    Intent intent = new Intent(mContext, clazz);
                                    intent.putExtra("type", pos);
                                    intent.putExtra("title", String.format("%s%s",afciSetting[position],afciRegister[position]));
                                    jumpTo(intent, false);
                                }
                                return true;
                            }
                        });
                        break;
                }

                if (position==0||position==1||position==2||position==5||position==6||position==7||position==8||position==9){
                    switch (type){
                        case 0:
                            clazz = USConfigTypeSelectActivity.class;
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

    /**
     * 弹框提示选择进入界面
     */
    private void dialogShow(List<String> items, OnLvItemClickListener listener) {
        CircleDialogUtils.showCommentItemDialog(this, getString(R.string.m225请选择),
                items, Gravity.CENTER, listener, null);

    }



}
