package com.growatt.shinetools.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.growatt.shinetools.R;

public class MaskPierceView extends View {


    private static final String TAG = "MaskPierceView";

    private Bitmap mSrcRect;
    private Bitmap mDstCircle;

    private int mScreenWidth;   // 屏幕的宽
    private int mScreenHeight;  // 屏幕的高

    private int mPiercedX, mPiercedY;
    private int mPiercedRadius;

    private Rect mTextRect=new Rect();
    private String mText = "";

    public MaskPierceView(Context context) {
        this(context, null);
    }

    public MaskPierceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        mText=getResources().getString(R.string.android_key3074);

        if (mScreenWidth == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }
    }


    public MaskPierceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaskPierceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * @param mPiercedX      镂空的圆心坐标
     * @param mPiercedY      镂空的圆心坐标
     * @param mPiercedRadius 镂空的圆半径
     */
    public void setPiercePosition(int mPiercedX, int mPiercedY, int mPiercedRadius) {
        this.mPiercedX = mPiercedX;
        this.mPiercedY = mPiercedY;
        this.mPiercedRadius = mPiercedRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mSrcRect = makeSrcRect();
        mDstCircle = makeDstCircle();
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        canvas.saveLayer(0, 0, mScreenWidth, mScreenHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstCircle, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        paint.setAlpha(160);
        canvas.drawBitmap(mSrcRect, 0, 0, paint);
        paint.setXfermode(null);
    }

    /**
     * 创建镂空层圆形形状
     *
     * @return
     */
    private Bitmap makeDstCircle() {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(43);

        if (TextUtils.isEmpty(mText)){
            canvcs.drawRoundRect(mScreenWidth-240,35,mScreenWidth-10,130,50,50,paint);
        }else {
            paint.getTextBounds(mText,0,mText.length(),mTextRect);
            canvcs.drawRoundRect(mScreenWidth-mTextRect.width()-10,35,mScreenWidth-10,130,50,50,paint);
        }
//        canvcs.drawCircle(mPiercedX, mPiercedY, mPiercedRadius, paint);
        return bm;
    }

    /**
     * 创建遮罩层形状
     *
     * @return
     */
    private Bitmap makeSrcRect() {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvcs.drawRect(new RectF(0, 0, mScreenWidth, mScreenHeight), paint);
        return bm;
    }



}
