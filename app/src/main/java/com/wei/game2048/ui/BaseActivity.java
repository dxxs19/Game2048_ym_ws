package com.wei.game2048.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wei.ad.AppConnect;
import com.wei.game2048.R;
import com.wei.game2048.bean.ChallengeMode;
import com.wei.library.utils.Log;
import com.wei.library.utils.ResourceUtil;
import com.wei.library.utils.SharedPreUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.moments.WechatMoments;

public class BaseActivity extends AppCompatActivity
{
    protected final static String SHARED_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.wei.game2048";
//    /**
//     * 微信分享appid
//     */
//    public final static String WX_SHARED_APPID = "wx1639c5fdecd0be7e";
//    /**
//     * 微信分享appsecret
//     */
//    public final static String WX_SHARED_APPSECRET = "7cbef56b8d97b3373ab434df89054f54";

    public final static String ShareSDK_AppKey = "17446c334de86";

    protected String TAG = "";
    public final String[] choiceItems = new String[]{"512", "1024", "2048", "4096", "8192"};
    public final String[] patternItems = new String[]{"4 x 4", "5 x 5", "6 x 6"};
    protected SharedPreUtils sharedPreUtils;
    protected Context mContext;
    protected final String APP_PID = "default";
    public static final String VOICE = "voice_key"; // 声音开启
    public static final String ANIM = "anim_key";  // 动画开启
    public static final String MODE = "mode_key";  // 模式选择
    public static final String PATTERN = "pattern_key"; // 格局选择
    public static final String REVOKE = "revoke_key";  // 是否曾经成功去过广告
    public static final String DROP_ADVS = "drop_advs_key"; // 去广告
    public static final String SHOW_BANNERADVS = "showBannerAdvs"; // 显示广告
    public static final String INPUT_USERNAME = "input_username"; // 用户名
    public static final String GET_PAY_POINTS = "pay_points"; // 该消耗的积分
    public static final String OBJECT_ID = "ObjectId"; // 玩家id
    public static final String LOGO_PATH = "logo_path"; // 玩家id
    public static float PAY_POINTS = 20;
    public static int mode = 2048;
    protected String lastPattern = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG = getClass().getSimpleName();
        sharedPreUtils = SharedPreUtils.getInstance(mContext);
        mode = sharedPreUtils.getInt(MODE, 2048);
        lastPattern = sharedPreUtils.getString(PATTERN, patternItems[0]);
        setContentView(R.layout.base);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PAY_POINTS = Float.parseFloat(sharedPreUtils.getString(GET_PAY_POINTS, "20"));
    }

    protected void showAlertDialog(String msg, String negativeTxt, String positiveTxt, final View.OnClickListener clickListener)
    {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton(negativeTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positiveTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (null != clickListener)
                        {
                            clickListener.onClick(null);
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 设置广告条广告
     */
    protected void setupBannerAd()
    {
//        showYouMiBanner();
        showWpasBanner();
    }

    private void showYouMiBanner()
    {
        /**
         * 悬浮布局
         */
        // 实例化LayoutParams
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置，这里示例为右下角
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // 获取广告条控件
        final View bannerView =
                BannerManager.getInstance(mContext).getBannerView(new BannerViewListener
                        () {

                    @Override
                    public void onRequestSuccess() {
                        Log.i(TAG, "请求广告条成功");
                    }

                    @Override
                    public void onSwitchBanner() {
                        Log.i(TAG, "广告条切换");
                    }

                    @Override
                    public void onRequestFailed() {
                        Log.i(TAG, "请求广告条失败");
                    }
                });
        ((Activity) mContext).addContentView(bannerView, layoutParams);
    }

    private void showWpasBanner()
    {
        LinearLayout adlayout = new LinearLayout(this);
        adlayout.setGravity(Gravity.CENTER_HORIZONTAL);
        RelativeLayout.LayoutParams rlayoutParams = new
                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        AppConnect.getInstance(this).showBannerAd(this, adlayout);
        rlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//设置顶端或底端
        this.addContentView(adlayout, params);
    }

    protected void showMsg(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 查询多条数据
     */
    protected void queryObjects()
    {
        String modeStr = getModeStr();

        String sql = "select * from ChallengeMode order by " + modeStr + " desc ";
        Log.e(TAG, "sql = " + sql);
        //------------缓存查询------------------------------------------------------------
        BmobQuery<ChallengeMode> query = new BmobQuery<ChallengeMode>();
        query.setSQL(sql);

        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);	// 如果没有缓存的话，则先从网络中取
        //执行SQL查询操作
        query.doSQLQuery(sql, new SQLQueryListener<ChallengeMode>(){

            @Override
            public void done(BmobQueryResult<ChallengeMode> result, BmobException e) {
                if(e ==null){
                    List<ChallengeMode> list = (List<ChallengeMode>) result.getResults();
                    if(list!=null && list.size()>0){
                        for(int i=0;i<list.size();i++){
                            ChallengeMode p = list.get(i);
                            Log.e(TAG, ""+p.getObjectId()+"-"+p.getUserName()+"-");
                        }
                        showMsg("查询成功："+list.size());
                    }else{
                        showMsg("查询成功，无数据返回");
                    }
                }else{
                    Log.e(TAG, "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });
    }

    protected String getModeStr()
    {
        String modeStr = "";
        int mode = sharedPreUtils.getInt(MODE, 2048);
        switch (mode)
        {
            case 512:
                modeStr = "mode_512";
                break;

            case 1024:
                modeStr = "mode_1024";
                break;

            case 2048:
                modeStr = "mode_2048";
                break;

            case 4096:
                modeStr = "mode_4096";
                break;

            case 8192:
                modeStr = "mode_8192";
                break;
        }
        return modeStr;
    }

    protected void shared(final String msg)
    {
        ShareSDK.initSDK(mContext, ShareSDK_AppKey);
        final OnekeyShare onekeyShare = new OnekeyShare();
        onekeyShare.disableSSOWhenAuthorize();
        onekeyShare.setTitle(getString(R.string.app_name));
        onekeyShare.setTitleUrl(SHARED_URL);
        onekeyShare.setText(msg);
        onekeyShare.setImagePath(sharedPreUtils.getString(LOGO_PATH, ""));
        onekeyShare.setImageUrl(SHARED_URL);
        onekeyShare.setUrl(SHARED_URL);
        onekeyShare.setComment("");
        onekeyShare.setSite(msg);
        onekeyShare.setSiteUrl(SHARED_URL);

        // 实现该接口为不同平台设置不同的分享内容
        onekeyShare.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (WechatMoments.NAME.equals(platform.getName()) || WechatFavorite.NAME.equals(platform.getName()))
                {
                    paramsToShare.setTitle(getString(R.string.app_name) + "," + msg);
                }
            }
        });

        onekeyShare.show(mContext);
    }

    protected void saveLogo()
    {
        if (ResourceUtil.isSDCardExist())
        {
            String filePath = Environment.getExternalStorageDirectory() + "/game2048" ;
            String logoFile = filePath + "/logo.png";
            File file2 = new File(logoFile);
            File file1 = new File(filePath);
            if (!file1.exists())
            {
                file1.mkdirs();
                if (!file2.exists())
                {
                    try {
                        saveLogoToSDCard(file2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                if (!file2.exists())
                {
                    try {
                        saveLogoToSDCard(file2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void saveLogoToSDCard(File file) throws IOException
    {
        Bitmap bitmap = getImageFromAssets();
        if (bitmap != null) {
            saveBitmapToFile(bitmap, file);
        }
    }

    protected void saveBitmapToFile(Bitmap bitmap, File file) throws IOException
    {
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        sharedPreUtils.putString(LOGO_PATH, file.getPath());
        fos.flush();
        fos.close();
    }

    private Bitmap getImageFromAssets() {
        Bitmap bitmap = null;
        AssetManager assetMng = getAssets();
        try {
            InputStream is = assetMng.open("ic_logo.png");
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
