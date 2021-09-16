package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxCheckAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckBean;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.widget.MultiSelectPopWindow;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;

public class MaxCheckActivity extends DemoBase implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private String mTitle;
    private MaxCheckAdapter mAdapter;
    private List<MaxCheckBean> mList;
    private String[] titles;
    private String[] titles1;
    private String[] contents;
    /**
     * 时间：单位s
     */
    private int[] times = {
            270, 180, 90, 60, 120
    };
    private String reminderText;
    private String reminderText2;

    private int user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_check);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        reminderText = getString(R.string.m458确保逆变器在运行状态1);
        reminderText2 = getString(R.string.m458确保逆变器在运行状态2);


        //数据集合
        titles = new String[]{
                getString(R.string.m445智能IV曲线扫描), getString(R.string.m446故障录波检测),
                getString(R.string.m447实时录波检测), getString(R.string.m448一键诊断)
//                "智能I-V曲线扫描", "故障录波检测", "实时录波检测", "一键诊断"
        };
        titles1 = new String[]{
                getString(R.string.m454IVPV曲线), getString(R.string.m455电网电压波形),
                getString(R.string.m456电网电压谐波THDV), getString(R.string.m457电网线路阻抗)
                , getString(R.string.mPV对地阻抗检测)
//                "I-V/P-V曲线", "电网电压波形", "电网电压谐波(THDV)", "电网线路阻抗"
        };

        contents = new String[]{
                getString(R.string.m449可远程扫描每路MPPT), getString(R.string.m450可远程快速精确地进行故障定位),
                getString(R.string.m451可实时观察逆变器电压电流质量等), getString(R.string.m452一键检测电站环境信息包括IV曲线扫描电网侧电压波形THDV以及线路阻抗)
//                "可远程扫描每路I-V曲线或P-V曲线", "可远程、快速、精确地进行故障定位", "可实时观察逆变器电压电流质量等", "一键检测电站环境信息，包括I-V曲线扫描，电网侧电压波形，THDV以及线路阻抗"
        };
        int[] imgs = {
                R.drawable.max_iv_graph, R.drawable.max_fault, R.drawable.max_real_time, R.drawable.max_onekey
        };


        user_type = ShineToosApplication.getContext().getUser_type();

        if (END_USER == user_type) {

            titles = new String[]{
                    getString(R.string.m445智能IV曲线扫描),getString(R.string.m448一键诊断)
//                "智能I-V曲线扫描", "故障录波检测", "实时录波检测", "一键诊断"
            };

            contents = new String[]{
                    getString(R.string.m449可远程扫描每路MPPT),  getString(R.string.m452一键检测电站环境信息包括IV曲线扫描电网侧电压波形THDV以及线路阻抗)
//                "可远程扫描每路I-V曲线或P-V曲线", "可远程、快速、精确地进行故障定位", "可实时观察逆变器电压电流质量等", "一键检测电站环境信息，包括I-V曲线扫描，电网侧电压波形，THDV以及线路阻抗"
            };
            imgs = new int[]{
                    R.drawable.max_iv_graph,  R.drawable.max_onekey
            };
        }


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MaxCheckBean bean = new MaxCheckBean();
            bean.setImgId(imgs[i]);
            bean.setTitle(titles[i]);
            bean.setContent(contents[i]);
            mList.add(bean);
        }
        mAdapter = new MaxCheckAdapter(R.layout.item_max_check_act, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 跳转到Max各设置界面
     */
    private void jumpMaxSet(Class clazz, String title) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        jumpTo(intent, false);
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
    }

    private boolean[] checkedItems = new boolean[]{false, false, false, false};

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == mAdapter) {

            if (END_USER==user_type){
                switch (position) {
                    case 0:
                        jumpMaxSet(MaxCheckIVActivity.class, titles[position]);
                        break;
                    case 1:
                        MultiSelectPopWindow pop = new MultiSelectPopWindow.Builder(MaxCheckActivity.this)
                                .setNameArray(new ArrayList(Arrays.asList(titles1)))
                                .setConfirmListener(new MultiSelectPopWindow.OnConfirmClickListener() {
                                    @Override
                                    public void onClick(ArrayList<Integer> indexList, ArrayList<String> selectedList) {
                                        //do something
                                        if (indexList != null && indexList.size() > 0) {
                                            //计算总时间
                                            int totalTime = 0;
                                            StringBuilder sb = new StringBuilder();
                                            for (Integer i : indexList) {
                                                sb.append(i);
                                                totalTime = totalTime + times[i];
                                            }
                                            final String content = String.valueOf(sb);
                                            //温馨提示
                                            String noteStr = reminderText;
                                            if (!String.valueOf(sb).contains("2")) {
                                                noteStr = reminderText2;
                                            }

                                            String dialogText = noteStr + new DecimalFormat("0.#").format(totalTime / 60.0f) + "min";

                                            CircleDialogUtils.showCommentDialog(MaxCheckActivity.this, getString(R.string.reminder), dialogText, getString(R.string.all_ok), getString(R.string.all_no), Gravity.CENTER, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent mIntent = new Intent(MaxCheckActivity.this, MaxCheckOneKeyActivity.class);
                                                    mIntent.putExtra("title", titles[position]);
                                                    mIntent.putExtra("content", content);
                                                    jumpTo(mIntent, false);
                                                }
                                            }, null);

                                        }
                                    }
                                })
                                .setCancel(getString(R.string.all_no))
                                .setConfirm(getString(R.string.all_ok))
                                .setTitle(getString(R.string.m225请选择))
                                .build();
                        pop.show(view);
                        pop.selectAll();
                        break;
                }
            }else {
                switch (position) {
                    case 0:
                        jumpMaxSet(MaxCheckIVActivity.class, titles[position]);
                        break;
                    case 1:
                        jumpMaxSet(MaxCheckErrorActivity.class, titles[position]);
                        break;
                    case 2:
                        jumpMaxSet(MaxCheckRealWaveActivity.class, titles[position]);
                        break;
                    case 3:
                        MultiSelectPopWindow pop = new MultiSelectPopWindow.Builder(MaxCheckActivity.this)
                                .setNameArray(new ArrayList(Arrays.asList(titles1)))
                                .setConfirmListener(new MultiSelectPopWindow.OnConfirmClickListener() {
                                    @Override
                                    public void onClick(ArrayList<Integer> indexList, ArrayList<String> selectedList) {
                                        //do something
                                        if (indexList != null && indexList.size() > 0) {
                                            //计算总时间
                                            int totalTime = 0;
                                            StringBuilder sb = new StringBuilder();
                                            for (Integer i : indexList) {
                                                sb.append(i);
                                                totalTime = totalTime + times[i];
                                            }
                                            final String content = String.valueOf(sb);
                                            //温馨提示
                                            String noteStr = reminderText;
                                            if (!String.valueOf(sb).contains("2")) {
                                                noteStr = reminderText2;
                                            }

                                            String dialogText = noteStr + new DecimalFormat("0.#").format(totalTime / 60.0f) + "min";

                                            CircleDialogUtils.showCommentDialog(MaxCheckActivity.this, getString(R.string.reminder), dialogText, getString(R.string.all_ok), getString(R.string.all_no), Gravity.CENTER, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent mIntent = new Intent(MaxCheckActivity.this, MaxCheckOneKeyActivity.class);
                                                    mIntent.putExtra("title", titles[position]);
                                                    mIntent.putExtra("content", content);
                                                    jumpTo(mIntent, false);
                                                }
                                            }, null);

                                        }
                                    }
                                })
                                .setCancel(getString(R.string.all_no))
                                .setConfirm(getString(R.string.all_ok))
                                .setTitle(getString(R.string.m225请选择))
                                .build();
                        pop.show(view);
                        pop.selectAll();
                        break;
                }
            }

        }
    }
}
