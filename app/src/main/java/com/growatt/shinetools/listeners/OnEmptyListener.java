package com.growatt.shinetools.listeners;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created：2018/2/1 on 20:08
 * Author:gaideng on dg
 * Description:
 */

public interface OnEmptyListener {
    void onEmpty(Entry e, Highlight highlight);
}
