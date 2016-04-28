package com.xxnr.operation.developTools;

import android.util.Log;


public class LogcatPrinter implements XLog.XPrinter {

	@Override
	public void println(int priority, String tag, String msg) {
		Log.println(priority, tag, "" + msg);
	}

}