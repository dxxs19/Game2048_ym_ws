package com.wei.library.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * 获取各种资源
 * @author X-WEI
 *
 */
public class ResourceUtil 
{
	/**
	 * 根据图片的资源id转换成drawable对象
	 * @param context
	 * @param id
	 * @return
	 */
    public static Drawable getDrawableById(Context context, int id)
    {
    	return context.getResources().getDrawable(id);
    }
    
    /**
     * 根据字符串的资源Id转换成相应的字符串
     * @param context
     * @param id
     * @return
     */
    public static String getStringById(Context context, int id)
    {
    	return context.getResources().getString(id);
    }
    
    /**
     * 判断手机是否存在SDCard及SDCard是否可用
     * @return
     */
    public static boolean isSDCardExist()
    {
    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return true;
    }
    
}
