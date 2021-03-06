package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsThroughAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.USVThroughBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class USRampRateActivity extends DemoBase implements UsThroughAdapter.OnChildCheckLiseners,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
//    private USVThroughAdatper mAdapter;
    private UsThroughAdapter mAdapter;
    private List<USVThroughBean> mList;
    private String[] titles;
    private String[] units;
    private int[] regists;
    private float[] mulits;
    /*??????*/
    private Button btnOK;
    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private boolean parseModel = false;
    private boolean parseRead = false;
    private int[] vols;
    private String mTitle;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usramp_rate);
        ButterKnife.bind(this);
        initIntent();
        initRecycler();
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", -1);
        }
    }

    private void initRecycler() {
        mTvTitle.setText(mTitle);
        vols = new int[]{
                277, 230, 288, 127, 800, 208
        };
        mTvRight.setText(R.string.m370??????);
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                {3, 0, 124}//????????????
        };
        titles = new String[]{
               getString(R.string.m379??????????????????),getString(R.string.m380??????????????????)
        };
        units = new String[]{
                "%/min", "%/min"
        };
        regists = new int[]{
                20,21
        };
        mulits = new float[]{
               0.1f,0.1f
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new UsThroughAdapter(mList,this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        /*??????*/
        View footerView = LayoutInflater.from(this).inflate(R.layout.footer_us, (ViewGroup) mRecyclerView.getParent(), false);
        btnOK = footerView.findViewById(R.id.btnOK);
        mAdapter.addFooterView(footerView);
        btnOK.setOnClickListener(this::onViewClicked);
        List<USVThroughBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            USVThroughBean bean = new USVThroughBean();
            bean.setMuilt(mulits[i]);
            bean.setTitle(titles[i]);
            bean.setRegistPos(regists[i]);
            bean.setUnit(units[i]);
            bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN);
            newList.add(bean);
        }
        mAdapter.replaceData(newList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                parseRead = true;
                //?????????????????????
                readRegisterValue();
                break;
            case R.id.btnOK:
                parseRead = false;
                if (parseModel) {
                    //?????????????????????
                    readRegisterValue();
                    return;
                }
                count = 0;
                //?????????????????????????????????,?????????????????????
                boolean isFlag = true;
                boolean isEmpty = true;
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    USVThroughBean bean = mAdapter.getData().get(i);
                    try {
                        String showValue = bean.getShowValue();
                        if (TextUtils.isEmpty(showValue)) {
                            continue;
                        }
                        isEmpty = false;
                        float value = Float.parseFloat(showValue);
                        bean.setRegistValue((int) Math.round(Arith.div(value, bean.getMuilt())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        toast(bean.getTitle() + "???" + getString(R.string.m363????????????));
                        isFlag = false;
                        break;
                    }
                }
                if (isEmpty) {
                    toast(R.string.all_blank);
                    return;
                }
                if (isFlag) {
                    connectServerWrite();
                }
                break;
        }
    }

    //????????????:???????????????
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    private int count = 0;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        USVThroughBean bean = mAdapter.getData().get(i);
                        if (count == i) {
                            if (TextUtils.isEmpty(bean.getShowValue())) {
                                count++;
                                continue;
                            }
                            int[] nowSends = new int[]{
                                    6, bean.getRegistPos(), bean.getRegistValue()
                            };
                            BtnDelayUtil.sendMessage(this, 6000);
                            sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilW, nowSends);
                            LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                            break;
                        }
                    }
                    if (count >= mAdapter.getData().size()) {
                        //??????????????????
                        count = 0;
                        SocketClientUtil.close(mClientUtilW);
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        USVThroughBean bean = mAdapter.getData().get(count);
                        if (isCheck) {
                            toast(bean.getTitle() + "???" + getString(R.string.all_success), Toast.LENGTH_SHORT);
                        } else {
                            toast(bean.getTitle() + "???" + getString(R.string.all_failed), Toast.LENGTH_SHORT);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
                        count++;
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        SocketClientUtil.close(mClientUtilW);
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    //?????????????????????
    private void readRegisterValue() {
        connectServer();
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }

    /**
     * ???????????????handle
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtil, funs[0]);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            if (parseModel) {
                                //??????120?????????  U ?????? 0-2bit
                                int value = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
                                try {
                                    refreshVol(vols[value]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                this.sendEmptyMessageDelayed(9898, 1000);
                            }
                            if (parseRead) {
                                refreshUI(bs);
                                //??????model  ????????????
                                toast(R.string.all_success);
                            }

                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtil);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case 9898:
                    onViewClicked(btnOK);
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    private void refreshUI(byte[] bs) {
        try {
            List<USVThroughBean> list = mAdapter.getData();
            List<USVThroughBean> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                USVThroughBean bean = new USVThroughBean();
                USVThroughBean item = mAdapter.getItem(i);
                bean.setRegistValue(item.getRegistValue());
                bean.setShowValue(item.getShowValue());
                bean.setUnit(item.getUnit());
                bean.setTitle(item.getTitle());
                bean.setMuilt(item.getMuilt());
                bean.setRegistPos(item.getRegistPos());
                bean.setVol(item.getVol());

                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN);
                int value = MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos());
                bean.setRegistValue(value);
                bean.setShowValue(String.valueOf(Arith.mul(value, bean.getMuilt(), 2)));
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshVol(int vol) {
        try {
            parseModel = false;
            List<USVThroughBean> list = mAdapter.getData();
            List<USVThroughBean> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                USVThroughBean bean = new USVThroughBean();
                USVThroughBean item = mAdapter.getItem(i);
                bean.setRegistValue(item.getRegistValue());
                bean.setShowValue(item.getShowValue());
                bean.setUnit(item.getUnit());
                bean.setTitle(item.getTitle());
                bean.setMuilt(item.getMuilt());
                bean.setRegistPos(item.getRegistPos());
                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN);
                bean.setVol(vol);
                //??????Muit
                if (i % 2 == 0) {
                    bean.setMuilt(Arith.div(10, vol));
                }

                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USVThroughBean usvThroughBean = mAdapter.getData().get(position);
        String tips="";
        String unit = "";
        switch (position){
            case 0://????????????
                tips=getString(R.string.android_key3048)+":"+"6%~6000%Pn/min";
                break;
            case 1://????????????
                tips=getString(R.string.android_key3048)+":"+"6%~6000%Pn/min";
                break;
        }

        String title = usvThroughBean.getTitle();

        showInputValueDialog(title, tips, unit, value -> {

            //????????????????????????
            if (TextUtils.isEmpty(value)) {
                toast(R.string.all_blank);
                return;
            }
            try {
                usvThroughBean.setShowValue(value);
                mAdapter.notifyDataSetChanged();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363????????????));
            }
        });
    }


    private BaseCircleDialog explainDialog;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        USVThroughBean bean = mAdapter.getData().get(position);
        switch (view.getId()) {
            case R.id.tvTitle:
                if (bean.getItemType() == UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN) {
                    String title = bean.getTitle();
                    String content="";
                    if (position==0){
                        content=getString(R.string.android_key3088);
                    }else if (position==1){
                        content=getString(R.string.android_key3089)+getString(R.string.android_key3090);
                    }
                    explainDialog = CircleDialogUtils.showExplainDialog(USRampRateActivity.this, title,content ,
                            new CircleDialogUtils.OndialogClickListeners() {
                                @Override
                                public void buttonOk() {
                                    explainDialog.dialogDismiss();
                                }
                                @Override
                                public void buttonCancel() {
                                    explainDialog.dialogDismiss();
                                }
                            });
                }
                break;

        }

    }


    interface OndialogComfirListener {
        void comfir(String value);
    }

    private BaseCircleDialog dialogFragment;

    private void showInputValueDialog(String title, String subTitle, String unit, OndialogComfirListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_input_custom, null, false);

        if (dialogFragment == null) {
            dialogFragment = CircleDialogUtils.showCommentBodyDialog(0.75f,
                    0.8f, contentView, getSupportFragmentManager(), new OnCreateBodyViewListener() {
                        @Override
                        public void onCreateBodyView(View view) {
                            CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                                    , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
                            view.setBackground(bgCircleDrawable);
                            TextView tvTitle = view.findViewById(R.id.tv_title);
                            TextView tvSubTtile = view.findViewById(R.id.tv_sub_title);
                            TextView tvUnit = view.findViewById(R.id.tv_unit);
                            TextView tvCancel = view.findViewById(R.id.tv_button_cancel);
                            TextView tvConfirm = view.findViewById(R.id.tv_button_confirm);
                            TextView etInput = view.findViewById(R.id.et_input);
                            tvCancel.setText(R.string.mCancel_ios);
                            tvConfirm.setText(R.string.android_key1935);


                            tvSubTtile.setText(subTitle);
                            tvUnit.setText(unit);
                            tvTitle.setText(title);

                            tvCancel.setOnClickListener(view1 -> {
                                dialogFragment.dialogDismiss();
                                dialogFragment = null;
                            });
                            tvConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String value = etInput.getText().toString();
                                    if (TextUtils.isEmpty(value)) {
                                        toast(R.string.android_key1945);
                                        return;
                                    }
                                    dialogFragment.dialogDismiss();
                                    listener.comfir(value);
                                    dialogFragment = null;
                                }
                            });

                        }
                    }, Gravity.CENTER,false);
        }

    }



    @Override
    public void oncheck(boolean check, int position) {

    }
}
