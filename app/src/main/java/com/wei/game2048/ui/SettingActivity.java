package com.wei.game2048.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wei.game2048.R;

import net.youmi.android.offers.PointsManager;

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private CheckBox mVoiceChk, mAnimChk, mRevokeChk, mDropAdvsChk;
    private RelativeLayout mRelativeLayout, mRlPattern, mDropAdvsRl;
    private TextView txt_mode, txt_pattern;
    private boolean isPayPoint = false, canShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        PAY_POINTS = Float.parseFloat(sharedPreUtils.getString(GET_PAY_POINTS, "20"));
        canShow = Boolean.parseBoolean(sharedPreUtils.getString(SHOW_BANNERADVS, "false"));

        if (!sharedPreUtils.getBoolean(DROP_ADVS, false))
        { // 如果还没去除广告，则当广告开关开时，显示广告
            if (canShow)
            {
                setupBannerAd();
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        mVoiceChk = (CheckBox) findViewById(R.id.chk_voice);
        mAnimChk = (CheckBox) findViewById(R.id.chk_animation);
        mRevokeChk = (CheckBox) findViewById(R.id.chk_revoke);
        mDropAdvsChk = (CheckBox) findViewById(R.id.chk_dropAdvs);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_selected);
        mRlPattern = (RelativeLayout) findViewById(R.id.rl_pattern);
        mDropAdvsRl = (RelativeLayout) findViewById(R.id.rl_dropAdvs);
        txt_mode = (TextView) findViewById(R.id.txt_mode);
        txt_pattern = (TextView) findViewById(R.id.txt_pattern);

        mVoiceChk.setOnCheckedChangeListener(this);
        mAnimChk.setOnCheckedChangeListener(this);
        mRevokeChk.setOnCheckedChangeListener(this);
        mDropAdvsChk.setOnCheckedChangeListener(this);
        mRelativeLayout.setOnClickListener(this);
        mRlPattern.setOnClickListener(this);

        mVoiceChk.setChecked(sharedPreUtils.getBoolean(VOICE, false));
        mAnimChk.setChecked(sharedPreUtils.getBoolean(ANIM, false));
        isPayPoint = sharedPreUtils.getBoolean(REVOKE, false);
        mRevokeChk.setChecked(isPayPoint);
        txt_mode.setText(sharedPreUtils.getInt(MODE, 2048) + "");
        txt_pattern.setText(sharedPreUtils.getString(PATTERN, patternItems[0]));

        canShow = Boolean.parseBoolean(sharedPreUtils.getString(SHOW_BANNERADVS, "false"));
        if (canShow) {
            mDropAdvsRl.setVisibility(View.VISIBLE);
            mDropAdvsRl.setVisibility(sharedPreUtils.getBoolean(DROP_ADVS, false) ? View.GONE : View.VISIBLE);
        }
        else
        {
            mDropAdvsRl.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.chk_voice:
                sharedPreUtils.putBoolean(VOICE, isChecked);
                break;

            case R.id.chk_animation:
                sharedPreUtils.putBoolean(ANIM, isChecked);
                break;

            case R.id.chk_revoke:
                if (!canShow)
                {// 还不能开广告
                    sharedPreUtils.putBoolean(REVOKE, isChecked);
                    return;
                }
                // 广告已开
                if (isPayPoint)
                { // 如果曾经用过积分开启，则不再执行下去
                    mRevokeChk.setChecked(true);
                    return;
                }
                if (isChecked)
                {
                    float pointsBalance = MainActivity.getMainActivity().pointsBalance;
                    // 积分大于PAY_POINTS，则开启成功。否则跳到积分赚取界面赚取积分
                    if (pointsBalance >= PAY_POINTS)
                    {
                        PointsManager.getInstance(this).spendPoints(PAY_POINTS);
                        sharedPreUtils.putBoolean(REVOKE, true);
                    }
                    else
                    {
                        mRevokeChk.setChecked(false);
                        showAlertDialog("开启无限撤消功能需要消耗" + PAY_POINTS + "积分！您目前积分余额不足，快去赚取吧！",
                                "取消", "去赚取积分", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.getMainActivity().showOffersWall();
                            }
                        });
                    }
                }
                else
                {
                    sharedPreUtils.putBoolean(REVOKE, isChecked);
                }
                break;

            case R.id.chk_dropAdvs:
                if (isChecked)
                {
                    float pointsBalance = MainActivity.getMainActivity().pointsBalance;
                    // 积分大于PAY_POINTS，则开启成功。否则跳到积分赚取界面赚取积分
                    if (pointsBalance >= PAY_POINTS)
                    {
                        PointsManager.getInstance(this).spendPoints(PAY_POINTS);
                        sharedPreUtils.putBoolean(DROP_ADVS, true);
                    }
                    else
                    {
                        mDropAdvsChk.setChecked(false);
                        showAlertDialog("开启去广告功能需要消耗" + PAY_POINTS + "积分！您目前积分余额不足，快去赚取吧！",
                                "取消", "去赚取积分", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.getMainActivity().showOffersWall();
                            }
                        });
                    }
                }
                else
                {
                    sharedPreUtils.putBoolean(DROP_ADVS, isChecked);
                }
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rl_selected:
                int index = 0;
                int value = sharedPreUtils.getInt(MODE, 2048);
                for (int i = 0; i < choiceItems.length; i ++)
                {
                    if (choiceItems[i].equals(value + ""))
                    {
                        index = i;
                        break;
                    }
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("请选择挑战模式")
                        .setSingleChoiceItems(choiceItems, index, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveMode(Integer.parseInt(choiceItems[which]));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;

            case R.id.rl_pattern:
                int index1 = 0;
                String value1 = sharedPreUtils.getString(PATTERN, patternItems[0]);
                for (int i = 0; i < patternItems.length; i ++)
                {
                    if (patternItems[i].equals(value1 + ""))
                    {
                        index1 = i;
                        break;
                    }
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("请选择挑战格局")
                        .setSingleChoiceItems(patternItems, index1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savePattern(patternItems[which]);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }

    private void savePattern(String patternItem)
    {
        txt_pattern.setText(patternItem);
        sharedPreUtils.putString(PATTERN, patternItem);
    }

    private void saveMode(int mode)
    {
        txt_mode.setText(mode + "");
        sharedPreUtils.putInt(MODE, mode);
    }
}
