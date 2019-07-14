package com.wei.game2048.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wei.ad.AppConnect;
import com.wei.game2048.R;
import com.wei.game2048.bean.ChallengeMode;
import com.wei.game2048.widgets.GameView;
import com.wei.library.constant.AdvConstant;
import com.wei.library.controller.ProgressDialogController;
import com.wei.library.utils.Log;

import net.youmi.android.AdManager;
import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.EarnPointsOrderInfo;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsEarnNotify;
import net.youmi.android.offers.PointsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseActivity implements PointsChangeNotify, PointsEarnNotify {
    private final static String TOPSCORE = "topscore";
    private TextView scoreTxt, topScoreTxt, currentModeTxt, currentPointsTxt;
    public static MainActivity mMainActivity;
    private int score, topScore;
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> soundMap = new HashMap<>();
    private GameView mGameView;
    public float pointsBalance;
    private boolean canShow;
    private String objectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        initView();
        initSoundPool();
        AppConnect.getInstance(AdvConstant.APP_ID, APP_PID,this);
        initYoumi();
        if (!hasInputUserName())
        {
            showInputDialog();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                saveLogo();
            }
        }).start();
    }

    private void showInputDialog()
    {
        final EditText editText = new EditText(mContext);
        new AlertDialog.Builder(mContext)
                .setTitle("输入昵称")
                .setMessage("请输入昵称，方便查看排行榜上排名情况！")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        List<BmobObject> scores = new ArrayList<BmobObject>();
                        ChallengeMode challengeMode = new ChallengeMode();
                        challengeMode.setUserName(editText.getText() + "");
                        scores.add(challengeMode);
                        BmobBatch batch = new BmobBatch();
                        batch.insertBatch(scores);
                        batch.doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> results, BmobException ex) {
                                if(ex==null){//这个ex只是代表此次请求正常返回-至于返回的是正确的还是错误的，需要查看BatchResult里面的数据
                                    for(int i=0;i<results.size();i++){
                                        BatchResult result= results.get(i);
                                        if(result.isSuccess()){//只有批量添加才返回objectId
                                            Log.e(TAG, result.getObjectId());
                                            sharedPreUtils.putString(OBJECT_ID,  result.getObjectId());
                                            sharedPreUtils.putString(INPUT_USERNAME, editText.getText() + "");
                                        }else{
                                            BmobException error= result.getError();
                                            Log.e(TAG, error.getErrorCode()+","+error.getMessage());
                                        }
                                    }
                                }else{
                                    Log.e(TAG, ex.getMessage());
                                }
                            }
                        });
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
    }

    private boolean hasInputUserName()
    {
        String userName = sharedPreUtils.getString(INPUT_USERNAME, "");
        Log.e(TAG, "userName : " + userName);
        return userName.equals("") ? false : true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        topScore = sharedPreUtils.getInt(TOPSCORE, 0);
        topScoreTxt.setText(topScore + "");
        currentModeTxt.setText(sharedPreUtils.getInt(MODE, 2048) + "");

        canShow = Boolean.parseBoolean(sharedPreUtils.getString(SHOW_BANNERADVS, "false"));
        if (!sharedPreUtils.getBoolean(DROP_ADVS, false))
        { // 如果还没去除广告，则当广告开关开时，显示广告
            if (canShow) {
                //设置广告条
                setupBannerAd();
            }
        }

        String currPattern = sharedPreUtils.getString(PATTERN, patternItems[0]);
        if (!lastPattern.equals(currPattern))
        { // 格局切换，则重新开始游戏
            lastPattern = currPattern;
            mGameView.removeAllViews();
            mGameView.initView();
            mGameView.clearLastGameMaps();
        }
        else
        {
            // 格局没变，则恢复最后的格局
            mGameView.recoverLastGameMaps();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_rankinglist:
                startActivity(new Intent(mContext, RankingListActivity.class));
                break;

            case R.id.menu_startGame:
                start();
                break;

            case R.id.menu_back:
                if (!sharedPreUtils.getBoolean(REVOKE, false)) {
                    showAlertDialog("请先到设置界面开启撤消功能,谢谢！", "取消", "去设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(mContext, SettingActivity.class));
                        }
                    });
                } else {
                    mGameView.popHistoryStack();
                }
                break;

            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.menu_myapps:
                AppConnect.getInstance(this).showMore(this);
                break;

            case R.id.menu_shared:
                shared(getString(R.string.description));
                break;

            case R.id.menu_more:
                if (canShow)
                {
                    showOffersWall();
                }
                else
                {
                    startActivity(new Intent(mContext, HelpActivity.class));
                }
                break;

            case R.id.menu_test:
                showShareDialog();
                break;
        }
        return true;
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundMap.put(1, mSoundPool.load(this, R.raw.score, 1));
        soundMap.put(2, mSoundPool.load(this, R.raw.swip, 1));
    }

    private void initView()
    {
        setContentView(R.layout.activity_main);
        scoreTxt = (TextView) findViewById(R.id.tvScore);
        topScoreTxt = (TextView) findViewById(R.id.tvTopScore);
        mGameView = (GameView) findViewById(R.id.gameView);
        currentModeTxt = (TextView) findViewById(R.id.tv_curentMode);
        currentPointsTxt = (TextView) findViewById(R.id.txt_points);
    }

    public static MainActivity getMainActivity()
    {
        return  mMainActivity;
    }

    /**
     * 显示当前分数
     */
    public void showScore()
    {
        if (null != scoreTxt)
        {
            scoreTxt.setText(score + "");
            if (0 == topScore)
            {
                topScoreTxt.setText(score + "");
                setTopScore();
            }
        }
    }

    /**
     * 分数叠加
     * @param num
     */
    public void addScore(int num)
    {
        score += num;
        playSoundPool(1);
        showScore();
    }

    /**
     * 清空分数
     */
    public void clearScore()
    {
        score = 0;
        showScore();
    }

    /**
     * 保存最高分
     */
    public void setTopScore()
    {
        if (score > topScore)
        {
            if (topScoreTxt != null)
            {
                topScoreTxt.setText(score + "");
            }
            sharedPreUtils.putInt(TOPSCORE, score);
        }
    }

    /**
     * 音效播放
     * @param key
     */
    public void playSoundPool(int key)
    {
        boolean isCanPlay = sharedPreUtils.getBoolean(VOICE, false);
        if (isCanPlay)
        {
            mSoundPool.play(soundMap.get(key), 1, 1, 0, 0, 1);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        setTopScore();
        // 保存游戏最后布局
        mGameView.saveLastGameMaps();
    }

    public void start()
    {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mGameView.clearLastGameMaps();
                mGameView.startGame();
            }
        };
        showAlertDialog("确定重新开始游戏吗？", "取消", "确定", clickListener);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
        scoreTxt.setText(score + "");
    }

    @Override
    public void onBackPressed()
    {
        showAlertDialog("确定退出游戏吗？", "取消", "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initYoumi()
    {
        AdManager.getInstance(this).init("654b2b2928bf642f", "e8221fb36973fec4", false, false);

        // 如果使用积分广告，请务必调用积分广告的初始化接口:
        OffersManager.getInstance(this).onAppLaunch();

        // (可选)注册积分监听-随时随地获得积分的变动情况
        PointsManager.getInstance(this).registerNotify(this);

        // (可选)注册积分订单赚取监听（sdk v4.10版本新增功能）
        PointsManager.getInstance(this).registerPointsEarnNotify(this);

        // 奖励20积分
//        PointsManager.getInstance(this).awardPoints(20.0f);
        // 查询积分余额
        // 从5.3.0版本起，客户端积分托管将由 int 转换为 float
        pointsBalance = PointsManager.getInstance(this).queryPoints();
//        currentPointsTxt.setText("积分余额：" + pointsBalance);
    }

    /**
     * 积分余额发生变动时，就会回调本方法（本回调方法执行在UI线程中）
     * <p/>
     * 从5.3.0版本起，客户端积分托管将由 int 转换为 float
     */
    @Override
    public void onPointBalanceChange(float pointsBalance) {
//        currentPointsTxt.setText("积分余额：" + pointsBalance);
        this.pointsBalance = pointsBalance;
    }

    /**
     * 积分订单赚取时会回调本方法（本回调方法执行在UI线程中）
     */
    @Override
    public void onPointEarn(Context context, EarnPointsOrderList list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 遍历订单并且toast提示
        for (int i = 0; i < list.size(); ++i) {
            EarnPointsOrderInfo info = list.get(i);
            Toast.makeText(this, info.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void showOffersWall()
    {
        OffersManager.getInstance(this).showOffersWall(new Interface_ActivityListener()
        {
            /**
             * 当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
             */
            @Override
            public void onActivityDestroy(Context context) {
//                Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 退出时回收资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // （可选）注销积分监听
        // 如果在onCreate调用了PointsManager.getInstance(this).registerNotify(this)进行积分余额监听器注册，那这里必须得注销
        PointsManager.getInstance(this).unRegisterNotify(this);

        // （可选）注销积分订单赚取监听
        // 如果在onCreate调用了PointsManager.getInstance(this).registerPointsEarnNotify(this)进行积分订单赚取监听器注册，那这里必须得注销
        PointsManager.getInstance(this).unRegisterPointsEarnNotify(this);

        // 回收积分广告占用的资源
        OffersManager.getInstance(this).onAppExit();
    }

    public void createGameScores()
    {
        if (!hasInputUserName())
        {
            showInputDialog();
        }
        else
        {
            ProgressDialogController.showProgressDialog(mContext, "您的分数正提交到排行榜，请稍候......");
            objectId = sharedPreUtils.getString(OBJECT_ID, "");

            BmobQuery<ChallengeMode> bmobQuery = new BmobQuery<>();
            bmobQuery.getObject(objectId, new QueryListener<ChallengeMode>() {
                @Override
                public void done(final ChallengeMode challengeMode, BmobException e) {
                    if (e == null)
                    {
                        // 成功
                        Log.e(TAG, challengeMode.getUserName() + ", 2048 : " + challengeMode.getMode_2048() + ", 512 : " +  challengeMode.getMode_512() + ", 1024 : " + challengeMode.getMode_1024());
                        challengeMode.setValue(getModeStr(), score);
                        Log.e(TAG, "objectId : " + objectId + ", mode : " + getModeStr() + ", score : " + score);
                        challengeMode.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.e(TAG, "提交成功：" + challengeMode.getUpdatedAt());
                                    showMsg("提交成功：" + challengeMode.getUpdatedAt());
                                    ProgressDialogController.dismissDialog();
                                }else{
                                    Log.e(TAG, "更新异常：" + e);
                                    ProgressDialogController.dismissDialog();
                                }
                            }
                        });
                    }
                    else
                    {
                        // 失败
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }

    }

    /**
     *  提示分享或将得分提交到排行榜
     */
    public void showShareDialog()
    {
        new AlertDialog.Builder(mContext)
                .setTitle("您赢了！")
                .setMessage("恭喜您在" + mode + "挑战模式中获得了 " + score + " 分，赶紧分享一下吧！")
                .setPositiveButton("提交到排行榜并分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        createGameScores();
                        shared("我在" + mode + "挑战模式中获得了 " + score + " 分，快来排行榜上看看你的实力吧！！！");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
