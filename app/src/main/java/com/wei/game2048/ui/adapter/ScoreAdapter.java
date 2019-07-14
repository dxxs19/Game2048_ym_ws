package com.wei.game2048.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wei.game2048.R;
import com.wei.game2048.bean.ChallengeMode;
import com.wei.game2048.ui.BaseActivity;
import com.wei.library.utils.SharedPreUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/15.
 */
public class ScoreAdapter extends BaseAdapter
{
    private Context mContext;
    private List<ChallengeMode> mGameUsers;

    public ScoreAdapter(Context context, List<ChallengeMode> gameUsers) {
        this.mContext = context;
        this.mGameUsers = gameUsers;
    }

    @Override
    public int getCount() {
        return mGameUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mGameUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_score, null);
            viewHolder = new ViewHolder();
            viewHolder.noTxt = (TextView) convertView.findViewById(R.id.tv_no);
            viewHolder.nameTxt = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.scoreTxt = (TextView) convertView.findViewById(R.id.tv_score);
            viewHolder.updateTxt = (TextView) convertView.findViewById(R.id.tv_update);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChallengeMode gameUser = (ChallengeMode) getItem(position);
        if (null != gameUser)
        {
            viewHolder.noTxt.setText(gameUser.getNo());
            viewHolder.nameTxt.setText(gameUser.getUserName());

            int score = 0;
            int mode = SharedPreUtils.getInstance(mContext).getInt(BaseActivity.MODE, 2048);
            switch (mode)
            {
                case 512:
                    score = gameUser.getMode_512();
                    break;

                case 1024:
                    score = gameUser.getMode_1024();
                    break;

                case 2048:
                    score = gameUser.getMode_2048();
                    break;

                case 4096:
                    score = gameUser.getMode_4096();
                    break;

                case 8192:
                    score = gameUser.getMode_8192();
                    break;
            }

            viewHolder.scoreTxt.setText(score + "");
            viewHolder.updateTxt.setText(gameUser.getUpdatedAt());
        }
        return convertView;
    }

    class ViewHolder
    {
        TextView noTxt, nameTxt, scoreTxt, updateTxt;
    }
}
