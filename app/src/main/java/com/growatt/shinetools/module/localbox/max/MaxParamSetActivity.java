package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.configtype.ConfigType1Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType2Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigTypeSelectActivity;
import com.growatt.shinetools.module.localbox.configtype.ConfigTypeTimeActivity;
import com.growatt.shinetools.module.localbox.configtype.NewConfigTypeHLActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.TLXModeSetActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXParamCountry2Activity;
import com.growatt.shinetools.utils.OssUtils;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaxParamSetActivity extends DemoBase {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_param_set);
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
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {
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
//        registers = new String[]{
//                "(30)", "(45~50)", "(17)", "(18)", "(19)",
//                "(15)", "(16)", "(51)", "(80)", "(81)",
//                "(88)", "(201)", "(202)", "(203)", "(28~29)",
//                "(122/123)", "(52/53)", "(54/55)", "(56/57)", "(58/59)",
//                "(60/61)", "(62/63)", "(64/65)", "(66/67)", "(68/69)",
//                "(70/71)", "(72/73)", "(74/75)", "(76/77)", "(78/79)",
//        };
        registers = new String[]{
                "", "(45~50)", "(15)", "(30)", "(17)",
                "(18)", "(19)", "(51)", "(80)", "(81)",
                "(88)", "(201)", "(202)", "(203)", "(28~29)",
                "(122/123)", "(52/53)", "(54/55)", "(56/57)", "(58/59)",
                "(60/61)", "(62/63)", "(64/65)", "(66/67)", "(68/69)",
                "(70/71)", "(72/73)", "(74/75)", "(76/77)", "(78/79)"
                , "(7147~7148)"
        };
//        titles = new String[]{
//                getString(R.string.m423通信地址),getString(R.string.mlocal逆变器时间),getString(R.string.m424启动电压),
//                getString(R.string.m425启动时间),getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m427语言),
//                getString(R.string.country_title),getString(R.string.m428系统一周),getString(R.string.m429AC电压10分钟保护值),
//                getString(R.string.m430PV电压高故障),getString(R.string.m431Modbus版本),getString(R.string.m432PID工作模式),
//                getString(R.string.m433PID开关),getString(R.string.m434PID工作电压),getString(R.string.m435逆变器模块),
//                getString(R.string.m436逆变器经纬度),"AC1" +getString(R.string.m437限制电压低高),"AC1" +getString(R.string.m438频率限制低高),
//                "AC2" +getString(R.string.m437限制电压低高),"AC2" +getString(R.string.m438频率限制低高), "AC3" +getString(R.string.m437限制电压低高),
//                "AC3" +getString(R.string.m438频率限制低高),getString(R.string.m439并网电压限制低高),getString(R.string.m440并网频率限制低高),
//                String.format("AC%s1%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
//                String.format("AC%s2%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
//                String.format("AC%s1%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
//                String.format("AC%s2%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
//                String.format("AC%s3%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
//                String.format("AC%s3%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
//
////                "通信地址(30)","系统时间(45~50)","启动电压(17)","启动时间(18)","故障恢复后重启延迟时间(19)","语言(15)","国家(16)"
////                , "系统一周(51)","AC电压10分钟保护值(80)","PV电压高故障(81)"
////                ,"Modbus版本(88)","PID工作模式(201)", "PID开关(202)","PID工作电压(203)","逆变器模块(28~29)"
////                ,"逆变器经纬度(122/123)","AC1限制电压低/高(52/53)", "AC1频率限制低/高(54/55)","AC2限制电压低/高(56/57)","AC2频率限制低/高(58/59)"
////                ,"AC3限制电压低/高(60/61)","AC3频率限制低/高(62/63)", "并网电压限制低/高(64/65)","并网频率限制低/高(66/67)","AC电压限制时间1低/高(68/69)"
////                ,"AC电压限制时间2低/高(70/71)","AC频率限制时间1低/高(72/73)", "AC频率限制时间2低/高(74/75)","AC电压限制时间3低/高(76/77)","AC频率限制时间3低/高(78/79)"
//////            ,"1","2", "3","4","5"
//        };
        titles = new String[]{
                getString(R.string.m国家安规),getString(R.string.mlocal逆变器时间),getString(R.string.m427语言),
                getString(R.string.m423通信地址),getString(R.string.m424启动电压),getString(R.string.m425启动时间),
                getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m428系统一周),getString(R.string.m429AC电压10分钟保护值),
                getString(R.string.m430PV电压高故障),getString(R.string.m431Modbus版本),getString(R.string.m432PID工作模式),
                getString(R.string.m433PID开关),getString(R.string.m434PID工作电压),getString(R.string.m435逆变器模块),
                getString(R.string.m436逆变器经纬度),"AC1" +getString(R.string.m437限制电压低高),"AC1" +getString(R.string.m438频率限制低高),
                "AC2" +getString(R.string.m437限制电压低高),"AC2" +getString(R.string.m438频率限制低高), "AC3" +getString(R.string.m437限制电压低高),
                "AC3" +getString(R.string.m438频率限制低高),getString(R.string.m439并网电压限制低高),getString(R.string.m440并网频率限制低高),
                String.format("AC%s1%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s2%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s1%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s2%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s3%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s3%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
                ,getString(R.string.修改总发电量)

//                "通信地址(30)","系统时间(45~50)","启动电压(17)","启动时间(18)","故障恢复后重启延迟时间(19)","语言(15)","国家(16)"
//                , "系统一周(51)","AC电压10分钟保护值(80)","PV电压高故障(81)"
//                ,"Modbus版本(88)","PID工作模式(201)", "PID开关(202)","PID工作电压(203)","逆变器模块(28~29)"
//                ,"逆变器经纬度(122/123)","AC1限制电压低/高(52/53)", "AC1频率限制低/高(54/55)","AC2限制电压低/高(56/57)","AC2频率限制低/高(58/59)"
//                ,"AC3限制电压低/高(60/61)","AC3频率限制低/高(62/63)", "并网电压限制低/高(64/65)","并网频率限制低/高(66/67)","AC电压限制时间1低/高(68/69)"
//                ,"AC电压限制时间2低/高(70/71)","AC频率限制时间1低/高(72/73)", "AC频率限制时间2低/高(74/75)","AC电压限制时间3低/高(76/77)","AC频率限制时间3低/高(78/79)"
////            ,"1","2", "3","4","5"
        };
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
                    case 2:type = 0;pos = 12;break;
                    //国家设置
                    case 0:type = 5;pos = 0;break;
                    case 11:type = 0;pos = 14;break;
                    case 12:type = 0;pos = 15;break;
                    //单个edittext单寄存器
                    case 4:type = 1;pos = 9;break;
                    case 5:type = 1;pos = 10;break;
                    case 6:type = 1;pos = 11;break;
                    case 3:type = 1;pos = 12;break;
                    case 7:type = 1;pos = 13;break;
                    case 8:type = 1;pos = 14;break;
                    case 9:type = 1;pos = 15;break;
                    case 10:type = 1;pos = 16;break;
                    case 13:type = 1;pos = 17;break;
                    //单个edittext高低位寄存器
                    case 14:type = 3;pos = 0;
                        OssUtils.circlerDialog(MaxParamSetActivity.this,note1,-1,false);
                        return;
                    //时间设置，年-2000
                    case 1:type = 4;pos = 0;break;
                    case 30:
                        pos = 1;
                        clazz = NewConfigTypeHLActivity.class;
                        break;
                    default:
                        if (position==15){
                            OssUtils.circlerDialog(MaxParamSetActivity.this,note2,-1,false);
                            return;
                        }
                        //2个edittext
                        if (position >= 15 && position <= 29){
                            type = 2;
                            pos = position - 9;
                        }
                        break;
                }
                switch (type){
                    case 0:
                        clazz = ConfigTypeSelectActivity.class;
                        break;
                    case 1:
                        clazz = ConfigType1Activity.class;
                        break;
                    case 2:
                        clazz = ConfigType2Activity.class;
                        break;
                    case 4:
                        clazz = ConfigTypeTimeActivity.class;
                        break;
                    case 5:
                        clazz = TLXParamCountry2Activity.class;
                        break;
                }
                if (clazz != null){
                    intent = new Intent(mContext,clazz);
                    intent.putExtra("type",pos);
                    intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                    jumpTo(intent,false);
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
