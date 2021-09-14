package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.utils.Position;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaxChartPowerActivity extends DemoBase {
    @BindView(R.id.headerView)
    View headerView;
    private String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_chart_power);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
        }
    }
}
