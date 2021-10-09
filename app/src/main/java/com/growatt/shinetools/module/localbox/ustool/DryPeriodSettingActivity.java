package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.DryPeriodAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.DryStartTimeBean;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DryPeriodSettingActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, BaseQuickAdapter.OnItemClickListener{


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_period_explain)
    AppCompatTextView tvPeriodExplain;
    @BindView(R.id.rv_period)
    RecyclerView rvPeriod;


    private DryPeriodAdapter mDryPeriodAdapter;
    private MenuItem item;

    private int[][] nowSet;


    @Override
    protected int getContentView() {
        return R.layout.activity_dry_period;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        //标题头部
        initToobar(toolbar);
        tvTitle.setText(R.string.dry_contact_setting);

        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m182保存);
        toolbar.setOnMenuItemClickListener(this);


        rvPeriod.setLayoutManager(new LinearLayoutManager(this));
        mDryPeriodAdapter = new DryPeriodAdapter(R.layout.item_dry_period, new ArrayList<>());
        rvPeriod.setAdapter(mDryPeriodAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {

        nowSet = new int[][]{
                {6, 706, 0},//启动时间点1
                {6, 707, 0},//退出时间点1
                {6, 708, 0},//启动时间点2
                {6, 709, 0},//退出时间点2
                {6, 710, 0},//启动时间点3
                {6, 711, 0},//退出时间点3
        };


        String[] titles = new String[]{getString(R.string.m222时间段) + 1, getString(R.string.m222时间段) + 2, getString(R.string.m222时间段) + 3};
        List<DryStartTimeBean> newList = new ArrayList<>();
        for (String title : titles) {
            DryStartTimeBean dryStartTimeBean = new DryStartTimeBean();
            dryStartTimeBean.setTime("00:00~00:00");
            dryStartTimeBean.setTitle(title);
            dryStartTimeBean.setStartTime("00:00");
            dryStartTimeBean.setEndTime("00:00");
            newList.add(dryStartTimeBean);
        }
        mDryPeriodAdapter.replaceData(newList);

        String timelist = getIntent().getStringExtra("timelist");
        if (!TextUtils.isEmpty(timelist)){
            try {
                JSONArray jsonArray=new JSONArray(timelist);
                List<DryStartTimeBean>list=new ArrayList<>();
                for (int i = 0; i < titles.length; i++) {
                    if (jsonArray.length()>i){
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        DryStartTimeBean dryStartTimeBean = new Gson().fromJson(jsonObject.toString(), DryStartTimeBean.class);
                        String title = getString(R.string.m222时间段) + i;
                        dryStartTimeBean.setTitle(title);
                        list.add(dryStartTimeBean);
                    }else {
                        DryStartTimeBean dryStartTimeBean = new DryStartTimeBean();
                        String title = getString(R.string.m222时间段) + i;
                        dryStartTimeBean.setTitle(title);
                        dryStartTimeBean.setStartHour(0);
                        dryStartTimeBean.setStartMin(0);
                        dryStartTimeBean.setEndHour(0);
                        dryStartTimeBean.setEndMin(0);
                        dryStartTimeBean.setTime("00:00-00:00");
                        list.add(dryStartTimeBean);
                    }

                }
                mDryPeriodAdapter.replaceData(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        List<DryStartTimeBean> data = mDryPeriodAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            DryStartTimeBean dryStartTimeBean = data.get(i);
            //启动时间点
            int startHour = dryStartTimeBean.getStartHour();
            int startMin = dryStartTimeBean.getStartMin();

            byte b1 = (byte) (startHour & 0xFF);
            byte b2 = (byte) (startMin & 0xFF);

            int startTime = (((0x000000ff & b1) << 8) & 0x0000ff00) | (0x000000ff & b2);
            nowSet[i*2][2]=startTime;
            //退出时间点
            int endHour = dryStartTimeBean.getEndHour();
            int endMin = dryStartTimeBean.getEndMin();


            byte b3 = (byte) (endHour & 0xFF);
            byte b4 = (byte) (endMin & 0xFF);

            int endTime = (((0x000000ff & b3) << 8) & 0x0000ff00) | (0x000000ff & b4);
            nowSet[i*2+1][2]=endTime;
        }
        writeRegisterValue();
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DryStartTimeBean dryStartTimeBean = mDryPeriodAdapter.getData().get(position);
        int startHour = dryStartTimeBean.getStartHour();
        int startMin = dryStartTimeBean.getStartMin();
        int endHour = dryStartTimeBean.getEndHour();
        int endMin = dryStartTimeBean.getEndMin();


        DryTimeSelectActivity.TimeBean bean = new DryTimeSelectActivity.TimeBean();
        bean.setStartHour(startHour);
        bean.setStartMin(startMin);
        bean.setEndHour(endHour);
        bean.setEndMin(endMin);
        bean.setPosition(position);

        String json = new Gson().toJson(bean);
        Intent intent = new Intent(DryPeriodSettingActivity.this, DryTimeSelectActivity.class);
        intent.putExtra(GlobalConstant.KEY_JSON, json);
        ActivityUtils.startActivity(DryPeriodSettingActivity.this, intent, false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMin(@NonNull DryTimeSelectActivity.TimeBean bean) {
        int position = bean.getPosition();
        int startHour = bean.getStartHour();
        int startMin = bean.getStartMin();

        int endHour = bean.getEndHour();
        int endMin = bean.getEndMin();


        String time = String.format("%02d:%02d-%02d:%02d", startHour, startMin, endHour, endMin);
        DryStartTimeBean dryStartTimeBean = mDryPeriodAdapter.getData().get(position);
        dryStartTimeBean.setTime(time);
        dryStartTimeBean.setStartHour(startHour);
        dryStartTimeBean.setStartMin(startMin);
        dryStartTimeBean.setEndHour(endHour);
        dryStartTimeBean.setEndMin(endMin);
        mDryPeriodAdapter.notifyDataSetChanged();
    }





    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //设置寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }


    private byte[] sendBytes;
    int count=0;

    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = sendMsg(mClientUtilWriter, nowSet[count]);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;

                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            if (count>=nowSet.length-1){
                                toast(R.string.all_success);
                            }
                        } else {
                            toast(R.string.all_failed);
                        }

                        if (count<nowSet.length-1){
                            count++;
                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        }else {
                            count=0;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };

    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
    private byte[] sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            return null;
        }
    }

}
