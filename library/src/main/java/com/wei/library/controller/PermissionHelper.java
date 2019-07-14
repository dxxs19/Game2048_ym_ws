package com.wei.library.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by WEI on 2016/8/19.
 */
public class PermissionHelper
{
    public static boolean isHavePermission(Context context, String permissions)
    {
        return ContextCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED;
    }
}
