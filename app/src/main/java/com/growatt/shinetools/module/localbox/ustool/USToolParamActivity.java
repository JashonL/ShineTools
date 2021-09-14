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
import com.growatt.shinetools.module.localbox.configtype.ConfigTypeTimeActivity;
import com.growatt.shinetools.module.localbox.configtype.NewConfigTypeHLActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigOneTextActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1AndPFActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType2Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType4Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigTypeSelectActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXModeSetActivity;
import com.growatt.shinetools.module.localbox.mintool.USParamCountryActivity;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class USToolParamActivity extends DemoBase {
    String rightTitle ;
    private String note1;
    private String note2 ;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles ;
    private String mTitle;
    private String[] registers;

    private String[]pfSetting;
    private String[]pfSettingRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustool_param);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListener();
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
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
        setHeaderTvTitle(headerView, rightTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TLXModeSetActivity.class);
                intent.putExtra("title",rightTitle);
                jumpTo(intent,false);
            }
        });
    }
    private void initString() {
        rightTitle = getString(R.string.m374设置Model);
        note1 = getString(R.string.m443该项暂不能设置请设置Model);
        note2 = getString(R.string.m444该项暂不能设置);

        registers = new String[]{
                "(16)", "(15)", "(30)", "(22)", "(45~50)"
                , "(51)", "(32)", "(33)", "(17)", "(52/53)"
                , "(54/55)", "(56/57)", "(58/59)", "(60/61)", "(62/63)"
                , "(64/65)", "(66/67)", "(68/69)", "(70/71)", "(72/73)"
                , "(74/75)", "(76/77)", "(78/79)"
                , "(80)","(8)", "(81)", "(88)","(233/234)"
                ,"(231)", "(201)", "(202)", "(203)", "(28~29)", "(122/123)"
                , "(7147~7148)"
                ,"" ,"" ,"" ,"" ,"" ,"" ,""
                ,"(539)"
        };
        titles = new String[]{
                getString(R.string.m国家安规),getString(R.string.m427语言), getString(R.string.m423通信地址),getString(R.string.m404选择通信波特率),getString(R.string.mlocal逆变器时间)
                ,getString(R.string.m428系统一周),getString(R.string.清除历史数据),getString(R.string.m恢复出厂设置) ,getString(R.string.m424启动电压),"AC1" +getString(R.string.m437限制电压低高)
                ,"AC1" +getString(R.string.m438频率限制低高), "AC2" +getString(R.string.m437限制电压低高),"AC2" +getString(R.string.m438频率限制低高), "AC3" +getString(R.string.m437限制电压低高), "AC3" +getString(R.string.m438频率限制低高)
                ,getString(R.string.m439并网电压限制低高),getString(R.string.m440并网频率限制低高), String.format("AC%s1%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s2%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s1%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
                , String.format("AC%s2%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s3%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s3%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
                ,getString(R.string.m429AC电压10分钟保护值),getString(R.string.m403PV电压), getString(R.string.m430PV电压高故障),getString(R.string.m431Modbus版本), String.format("%s1/2",getString(R.string.m389检查固件))
                ,getString(R.string.m412风扇检查),getString(R.string.m432PID工作模式), getString(R.string.m433PID开关),getString(R.string.m434PID工作电压),getString(R.string.m435逆变器模块), getString(R.string.m436逆变器经纬度)
                ,getString(R.string.修改总发电量)
                ,getString(R.string.电压穿越)
                ,getString(R.string.频率穿越)
                ,getString(R.string.上升斜率)
                ,getString(R.string.频率有功)
                ,getString(R.string.电压有功)
                ,getString(R.string.电压无功设置)
                ,getString(R.string.并网范围)
                ,getString(R.string.过温降载点)
        };
        registers = new String[]{
                "",
//                , "(15)"
                 "(30)", "(22)", "(45~50)"
                , "(51)",
//                , "(17)"
//                , "(52/53)"
//                , "(54/55)", "(56/57)", "(58/59)", "(60/61)", "(62/63)"
//                , "(64/65)", "(66/67)", "(68/69)", "(70/71)", "(72/73)"
//                , "(74/75)", "(76/77)", "(78/79)"
//                , "(80)"
//                ,"(8)"
//                , "(81)",
                "(88)"
//                ,"(233/234)"
                ,"(231)"
//                , "(201)", "(202)", "(203)"
//                , "(28~29)"
//                , "(122/123)"
                , "(7147~7148)"
                ,"" ,"" ,"" ,"" ,"" ,"" ,""
                , "(32)", "(33)",
//                ,"(539)"
                "",//pfsetting
        };
        titles = new String[]{
                getString(R.string.m国家安规),//0
//                ,getString(R.string.m427语言)
                 getString(R.string.m423通信地址),getString(R.string.m404选择通信波特率),getString(R.string.mlocal逆变器时间)
                ,getString(R.string.m428系统一周) ,
//                "PV " + getString(R.string.m424启动电压)
//                ,"AC1" +getString(R.string.m437限制电压低高)
//                ,"AC1" +getString(R.string.m438频率限制低高), "AC2" +getString(R.string.m437限制电压低高),"AC2" +getString(R.string.m438频率限制低高), "AC3" +getString(R.string.m437限制电压低高), "AC3" +getString(R.string.m438频率限制低高)
//                ,getString(R.string.m439并网电压限制低高),getString(R.string.m440并网频率限制低高), String.format("AC%s1%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s2%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s1%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
//                , String.format("AC%s2%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s3%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)), String.format("AC%s3%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
//                ,getString(R.string.m429AC电压10分钟保护值)
//                ,getString(R.string.m403PV电压)
//                , getString(R.string.m430PV电压高故障),
                getString(R.string.m431Modbus版本)
//                ,String.format("%s1/2",getString(R.string.m389检查固件))
                ,getString(R.string.m412风扇检查)
//                ,getString(R.string.m432PID工作模式), getString(R.string.m433PID开关),getString(R.string.m434PID工作电压)
//                ,getString(R.string.m435逆变器模块)
//                , getString(R.string.m436逆变器经纬度)
                ,getString(R.string.修改总发电量)
                ,getString(R.string.电压穿越)//8
                ,getString(R.string.频率穿越)//9
                ,getString(R.string.上升斜率)//10
                ,getString(R.string.频率有功)//11
                ,getString(R.string.电压有功)//12
                ,getString(R.string.电压无功设置)//13
                ,getString(R.string.并网范围)//14
//                ,getString(R.string.过温降载点)
                ,getString(R.string.清除历史数据),getString(R.string.m恢复出厂设置),
                getString(R.string.pf_setting)//17
        };


        pfSetting=new String[]{ getString(R.string.m405运行PF为1),getString(R.string.m402感性PF),getString(R.string.m399感性载率)
                ,getString(R.string.m400容性载率), getString(R.string.m401容性PF),getString(R.string.m默认PF曲线),getString(R.string.m422无功曲线切入切出电压),getString(R.string.m391PF限制负载百分比点)
                , getString(R.string.m392PF限制功率因数)};




        pfSettingRegister=new String[]{ "(89)","(5)","(4)"
                ,"(4)","(5)","(89)","(99/100)","1~4(110/112/114/116)"
                , "1~4(111/113/115/117)"};

    }


    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = null;
                Class clazz = null;
                int type = -1;
                int pos = -1;
                switch (position){
                    case 0:type = 5;pos = 0;break;
//                    case 1:type = 0;pos = 12;break;
                    case 1:type = 1;pos = 12;break;
                    case 2:type = 0;pos = 29;break;
                    case 3:type = 4;pos = 0;break;

                    case 4:type = 1;pos = 13;break;

//                    case 7:type = 1;pos = 9;break;
//                    case 9:type = 2;pos = 7;break;
//
//                    case 10:type = 2;pos = 8;break;
//                    case 11:type = 2;pos = 9;break;
//                    case 12:type = 2;pos = 10;break;
//                    case 13:type = 2;pos = 11;break;
//                    case 14:type = 2;pos = 12;break;
//
//                    case 15:type = 2;pos = 13;break;
//                    case 16:type = 2;pos = 14;break;
//                    case 17:type = 2;pos = 15;break;
//                    case 18:type = 2;pos = 16;break;
//                    case 19:type = 2;pos = 17;break;
//
//                    case 20:type = 2;pos = 18;break;
//                    case 21:type = 2;pos = 19;break;
//                    case 22:type = 2;pos = 20;break;

//                    case 8:type = 1;pos = 14;break;
//                    case 24:type = 1;pos = 3;break;
//                    case 9:type = 1;pos = 15;break;
                    case 5:type = 1;pos = 16;break;
//                    case 11:type = 2;pos = 5;break;

                    case 6:type = 0;pos = 6;break;
//                    case 13:type = 0;pos = 14;break;
//                    case 14:type = 0;pos = 15;break;
//                    case 15:type = 1;pos = 17;break;
//                    case 16:type = 3;pos = 0;break;
//                    case 33:type = 2;pos = 6;break;
                    case 7:
                        pos = 1;
                        clazz = NewConfigTypeHLActivity.class;
                        break;
                    case 8:
                        clazz = USVThroughActivity.class;
                        break;
                    case 9:
                        clazz = USFreThroughActivity.class;
                        break;
                    case 10:
                        clazz = USRampRateActivity.class;
                        break;
                    case 11:
                        clazz = USFWattActivity.class;
                        break;
                    case 12:
                        clazz = USVWattActivity.class;
                        break;
                    case 13:
                        clazz = USVolVarActivity.class;
                        break;
                    case 14:
                        clazz = USGridActivity.class;
                        break;
//                    case 20:type = 1;pos = 31;break;
                    case 15:type = 6;pos = 0;break;
                    case 16:type = 6;pos = 1;break;

                    case 17:
                        dialogShow(Arrays.asList(pfSetting), new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Class clazz = null;
                                int type = -1;
                                int pos = -1;
                                switch (position){
                                    case 0:type = 0;pos = 4;break;
                                    case 1:type = 9;pos = 4;break;
                                    case 2:type = 9;pos = 1;break;

                                    case 3:type = 9;pos = 2;break;
                                    case 4:type = 9;pos = 3;break;
                                    case 5:type = 0;pos = 27;break;
                                    case 6:type = 2;pos = 4;break;
                                    case 7:type = 7;pos = 0;break;

                                    case 8:type = 7;pos = 1;break;
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
                                    intent.putExtra("title", String.format("%s%s",pfSetting[position],pfSettingRegister[position]));
                                    jumpTo(intent, false);
                                }
                                return true;
                            }
                        });
                        break;
                }

                if (position!=17){
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
                        case 4:
                            clazz = ConfigTypeTimeActivity.class;
                            break;
                        case 5:
                            clazz = USParamCountryActivity.class;
                            break;
                        case 6:
                            clazz = TLXConfigOneTextActivity.class;
                            break;
                    }
                    if (clazz != null){
                        intent = new Intent(mContext,clazz);
                        intent.putExtra("type",pos);
                        intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                        jumpTo(intent,false);
                    }else {
                        MyControl.circlerDialog(USToolParamActivity.this,getString(R.string.该项暂不能进入),-1,false);
                    }
                }


            }
        });
    }

    /**
     * 弹框提示选择进入界面
     */
    private void dialogShow(List<String> items, OnLvItemClickListener listener) {
         CircleDialogUtils.showCommentItemDialog(this, getString(R.string.m225请选择),
                items, Gravity.CENTER, listener, null);
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
