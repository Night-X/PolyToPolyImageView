/*
 *  Copyright (C) 2017 wt. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.knightxie.polytopolyimageview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wt on 17/4/28.
 * 图像倾斜
 * mode 二进制数的最后一位控制在内部或者外部扩展，倒数第二位控制左右或者上下
 * delta 控制偏移，符号控制左或右、上或下
 */

public class VisualImageView
        extends android.support.v7.widget.AppCompatImageView
{

    public final static int LR_INS = 0x00;
    public final static int LR_EXT = 0x01;
    public final static int TB_INS = 0x02;
    public final static int TB_EXT = 0x03;

    @IntDef({LR_INS, LR_EXT, TB_INS, TB_EXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VisualMode
    {

    }

    private int mMode = 0;
    private Matrix mMatrix;
    private float mDelta = 1.0F;

    private DrawFilter mDrawFilter;

    public VisualImageView(Context context)
    {
        this(context, null);
    }

    public VisualImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public VisualImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        mMatrix = new Matrix();
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Bitmap bitmap = null;
        if (getDrawable() instanceof BitmapDrawable)
        {
            bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        if (bitmap != null && !bitmap.isRecycled())
        {
            final int dWidth = getDrawable().getIntrinsicWidth();
            final int dHeight = getDrawable().getIntrinsicHeight();

            final int vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            final int vHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            float[] src = {0, 0, 0, dHeight, dWidth, dHeight, dWidth, 0};
            float[] dst = {getPaddingLeft(), getPaddingTop(), getPaddingLeft(), vHeight, vWidth,
                    vHeight, vWidth, getPaddingTop()};
            setPolyToPolyAndDrawInCanvas(canvas, src, dst, vWidth, vHeight);
        }
        else
            super.onDraw(canvas);
    }

    public void setDelta(float delta)
    {
        this.mDelta = delta;
        invalidate();
    }

    public void setMode(@VisualMode int mode)
    {
        this.mMode = mode;
    }

    /**
     * @param maxOutSize 较长边最大长度
     * @return 结果图片Bitmap
     */
    public Bitmap getOutputBitmap(int maxOutSize)
    {
        float width = getDrawable().getIntrinsicWidth();
        float height = getDrawable().getIntrinsicHeight();
        float outWidth = width;
        float outHeight = height;
        if (width < height && height > maxOutSize)
        {
            outWidth = width * maxOutSize / height;
            outHeight = maxOutSize;
        }
        else if (width >= height && width > maxOutSize)
        {
            outHeight = height * maxOutSize / width;
            outWidth = maxOutSize;
        }
        Bitmap output = Bitmap.createBitmap((int) outWidth, (int) outHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        float[] src = {0, 0, 0, height, width, height, width, 0};
        float[] dst = {0, 0, 0, outHeight, outWidth, outHeight, outWidth, 0};
        setPolyToPolyAndDrawInCanvas(canvas, src, dst, outWidth, outHeight);
        return output;
    }

    private void setPolyToPolyAndDrawInCanvas(Canvas canvas, float[] src, float[] dst, float w, float h)
    {
        int[][][] tempArr = {{{5, 7}, {3, 1}}, {{6, 0}, {4, 2}}};
        dst[tempArr[mMode >> 1 & 0x01][mDelta > 0 ? 0 : 1][0]] =
                ((mMode & 0x02) == 0 ? h : w) * (1.0F - Math.abs(mDelta)
                        * ((mMode & 0x01) == 0 ? 1 : -1));
        dst[tempArr[mMode >> 1 & 0x01][mDelta > 0 ? 0 : 1][1]] =
                ((mMode & 0x02) == 0 ? h : w) * Math.abs(mDelta) * (
                        (mMode & 0x01) == 0 ? 1 : -1);
        mMatrix.setPolyToPoly(src, 0, dst, 0, 4);
        canvas.concat(mMatrix);
        canvas.setDrawFilter(mDrawFilter);
        getDrawable().draw(canvas);
    }
}
