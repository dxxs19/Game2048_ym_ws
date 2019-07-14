package com.wei.game2048.bean;

import com.wei.game2048.widgets.Card;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/9/11.
 */
public class History
{
    private int[][] cardMaps ;
    private int score;

    public History(int[][] cardMaps, int score)
    {
        this.cardMaps = cardMaps;
        this.score = score;
    }

    public int[][] getCardMaps() {
        return cardMaps;
    }

    public void setCardMaps(int[][] cardMaps) {
        this.cardMaps = cardMaps;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "History{" +
                "cardMaps=" + Arrays.toString(cardMaps) +
                ", score=" + score +
                '}';
    }
}
