package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.afci.AFCIChartActivity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType1Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType1AndPFActivity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType2Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType4Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigType6Activity;
import com.growatt.shinetools.module.localbox.configtype.ConfigTypeSelectActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public class MaxConfigActivity extends DemoBase {

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
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
        user_type = ShineToosApplication.getContext().getUser_type();

    }

    private void initString() {


        //???????????????1???8???15???16???17???19???20???21???27???36
        if (user_type == END_USER) {
            titles = new String[]{
                    getString(R.string.m396???????????????), getString(R.string.m398?????????????????????), getString(R.string.m399????????????), getString(R.string.m400????????????),
                    getString(R.string.m401??????PF), getString(R.string.m402??????PF), getString(R.string.m403PV??????),  getString(R.string.m405??????PF???1),
                    getString(R.string.m406??????????????????), getString(R.string.m407?????????????????????), getString(R.string.m408Qv????????????), getString(R.string.m409??????????????????), getString(R.string.m410Qv??????Q?????????),
                    getString(R.string.m414N???GND??????????????????),
                    getString(R.string.m418????????????????????????), getString(R.string.m419Qv??????????????????), getString(R.string.m420Qv??????????????????),
                    getString(R.string.m421Qv??????????????????), getString(R.string.m422??????????????????????????????),getString(R.string.m390PF?????????), getString(R.string.m391PF????????????????????????),
                    getString(R.string.m392PF??????????????????), getString(R.string.mGPRS4GPLC??????), getString(R.string.AFCI??????) + " 1", getString(R.string.AFCI??????) + " 2", getString(R.string.AFCI??????) + " 3", getString(R.string.FFT??????????????????)
            };

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
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
            };
        } else {
            titles = new String[]{
                    getString(R.string.m396???????????????), getString(R.string.m397??????????????????), getString(R.string.m398?????????????????????), getString(R.string.m399????????????), getString(R.string.m400????????????),
                    getString(R.string.m401??????PF), getString(R.string.m402??????PF), getString(R.string.m403PV??????), getString(R.string.m404?????????????????????), getString(R.string.m405??????PF???1),
                    getString(R.string.m406??????????????????), getString(R.string.m407?????????????????????), getString(R.string.m408Qv????????????), getString(R.string.m409??????????????????), getString(R.string.m410Qv??????Q?????????),
                    getString(R.string.m411Island??????), getString(R.string.m412????????????), getString(R.string.m413??????N?????????), getString(R.string.m414N???GND??????????????????), getString(R.string.m415?????????????????????????????????),
                    getString(R.string.m416???????????????????????????), getString(R.string.m417MPPT??????), getString(R.string.m418????????????????????????), getString(R.string.m419Qv??????????????????), getString(R.string.m420Qv??????????????????),
                    getString(R.string.m421Qv??????????????????), getString(R.string.m422??????????????????????????????), getString(R.string.m389????????????), getString(R.string.m390PF?????????), getString(R.string.m391PF????????????????????????),
                    getString(R.string.m392PF??????????????????), getString(R.string.mGPRS4GPLC??????), getString(R.string.AFCI??????) + " 1", getString(R.string.AFCI??????) + " 2", getString(R.string.AFCI??????) + " 3", getString(R.string.FFT??????????????????)
                    , getString(R.string.??????SVG????????????)
//
//                        "???????????????(0)","??????????????????(1)","?????????????????????(3)","????????????(4)","????????????(4)","??????PF(5)","??????PF(5)",
//                        "PV??????(8)","?????????????????????(22)","??????PF???1(89)","??????????????????(91)",
//                        "??????-???????????????(92)","Q(v)????????????(107)","??????????????????(108)","Q(v)??????Q?????????(109)","Island??????(230)",
//                        "????????????(231)","??????N?????????(232)","N???GND??????????????????(235)","?????????????????????????????????(236)","???????????????????????????(237)",
//                        "MPPT??????(238)","????????????/????????????(20/21)","Q(v)??????/????????????(93/94)","Q(v)??????/????????????(95/96)","Q(v)??????/????????????(97/98)",
//                        "??????????????????/????????????(99/100)","????????????1/2(233/234)","PF?????????(101~106)","PF????????????????????????1~4(110/112/114/116)","PF??????????????????1~4(111/113/115/117)",
            };
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
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
                    "", "", "",
            };
/*
            registers = new String[]{
                    "(0)","(1)" ,"(3)","(4)","(4)","(5)","(5)","(8)","(22)","(89)",
                    "(91)","(92)" ,"(107)","(108)","(109)","(230)","(231)","(232)","(235)","(236)",
                    "(237)","(399)" ,"(20/21)","(93/94)","(95/96)","(97/98)","(99/100)","1/2(233/234)","(101~106)","1~4(110/112/114/116)",
                    "1~4(111/113/115/117)","(310)","(544)","(545)","(546)","(547)"
                    ,"(141)"
            };*/
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
        setHeaderTvRight(headerView, "AFCI", view -> {
            jumpTo(AFCIChartActivity.class, false);
        }, R.color.blue_1);
    }


    private void initListener() {

        if (user_type == END_USER) {
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                Intent intent = null;
                Class clazz = null;
                int pos = position;
                switch (position) {
                    case 0:
                    case 7:
                    case 13:
                        if (position == 7) {
                            pos = 4;
                        }
                        if (position == 13) {
                            pos = 8;
                        }

                        clazz = ConfigTypeSelectActivity.class;
                        break;
//                    case 3: case 4:case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 23:
                    case 24:
                    case 25:
                    case 26:

                        if (position == 6) {
                            pos = 3;
                        }
                        if (position == 8) {
                            pos = 4;
                        }
                        if (position == 9) {
                            pos = 5;
                        }
                        if (position == 10) {
                            pos = 6;
                        }
                        if (position == 11) {
                            pos = 7;
                        }
                        if (position == 12) {
                            pos = 8;
                        }

                        if (position == 23) {
                            pos = 31;
                        }
                        if (position == 24) {
                            pos = 32;
                        }
                        if (position == 25) {
                            pos = 33;
                        }
                        if (position == 26) {
                            pos = 34;
                        }
                        clazz = ConfigType1Activity.class;
                        break;
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                        pos = position - 14;
                        clazz = ConfigType2Activity.class;
                        break;
                    case 19:
                        pos = 0;
                        clazz = ConfigType6Activity.class;
                        break;
                    case 20:
                    case 21:
                        pos = position - 20;
                        clazz = ConfigType4Activity.class;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        pos = position - 1;
                        clazz = ConfigType1AndPFActivity.class;
                        break;
                    case 22:
                        pos = 0;
                        clazz = ToolOnlyReadActivity.class;
                    default:
                        break;
                }
                if (clazz != null) {
                    intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                    jumpTo(intent, false);
                }
            });

        } else {
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                Intent intent = null;
                Class clazz = null;
                int pos = position;
                switch (position) {
                    case 1:
                        pos = 29;
                        clazz = ConfigType1Activity.class;
                        break;
                    case 0:
                    case 8:
                    case 9:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 36:
                        if (position == 8) {
                            pos = 3;
                        }
                        if (position == 9) {
                            pos = 4;
                        }
                        if (position == 15) {
                            pos = 5;
                        }
                        if (position == 16) {
                            pos = 6;
                        }
                        if (position == 17) {
                            pos = 7;
                        }
                        if (position == 18) {
                            pos = 8;
                        }
                        if (position == 19) {
                            pos = 9;
                        }
                        if (position == 20) {
                            pos = 10;
                        }
                        if (position == 21) {
                            pos = 11;
                        }
                        if (position == 36) {
                            pos = 24;
                        }
                        clazz = ConfigTypeSelectActivity.class;
                        break;
//                    case 3: case 4:case 5:
                    case 7:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                        if (position == 3) {
                            pos = 0;
                        }
                        if (position == 4) {
                            pos = 1;
                        }
                        if (position == 5) {
                            pos = 2;
                        }
                        if (position == 7) {
                            pos = 3;
                        }
                        if (position == 10) {
                            pos = 4;
                        }
                        if (position == 11) {
                            pos = 5;
                        }
                        if (position == 12) {
                            pos = 6;
                        }
                        if (position == 13) {
                            pos = 7;
                        }
                        if (position == 14) {
                            pos = 8;
                        }

                        if (position == 32) {
                            pos = 31;
                        }
                        if (position == 33) {
                            pos = 32;
                        }
                        if (position == 34) {
                            pos = 33;
                        }
                        if (position == 35) {
                            pos = 34;
                        }
                        clazz = ConfigType1Activity.class;
                        break;
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                        if (position >= 22 && position <= 27) {
                            pos = position - 22;
                        }
                        clazz = ConfigType2Activity.class;
                        break;
                    case 28:
                        if (position == 28) {
                            pos = 0;
                        }
                        clazz = ConfigType6Activity.class;
                        break;
                    case 29:
                    case 30:
                        if (position >= 29 && position <= 30) {
                            pos = position - 29;
                        }
                        clazz = ConfigType4Activity.class;
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        pos = position - 2;
                        clazz = ConfigType1AndPFActivity.class;
                        break;
                    case 31:
                        pos = 0;
                        clazz = ToolOnlyReadActivity.class;
                    default:
                        break;
                }
                if (clazz != null) {
                    intent = new Intent(mContext, clazz);
                    intent.putExtra("type", pos);
                    intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                    jumpTo(intent, false);
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
//            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            bean.setTitle(String.format("%d.%s", i + 1, titles[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
