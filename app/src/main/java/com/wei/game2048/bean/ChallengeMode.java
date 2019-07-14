package com.wei.game2048.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/15.
 */
public class ChallengeMode extends BmobObject
{
    /**
     * 分数
     */
    private int score;

    /**
     * 模式得分
     */
    private int mode_512;
    private int mode_1024;
    private int mode_2048;
    private int mode_4096;
    private int mode_8192;

    /**
     * 排名
     */
    private String no;

    /**
     * 玩家
     */
    private String userName;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMode_512() {
        return mode_512;
    }

    public void setMode_512(int mode_512) {
        this.mode_512 = mode_512;
    }

    public int getMode_1024() {
        return mode_1024;
    }

    public void setMode_1024(int mode_1024) {
        this.mode_1024 = mode_1024;
    }

    public int getMode_2048() {
        return mode_2048;
    }

    public void setMode_2048(int mode_2048) {
        this.mode_2048 = mode_2048;
    }

    public int getMode_4096() {
        return mode_4096;
    }

    public void setMode_4096(int mode_4096) {
        this.mode_4096 = mode_4096;
    }

    public int getMode_8192() {
        return mode_8192;
    }

    public void setMode_8192(int mode_8192) {
        this.mode_8192 = mode_8192;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
