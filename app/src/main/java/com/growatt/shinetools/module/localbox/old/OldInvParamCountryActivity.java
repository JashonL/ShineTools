package com.growatt.shinetools.module.localbox.old;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.TLXParamCountryAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.ToolModelBean;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.tlx.bean.TLXParamCountryBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.mylhyl.circledialog.CircleDialog;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



/**
 * Max 本地调试国家以及安规
 */
public class OldInvParamCountryActivity extends DemoBase implements BaseQuickAdapter.OnItemClickListener {
    String readStr;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.btnSelect)
    Button mBtnSelect;
    @BindView(R.id.llSelectType)
    LinearLayout mLlSelectType;
    @BindView(R.id.btnSelectType)
    TextView mBtnSelectType;
    @BindView(R.id.btnSelectMech)
    TextView mBtnSelectMech;
    @BindView(R.id.btnSelectModel)
    TextView mBtnSelectModel;
    private String mTitle;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private int mType = 0;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int[][] nowSet;
    //读取的model值
    private int mReadValue = -1;
    private String[][][][] modelToal;
    //当前读取的组
    private String[][] nowModels;
    //当前的值
    private String[] nowModel;
    //寄存器设置数据集合
    private String[] contents = new String[8];
    /**
     * model类型：-1未获取，0：老版本（28-29寄存器）；1：新版本（118-121寄存器）
     */
    private int modeType = -1;
    private int[][] funs2;
    private TLXParamCountryAdapter mAdapter;
    private DialogFragment panelDialog;
    private TLXParamCountryBean mItem;
    //DTC
    private String[] select1Types = {
            "MTL-S/-S","3-15K TL3-S","17-25K 30-50K TL3-S"
    };
    private int[] seletTypeDTCs = {
            210,2049,2069
    };
    private String[] select1Mechs = {
            "EU Model","Australia Model","UK Model"
    };
    private List<String> select1Models = new ArrayList<>();
    private List<String> select1ModelValues = new ArrayList<>();
    private int mSelectType = -1;
    private int mSelectMech = -1;
    private String mSelectModel = "";
    private boolean isFlagModel;//是否匹配到了DTC model
    private boolean isShowDialog = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_inv_param_country);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initString();
        initIntent();
        initHeaderView();
        mAdapter = new TLXParamCountryAdapter(R.layout.item_panel_select, new ArrayList<>());
        mAdapter.setOnItemClickListener(this);
        //读取寄存器的值
        readRegisterValue();
    }

    private void initString() {
        modelToal = new String[][][][]{
                {{
                        {"A0S1", "VDE0126", "VDE0126;Spain"}, {"A0S4", "Italy"}
                        , {"A0S7", "N4105", "Germany;Austria;Switzerland"}, {"A0SB", "EN50438", "EN50438;Poland"}
                        , {"A0SD", "Belgium"}, {"A1S4", "Sweden"}
                        , {"A1S5", "EN50438_Norway"}, {"A0S6", "Greece"}
                        , {"A1S7", "France"}, {"A1SA", "CEI0-16"}
                        , {"A2S0", "Denmark", "Denmark（DK1);Denmark（DK2）"}, {"A1SE", "BDEW"}
                        , {"A1SB", "DRRG"}, {"A1SC", "Chile"}
                        , {"A1SD", "Argentina"}
                },
                        {{"A0S2", "UK_G59"}, {"A0S8", "UK_G83"}, {"A0S9", "EN50438_Ireland"}},
                        {{"A0S3", "AS4777_Australia"}, {"A1S0", "AS4777_Newzealand"}},
                        {{"A0SE", "MEA"}, {"A0SF", "PEA"}},
                        {{"A0S4", "Italy"}},
                        {{"A0SC", "Hungary"}},
//                        {{"A0SA", "中国"}},
//                        {{"A1S1", "中国"}},
                        {{"A1S2", "India"}},
                        {{"A1S8", "Korea"}},
                        {{"A1S9", "Brazil"}},
                }
                ,
                {{
                        {"S01", "VDE0126", "VDE0126;Spain"}, {"S04", "Italy"}
                        , {"S07", "N4105", "Germany;Austria;Switzerland"}, {"S0B", "EN50438", "EN50438;Poland"}
                        , {"S0D", "Belgium"}, {"S14", "Sweden"}
                        , {"S15", "EN50438_Norway"}, {"S06", "Greece"}
                        , {"S17", "France"}, {"S1A", "CEI0-16"}
                        , {"S20", "Denmark", "Denmark（DK1);Denmark（DK2）"}, {"S1E", "BDEW"}
                        , {"S1B", "DRRG"}, {"S1C", "Chile"}
                        , {"S1D", "Argentina"}
                },
                        {{"S02", "UK_G59"}, {"S08", "UK_G83"}, {"S09", "EN50438_Ireland"}},
                        {{"S03", "AS4777_Australia"}, {"S10", "AS4777_Newzealand"}},
                        {{"S0E", "MEA"}, {"S0F", "PEA"}},
                        {{"S04", "Italy"}},
                        {{"S0C", "Hungary"}},
//                        {{"S0A", "中国"}},
//                        {{"S11", "中国"}},
                        {{"S12", "India"}},
                        {{"S18", "Korea"}},
                        {{"S19", "Brazil"}},
                }
        };
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                //参数设置
                {3, 0, 44}
        };
        funs2 = new int[][]{
                //参数设置
                {3, 28, 29}//Model old
                , {3, 118, 121}//Model new
        };
        //需要设置的内容
        nowSet = new int[][]{
                {6, funs2[mType][1], -1}
                , {6, funs2[mType][1] + 1, -1}
                , {6, funs2[mType][1] + 2, -1}
                , {6, funs2[mType][1] + 3, -1}
        };
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
        setHeaderTvTitle(headerView, getString(R.string.m370读取), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取寄存器的值
                readRegisterValue();
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
        mHandlerRead.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_EXCETION_CLOSE,3000);
    }

    //读取寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (nowSet != null) {
                        if (nowSet[0][2] == -1 && nowSet[1][2] == -1) {
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        } else {
                            for (int len = nowSet.length, i = 0; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    BtnDelayUtil.sendMessageWrite(this);
                                    sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //发送完将值设置为-1
                                    nowSet[i][2] = -1;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                            toast(getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            nowSet[0][2] = -1;
                            nowSet[1][2] = -1;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入" + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle
     */
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funs[0]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    this.removeMessages(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 28, 0, 2);
                            modeType = 0;
                            mType = 0;
                            parseOldModle(valueBs);
                            //解析DTC
                            int dtc = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,43,0,1));
                            //是否在可选DTC上
                            boolean isFlag = false;
                            int typePos = -1;
                            for (int i = 0; i < seletTypeDTCs.length; i++) {
                                if (dtc == seletTypeDTCs[i]){
                                    typePos = i;
                                    isFlag = true;
                                    break;
                                }
                            }
                            if (!isFlag){
                                this.sendEmptyMessage(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                            }else {
                                //设置对应可选model
                                LinkedHashMap<Integer, LinkedHashMap<String, String>> typeMap = ToolModelBean.getOldInvModelMap().get(typePos);
                                //匹配model
                                String nowModel = String.format("A%sS%s",contents[0],contents[7]);
                                isFlagModel = false;//是否能匹配到model
                                int mechType = -1;
                                out:for(Map.Entry<Integer,LinkedHashMap<String,String>> mechEntry : typeMap.entrySet()) {
                                    LinkedHashMap<String, String> mechMap = mechEntry.getValue();
                                    mechType = mechEntry.getKey();
                                    for(Map.Entry<String, String> entry : mechMap.entrySet()) {
                                        if (nowModel.equals(entry.getValue())){
                                            isFlagModel = true;
                                            break out;
                                        }
                                    }
                                }
                                if (isFlagModel){//识别到了model对应分组
                                    mBtnSelect.setText(nowModel);
                                    mTvContent1.setText(nowModel);
                                    select1ModelValues.clear();
                                    select1Models.clear();
                                    for(Map.Entry<String, String> entry : typeMap.get(mechType).entrySet()) {
                                        String modelEn = String.format("%s(%s)",entry.getKey(),entry.getValue());
                                        if (nowModel.equals(entry.getValue())){
                                            mBtnSelect.setText(modelEn);
                                            mTvContent1.setText(modelEn);
                                        }
                                        select1ModelValues.add(entry.getValue());
                                        select1Models.add(modelEn);
                                    }
                                    if (isShowDialog) {
                                        new CircleDialog.Builder()
                                                .setWidth(0.7f)
                                                .setGravity(Gravity.CENTER)
                                                .setMaxHeight(0.5f)
                                                .setTitle(getString(R.string.m225请选择))
                                                .setNegative(getString(R.string.all_no), null)
                                                .setItems(select1Models, (parent, view1, position, id) -> {
                                                    mBtnSelect.setText(select1Models.get(position));
                                                    mSelectModel = select1ModelValues.get(position);
                                                    return true;
                                                })
                                                .show(getSupportFragmentManager());
                                    }
                                }else {//没有识别到，直接显示model
                                    mSelectModel= nowModel;
                                    mBtnSelect.setText(nowModel);
                                    mTvContent1.setText(nowModel);
                                    dialogShow(getString(R.string.m1259未识别对应的安规));
                                }

                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    //关闭tcp连接
                    SocketClientUtil.close(mClientUtilRead);
                    Mydialog.Dismiss();
                    dialogShow(getString(R.string.reminder));
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };
    /**
     * 弹框提示选择进入界面
     */
    private void dialogShow(String title) {
        new CircleDialog.Builder()
                .setTitle(title)
                .setText(getString(R.string.m1257是否需要手动选择) + "?")
                .setGravity(Gravity.CENTER)
                .setWidth(0.8f)
                .setNegative(getString(R.string.all_no),null)
                .setPositive(getString(R.string.all_ok),view -> {
                    CommenUtils.showAllView(mLlSelectType);
                    CommenUtils.hideAllView(View.GONE,mBtnSelect);
                })
                .show(getSupportFragmentManager());
    }
    private void parseOldModle(byte[] bs) {
        //解析int值
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        mReadValue = value0;
        //设置值
        contents[0] = MaxUtil.getDeviceModelSingle(value0, 8);
        contents[1] = MaxUtil.getDeviceModelSingle(value0, 7);
        contents[2] = MaxUtil.getDeviceModelSingle(value0, 6);
        contents[3] = MaxUtil.getDeviceModelSingle(value0, 5);
        contents[4] = MaxUtil.getDeviceModelSingle(value0, 4);
        contents[5] = MaxUtil.getDeviceModelSingle(value0, 3);
        contents[6] = MaxUtil.getDeviceModelSingle(value0, 2);
        contents[7] = MaxUtil.getDeviceModelSingle(value0, 1);
//        //model as简写
//        String as = String.format("A%sS%s", contents[0], contents[7]);
//        String[][][] singleModel = modelToal[mType];
//        out:
//        for (int i = 0; i < singleModel.length; i++) {
//            String[][] models = singleModel[i];
//            int jLen = models.length;
//            for (int j = 0; j < jLen; j++) {
//                String[] model = models[j];
//                if (model[0].equals(as)) {
//                    nowModels = singleModel[i];
//                    nowModel = nowModels[j];
//                    //更新ui
////                    mBtnSelect.setText(nowModel[1]);
//                    String setText = nowModel[1];
//                    String modelSave = SharedPreferencesUnit.getInstance(this).getDefNull(Constant.TOOL_TLX_MODEL_COUNTRY);
//                    if (!TextUtils.isEmpty(modelSave)) {
//                        String[] split = modelSave.split(":");
//                        if (setText.equals(split[0]) && nowModel.length > 2) {
//                            String[] countries = nowModel[2].split(";");
//                            try {
//                                setText = countries[Integer.parseInt(split[1])];
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    mBtnSelect.setText(setText);
//                    mTvContent1.setText(String.format("%s-%s", nowModel[0], setText));
//                    break out;
//                }
//            }
//        }
    }

    private void parseNewModle(byte[] bs) {
        //解析int值
        BigInteger big = new BigInteger(1, bs);
        long bigInteger = big.longValue();
        //更新ui
//        mTvContent1.setText(readStr + ":" + MaxUtil.getDeviceModelNew4(bigInteger));
//        mEt1New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15));
//        mEt2New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13));
//        mEt3New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11));
//        mEt4New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9));
//        mEt5New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7));
//        mEt6New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5));
//        mEt7New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3)
//                + MaxUtil.getDeviceModelSingleNew(bigInteger, 2)  + MaxUtil.getDeviceModelSingleNew(bigInteger, 1) );
        //设置值
        contents[0] = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
        contents[1] = MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13);
        contents[2] = MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11);
        contents[3] = MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9);
        contents[4] = MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7);
        contents[5] = MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5);
        contents[6] = MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3);
        contents[7] = MaxUtil.getDeviceModelSingleNew(bigInteger, 2) + MaxUtil.getDeviceModelSingleNew(bigInteger, 1);
        //model as简写
        String as = String.format("S%s", contents[0]);
        String[][][] singleModel = modelToal[mType];
        out:
        for (int i = 0; i < singleModel.length; i++) {
            String[][] models = singleModel[i];
            int jLen = models.length;
            for (int j = 0; j < jLen; j++) {
                String[] model = models[j];
                if (model[0].equals(as)) {
                    nowModels = singleModel[i];
                    nowModel = nowModels[j];
                    //更新ui
//                    mBtnSelect.setText(nowModel[1]);
                    String setText = nowModel[1];
                    String modelSave = SharedPreferencesUnit.getInstance(this).getDefNull(GlobalConstant.TOOL_TLX_MODEL_COUNTRY);
                    if (!TextUtils.isEmpty(modelSave)) {
                        String[] split = modelSave.split(":");
                        if (setText.equals(split[0]) && nowModel.length > 2) {
                            String[] countries = nowModel[2].split(";");
                            try {
                                setText = countries[Integer.parseInt(split[1])];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mBtnSelect.setText(setText);
                    mTvContent1.setText(String.format("%s-%s", nowModel[0], setText));
                    break out;
                }
            }
        }
    }

    @OnClick({R.id.btnSelect, R.id.btnSetting, R.id.btnSelectType, R.id.btnSelectMech, R.id.btnSelectModel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelectType:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no),null)
                        .setItems(select1Types,(parent, view1, position, id) -> {
                            mBtnSelectType.setText(select1Types[position]);
                            mSelectType = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelectMech:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no),null)
                        .setItems(select1Mechs,(parent, view1, position, id) -> {
                            mBtnSelectMech.setText(select1Mechs[position]);
                            mSelectMech = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelectModel:
                if (mSelectType == -1){
                    toast(R.string.m257请选择设置值);
                    return;
                }
                if (mSelectMech == -1){
                    toast(R.string.m257请选择设置值);
                    return;
                }
                LinkedHashMap<String, String> modelMap = ToolModelBean.getOldInvModelMap().get(mSelectType).get(mSelectMech);
                if (modelMap == null) return;
                select1ModelValues.clear();
                select1Models.clear();
                for(Map.Entry<String, String> entry : modelMap.entrySet()) {
                    select1ModelValues.add(entry.getValue());
                    select1Models.add(String.format("%s(%s)",entry.getKey(),entry.getValue()));
                }
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no),null)
                        .setItems(select1Models,(parent, view1, position, id) -> {
                            mBtnSelectModel.setText(select1Models.get(position));
                            mSelectModel = select1ModelValues.get(position);
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelect:
                if (isFlagModel){
                    new CircleDialog.Builder()
                            .setWidth(0.7f)
                            .setGravity(Gravity.CENTER)
                            .setMaxHeight(0.5f)
                            .setTitle(getString(R.string.m225请选择))
                            .setNegative(getString(R.string.all_no),null)
                            .setItems(select1Models,(parent, view1, position, id) -> {
                                mBtnSelect.setText(select1Models.get(position));
                                mSelectModel = select1ModelValues.get(position);
                                return true;
                            })
                            .show(getSupportFragmentManager());
                }else {
                    isShowDialog = true;
                    readRegisterValue();
                }
//                if (nowModels == null) return;
//                List<TLXParamCountryBean> newList = new ArrayList<>();
//                for (int i = 0; i < nowModels.length; i++) {
//                    String[] nowModel = nowModels[i];
//                    if (nowModel.length > 2) {
//                        String[] split = nowModel[2].split(";");
//                        for (int i1 = 0; i1 < split.length; i1++) {
//                            TLXParamCountryBean bean = new TLXParamCountryBean();
//                            bean.setCountry(split[i1]);
//                            bean.setModel(nowModel[0]);
//                            bean.setSave(true);
//                            bean.setIndex(i1);
//                            bean.setModelTitle(nowModel[1]);
//                            newList.add(bean);
//                        }
//                    } else {
//                        TLXParamCountryBean bean = new TLXParamCountryBean();
//                        bean.setCountry(nowModel[1]);
//                        bean.setModel(nowModel[0]);
//                        newList.add(bean);
//                    }
//                }
//                mAdapter.replaceData(newList);
//                panelDialog = new CircleDialog.Builder()
//                        .setWidth(0.8f)
//                        .setMaxHeight(0.5f)
//                        .setGravity(Gravity.CENTER)
//                        .setTitle(getString(R.string.countryandcity_first_country))
//                        .setNegative(getString(R.string.all_no), null)
//                        .setItems(mAdapter, new LinearLayoutManager(this))
//                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSetting:
                if (TextUtils.isEmpty(contents[1])){
                    toast(R.string.m请先读取值);
                    return;
                }
                //需要设置的内容
                nowSet = new int[][]{
                        {6, funs2[mType][1], -1}
                        , {6, funs2[mType][1] + 1, -1}
                        , {6, funs2[mType][1] + 2, -1}
                        , {6, funs2[mType][1] + 3, -1}
                };
                switch (mType) {
                    case 0:
                        setOld();
                        break;
                    case 1:
                        setNew();
                        break;
                }
//                //保存本地记录安规
//                if (mItem != null && mItem.isSave()) {
//                    SharedPreferencesUnit.getInstance(this).put(Constant.TOOL_TLX_MODEL_COUNTRY, mItem.getModelTitle() + ":" + mItem.getIndex());
//                }
                break;
        }
    }

    private void setOld() {
        try {
            contents[0] = mSelectModel.substring(1,2);
            contents[7] = mSelectModel.substring(3,4);
            StringBuilder sbHigh = new StringBuilder();
            StringBuilder sbLow = new StringBuilder();
            for (int i = 0, len = contents.length; i < len; i++) {
                if (i < 4) {
                    sbHigh.append(contents[i]);
                } else {
//                            sbHigh.append("0");
                    sbLow.append(contents[i]);
                }
            }
            int high = Integer.parseInt(sbHigh.toString(), 16);
            int low = Integer.parseInt(sbLow.toString(), 16);
            nowSet[0][2] = high;
            nowSet[1][2] = low;
            readRegisterValueCom();
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363设置失败));
            nowSet[0][2] = -1;
            nowSet[1][2] = -1;
        }
    }

    private void setNew() {
        try {
            nowSet[0][2] = Integer.parseInt(contents[0] + contents[1], 16);
            nowSet[1][2] = Integer.parseInt(contents[2] + contents[3], 16);
            nowSet[2][2] = Integer.parseInt(contents[4] + contents[5], 16);
            nowSet[3][2] = Integer.parseInt(contents[6] + contents[7], 16);
            readRegisterValueCom();
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363设置失败));
            nowSet[0][2] = -1;
            nowSet[1][2] = -1;
            nowSet[2][2] = -1;
            nowSet[3][2] = -1;
        }
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilReadCom;
    private int[] funCom = {3, 0, 40};

    //读取寄存器的值
    private void readRegisterValueCom() {
        Mydialog.Show(mContext);
        mClientUtilReadCom = SocketClientUtil.connectServer(mHandlerReadCom);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadCom = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilReadCom, funCom);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
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
                            //解析读取值，设置com地址以及model
                            int comAddress = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 30, 0, 1));
                            ModbusUtil.setComAddressOldInv(comAddress);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                            //设置值
                            writeRegisterValue();
                        } else {
                            toast(R.string.all_failed);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilReadCom);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapter) {
            if (panelDialog != null) panelDialog.dismiss();
            TLXParamCountryBean item = mAdapter.getItem(position);
            mBtnSelect.setText(item.getCountry());
            mItem = item;
            //设置选中的值
            String value = item.getModel();
            switch (mType) {
                case 0:
                    contents[0] = value.substring(1, 2);
                    contents[7] = value.substring(3, 4);
                    break;
                case 1:
                    contents[0] = value.substring(1, 3);
                    break;
            }
        }
    }

}
