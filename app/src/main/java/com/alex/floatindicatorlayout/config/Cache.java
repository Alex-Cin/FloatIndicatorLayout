package com.alex.floatindicatorlayout.config;

import android.os.Environment;

import java.io.File;

public class Cache
{
	private static final String split = File.separator;
	private static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()+split;
	public static final String crashLogPath = rootPath + "第12人"+split+"crash.Log";

}
