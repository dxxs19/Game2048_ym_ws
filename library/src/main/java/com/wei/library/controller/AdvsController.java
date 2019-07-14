package com.wei.library.controller;

import android.content.Context;

import com.wei.library.ui.BaseActivity;
import com.wei.library.utils.Log;
import com.wei.library.utils.SharedPreUtils;

import net.youmi.android.AdManager;
import net.youmi.android.onlineconfig.OnlineConfigCallBack;
import net.youmi.android.onlineconfig.ntp.NtpResultListener;

/**
 * Created by Administrator on 2016/9/12.
 */
public class AdvsController
{
    private final String TAG = getClass().getSimpleName();
    private static Context mContext;
    private static AdvsController advsController;

    public static AdvsController getInstance(Context context)
    {
        if (null == advsController)
        {
            mContext = context;
            advsController = new AdvsController();
        }
        return advsController;
    }

    /**
     * 使用有米在线参数:开发者可以用于动态修改应用中的配置项，如欢迎语、道具价格、广告开关等等。
     * 开发者在 「有米主站」 开发者面板的应用详情里面设置指定应用的在线参数，然后在代码中调用它。
     * @param key
     */
    public void getTargetValue(String key)
    {
        AdManager.getInstance(mContext).asyncGetOnlineConfig(key, new OnlineConfigCallBack() {
            @Override
            public void onGetOnlineConfigSuccessful(String key, String value)
            {
                SharedPreUtils.getInstance(mContext).putString(key, value);
                Log.e(TAG, " key : " + key + ", value : " + value);
            }

            @Override
            public void onGetOnlineConfigFailed(String key) {
                Log.e(TAG, " key : " + key );
            }
        });
    }

    /**
     * 使用在线时间:通过检查在线时间，开发者可以设置应用的部分功能开启，如：神秘任务的开启、广告开关等等。
     * 开发者指定一个目标日期(年、月、日)，然后通过下面代码即可检查是否到达该目标日期
     * @param targetYear
     * @param targetMonth
     * @param targetDay
     */
    public void isReachTargetDate(final int targetYear, final int targetMonth, final int targetDay)
    {
        AdManager.getInstance(mContext).asyncCheckIsReachNtpTime(targetYear, targetMonth, targetDay, new NtpResultListener()
        {
            @Override
            public void onCheckNtpFinish(boolean b) {
                SharedPreUtils.getInstance(mContext).putBoolean("isReach", b);
                Log.e(TAG, String.format("是否到达指定日期:%d-%d-%d %s", targetYear, targetMonth, targetDay, b ? "是" : "否"));
                if (b)
                {
                    SharedPreUtils.getInstance(mContext).putString("showBannerAdvs", "true");
                }
            }
        });
    }
}
