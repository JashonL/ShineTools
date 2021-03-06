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
        rightTitle = getString(R.string.m374??????Model);
        note1 = getString(R.string.m443??????????????????????????????Model);
        note2 = getString(R.string.m444?????????????????????);

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
                getString(R.string.m????????????),getString(R.string.m427??????), getString(R.string.m423????????????),getString(R.string.m404?????????????????????),getString(R.string.mlocal???????????????)
                ,getString(R.string.m428????????????),getString(R.string.??????????????????),getString(R.string.m??????????????????) ,getString(R.string.m424????????????),"AC1" +getString(R.string.m437??????????????????)
                ,"AC1" +getString(R.string.m438??????????????????), "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????), "AC3" +getString(R.string.m437??????????????????), "AC3" +getString(R.string.m438??????????????????)
                ,getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????), String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
                , String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
                ,getString(R.string.m429AC??????10???????????????),getString(R.string.m403PV??????), getString(R.string.m430PV???????????????),getString(R.string.m431Modbus??????), String.format("%s1/2",getString(R.string.m389????????????))
                ,getString(R.string.m412????????????),getString(R.string.m432PID????????????), getString(R.string.m433PID??????),getString(R.string.m434PID????????????),getString(R.string.m435???????????????), getString(R.string.m436??????????????????)
                ,getString(R.string.??????????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.??????????????????)
                ,getString(R.string.????????????)
                ,getString(R.string.???????????????)
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
                getString(R.string.m????????????),//0
//                ,getString(R.string.m427??????)
                 getString(R.string.m423????????????),getString(R.string.m404?????????????????????),getString(R.string.mlocal???????????????)
                ,getString(R.string.m428????????????) ,
//                "PV " + getString(R.string.m424????????????)
//                ,"AC1" +getString(R.string.m437??????????????????)
//                ,"AC1" +getString(R.string.m438??????????????????), "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????), "AC3" +getString(R.string.m437??????????????????), "AC3" +getString(R.string.m438??????????????????)
//                ,getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????), String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
//                , String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)), String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
//                ,getString(R.string.m429AC??????10???????????????)
//                ,getString(R.string.m403PV??????)
//                , getString(R.string.m430PV???????????????),
                getString(R.string.m431Modbus??????)
//                ,String.format("%s1/2",getString(R.string.m389????????????))
                ,getString(R.string.m412????????????)
//                ,getString(R.string.m432PID????????????), getString(R.string.m433PID??????),getString(R.string.m434PID????????????)
//                ,getString(R.string.m435???????????????)
//                , getString(R.string.m436??????????????????)
                ,getString(R.string.??????????????????)
                ,getString(R.string.????????????)//8
                ,getString(R.string.????????????)//9
                ,getString(R.string.????????????)//10
                ,getString(R.string.????????????)//11
                ,getString(R.string.????????????)//12
                ,getString(R.string.??????????????????)//13
                ,getString(R.string.????????????)//14
//                ,getString(R.string.???????????????)
                ,getString(R.string.??????????????????),getString(R.string.m??????????????????),
                getString(R.string.pf_setting)//17
        };


        pfSetting=new String[]{ getString(R.string.m405??????PF???1),getString(R.string.m402??????PF),getString(R.string.m399????????????)
                ,getString(R.string.m400????????????), getString(R.string.m401??????PF),getString(R.string.m??????PF??????),getString(R.string.m422??????????????????????????????),getString(R.string.m391PF????????????????????????)
                , getString(R.string.m392PF??????????????????)};




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
                        MyControl.circlerDialog(USToolParamActivity.this,getString(R.string.?????????????????????),-1,false);
                    }
                }


            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void dialogShow(List<String> items, OnLvItemClickListener listener) {
         CircleDialogUtils.showCommentItemDialog(this, getString(R.string.m225?????????),
                items, Gravity.CENTER, listener, null);
    }

    /**
     * ???????????????
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
