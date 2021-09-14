package com.growatt.shinetools.utils.chartformatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MaxChartFormatter extends ValueFormatter {

    int nowPos=0;

    public MaxChartFormatter(int nowPos) {
        this.nowPos=nowPos;
    }

    @Override
    public String getFormattedValue(float valueF) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat spfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat spfMonth = new SimpleDateFormat("yy-MM");
        SimpleDateFormat spfDay = new SimpleDateFormat("MM-dd");
        SimpleDateFormat spfHour = new SimpleDateFormat("dd/HH");


        int value = (int) valueF;
        String result = (value + 1) + "";
        if (nowPos == 2) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int totalMon = year * 12 + month - value;
            year = totalMon / 12;
            month = totalMon % 12;
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            result = spfMonth.format(calendar.getTime());
        } else if (nowPos == 3) {
            int year = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, year - value);
            result = spfYear.format(calendar.getTime());
        } else if (nowPos == 1) {
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.set(Calendar.DAY_OF_YEAR, day - value);
            result = spfDay.format(calendar.getTime());
        } else if (nowPos == 0) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int totalDay = day * 24 + hour - value;
            day = totalDay / 24;
            hour = totalDay % 24;
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            result = spfHour.format(calendar.getTime());
        }
        return result;
    }

}
