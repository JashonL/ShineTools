package com.growatt.shinetools.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.growatt.shinetools.R;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.CommenUtils;

import java.util.Date;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {
    //    public static final int minTamp = 60*1000;//一分钟的时间戳
//    public static SimpleDateFormat sdf_hm = new SimpleDateFormat("HH:mm");


    private TextView tvContent;
    private Context mContext;
    private int mXTextId;//x轴数据名称
    private int mYTextId;//y轴数据名称m
    private boolean mShowXy = false;//显示xy名称
    private OnEmptyListener mListener;
    private String unit;//单位
    private boolean isDate=true;


    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public MyMarkerView(Context context, int layoutResource,String unit,boolean isDate) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.unit=unit;
        this.isDate=isDate;
    }

    public MyMarkerView(Context context, int layoutResource, int textColor) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context, textColor));
    }


    public MyMarkerView(Context context, int layoutResource, int textColor,String unit,boolean isDate) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context, textColor));
        this.unit=unit;
        this.isDate=isDate;
    }

    public MyMarkerView(Context context, int layoutResource, int textColor, boolean showXy, int xTextId, int yTextId) {
        super(context, layoutResource);
        mContext = context;
        mXTextId = xTextId;
        mYTextId = yTextId;
        mShowXy = showXy;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context, textColor));
    }

    public MyMarkerView(Context context, int layoutResource, int textColor, boolean showXy, int xTextId, int yTextId,String unit,boolean isDate) {
        super(context, layoutResource);
        mContext = context;
        mXTextId = xTextId;
        mYTextId = yTextId;
        mShowXy = showXy;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context, textColor));
        this.unit=unit;
        this.isDate=isDate;
    }




    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (mListener != null) {
            mListener.onEmpty(e, highlight);
        }
        if (!mShowXy) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                tvContent.setText("" + ce.getHigh());
            } else {
                tvContent.setText("" + e.getY());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                if (isDate){
                    sb.append(mContext.getText(mXTextId)).append(":").append(ChartUtils.sdf_hm.format(new Date((long) (ce.getX() * ChartUtils.minTamp)))).append("\n").append(mContext.getText(mYTextId)).append(":").append(ce.getHigh());
                }else {
                    int hourValue= (int) (ce.getX()/60);
                    int minValue= (int) (ce.getX()%60);
                    String xValue= ((hourValue ) < 10 ? "0" + hourValue : hourValue) +
                            ":" + ((minValue < 10) ? "0" + minValue : minValue);
                    sb.append(mContext.getText(mXTextId)).append(":").append(xValue).append("\n").append(mContext.getText(mYTextId)).append(":").append(ce.getHigh());
                }
                if (!TextUtils.isEmpty(unit)){
                    tvContent.setText(sb.toString());
                }

            } else {

                if (isDate){
                    sb.append(mContext.getText(mXTextId)).append(":").append(ChartUtils.sdf_hm.format(new Date((long) (e.getX() * ChartUtils.minTamp)))).append("\n").append(mContext.getText(mYTextId)).append(":").append(e.getY());

                }else {
                    int hourValue= (int) (e.getX()/60);
                    int minValue= (int) (e.getX()%60);
                    String xValue= ((hourValue ) < 10 ? "0" + hourValue : hourValue) +
                            ":" + ((minValue < 10) ? "0" + minValue : minValue);
                    sb.append(mContext.getText(mXTextId)).append(":").append(xValue).append("\n").append(mContext.getText(mYTextId)).append(":").append(e.getY());
                }
                if (!TextUtils.isEmpty(unit)){
                    tvContent.setText(sb.toString());
                }

                tvContent.setText(sb.toString());
            }
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
