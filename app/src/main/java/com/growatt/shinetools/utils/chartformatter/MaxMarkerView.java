package com.growatt.shinetools.utils.chartformatter;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.growatt.shinetools.R;
import com.growatt.shinetools.listeners.OnEmptyListener;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MaxMarkerView extends MarkerView {
    private TextView tvContent;
    private Context mContext;
    private int mXTextId;//x轴数据名称
    private int mYTextId;//y轴数据名称m
    private boolean mShowXy = true;//显示X
    private OnEmptyListener mListener;
    public MaxMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }
    public MaxMarkerView(Context context, int layoutResource,int textColor) {
        super(context, layoutResource);
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context,textColor));
    }
    public MaxMarkerView(Context context, int layoutResource, int textColor, OnEmptyListener listener) {
        super(context, layoutResource);
        mListener = listener;
        mContext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context,textColor));
    }
    public MaxMarkerView(Context context, int layoutResource, int textColor,boolean showX, OnEmptyListener listener) {
        super(context, layoutResource);
        mListener = listener;
        mContext = context;
        mShowXy = showX ;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context,textColor));
    }
    public MaxMarkerView(Context context, int layoutResource, int textColor,boolean showXy,int xTextId,int yTextId) {
        super(context, layoutResource);
        mContext = context;
        mXTextId = xTextId;
        mYTextId = yTextId;
        mShowXy = showXy ;
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTextColor(ContextCompat.getColor(context,textColor));
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (mListener != null) {
            mListener.onEmpty(e,highlight);
        }
        StringBuilder sb = new StringBuilder();
        if (mShowXy) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                sb.append("X:").append(ce.getX()).append("\n").append("Y:").append(ce.getHigh());
                tvContent.setText(sb.toString());
            } else {
                sb.append("X:").append(e.getX()).append("\n").append("Y:").append(e.getY());
                tvContent.setText(sb.toString());
            }
        }else {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                sb.append(ce.getHigh());
                tvContent.setText(sb.toString());
            } else {
                sb.append(e.getY());
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
