package com.growatt.shinetools.module.localbox.old;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.OldInvConfigTypeSelectActivity;
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public class OldInvConfigActivity extends DemoBase {

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
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
        }
        user_type = ShineToosApplication.getContext().getUser_type();
    }
    private void initString() {
        if (user_type == END_USER) {
            //1,14,15,16,18,19,20,32
            titles = new String[]{
                    getString(R.string.m396???????????????),getString(R.string.m398?????????????????????),getString(R.string.m399????????????),getString(R.string.m400????????????),
                    getString(R.string.m401??????PF),getString(R.string.m402??????PF),getString(R.string.m403PV??????),getString(R.string.m405??????PF???1),
                    getString(R.string.m406??????????????????),getString(R.string.m407?????????????????????),getString(R.string.m408Qv????????????),getString(R.string.m409??????????????????),getString(R.string.m410Qv??????Q?????????),
                    getString(R.string.m414N???GND??????????????????),
                    getString(R.string.m418????????????????????????),getString(R.string.m419Qv??????????????????),getString(R.string.m420Qv??????????????????),
                    getString(R.string.m421Qv??????????????????),getString(R.string.m422??????????????????????????????),getString(R.string.m389????????????),getString(R.string.m390PF?????????),getString(R.string.m391PF????????????????????????),
                    getString(R.string.m392PF??????????????????),getString(R.string.m???????????????),getString(R.string.m????????????????????????)
            };
            registers = new String[]{
                    "","" ,"","","","","","","",
                    "","" ,"","","","","","","","",
                    "","" ,"","","","","","","","",
                    "","","",""
            };
        }else {
            titles = new String[]{
                    getString(R.string.m396???????????????),getString(R.string.m397??????????????????),getString(R.string.m398?????????????????????),getString(R.string.m399????????????),getString(R.string.m400????????????),
                    getString(R.string.m401??????PF),getString(R.string.m402??????PF),getString(R.string.m403PV??????),getString(R.string.m405??????PF???1),
                    getString(R.string.m406??????????????????),getString(R.string.m407?????????????????????),getString(R.string.m408Qv????????????),getString(R.string.m409??????????????????),getString(R.string.m410Qv??????Q?????????),
                    getString(R.string.m411Island??????),getString(R.string.m412????????????),getString(R.string.m413??????N?????????),getString(R.string.m414N???GND??????????????????),getString(R.string.m415?????????????????????????????????),
                    getString(R.string.m416???????????????????????????),getString(R.string.m417MPPT??????),getString(R.string.m418????????????????????????),getString(R.string.m419Qv??????????????????),getString(R.string.m420Qv??????????????????),
                    getString(R.string.m421Qv??????????????????),getString(R.string.m422??????????????????????????????),getString(R.string.m389????????????),getString(R.string.m390PF?????????),getString(R.string.m391PF????????????????????????),
                    getString(R.string.m392PF??????????????????),getString(R.string.m???????????????),getString(R.string.m????????????????????????),getString(R.string.m???????????????????????????????????????)
            };
            registers = new String[]{
                    "","" ,"","","","","","","",
                    "","" ,"","","","","","","","",
                    "","" ,"","","","","","","","",
                    "","","",""
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
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                if(position == 18
                        || position == 12
                        || position == 20
                ){
                    new CircleDialog.Builder()
                            .setTitle(getString(R.string.reminder))
                            .setText(getString(R.string.m1256????????????????????????) + "?")
                            .setGravity(Gravity.CENTER)
                            .setWidth(0.7f)
                            .setNegative(getString(R.string.all_no),view1 -> {
                                setPos(position, false);
                            })
                            .setPositive(getString(R.string.all_ok),view1 -> {
                                setPos(position, true);
                            })
                            .show(getSupportFragmentManager());
                }else {
                    setPos(position, false);
                }
            });

        }else {
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                if(position == 11){
                    new CircleDialog.Builder()
                            .setTitle(getString(R.string.reminder))
                            .setText(getString(R.string.m1256????????????????????????) + "?")
                            .setGravity(Gravity.CENTER)
                            .setWidth(0.7f)
                            .setNegative(getString(R.string.all_no),view1 -> {
                                setPos(position, false);
                            })
                            .setPositive(getString(R.string.all_ok),view1 -> {
                                setPos(position, true);
                            })
                            .show(getSupportFragmentManager());
                }else {
                    setPos(position, false);
                }
            });

        }

    }

    /**
     *
     * @param position
     * @param isSingle ??????????????????
     */
    private void setPos(int position,boolean isSingle) {
        String title = "";
        Class clazz = null;
        int type = -1;
        int pos = -1;
        if (user_type==END_USER){
            switch (position){
                case 0:type = 0;pos = 0;break;
                case 1:type = 9;pos = 0;break;
                case 2:type = 9;pos = 1;break;
                case 3:type = 9;pos = 2;break;
                case 4:type = 9;pos = 3;break;
                case 5:type = 9;pos = 4;break;
                case 6:type = 1;pos = 3;break;
                case 7:type = 0;pos = 4;break;
                case 8:type = 1;pos = 4;break;
                case 9:type = 1;pos = 5;break;
                case 10:type = 1;pos = 6;break;
                case 11:
                    if (isSingle){
                        type = 1;
                        pos = 32;
                    }else {
                        type = 1;
                        pos = 7;
                    }

                    break;
                case 12:type = 1;pos = 8;break;
                case 13:type = 0;pos = 8;break;
                case 14:type = 2;pos = 0;break;
                case 15:type = 2;pos = 1;break;
                case 16:type = 2;pos = 2;break;
                case 17:type = 2;pos = 3;break;
                case 18:type = 2;pos = 4;break;
                case 19:type = 1;pos = 31;break;
                case 20:type = 10;pos = 0;break;
                case 21:type = 7;pos = 2;break;
                case 22:type = 7;pos = 3;break;
                case 23:type = 0;pos = 16;break;
                case 24:type = 1;pos = 18;break;

            }

        }else {
            switch (position){
                case 0:type = 0;pos = 0;break;
                case 1:type = 1;pos = 29;break;
                case 2:type = 9;pos = 0;break;
                case 3:type = 9;pos = 1;break;
                case 4:type = 9;pos = 2;break;
                case 5:type = 9;pos = 3;break;
                case 6:type = 9;pos = 4;break;
                case 7:type = 1;pos = 3;break;
                case 8:type = 0;pos = 4;break;
                case 9:type = 1;pos = 4;break;
                case 10:type = 1;pos = 5;break;
                case 11:type = 1;pos = 6;break;
                case 12:
                    if (isSingle){
                        type = 1;
                        pos = 32;
                    }else {
                        type = 1;
                        pos = 7;
                    }

                    break;
                case 13:type = 1;pos = 8;break;
                case 14:type = 0;pos = 5;break;
                case 15:type = 0;pos = 6;break;
                case 16:type = 0;pos = 7;break;
                case 17:type = 0;pos = 8;break;
                case 18:
                    if (isSingle){
                        type = 0;
                        pos = 29;
                    }else {
                        type = 0;
                        pos = 9;
                    }
                    break;
                case 19:type = 0;pos = 10;break;
                case 20:
                    if (isSingle){
                        type = 0;
                        pos = 30;
                        title = "(83)";
                    }else {
                        type = 0;
                        pos = 31;
                        title = "(163)";
                    }
                    break;
                case 21:type = 2;pos = 0;break;
                case 22:type = 2;pos = 1;break;
                case 23:type = 2;pos = 2;break;
                case 24:type = 2;pos = 3;break;
                case 25:type = 2;pos = 4;break;
                case 26:type = 1;pos = 31;break;
                case 27:type = 10;pos = 0;break;
                case 28:type = 7;pos = 2;break;
                case 29:type = 7;pos = 3;break;
                case 30:type = 0;pos = 16;break;
                case 31:type = 1;pos = 18;break;
                case 32:type = 1;pos = 19;break;
            }

        }




        switch (type){
            case 0:
                clazz = OldInvConfigTypeSelectActivity.class;
                break;
            case 1:
                clazz = OldInvConfigType1Activity.class;
                break;
            case 2:
                clazz = OldInvConfigType2Activity.class;
                break;
            case 7:
                clazz = OldInvConfigType4Activity.class;
                break;
            case 9:
                clazz = OldInvConfigType1AndPFActivity.class;
                break;
            case 10:
                clazz = OldInvConfigType6Activity.class;
                break;
        }
        if (clazz != null) {
            Intent intent = new Intent(mContext, clazz);
            intent.putExtra("type", pos);
            intent.putExtra("title", String.format("%s%s",titles[position], TextUtils.isEmpty(title)?registers[position]:title));
            jumpTo(intent, false);
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
            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
