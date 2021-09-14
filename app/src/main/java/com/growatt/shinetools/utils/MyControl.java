package com.growatt.shinetools.utils;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.listeners.OnCirclerDialogListener;

public class MyControl {
	public static void showJumpWifiSet(FragmentActivity act){
		CommenUtils.showJumpWifiSet(act);
	}

	public static void circlerDialog(FragmentActivity act, String text, int result){
		OssUtils.circlerDialog(act,text,result);
	}
	public static void circlerDialog(FragmentActivity act, String text, int result,boolean isFinish){
		OssUtils.circlerDialog(act,text,result,isFinish);
	}

	public static void circlerDialog(FragmentActivity act, String text, int result, OnCirclerDialogListener circlerDialogListener){
		OssUtils.circlerDialog(act,text,result,circlerDialogListener);
	}
}
