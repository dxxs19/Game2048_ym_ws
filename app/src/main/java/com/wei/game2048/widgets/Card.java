package com.wei.game2048.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wei.game2048.R;
import com.wei.game2048.ui.BaseActivity;
import com.wei.library.utils.SharedPreUtils;

/**
 * Created by Administrator on 2016/9/9.
 */
public class Card extends FrameLayout {
    private static final String TAG = "Card";
    private int num = 0;
    private TextView labelTv;
    private int radius = 0;

    public Card(Context context) {
        super(context);

        radius = (int) getResources().getDimension(R.dimen.corner);
        labelTv = new TextView(context);
        float size = 0f;
        if (GameView.COLUMN == 4)
        {
            size = 32;
        }
        else if (GameView.COLUMN == 5)
        {
            size = 20;
        }
        else if (GameView.COLUMN == 6)
        {
            size = 16;
        }
        labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        labelTv.setGravity(Gravity.CENTER);
        labelTv.setBackgroundResource(com.wei.library.R.drawable.lilac_bg);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(GameView.MARGIN, GameView.MARGIN, 0, 0);
        removeAllViews();
        addView(labelTv, params);
    }

    public int getNum() {
        return num;
    }

    String mBgColor;
    public void setNum(int num)
    {
        this.num = num;
        labelTv.setText(num <= 0 ? "" : num + "");

        switch (num) {
            case 0:
                mBgColor = "#33ffffff";
                break;
            case 2:
                mBgColor = "#66ffffff";
                break;
            case 4:
                mBgColor = "#66EDE0C8";
                break;
            case 8:
                mBgColor = "#66F2B179";// #F2B179
                break;
            case 16:
                mBgColor = "#66F49563";
                break;
            case 32:
                mBgColor = "#66F5794D";
                break;
            case 64:
                mBgColor = "#66F55D37";
                break;
            case 128:
                mBgColor = "#66EEE863";
                break;
            case 256:
                mBgColor = "#66EDB04D";
                break;
            case 512:
                mBgColor = "#66ECB04D";
                break;
            case 1024:
                mBgColor = "#66EB9437";
                break;
            case 2048:
                mBgColor = "#66EA7821";
                break;
            default:
                mBgColor = "#66EA7821";
                break;
        }

        drawRoundBg();
    }

    public void drawRoundBg()
    {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0)
        {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor(mBgColor));
            RectF rectF = new RectF(0, 0, width, height);

            canvas.drawRoundRect(rectF, radius, radius, paint);
            labelTv.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public boolean equals(Card obj) {
        return getNum() == obj.getNum();
    }

    public void startAnim()
    {
        boolean isPlayAnim = SharedPreUtils.getInstance(getContext()).getBoolean(BaseActivity.ANIM, false);
        if (isPlayAnim)
        {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 2, 0, 2,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(500);
            startAnimation(scaleAnimation);
        }
    }

    @Override
    public String toString() {
        return "Card{" +
                "num=" + num +
                ", labelTv=" + labelTv +
                ", radius=" + radius +
                ", mBgColor='" + mBgColor + '\'' +
                '}';
    }
}
