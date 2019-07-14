package com.wei.library.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/9/9.
 */
public class ScreenUtils
{
    private final static int WIDTH = 0x10;
    private final static int HEIGHT = 0x11;

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context)
    {
        return getSize(context, WIDTH);
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context)
    {
        return getSize(context, HEIGHT);
    }

    private static int getSize(Context context, int flag)
    {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        if (flag == WIDTH)
        {
            return displayMetrics.widthPixels;
        }
        else if (flag == HEIGHT)
        {
            return displayMetrics.heightPixels;
        }
        return 0;
    }
}
