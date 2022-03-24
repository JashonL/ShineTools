package com.growatt.shinetools.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.growatt.shinetools.R;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.utils.chartformatter.MaxMarkerView;
import com.growatt.shinetools.widget.MyMarkerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChartUtils {
    public static SimpleDateFormat sdf_hm = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
    public static SimpleDateFormat sdf_ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final int animTamp = 1000;//动画时间

    public static final int minTamp = 60 * 1000;//一分钟的时间戳

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 曲线图汇总
     */
    /**
     * 初始化折线图
     * @param lineChart：折线图控件
     * @param format：y轴刻度显示类型
     *              1：代表转化成百分比
     *              0：代表转换成数字保留一位小数
     * @param unit:代表刻度单位
     * @param hasXGrid:是否有x轴网格线
     * @param xGridColor:x轴网格线颜色
     * @param hasYAxis:是否有y轴
     * @param yAxixColor:y轴颜色
     * @param isTouchEnable:是否允许触摸显示数值
     * @param XYTextColorId:xy轴文本颜色
     * @param XAxisLineColorId:x轴颜色
     * @param yGridLineColorId:y轴网格线颜色
     * @param highTextColor:高亮文本颜色：0：代表默认白色；其他：设置成具体颜色
     * @param showXYName:显示高亮文本Xy名称
     * @param xTextId:高亮X文本id
     * @param yTextId:高亮Y文本id
     */
    public static void initLineChart(Context context, LineChart lineChart, int format, final String unit, boolean hasXGrid, int xGridColor, boolean hasYAxis, int yAxixColor, boolean isTouchEnable, int XYTextColorId, int XAxisLineColorId, int yGridLineColorId, int highTextColor, boolean showXYName, int xTextId, int yTextId, OnEmptyListener listener) {
        //颜色转换
        int mXYTextColorId = ContextCompat.getColor(context,XYTextColorId);
        int mXAxisLineColorId = ContextCompat.getColor(context,XAxisLineColorId);
        int myGridLineColorId = ContextCompat.getColor(context,yGridLineColorId);
        int myXGridColor = ContextCompat.getColor(context,xGridColor);
        int myYAxixColor = ContextCompat.getColor(context,yAxixColor);
        lineChart.setDrawGridBackground(false);
        // no description text
        lineChart.getDescription().setEnabled(false);
        // enable touch gestures
        lineChart.setTouchEnabled(isTouchEnable);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.getViewPortHandler().setMaximumScaleX(8f);
//        lineChart.setVisibleXRangeMaximum(2f);
        lineChart.setPinchZoom(true);//设置mark轴
        if (isTouchEnable){
            if (highTextColor == 0){
                MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view);
                mv.setChartView(lineChart); // For bounds control
                lineChart.setMarker(mv); // Set the marker to the chart
            }else {
                if (showXYName){
                    MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,highTextColor,showXYName,xTextId,yTextId);
                    mv.setChartView(lineChart); // For bounds control
                    lineChart.setMarker(mv); // Set the marker to the chart
                }else {
                    MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,highTextColor,listener);
                    mv.setChartView(lineChart); // For bounds control
                    lineChart.setMarker(mv); // Set the marker to the chart
                }
            }
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        xAxis.setAxisMinimum(0f);
        xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(hasXGrid);//设置x轴上每个点对应的线
        xAxis.setGridColor(myXGridColor);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setTextColor(mXYTextColorId);//设置x轴文本颜色
//        xAxis.setGridColor(colorId);
        xAxis.setAxisLineColor(mXAxisLineColorId);
//		xAxis.enableAxisLineDashedLine(10f,0f,0f);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
////				return String.format("%.2f", value).replace",":");
//                return sdf_hm.format(new Date((long) (value * minTamp)));
//            }
//        });
        YAxis leftAxis = lineChart.getAxisLeft();
        if (format==1){
            leftAxis.setValueFormatter(new PercentFormatter());
            leftAxis.setAxisMaximum(100);
        }else if (format == 0){
            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (float)(Math.round(value*10))/10 + unit;
                }
            });
        }
        leftAxis.setTextColor(mXYTextColorId);
//		leftAxis.enableGridDashedLine(10f,10f,0f);//虚线
        leftAxis.setDrawAxisLine(hasYAxis);
        leftAxis.setAxisLineColor(myYAxixColor);
//        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisLineWidth(1.0f);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setGridColor(myGridLineColorId);
//		leftAxis.setAxisLineColor(colorId);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
////        xAxis.setTypeface(mTf); // 设置字体
//        xAxis.setEnabled(false);
//        // 上面第一行代码设置了false,所以下面第一行即使设置为true也不会绘制AxisLine
//        xAxis.setDrawAxisLine(true);
//
//        // 前面xAxis.setEnabled(false);则下面绘制的Grid不会有"竖的线"（与X轴有关）
//        xAxis.setDrawGridLines(true); // 效果如下图
        lineChart.animateX(2000);
        Legend mLegend = lineChart.getLegend(); // 设置坐标线描述?? 的样式
        mLegend.setEnabled(false);
    }



    public static void initBarChart(Context context, BarChart mChart, final String unit,
                                    boolean isTouchEnable, int XYTextColorId, int XAxisLineColorId,
                                    int yGridLineColorId, boolean hasYAxis, int yAxisColor,
                                    boolean hasXGrid, int xGridColor, int heighLightColor) {
        //颜色转换
        int mXYTextColorId = ContextCompat.getColor(context, XYTextColorId);
        int mXAxisLineColorId = ContextCompat.getColor(context, XAxisLineColorId);
        int myGridLineColorId = ContextCompat.getColor(context, yGridLineColorId);
        int myYAxisColor = ContextCompat.getColor(context, yAxisColor);
        int myXGridColor = ContextCompat.getColor(context, xGridColor);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(isTouchEnable);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        if (isTouchEnable) {
            MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view, heighLightColor);
            mv.setChartView(mChart); // For bounds control
            mChart.setMarker(mv); // Set the marker to the chart
        }
        mChart.animateY(animTamp);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(hasXGrid);
        xAxis.setGridColor(myXGridColor);
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setGranularity(1f);
//		xAxis.setAxisMinimum(1f);
//		xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        xAxis.setTextColor(mXYTextColorId);//设置x轴文本颜色
        xAxis.setAxisLineColor(mXAxisLineColorId);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String day = (int) value + "";
                LogUtil.i("day-->" + day);
                return day;
            }
        });


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0.0f);//设置0值下面没有间隙
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (float) (Math.round(value * 10)) / 10 + unit;
            }
        });

        leftAxis.setDrawAxisLine(hasYAxis);
        leftAxis.setAxisLineColor(myYAxisColor);
        leftAxis.setAxisLineWidth(1.0f);
        leftAxis.setGridLineWidth(0.5f);
//		leftAxis.enableGridDashedLine(10f,10f,0f);
        leftAxis.setTextColor(mXYTextColorId);
        leftAxis.setGridColor(myGridLineColorId);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }





    public static void initBarChart(Context context, BarChart mChart, final String unit, boolean isTouchEnable, int XYTextColorId, int XAxisLineColorId, int yGridLineColorId, boolean hasYAxis, int yAxisColor, boolean hasXGrid, int xGridColor, int heighLightColor, OnEmptyListener listener) {
        //颜色转换
        int mXYTextColorId = ContextCompat.getColor(context,XYTextColorId);
        int mXAxisLineColorId = ContextCompat.getColor(context,XAxisLineColorId);
        int myGridLineColorId = ContextCompat.getColor(context,yGridLineColorId);
        int myYAxisColor = ContextCompat.getColor(context,yAxisColor);
        int myXGridColor = ContextCompat.getColor(context,xGridColor);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(isTouchEnable);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        if (isTouchEnable){
            MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,heighLightColor,false,listener);
            mv.setChartView(mChart); // For bounds control
            mChart.setMarker(mv); // Set the marker to the chart
        }
        mChart.animateY(1500);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(hasXGrid);
        xAxis.setGridColor(myXGridColor);
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setGranularity(1f);
//		xAxis.setAxisMinimum(1f);
        xAxis.setLabelCount(9);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        xAxis.setTextColor(mXYTextColorId);//设置x轴文本颜色
        xAxis.setAxisLineColor(mXAxisLineColorId);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String day = ((((int) value)-1) * 2 +1) +"";
                LogUtil.i("day-->"+day);
                return day;
            }
        });


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0.0f);//设置0值下面没有间隙
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (float)(Math.round(value*10))/10 + unit;
            }
        });


        leftAxis.setDrawAxisLine(hasYAxis);
        leftAxis.setAxisLineColor(myYAxisColor);
        leftAxis.setAxisLineWidth(1.0f);
        leftAxis.setGridLineWidth(0.5f);
//		leftAxis.enableGridDashedLine(10f,10f,0f);
        leftAxis.setTextColor(mXYTextColorId);
        leftAxis.setGridColor(myGridLineColorId);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }






    public static String getFormatDate(String dateFromat, Date date) {
        if (TextUtils.isEmpty(dateFromat)) {
            dateFromat = DATE_FORMAT;
        }
        if (date == null) {
            date = new Date();
        }
        return getDateFormat(dateFromat).format(date);
    }


    /**
     * 获取SimpleDateFormat对象，线程安全
     *
     * @param dateFormat："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static DateFormat getDateFormat(String dateFormat) {
        return new SimpleDateFormat(dateFormat);
    }


    /**
     * 解析寄存器数据,单个柱状图
     *
     * @param dataList
     * @param bs
     * @param type:0--时；1--天；2--月；3--年
     * @return
     * @throws Exception
     */
    public static List<List<BarEntry>> parseBarChartRegister(List<List<BarEntry>> dataList, byte[] bs, int type) throws Exception {
        if (bs != null) {
            //获取字节数组长度
            int len = bs.length;
            //获取数据数量
            int size = len / 4;
            //解析数据并封装
            List<BarEntry> entrys = dataList.get(0);
            for (int i = 0; i < size; i++) {
                //获取单个数据，高低寄存器
                int obtain = MaxWifiParseUtil.obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, i * 2, 0, 2));
                double value = Arith.mul(obtain, 0.1);
                BarEntry entry = new BarEntry(i, (float) value);
                entrys.add(entry);
            }
        } else {
            List<BarEntry> entrys = dataList.get(0);
            for (int i = 1; i < 7; i++) {
                BarEntry entry = new BarEntry(i, 0);
                entrys.add(entry);
            }
        }
        return dataList;
    }



    public static void setBarChartData(Context context, BarChart mChart, List<List<BarEntry>> barYList, int[] colors, int count) {
        setBarChartData(context, mChart, barYList, colors, new int[]{-1}, count);
    }

    public static void setBarChartData(Context context, BarChart mChart, List<List<BarEntry>> barYList, int[] colors, int[] colorHights, int count) {
        if (mChart == null || barYList == null) return;
        List<IBarDataSet> barSetDatas = new ArrayList<>();
        BarData barData = mChart.getBarData();
        //计算最小横坐标
        float minX = barYList.get(0).get(0).getX();
        if (barData != null && barData.getDataSetCount() >= count) {
            for (int i = 0; i < count; i++) {
                BarDataSet dataSet = (BarDataSet) barData.getDataSetByIndex(i);
                dataSet.setValues(barYList.get(i));
            }
//			mChart.getXAxis().setAxisMaximum(minX);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            for (int i = 0; i < count; i++) {
                BarDataSet barSet = new BarDataSet(barYList.get(i), "");
                barSet.setColor(ContextCompat.getColor(context, colors[i]));
                if (colorHights[i] != -1) {
                    barSet.setHighLightColor(ContextCompat.getColor(context, colorHights[i]));
                }
                barSet.setDrawValues(false);
//                barSet.setValueTextColor(colorId);
                barSetDatas.add(barSet);
            }
            BarData data = new BarData(barSetDatas);
            mChart.setData(data);
            mChart.setFitBars(true);
            mChart.getBarData().setBarWidth(0.75f);
//			mChart.getXAxis().setAxisMinimum(minX);
        }
        mChart.animateY(animTamp);
        mChart.invalidate();
    }

}
