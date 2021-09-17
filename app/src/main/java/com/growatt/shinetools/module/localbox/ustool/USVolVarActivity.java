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

public class USVolVarActivity extends DemoBase implements UsThroughAdapter.OnChildCheckLiseners, BaseQuickAdapter.OnItemClickListener{

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
    /*脚部*/
    private Button btnOK;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private boolean parseModel = false;
    private boolean parseRead = false;
    private int[] vols;
    private String mTitle;
    private int mType = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usvol_var);
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
        mTvRight.setText(R.string.m370读取);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 124}
                ,{3, 125, 249}
        };
        titles = new String[]{
                getString(R.string.m382Qv切出高压)
                ,getString(R.string.m384Qv切出低压)
                ,getString(R.string.m381Qv切入高压)
                ,getString(R.string.m383Qv切入低压)
                ,getString(R.string.m408Qv无功延时)
                ,getString(R.string.m410Qv曲线Q最大值)
                ,getString(R.string.Qv响应时间)
        };
        units = new String[]{
                "V", "V", "V", "V", "s","%","s"
        };
        regists = new int[]{
                93,95,94,96,107,109,150
        };
        mulits = new float[]{
                0.1f, 0.1f, 0.1f, 0.1f, 0.02f,0.1f,0.1f
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
//        mAdapter = new USVThroughAdatper(R.layout.item_us_vthrough, mList);
        mAdapter = new UsThroughAdapter(mList,this);
        mRecyclerView.setAdapter(mAdapter);
        /*脚部*/
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
            bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT);
            newList.add(bean);
        }
        mAdapter.replaceData(newList);
        mAdapter.setOnItemClickListener(this);
    }

    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                parseRead = true;
                readCount = 0;
                //读取寄存器的值
                readRegisterValue();
                break;
            case R.id.btnOK:
                parseRead = false;
                if (parseModel) {
                    //读取寄存器的值
                    readRegisterValue();
                    return;
                }
                count = 0;
                //发送前校验数据是否正确,并设置寄存器值
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
                        toast(bean.getTitle() + "：" + getString(R.string.m363设置失败));
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

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    private int count = 0;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
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
                            LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                            break;
                        }
                    }
                    if (count >= mAdapter.getData().size()) {
                        //无则关闭连接
                        count = 0;
                        SocketClientUtil.close(mClientUtilW);
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        USVThroughBean bean = mAdapter.getData().get(count);
                        if (isCheck) {
                            toast(bean.getTitle() + "：" + getString(R.string.all_success), Toast.LENGTH_SHORT);
                        } else {
                            toast(bean.getTitle() + "：" + getString(R.string.all_failed), Toast.LENGTH_SHORT);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                        //发送下一条指令
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

    //读取寄存器的值
    private void readRegisterValue() {
        connectServer();
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }

    /**
     * 读取寄存器handle
     */
    private int readCount = 0;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (readCount < funs.length) {
                        BtnDelayUtil.sendMessage(this);
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtil, funs[readCount]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            if (parseModel) {
                                //解析120寄存器  U 电压 0-2bit
                                int value = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
                                try {
                                    refreshVol(vols[value]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                this.sendEmptyMessageDelayed(9898, 1000);
                            }
                            if (parseRead) {
                                //更新ui
                                refreshUI(bs);
                                if (readCount < funs.length - 1) {
                                    readCount++;
                                    mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                                } else {
                                    readCount = 0;
                                    //关闭连接
                                    SocketClientUtil.close(mClientUtil);
                                }
                            }

                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        readCount = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
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

    private void refreshUI2(byte[] bs) {
        try {
            List<USVThroughBean> list = mAdapter.getData();
            if (readCount == 0) {
                for (int i = 0; i < list.size() - 1; i++) {
                    USVThroughBean item = list.get(i);
                    int value = MaxWifiParseUtil.obtainValueOne(bs, item.getRegistPos());
                    item.setRegistValue(value);
                    item.setShowValue(String.valueOf(Arith.mul(value, item.getMuilt(), 2)));
                }
            }else if (readCount == 1){
                USVThroughBean item = mAdapter.getItem(list.size()-1);
                int value = MaxWifiParseUtil.obtainValueOne(bs, item.getRegistPos());
                item.setRegistValue(value);
                item.setShowValue(String.valueOf(Arith.mul(value, item.getMuilt(), 2)));
            }
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshUI(byte[] bs) {
        if (readCount == 0){
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
                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT);
                bean.setRegistPos(item.getRegistPos());
                if (i < list.size()-1){
                    int value = MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos());
                    bean.setRegistValue(value);
                    bean.setShowValue(String.valueOf(Arith.mul(value, bean.getMuilt(), 2)));
                }
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        }else if (readCount == 1){
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
                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT);
                bean.setRegistPos(item.getRegistPos());
                if (i == list.size()-1){
                    int value = MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos());
                    bean.setRegistValue(value);
                    bean.setShowValue(String.valueOf(Arith.mul(value, bean.getMuilt(), 2)));
                }
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
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
                bean.setVol(vol);
                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT);
                //设置Muit
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
            case 0://切出高压
                tips=getString(R.string.android_key3048)+":"+"176.80~228.80/204.00~264.00";
                break;
            case 1://切出低压
                tips=getString(R.string.android_key3048)+":"+"176.80~228.80/204.00~264.00";
                break;

            case 2://切出高压
                tips=getString(R.string.android_key3048)+":"+"176.80~228.80/204.00~264.00";
                break;

            case 3://切出低压
                tips=getString(R.string.android_key3048)+":"+"176.80~228.80/204.00~264.00";
                break;

            case 4://无功延时
                tips=getString(R.string.android_key3048)+":"+"0~60s";
                break;

            case 5://曲线Q最大值
                tips=getString(R.string.android_key3048)+":"+"0~100%";
                break;

            case 6://相应时间
                tips=getString(R.string.android_key3048)+":"+"0~60s";
                break;

        }

        String title = usvThroughBean.getTitle();

        showInputValueDialog(title, tips, unit, value -> {

            //获取用户输入内容
            if (TextUtils.isEmpty(value)) {
                toast(R.string.all_blank);
                return;
            }
            try {
                usvThroughBean.setShowValue(value);
                mAdapter.notifyDataSetChanged();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
            }
        });
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
