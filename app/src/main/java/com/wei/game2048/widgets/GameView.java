package com.wei.game2048.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridLayout;

import com.wei.game2048.R;
import com.wei.game2048.bean.History;
import com.wei.game2048.ui.BaseActivity;
import com.wei.game2048.ui.MainActivity;
import com.wei.library.utils.GsonUtil;
import com.wei.library.utils.Log;
import com.wei.library.utils.ScreenUtils;
import com.wei.library.utils.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/9/8.
 */
public class GameView extends GridLayout {
    private final String TAG = getClass().getSimpleName();
    public static final int MARGIN = 20;
    public static int COLUMN = 4;
    private static Card[][] cardMaps = new Card[COLUMN][COLUMN];
    private Stack<History> mHistoryStack = new Stack<>();
    private List<Point> emptyPoints = new ArrayList<>();
    public int TARGET = 2048;
    private int cardWidth;

    public GameView(Context context) {
        super(context);
        initView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(21)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void initView()
    {
        String pattern = SharedPreUtils.getInstance(getContext()).getString(BaseActivity.PATTERN, "4 x 4");
        if (pattern.contains("4"))
        {
            COLUMN = 4;
        }
        else if (pattern.contains("5"))
        {
            COLUMN = 5;
        }
        else if (pattern.contains("6"))
        {
            COLUMN = 6;
        }
        cardMaps = new Card[COLUMN][COLUMN];

        setColumnCount(COLUMN);
        setBackgroundColor(getResources().getColor(R.color.gamebg));

        int width = ScreenUtils.getScreenWidth(getContext());
        int height = ScreenUtils.getScreenHeight(getContext());
//        Log.e(TAG, "width : " + width + ", height : " + height);
        cardWidth = (Math.min(width, height) - MARGIN )/COLUMN;
        // 添加卡片
        addCards(cardWidth, cardWidth);
        // 开始游戏
        startGame();
    }

    public void startGame()
    {
        clearHistoryStack();

        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = 0 ; x < COLUMN ; x ++)
            {
                cardMaps[x][y].setNum(0);
            }
        }
        MainActivity.getMainActivity().clearScore();
        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum()
    {
        emptyPoints.clear();

        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = 0 ; x < COLUMN ; x ++)
            {
                if (cardMaps[x][y].getNum() <= 0)
                {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        Point point = emptyPoints.remove((int) (Math.random()*emptyPoints.size()));
        cardMaps[point.x][point.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }

    private void addCards(int cardWidth, int cardHeight)
    {
        Card card;
        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = 0 ; x < COLUMN ; x ++)
            {
                card = new Card(getContext());
                card.setNum(0);
                addView(card, cardWidth, cardHeight);

                cardMaps[x][y] = card;
            }
        }
    }

    private float startX, startY, offsetX, offsetY;
    private final int LIMIT = 5;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                offsetX = event.getX() - startX;
                offsetY = event.getY() - startY;
                if (Math.abs(offsetX) > Math.abs(offsetY))
                { // 水平方向移动
                    if (offsetX < -LIMIT)
                    {
                        // 向左
                        swipeLeft();
                    }
                    else if (offsetX > LIMIT)
                    {
                        // 向右
                        swipeRight();
                    }
                }
                else
                { // 竖直方向移动
                    if (offsetY < -LIMIT)
                    {
                        // 向上
                        swipeUp();
                    }
                    else if (offsetY > LIMIT)
                    {
                        // 向下
                        swipeDown();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void swipeLeft()
    {
        boolean merge = false;
        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = 0 ; x < COLUMN ; x ++)
            {
                for (int x1 = x + 1; x1 < COLUMN; x1 ++)
                {
                    if (cardMaps[x1][y].getNum() > 0)
                    {
                        if (cardMaps[x][y].getNum() <= 0)
                        {// 排头为空，则移到排头
                            cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
                            cardMaps[x1][y].setNum(0);
                            merge = true;

                            x--;
                            MainActivity.getMainActivity().playSoundPool(2);
                        }
                        else if (cardMaps[x][y].equals(cardMaps[x1][y]))
                        {// 排头不为空，且与当前卡片相同，则合并
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y].startAnim();
                            cardMaps[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (merge)
        {
            addRandomNum();
            checkGameComplete();
        }

        addToHistoryStack(cardMaps);
    }

    private void swipeRight()
    {
        boolean merge = false;
        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = COLUMN-1 ; x >= 0 ; x --)
            {
                for (int x1 = x - 1; x1 >= 0; x1 --)
                {
                    if (cardMaps[x1][y].getNum() > 0)
                    {
                        if (cardMaps[x][y].getNum() <= 0)
                        {// 排头为空，则移到排头
                            cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
                            cardMaps[x1][y].setNum(0);
                            merge = true;

                            x++;
                            MainActivity.getMainActivity().playSoundPool(2);
                        }
                        else if (cardMaps[x][y].equals(cardMaps[x1][y]))
                        {// 排头不为空，且与当前卡片相同，则合并
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y].startAnim();
                            cardMaps[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (merge)
        {
            addRandomNum();
            checkGameComplete();
        }

        addToHistoryStack(cardMaps);
    }

    private void swipeUp()
    {
        boolean merge = false;
        for (int x = 0 ; x < COLUMN; x ++)
        {
            for (int y = 0 ; y < COLUMN ; y ++)
            {
                for (int y1 = y + 1; y1 < COLUMN; y1 ++)
                {
                    if (cardMaps[x][y1].getNum() > 0)
                    {
                        if (cardMaps[x][y].getNum() <= 0)
                        {// 排头为空，则移到排头
                            cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
                            cardMaps[x][y1].setNum(0);
                            merge = true;

                            y--;
                            MainActivity.getMainActivity().playSoundPool(2);
                        }
                        else if (cardMaps[x][y].equals(cardMaps[x][y1]))
                        {// 排头不为空，且与当前卡片相同，则合并
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y].startAnim();
                            cardMaps[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (merge)
        {
            addRandomNum();
            checkGameComplete();
        }

        addToHistoryStack(cardMaps);
    }

    private void swipeDown()
    {
        boolean merge = false;
        for (int x = 0 ; x < COLUMN; x ++)
        {
            for (int y = COLUMN-1 ; y >= 0 ; y --)
            {
                for (int y1 = y - 1; y1 >= 0; y1 --)
                {
                    if (cardMaps[x][y1].getNum() > 0)
                    {
                        if (cardMaps[x][y].getNum() <= 0)
                        {// 排头为空，则移到排头
                            cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
                            cardMaps[x][y1].setNum(0);
                            merge = true;

                            y++;
                            MainActivity.getMainActivity().playSoundPool(2);
                        }
                        else if (cardMaps[x][y].equals(cardMaps[x][y1]))
                        {// 排头不为空，且与当前卡片相同，则合并
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y].startAnim();
                            cardMaps[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (merge)
        {
            addRandomNum();
            checkGameComplete();
        }

        addToHistoryStack(cardMaps);
    }

    private void checkGameComplete()
    {
        TARGET = SharedPreUtils.getInstance(getContext()).getInt(BaseActivity.MODE, 2048);
        boolean complete = true, win = false;

        ALL:
        for (int y = 0 ; y < COLUMN; y ++) {
            for (int x = 0; x < COLUMN; x++) {
                if (cardMaps[x][y].getNum() == TARGET)
                {
                    win = true;
                    break ALL;
                }
            }
        }

        if (win)
        {
            showResultDialog("恭喜您，您赢了！再接再厉吧！！");
            MainActivity.getMainActivity().showShareDialog();
            return;
        }

        ALL:
        for (int y = 0 ; y < COLUMN; y ++)
        {
            for (int x = 0; x < COLUMN; x++)
            {
                // 如果还有位置是空的或者前后左右还有数字是相同的，则游戏还没结束
                if (cardMaps[x][y].getNum() == 0 ||
                        (x > 0 && cardMaps[x][y].equals(cardMaps[x-1][y]))||
                        (x < 3 && cardMaps[x][y].equals(cardMaps[x+1][y]))||
                        (y > 0 && cardMaps[x][y].equals(cardMaps[x][y-1]))||
                        (y < 3 && cardMaps[x][y].equals(cardMaps[x][y+1])))
                {
                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete)
        {
            showResultDialog("很遗憾，游戏结束了！再来一局吧！！");
        }
    }

    private void showResultDialog(String msg)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("再战", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.getMainActivity().setTopScore();
                        dialog.dismiss();
                        startGame();
                        clearLastGameMaps();
                    }
                })
                .setNegativeButton("休息一下", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.getMainActivity().setTopScore();
                        clearLastGameMaps();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void clearHistoryStack()
    {
        mHistoryStack.clear();
    }

    private void addToHistoryStack(Card[][] cardMaps)
    {
        boolean isAdd = SharedPreUtils.getInstance(getContext()).getBoolean(BaseActivity.REVOKE, false);
        if (!isAdd)
            return;

        int[][] nums = new int[COLUMN][COLUMN];
        for (int i = 0; i < cardMaps.length; i ++)
        {
            for (int j = 0; j < cardMaps[i].length; j ++)
            {
                nums[i][j] = cardMaps[i][j].getNum();
            }
        }

        History history = new History(nums, MainActivity.getMainActivity().getScore());
        mHistoryStack.push(history);
    }

    public void popHistoryStack()
    {
        if (mHistoryStack.size() >= 2)
        {
            mHistoryStack.pop(); // 弹出栈顶元素，并删除
            History history = mHistoryStack.peek(); // 弹出栈顶元素，但不删除
            int[][] cards = history.getCardMaps();
            int score = history.getScore();
            for (int i = 0; i < cards.length; i ++)
            {
                for (int j = 0; j < cards[i].length; j ++)
                {
                    cardMaps[i][j].setNum(cards[i][j]);
                }
            }
            MainActivity.getMainActivity().setScore(score);
        }
    }

    // 清空游戏最后布局，通常在游戏正常结束后调用
    public void clearLastGameMaps()
    {
        SharedPreUtils.getInstance(getContext()).putString("last_game", "");
    }

    /**
     * 保存最后游戏布局
     */
    public void saveLastGameMaps()
    {
        if (mHistoryStack.size() >= 1)
        {
            History history = mHistoryStack.peek(); // 获取栈顶元素，不删除
            String jsonHistory = GsonUtil.bean2json(history);
            SharedPreUtils.getInstance(getContext()).putString("last_game", jsonHistory);
        }
    }

    /**
     * 恢复最后游戏布局
     */
    public void recoverLastGameMaps()
    {
        String lastGame = SharedPreUtils.getInstance(getContext()).getString("last_game", "");
        if (!"".equals(lastGame))
        {
            History history = GsonUtil.json2bean(lastGame, History.class);
            int[][] cards = history.getCardMaps();
            int score = history.getScore();
            try {
                for (int i = 0; i < cards.length; i ++)
                {
                    for (int j = 0; j < cards[i].length; j ++)
                    {
                        cardMaps[i][j].setNum(cards[i][j]);
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "数组越界：" + e.getMessage());
            }
            MainActivity.getMainActivity().setScore(score);
        }
    }
}
