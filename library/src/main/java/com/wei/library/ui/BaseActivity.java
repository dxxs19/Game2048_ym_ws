package com.wei.library.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wei.library.R;
import com.wei.library.utils.Log;

public class BaseActivity extends FragmentActivity
{
    protected  String TAG = "";
    protected Context mContext;
    private LinearLayout viewGroup;
    protected LinearLayout titleCustom;
    protected LinearLayout container;
    protected RelativeLayout titleContainer;
    protected LinearLayout headMid;
    protected View back;
    protected TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG = getClass().getSimpleName();
        initBaseView();
    }

    private void initBaseView()
    {
        viewGroup = (LinearLayout) View.inflate(this, R.layout.activity_base, null);
        titleCustom = (LinearLayout) viewGroup.findViewById(R.id.title_custom);
        titleContainer = (RelativeLayout) viewGroup.findViewById(R.id.title_container);
        container = (LinearLayout) viewGroup.findViewById(R.id.container);
        headMid = (LinearLayout) viewGroup.findViewById(R.id.ll_head);
        back = viewGroup.findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        title = (TextView) viewGroup.findViewById(R.id.title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = View.inflate(this, layoutResID, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -1);
        contentView.setLayoutParams(layoutParams);
        container.addView(contentView);
        super.setContentView(viewGroup);
    }

    public void hideTitle() {
        titleContainer.setVisibility(View.GONE);
        //title.setVisibility(View.GONE);
    }

    public void setCustomTitle(int resid) {
        title.setText(resid);
        title.setVisibility(View.VISIBLE);
    }

    public void setCustomTitle(String text) {
        title.setText(text);
        title.setVisibility(View.VISIBLE);
    }

    /**
     * 打开应用设置界面
     *
     * @param requestCode 请求码
     *
     * @return
     */
    protected boolean openApplicationSettings(int requestCode) {
        try {
            Intent intent =
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            // Android L 之后Activity的启动模式发生了一些变化
            // 如果用了下面的 Intent.FLAG_ACTIVITY_NEW_TASK ，并且是 startActivityForResult
            // 那么会在打开新的activity的时候就会立即回调 onActivityResult
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, requestCode);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }
        return false;
    }

    public void showMsg(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
