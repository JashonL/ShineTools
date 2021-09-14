package com.growatt.shinetools.module.localbox.ustool;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.localbox.ustool.errorcode.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class USFaultDetailActivity extends BaseActivity {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.iv_fault)
    ImageView ivFault;
    @BindView(R.id.tv_fault)
    AppCompatTextView tvFault;
    @BindView(R.id.tv_fault_code)
    AppCompatTextView tvFaultCode;
    @BindView(R.id.v_fault)
    View vFault;
    @BindView(R.id.tv_fault_detail)
    AppCompatTextView tvFaultDetail;
    @BindView(R.id.cvError)
    CardView cvError;
    @BindView(R.id.iv_warn)
    ImageView ivWarn;
    @BindView(R.id.tv_warning)
    AppCompatTextView tvWarning;
    @BindView(R.id.tv_warning_code)
    AppCompatTextView tvWarningCode;
    @BindView(R.id.v_warning)
    View vWarning;
    @BindView(R.id.tv_warning_detail)
    AppCompatTextView tvWarningDetail;
    @BindView(R.id.cvWarning)
    CardView cvWarning;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_fault_detail;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3106);
    }

    @Override
    protected void initData() {
        int errorCode = getIntent().getIntExtra(ErrorCode.KEY_US_ERROR, -1);
        int warnning = getIntent().getIntExtra(ErrorCode.KEY_US_WARNING, -1);

        int secondError = getIntent().getIntExtra(ErrorCode.KEY_US_SECOND_ERROR, -1);
        int secondWarnning = getIntent().getIntExtra(ErrorCode.KEY_US_SECOND_WARNING, -1);

        if (errorCode != -1) {
            String s = errorCode + "(" + secondError + ")";

            tvFaultCode.setText(s);

            try {
                JSONObject jsonObject = new JSONObject(ErrorCode.ERROR);
                String value = jsonObject.optString(String.valueOf(errorCode), "");
                if (!TextUtils.isEmpty(value)) {
                    String[] content = value.split("_");
                    if (content.length > 0) {
                        int language = getLanguage();
                        if (language==0){
                            //中文描述
                            String des_zn = content[0];
                            //解决方案_中文
                            String deal_zn = content[1];

                            if (!TextUtils.isEmpty(deal_zn)&&!TextUtils.isEmpty(des_zn)&&!"null".equals(deal_zn)&&!"null".equals(des_zn)){
                                String detail =des_zn+"\n"+deal_zn;
                                tvFaultDetail.setText(detail);
                            }

                        }else {

                            //英文描述
                            String des_en = content[2];
                            //英文解决_英文
                            String deal_en = content[3];

                            if (!TextUtils.isEmpty(des_en)&&!TextUtils.isEmpty(deal_en)&&!"null".equals(des_en)&&!"null".equals(deal_en)){
                                String detail =des_en+"\n"+deal_en;
                                tvFaultDetail.setText(detail);
                            }

                        }








                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (warnning != -1) {
            String s = warnning + "(" + secondWarnning + ")";

            tvWarningCode.setText(s);

            try {
                JSONObject jsonObject = new JSONObject(ErrorCode.WARNING);
                String value = jsonObject.optString(String.valueOf(warnning), "");
                if (!TextUtils.isEmpty(value)) {
                    String[] content = value.split("_");
                    if (content.length > 0) {
                        int language = getLanguage();
                        if (language==0){
                            //中文描述
                            String des_zn = content[0];
                            //解决方案_中文
                            String deal_zn = content[1];

                            if (!TextUtils.isEmpty(deal_zn)&&!TextUtils.isEmpty(des_zn)&&!"null".equals(deal_zn)&&!"null".equals(des_zn)){
                                String detail =des_zn+"\n"+deal_zn;
                                tvWarningDetail.setText(detail);
                            }

                        }else {

                            //英文描述
                            String des_en = content[2];
                            //英文解决_英文
                            String deal_en = content[3];

                            if (!TextUtils.isEmpty(des_en)&&!TextUtils.isEmpty(deal_en)&&!"null".equals(des_en)&&!"null".equals(deal_en)){
                                String detail =des_en+"\n"+deal_en;
                                tvWarningDetail.setText(detail);
                            }

                        }

                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


}
