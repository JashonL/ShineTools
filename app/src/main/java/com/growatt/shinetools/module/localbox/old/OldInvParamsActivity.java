package com.growatt.shinetools.module.localbox.old;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.OldInvConfigTypeSelectActivity;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public class OldInvParamsActivity extends DemoBase {
    String rightTitle;
    private String note1;
    private String note2;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles;
    private String mTitle;
    private String[] registers;

    private int user_type = KEFU_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_param_set);
        ButterKnife.bind(this);
        user_type = ShineToosApplication.getContext().getUser_type();
        initString();
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListener();
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
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
        if (user_type!=END_USER){
            setHeaderTvTitle(headerView, rightTitle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OldInvModeSetActivity.class);
                    intent.putExtra("title", rightTitle);
                    jumpTo(intent, false);
                }
            });
        }

    }

    private void initString() {
        rightTitle = getString(R.string.m374??????Model);
        note1 = getString(R.string.m443??????????????????????????????Model);
        note2 = getString(R.string.m444?????????????????????);

        if (user_type == END_USER) {

            registers = new String[]{
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
            };
            //0,10,11,12,13,14,19,29
            titles = new String[]{
                   getString(R.string.mlocal???????????????),getString(R.string.m427??????),
                    getString(R.string.m423????????????),getString(R.string.m424????????????),getString(R.string.m425????????????),
                    getString(R.string.m426?????????????????????????????????),getString(R.string.m428????????????),getString(R.string.m429AC??????10???????????????),
                    getString(R.string.m430PV???????????????),
                    "AC1" +getString(R.string.m437??????????????????),"AC1" +getString(R.string.m438??????????????????),
                    "AC2" +getString(R.string.m437??????????????????),"AC2" +getString(R.string.m438??????????????????),
                    "AC3" +getString(R.string.m438??????????????????),getString(R.string.m439????????????????????????),getString(R.string.m440????????????????????????),
                    String.format("AC%s1%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m441??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s1%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s2%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???)),
                    String.format("AC%s3%s/%s",getString(R.string.m442??????????????????),getString(R.string.m373???),getString(R.string.m372???))

            };

        } else {
            registers = new String[]{
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
                    "", "", "", "","", "", "", "","","",
            };
            titles = new String[]{
                    getString(R.string.m????????????), getString(R.string.mlocal???????????????), getString(R.string.m427??????),
                    getString(R.string.m423????????????), getString(R.string.m424????????????), getString(R.string.m425????????????),
                    getString(R.string.m426?????????????????????????????????), getString(R.string.m428????????????), getString(R.string.m429AC??????10???????????????),
                    getString(R.string.m430PV???????????????), getString(R.string.m431Modbus??????), getString(R.string.m432PID????????????),
                    getString(R.string.m433PID??????), getString(R.string.m434PID????????????), getString(R.string.m435???????????????),
                    "AC1" + getString(R.string.m437??????????????????), "AC1" + getString(R.string.m438??????????????????),
                    "AC2" + getString(R.string.m437??????????????????), "AC2" + getString(R.string.m438??????????????????), "AC3" + getString(R.string.m437??????????????????),
                    "AC3" + getString(R.string.m438??????????????????), getString(R.string.m439????????????????????????), getString(R.string.m440????????????????????????),
                    String.format("AC%s1%s/%s", getString(R.string.m441??????????????????), getString(R.string.m373???), getString(R.string.m372???)),
                    String.format("AC%s2%s/%s", getString(R.string.m441??????????????????), getString(R.string.m373???), getString(R.string.m372???)),
                    String.format("AC%s3%s/%s", getString(R.string.m441??????????????????), getString(R.string.m373???), getString(R.string.m372???)),
                    String.format("AC%s1%s/%s", getString(R.string.m442??????????????????), getString(R.string.m373???), getString(R.string.m372???)),
                    String.format("AC%s2%s/%s", getString(R.string.m442??????????????????), getString(R.string.m373???), getString(R.string.m372???)),
                    String.format("AC%s3%s/%s", getString(R.string.m442??????????????????), getString(R.string.m373???), getString(R.string.m372???))
                    , getString(R.string.??????????????????)
            };
        }


    }

    private void initListener() {
        if (user_type==END_USER){
            //0,10,11,12,13,14,19,29
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                Class clazz = null;
                int type = -1;
                int pos = -1;
                switch (position) {
                    case 0:
                        type = 4;
                        pos = 0;
                        break;
                    case 1:
                        type = 0;
                        pos = 12;
                        break;
                    case 2:
                        type = 1;
                        pos = 12;
                        break;
                    case 3:
                        type = 1;
                        pos = 9;
                        break;
                    case 4:
                        type = 1;
                        pos = 10;
                        break;
                    case 5:
                        type = 1;
                        pos = 11;
                        break;
                    case 6:
                        type = 1;
                        pos = 13;
                        break;
                    case 7:
                        type = 1;
                        pos = 14;
                        break;
                    case 8:
                        type = 1;
                        pos = 15;
                        break;

//                case 14:type = 3;pos = 0;break;

                    case 9:
                        type = 2;
                        pos = 7;
                        break;
                    case 10:
                        type = 2;
                        pos = 8;
                        break;
                    case 11:
                        type = 2;
                        pos = 9;
                        break;
                    case 12:
                        type = 2;
                        pos = 10;
                        break;

                    case 13:
                        type = 2;
                        pos = 12;
                        break;
                    case 14:
                        type = 2;
                        pos = 13;
                        break;
                    case 15:
                        type = 2;
                        pos = 14;
                        break;
                    case 16:
                        type = 2;
                        pos = 15;
                        break;
                    case 17:
                        type = 2;
                        pos = 16;
                        break;
                    case 18:
                        type = 2;
                        pos = 17;
                        break;
                    case 19:
                        type = 2;
                        pos = 18;
                        break;
                    case 20:
                        type = 2;
                        pos = 19;
                        break;
                    case 21:
                        type = 2;
                        pos = 20;
                        break;

                }
                switch (type) {
                    case 0:
                        clazz = OldInvConfigTypeSelectActivity.class;
                        break;
                    case 1:
                        clazz = OldInvConfigType1Activity.class;
                        break;
                    case 2:
                        clazz = OldInvConfigType2Activity.class;
                        break;
                    case 3:
                        clazz = OldInvConfigTypeHLActivity.class;
                        break;
                    case 4:
                        clazz = OldInvConfigTypeTimeActivity.class;
                        break;
                    case 5:
                        clazz = OldInvParamCountryActivity.class;
                        break;
                }
                if (clazz != null) {
                    Intent intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                    jumpTo(intent, false);
                } else {
                    MyControl.circlerDialog(this, getString(R.string.?????????????????????), -1, false);
                }
            });

        }else {
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                Class clazz = null;
                int type = -1;
                int pos = -1;
                switch (position) {
                    case 0:
                        type = 5;
                        pos = 0;
                        break;
                    case 1:
                        type = 4;
                        pos = 0;
                        break;
                    case 2:
                        type = 0;
                        pos = 12;
                        break;
                    case 3:
                        type = 1;
                        pos = 12;
                        break;
                    case 4:
                        type = 1;
                        pos = 9;
                        break;
                    case 5:
                        type = 1;
                        pos = 10;
                        break;
                    case 6:
                        type = 1;
                        pos = 11;
                        break;
                    case 7:
                        type = 1;
                        pos = 13;
                        break;
                    case 8:
                        type = 1;
                        pos = 14;
                        break;
                    case 9:
                        type = 1;
                        pos = 15;
                        break;
                    case 10:
                        type = 1;
                        pos = 16;
                        break;
                    case 11:
                        type = 0;
                        pos = 14;
                        break;
                    case 12:
                        type = 0;
                        pos = 15;
                        break;
                    case 13:
                        type = 1;
                        pos = 17;
                        break;
//                case 14:type = 3;pos = 0;break;

                    case 15:
                        type = 2;
                        pos = 7;
                        break;
                    case 16:
                        type = 2;
                        pos = 8;
                        break;
                    case 17:
                        type = 2;
                        pos = 9;
                        break;
                    case 18:
                        type = 2;
                        pos = 10;
                        break;
                    case 19:
                        type = 2;
                        pos = 11;
                        break;
                    case 20:
                        type = 2;
                        pos = 12;
                        break;
                    case 21:
                        type = 2;
                        pos = 13;
                        break;
                    case 22:
                        type = 2;
                        pos = 14;
                        break;
                    case 23:
                        type = 2;
                        pos = 15;
                        break;
                    case 24:
                        type = 2;
                        pos = 16;
                        break;
                    case 25:
                        type = 2;
                        pos = 17;
                        break;
                    case 26:
                        type = 2;
                        pos = 18;
                        break;
                    case 27:
                        type = 2;
                        pos = 19;
                        break;
                    case 28:
                        type = 2;
                        pos = 20;
                        break;
                    case 29:
                        type = 3;
                        pos = 1;
                        break;
                }
                switch (type) {
                    case 0:
                        clazz = OldInvConfigTypeSelectActivity.class;
                        break;
                    case 1:
                        clazz = OldInvConfigType1Activity.class;
                        break;
                    case 2:
                        clazz = OldInvConfigType2Activity.class;
                        break;
                    case 3:
                        clazz = OldInvConfigTypeHLActivity.class;
                        break;
                    case 4:
                        clazz = OldInvConfigTypeTimeActivity.class;
                        break;
                    case 5:
                        clazz = OldInvParamCountryActivity.class;
                        break;
                }
                if (clazz != null) {
                    Intent intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                    jumpTo(intent, false);
                } else {
                    MyControl.circlerDialog(this, getString(R.string.?????????????????????), -1, false);
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
