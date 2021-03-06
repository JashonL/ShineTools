package com.growatt.shinetools.module.localbox.max;

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

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;

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
    private int user_type = KEFU_USER;

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
        rightTitle = getString(R.string.m374??????Model);
        note1 = getString(R.string.m443??????????????????????????????Model);
        note2 = getString(R.string.m444?????????????????????);


        user_type = ShineToosApplication.getContext().getUser_type();
        if (user_type == END_USER) {
            registers = new String[]{
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    "", "",
                    ""
            };
            //0,10,11,12,13,14,15,30
            titles = new String[]{
                    getString(R.string.mlocal???????????????),getString(R.string.m427??????),
                    getString(R.string.m423????????????),getString(R.string.m424????????????),getString(R.string.m425????????????),
                    getString(R.string.m426?????????????????????????????????),getString(R.string.m428????????????),getString(R.string.m429AC??????10???????????????),
                    getString(R.string.m430PV???????????????),
                   "AC1" +getString(R.string.m437??????????????????),"AC1" +getString(R.string.m438??????????????????),
                    "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????), "AC3" +getString(R.string.m437??????????????????),
                    "AC3" +getString(R.string.m438??????????????????),getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????),
                    String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))


            };



        }else if (user_type==MAINTEAN_USER){
            registers = new String[]{
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    ""
            };


            titles = new String[]{
                    getString(R.string.m????????????),getString(R.string.mlocal???????????????),getString(R.string.m427??????),
                    getString(R.string.m423????????????),getString(R.string.m424????????????),getString(R.string.m425????????????),
                    getString(R.string.m426?????????????????????????????????),getString(R.string.m428????????????),getString(R.string.m429AC??????10???????????????),
                    getString(R.string.m430PV???????????????),getString(R.string.m431Modbus??????),getString(R.string.m432PID????????????),
                    getString(R.string.m433PID??????),getString(R.string.m434PID????????????),getString(R.string.m435???????????????),
                    getString(R.string.m436??????????????????),"AC1" +getString(R.string.m437??????????????????),"AC1" +getString(R.string.m438??????????????????),
                    "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????), "AC3" +getString(R.string.m437??????????????????),
                    "AC3" +getString(R.string.m438??????????????????),getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????),
                    String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
            };
        }else {

        /*    registers = new String[]{
                    "", "(45~50)", "(15)", "(30)", "(17)",
                    "(18)", "(19)", "(51)", "(80)", "(81)",
                    "(88)", "(201)", "(202)", "(203)", "(28~29)",
                    "(122/123)", "(52/53)", "(54/55)", "(56/57)", "(58/59)",
                    "(60/61)", "(62/63)", "(64/65)", "(66/67)", "(68/69)",
                    "(70/71)", "(72/73)", "(74/75)", "(76/77)", "(78/79)"
                    , "(7147~7148)"
            };*/

            registers = new String[]{
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    ""
            };


            titles = new String[]{
                    getString(R.string.m????????????),getString(R.string.mlocal???????????????),getString(R.string.m427??????),
                    getString(R.string.m423????????????),getString(R.string.m424????????????),getString(R.string.m425????????????),
                    getString(R.string.m426?????????????????????????????????),getString(R.string.m428????????????),getString(R.string.m429AC??????10???????????????),
                    getString(R.string.m430PV???????????????),getString(R.string.m431Modbus??????),getString(R.string.m432PID????????????),
                    getString(R.string.m433PID??????),getString(R.string.m434PID????????????),getString(R.string.m435???????????????),
                    getString(R.string.m436??????????????????),"AC1" +getString(R.string.m437??????????????????),"AC1" +getString(R.string.m438??????????????????),
                    "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????), "AC3" +getString(R.string.m437??????????????????),
                    "AC3" +getString(R.string.m438??????????????????),getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????),
                    String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))
                    ,getString(R.string.??????????????????)

            };
        }



    }

    private void initListener() {
        if (user_type == END_USER) {
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = null;
                    Class clazz = null;
                    int type = -1;
                    int pos = -1;
                    switch (position){
                        case 1:type = 0;pos = 12;break;

                        //??????edittext????????????
                        case 3:type = 1;pos = 9;break;
                        case 4:type = 1;pos = 10;break;
                        case 5:type = 1;pos = 11;break;
                        case 2:type = 1;pos = 12;break;
                        case 6:type = 1;pos = 13;break;
                        case 7:type = 1;pos = 14;break;
                        case 8:type = 1;pos = 15;break;


                        //??????????????????-2000
                        case 0:type = 4;pos = 0;break;

                        default:
                            //2???edittext
                            if (position >= 9 && position <= 22){
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
        }else {
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = null;
                    Class clazz = null;
                    int type = -1;
                    int pos = -1;
                    switch (position){
                        case 2:type = 0;pos = 12;break;
                        //????????????
                        case 0:type = 5;pos = 0;break;
                        case 11:type = 0;pos = 14;break;
                        case 12:type = 0;pos = 15;break;
                        //??????edittext????????????
                        case 4:type = 1;pos = 9;break;
                        case 5:type = 1;pos = 10;break;
                        case 6:type = 1;pos = 11;break;
                        case 3:type = 1;pos = 12;break;
                        case 7:type = 1;pos = 13;break;
                        case 8:type = 1;pos = 14;break;
                        case 9:type = 1;pos = 15;break;
                        case 10:type = 1;pos = 16;break;
                        case 13:type = 1;pos = 17;break;
                        //??????edittext??????????????????
                        case 14:type = 3;pos = 0;
                            OssUtils.circlerDialog(MaxParamSetActivity.this,note1,-1,false);
                            return;
                        //??????????????????-2000
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
                            //2???edittext
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
