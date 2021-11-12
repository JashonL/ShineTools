package com.growatt.shinetools.module.localbox.mintool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.TLXHAutoTestReportAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHAutoTestReportBean;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHToolAutoTestBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TLXHToolAutoTestReportActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.ivRight)
    ImageView mIvRight;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private View headerView;
    private ViewHolder headerHolder;
    private TLXHAutoTestReportBean mReportBean;
    private TLXHAutoTestReportAdapter mAdapter;
    private List<List<TLXHToolAutoTestBean>> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxhtool_auto_test_report);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mTvTitle.setText(R.string.自动测试报告);
        mIvRight.setImageResource(R.drawable.me_share_icon);
        headerView = LayoutInflater.from(this).inflate(R.layout.header_tlxhtool_auto_test_report, null);
        headerHolder = new ViewHolder(headerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new TLXHAutoTestReportAdapter(R.layout.item_autotest_report_rv,mList);
        mAdapter.addHeaderView(headerView);
        mRecyclerView.setAdapter(mAdapter);
        if (mReportBean != null){
            headerHolder.mTvFinishTime.setText(mReportBean.getFinishTime() + " " + mReportBean.getTitle());
            headerHolder.mTvContent1.setText(mReportBean.getStartDate());
            headerHolder.mTvContent2.setText(mReportBean.getStartTime());
            headerHolder.mTvContent3.setText(mReportBean.getDeviceSn());
            headerHolder.mTvContent4.setText(mReportBean.getVersion());
            mAdapter.replaceData(mReportBean.getList());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMin(@NonNull TLXHAutoTestReportBean mReportBean) {
        this.mReportBean = mReportBean;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ivLeft, R.id.ivRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.ivRight:

                break;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tvFinishTime)
        TextView mTvFinishTime;
        @BindView(R.id.tvContent1)
        TextView mTvContent1;
        @BindView(R.id.tvContent2)
        TextView mTvContent2;
        @BindView(R.id.tvContent3)
        TextView mTvContent3;
        @BindView(R.id.tvContent4)
        TextView mTvContent4;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
