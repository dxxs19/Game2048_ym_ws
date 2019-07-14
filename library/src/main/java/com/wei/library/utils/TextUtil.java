package com.wei.library.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by Administrator on 2016/9/10.
 */
public class TextUtil
{
    public static void copyText(Context context, String txt)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(txt);
        }
        else
        {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(txt);
        }
    }
}
