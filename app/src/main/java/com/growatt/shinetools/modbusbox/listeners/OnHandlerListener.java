package com.growatt.shinetools.modbusbox.listeners;

/**
 * Created by dg on 2017/6/15.
 */

public interface OnHandlerListener {
    void handlerDeal(int result, String msg);
}
