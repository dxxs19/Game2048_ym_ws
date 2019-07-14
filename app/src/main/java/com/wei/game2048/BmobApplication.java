package com.wei.game2048;

import android.app.Application;
import android.content.Context;

import com.wei.game2048.config.BmobConfig;
import com.wei.game2048.ui.BaseActivity;
import com.wei.library.controller.AdvsController;
import com.wei.library.utils.SharedPreUtils;

import cn.bmob.v3.Bmob;

public class BmobApplication extends Application {
	/**
	 * SDK初始化也可以放到Application中
	 * 46c730e7e33eabeb3ec790b3fb0a02d7
	 * 3124f50157a5df138aba77a85e1d8909
	 */
	public static String APPID = BmobConfig.Bmob_APP_ID;

	@Override
	public void onCreate() {
		super.onCreate();
		Bmob.initialize(this,APPID);
		checkOnlineConfig();
	}

	/**
	 * 检测在线配置参数
	 */
	protected void checkOnlineConfig()
	{
		Context mContext = getApplicationContext();
//		AdvsController.getInstance(mContext).getTargetValue(BaseActivity.SHOW_BANNERADVS);
//		AdvsController.getInstance(mContext).getTargetValue(BaseActivity.GET_PAY_POINTS);
//		AdvsController.getInstance(mContext).isReachTargetDate(2016, 9, 22);

		SharedPreUtils.getInstance(mContext).putString(BaseActivity.SHOW_BANNERADVS, "true");
		SharedPreUtils.getInstance(mContext).putString(BaseActivity.GET_PAY_POINTS, "30");
	}
}