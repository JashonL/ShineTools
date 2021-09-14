package com.growatt.shinetools.module.localbox.configtype;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.configtype.usconfig.USPFsettingActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigOneTextActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType2Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigTypeSelectActivity;
import com.growatt.shinetools.module.localbox.mintool.USParamCountryActivity;
import com.growatt.shinetools.module.localbox.ustool.USFWattActivity;
import com.growatt.shinetools.module.localbox.ustool.USFreThroughActivity;
import com.growatt.shinetools.module.localbox.ustool.USGridActivity;
import com.growatt.shinetools.module.localbox.ustool.USRampRateActivity;
import com.growatt.shinetools.module.localbox.ustool.USVThroughActivity;
import com.growatt.shinetools.module.localbox.ustool.USVWattActivity;
import com.growatt.shinetools.module.localbox.ustool.USVolVarActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainsCodeParamSetActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;


    private MenuItem item;


    private UsSettingAdapter usParamsetAdapter;
    private String[] registers;
    private String[] titles;

    @Override
    protected int getContentView() {
        return R.layout.activity_mains_code_param;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
//        toolbar.inflateMenu(R.menu.comment_right_menu);
//        item = toolbar.getMenu().findItem(R.id.right_action);
//        item.setTitle(R.string.m370读取);
        toolbar.setOnMenuItemClickListener(this);


        rvSetting.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSetting.addItemDecoration(gridDivider);
        rvSetting.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
        usParamsetAdapter.setOnItemChildClickListener(this);
    }

    @Override
    protected void initData() {
        registers = new String[]{
                "", "", "", "", "", "", "", "",
        };
        titles = new String[]{
                getString(R.string.电压穿越)//0
                , getString(R.string.频率穿越)//2
                , getString(R.string.上升斜率)//3
                , getString(R.string.频率有功)//4
                , getString(R.string.电压有功)//5
                , getString(R.string.电压无功设置)//6
                , getString(R.string.并网范围),//7
                getString(R.string.pf_setting)//8
        };


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            int itemType = 0;
            if (i == 0 || i == 1) {
                itemType = UsSettingConstant.SETTING_TYPE_EXPLAIN;
            } else {
                itemType = UsSettingConstant.SETTING_TYPE_SELECT;
            }
            bean.setItemType(itemType);
            bean.setRegister(registers[i]);
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = null;
        Class clazz = null;
        int type = -1;
        int pos = -1;
        switch (position) {
            case 0:
                clazz = USVThroughActivity.class;
                break;
            case 1:
                clazz = USFreThroughActivity.class;
                break;
            case 2:
                clazz = USRampRateActivity.class;
                break;
            case 3:
                clazz = USFWattActivity.class;
                break;
            case 4:
                clazz = USVWattActivity.class;
                break;
            case 5:
                clazz = USVolVarActivity.class;
                break;
            case 6:
                clazz = USGridActivity.class;
                break;

            case 7:
             /*   dialogShow(Arrays.asList(pfSetting), new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Class clazz = null;
                        int type = -1;
                        int pos = -1;
                        switch (position){
                            case 0:type = 0;pos = 4;break;
                            case 1:type = 9;pos = 4;break;
                            case 2:type = 9;pos = 1;break;

                            case 3:type = 9;pos = 2;break;
                            case 4:type = 9;pos = 3;break;
                            case 5:type = 0;pos = 27;break;
                            case 6:type = 2;pos = 4;break;
                            case 7:type = 7;pos = 0;break;

                            case 8:type = 7;pos = 1;break;
                        }
                        switch (type){
                            case 0:
                                clazz = USConfigTypeSelectActivity.class;
                                break;
                            case 1:
                                clazz = TLXConfigType1Activity.class;
                                break;
                            case 2:
                                clazz = TLXConfigType2Activity.class;
                                break;
                            case 7:
                                clazz = TLXConfigType4Activity.class;
                                break;
                            case 9:
                                clazz = TLXConfigType1AndPFActivity.class;
                                break;
                        }
                        if (clazz != null) {
                            Intent intent = new Intent(mContext, clazz);
                            intent.putExtra("type", pos);
                            intent.putExtra("title", String.format("%s%s",pfSetting[position],pfSettingRegister[position]));
                            jumpTo(intent, false);
                        }
                        return true;
                    }
                });*/

                clazz = USPFsettingActivity.class;
                break;
        }

        if (position != 17) {
            switch (type) {
                case 0:
                    clazz = TLXConfigTypeSelectActivity.class;
                    break;
                case 1:
                    clazz = TLXConfigType1Activity.class;
                    break;
                case 2:
                    clazz = TLXConfigType2Activity.class;
                    break;
                case 4:
                    clazz = ConfigTypeTimeActivity.class;
                    break;
                case 5:
                    clazz = USParamCountryActivity.class;
                    break;
                case 6:
                    clazz = TLXConfigOneTextActivity.class;
                    break;
            }
            if (clazz != null) {
                intent = new Intent(mContext, clazz);
                intent.putExtra("type", pos);
                intent.putExtra("title", String.format("%s%s", titles[position], registers[position]));
                ActivityUtils.startActivity(MainsCodeParamSetActivity.this, intent, false);
            } else {
                MyControl.circlerDialog(MainsCodeParamSetActivity.this, getString(R.string.该项暂不能进入), -1, false);
            }
        }
    }

    @Override
    public void oncheck(boolean check, int position) {

    }

    private BaseCircleDialog explainDialog;
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        switch (view.getId()) {
            case R.id.tv_title:
                if (bean.getItemType() == UsSettingConstant.SETTING_TYPE_EXPLAIN) {
                    String title = bean.getTitle();
                    String content="";
                    if (position==0){
                        content=getString(R.string.android_key3104);
                    }else if (position==1){
                        content=getString(R.string.android_key3105);
                    }
                    explainDialog = CircleDialogUtils.showExplainDialog(MainsCodeParamSetActivity.this, title,content ,
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
}
