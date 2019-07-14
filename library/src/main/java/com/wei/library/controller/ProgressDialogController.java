package com.wei.library.controller;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by WEI on 2016/8/29.
 */
public class ProgressDialogController
{
    public static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context, String msg)
    {
        dismissDialog();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public static void dismissDialog()
    {
        if (null != mProgressDialog)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
