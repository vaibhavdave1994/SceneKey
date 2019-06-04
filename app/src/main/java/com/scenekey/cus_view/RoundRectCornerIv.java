package com.scenekey.cus_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by mindiii on 23/11/18.
 */

public class RoundRectCornerIv extends android.support.v7.widget.AppCompatImageView {

    private float radius = 15.0f;
    private Path path;
    private RectF rect;

    public RoundRectCornerIv(Context context) {
        super(context);
        init();
    }

    public RoundRectCornerIv(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundRectCornerIv(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}