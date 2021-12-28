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

public class USFWattActivity extends DemoBase implements UsThroughAdapter.OnChildCheckLiseners,
        BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
//    private USTotalAdapter mAdapter;
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
    private boolean parseModel = true;
    private boolean parseRead = false;
    private boolean isSet = false;
    private int[] vols;
    private String mTitle;
    private int mType = -1;
    private String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usfwatt);
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
        items = new String[]{
                getString(R.string.m89禁止),
                getString(R.string.m88使能)
        };
        mTvTitle.setText(mTitle);
        vols = new int[]{
                277, 230, 288, 127, 800, 208
        };
        mTvRight.setText(R.string.m370读取);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 124},
                {3, 125, 249},
                {3,334,334}
        };
        titles = new String[]{
                getString(R.string.过频降载使能)
                ,getString(R.string.过频降载) + " " + getString(R.string.点)
                ,getString(R.string.过频降载) + " " + getString(R.string.恢复点)
                ,getString(R.string.过频降载) + " " + getString(R.string.斜率)
                ,getString(R.string.过频降载) + " " + getString(R.string.延时时间)
                ,getString(R.string.过频降载) + " " + getString(R.string.恢复时间)
                ,getString(R.string.过频降载) + " " + getString(R.string.响应时间)

                ,getString(R.string.android_key2425)
                ,getString(R.string.欠频加载) + " " + getString(R.string.点)
                ,getString(R.string.欠频加载) + " " + getString(R.string.停止点)
                ,getString(R.string.欠频加载) + " " + getString(R.string.斜率)
                ,getString(R.string.欠频加载) + " " + getString(R.string.延时时间)
                ,getString(R.string.欠频加载) + " " + getString(R.string.响应时间)
        };
        units = new String[]{
                " "
                , "Hz", "Hz","%","s","s","s",
                ""
                , "Hz", "Hz","%","s","s"
        };
        regists = new int[]{
                1,91,143,92,108,144,178,334,142,151,176,175,179
        };
        mulits = new float[]{
                1f
                ,0.01f,0.01f,0.01f,0.05f,0.05f,0.02f
                ,1f
                ,0.01f,0.01f,0.01f,0.05f,0.02f
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
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
            if (i == 0||i==7){
                bean.setType(UsSettingConstant.SETTING_TYPE_SWITCH);
                if (i==0){
                    bean.setItemPos(1);
                }else {
                    bean.setItemPos(8);
                }
                bean.setShowValue(String.valueOf(bean.getItemPos()));
            }else {
                bean.setType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT);
            }
            bean.setMuilt(mulits[i]);
            bean.setTitle(titles[i]);
            bean.setRegistPos(regists[i]);
            bean.setUnit(units[i]);
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
                isSet = false;
                parseRead = true;
                //读取寄存器的值
                readRegisterValue();
                break;
            case R.id.btnOK:
                parseRead = false;
                if (parseModel) {
                    isSet = true;
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
                        if (i == 0){
                            int itemPos = bean.getItemPos();
                            int newValue = bean.getRegistValue() & 0b1111110111110111 | (itemPos << 3);
                            bean.setRegistValue(newValue);
                        }else if (i==7){
                            bean.setRegistValue(Integer.parseInt(showValue));
                        }else {
                            float value = Float.parseFloat(showValue);
                            if (i == 3 || i == 10){
                                bean.setRegistValue((int) Math.round(Arith.div(2000, value)));
                            }else {
                                bean.setRegistValue((int) Math.round(Arith.div(value, bean.getMuilt())));
                            }
                        }
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
        countRead = 0;
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
    private int countRead = 0;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtil, funs[countRead]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            if (parseModel) {
                                parseModel = false;
                                //解析第一号寄存器
                                int value = MaxWifiParseUtil.obtainValueOne(bs, 1);
                                USVThroughBean item = mAdapter.getItem(0);
                                item.setRegistValue(value);
                                if (isSet){
                                    //关闭tcp连接
                                    SocketClientUtil.close(mClientUtil);
                                    BtnDelayUtil.refreshFinish();
                                    this.sendEmptyMessageDelayed(9898, 1000);
                                }
                            }
                            if (parseRead) {
                                refreshUI(bs,countRead);
                                //设置model  对应电压
                                toast(R.string.all_success);
                            }

                        } else {
                            toast(R.string.all_failed);
                        }
                        if (!isSet){
                            if (countRead < 2){
                                countRead ++;
                                this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            }else {
                                countRead = 0;
                                //关闭tcp连接
                                SocketClientUtil.close(mClientUtil);
                                BtnDelayUtil.refreshFinish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
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

    private void refreshUI(byte[] bs,int count) {
        if (count == 0){
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
                bean.setType(item.getType());
                bean.setItemPos(item.getItemPos());
                if (i == 0){
                    bean.setRegistValue(MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos()));
                    bean.setItemPos(bean.getRegistValue() >>> 3 & 1);
                    bean.setShowValue(String.valueOf(bean.getItemPos()));
                }else if (i == 1 || i==3 || i==4){
                        int value = MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos());
                        bean.setRegistValue(value);
                    if (i == 3){//计算规则不同，特殊处理
                        bean.setShowValue(String.valueOf(Arith.div(2000, value, 2)));
                    }else {
                        bean.setShowValue(String.valueOf(Arith.mul(value, bean.getMuilt(), 2)));
                    }
                }
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        }else if (count == 1){
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
                bean.setType(item.getType());
                bean.setItemPos(item.getItemPos());
                if (i == 2 || i > 4) {
                    int value = MaxWifiParseUtil.obtainValueOne(bs, bean.getRegistPos());
                    bean.setRegistValue(value);
                    if (i == 9){
                        bean.setShowValue(String.valueOf(Arith.div(2000, value, 2)));
                    }else {
                        bean.setShowValue(String.valueOf(Arith.mul(value, bean.getMuilt(), 2)));
                    }
                }
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        }else if (count==2){
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
                bean.setType(item.getType());
                bean.setItemPos(item.getItemPos());
                if (i==7){
                    int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
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
            case 1://过频降载点
                tips=getString(R.string.android_key3048)+":"+"50.01~51Hz";
                break;
            case 2://过频降载恢复点
                tips=getString(R.string.android_key3048)+":"+"50.01~51Hz";
                break;
            case 3://过频降载 斜率
                tips=getString(R.string.android_key3048)+":"+"20%~100%Pn/Hz";
                break;
            case 4://过频降载 延时时间
                tips=getString(R.string.android_key3048)+":"+"0~2";
                break;
            case 5://过频降载恢复时间
                tips=getString(R.string.android_key3048)+":"+"0~600";
                break;
            case 6://过频降载相应时间
                tips=getString(R.string.android_key3048)+":"+"0~10";
                break;

            case 8://欠频加载点
                tips=getString(R.string.android_key3048)+":"+"48~49.9Hz";
                break;
            case 9://欠频加载停止点
                tips=getString(R.string.android_key3048)+":"+"48~49.9Hz";
                break;
            case 10://欠频加载斜率
                tips=getString(R.string.android_key3048)+":"+"5~100%";
                break;
            case 11://欠频加载延时时间
                tips=getString(R.string.android_key3048)+":"+"0~2s";
                break;
            case 12://欠频加载相应
                tips=getString(R.string.android_key3048)+":"+"0~10s";
                break;
        }

        String title = usvThroughBean.getTitle();

        if (position!=0&&position!=7){
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
        int value = check ? 1 : 0;
        USVThroughBean usvThroughBean = mAdapter.getData().get(position);
        usvThroughBean.setShowValue(String.valueOf(value));
        usvThroughBean.setRegistValue(value);
        mAdapter.notifyDataSetChanged();
    }
}
