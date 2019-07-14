package com.wei.game2048.config;

/**
 * Created by Administrator on 2016/9/14.
 */
public class BmobConfig
{
    /**
     * Application ID，SDK初始化必须用到此密钥
     */
    public static final String Bmob_APP_ID = "ab734e43534180303016e378fbef376f";

    /**
     * REST API请求中HTTP头部信息必须附带密钥之一
     */
    public static final String BMOB_RESET_API_KEY = "2fc2d54dd5df1c32939861b809e3cfd0";

    /**
     * 是SDK安全密钥，不可泄漏，在云端逻辑测试云端代码时需要用到
     */
    public static final String BMOB_SECRET_KEY = "ac2c0b7764b76a9f";

    /**
     * 超级权限Key。应用开发或调试的时候可以使用该密钥进行各种权限的操作，此密钥不可泄漏
     */
    public static final String BMOB_MASTER_KEY = "da2878d6ced28133d9f13d04666d861b";
}
