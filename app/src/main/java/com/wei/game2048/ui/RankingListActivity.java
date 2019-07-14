package com.wei.game2048.ui;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.wei.game2048.R;
import com.wei.game2048.bean.ChallengeMode;
import com.wei.game2048.ui.adapter.ScoreAdapter;
import com.wei.library.controller.ProgressDialogController;
import com.wei.library.utils.Log;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class RankingListActivity extends BaseActivity
{
    private ListView mListView;
    private TextView mRankinglistTitleTxt;
    private int mCurrentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);
        mListView = (ListView) findViewById(R.id.lv);
        mRankinglistTitleTxt = (TextView) findViewById(R.id.tv_rankinglist);

        mCurrentMode = sharedPreUtils.getInt(MODE, 2048);
        mRankinglistTitleTxt.setText( mCurrentMode + "排行榜");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getData();
    }

    private void getData()
    {
        ProgressDialogController.showProgressDialog(mContext, "正在获取数据，请稍候......");
        String sql = "select * from ChallengeMode order by " + getModeStr() + " desc";
        BmobQuery<ChallengeMode> query = new BmobQuery<>();
        query.setSQL(sql);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);  // 如果没有缓存的话，则先从网络中取

        query.doSQLQuery(sql, new SQLQueryListener<ChallengeMode>() {
            @Override
            public void done(BmobQueryResult<ChallengeMode> bmobQueryResult, BmobException e) {
                if (e == null)
                {
                    List<ChallengeMode> gameUsers =bmobQueryResult.getResults();
                    if (null != gameUsers && gameUsers.size() > 0)
                    {
                        initListView(gameUsers);
                        ProgressDialogController.dismissDialog();
                    }
                    else
                    {
                        Log.e(TAG, "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                        ProgressDialogController.dismissDialog();
                    }
                }
            }
        });

    }

    private void initListView(List<ChallengeMode> gameUsers)
    {
        int size = gameUsers.size();
        for (int i = 0; i < size; i ++)
        {
            ChallengeMode gameUser = gameUsers.get(i);
            gameUser.setNo("No. " + (i+1));
        }
        ScoreAdapter scoreAdapter = new ScoreAdapter(this, gameUsers);
        mListView.setAdapter(scoreAdapter);
    }
}
