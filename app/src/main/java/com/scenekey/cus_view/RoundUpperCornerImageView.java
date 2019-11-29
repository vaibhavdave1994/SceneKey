package com.scenekey.cus_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by mindiii on 23/11/18.
 */

public class RoundUpperCornerImageView extends androidx.appcompat.widget.AppCompatImageView {

    private float radius = 10.0f;
    private Path path;
    private RectF rect;
    boolean topLeft = true ; boolean topRight = true;
    boolean bottomRight= false; boolean bottomLeft = false;
    final float[] radii = new float[8];

    public RoundUpperCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundUpperCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundUpperCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

        if (topLeft) {
            radii[0] = radius;
            radii[1] = radius;
        }

        if (topRight) {
            radii[2] = radius;
            radii[3] = radius;
        }

        if (bottomRight) {
            radii[4] = radius;
            radii[5] = radius;
        }

        if (bottomLeft) {
            radii[6] = radius;
            radii[7] = radius;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                radii, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}