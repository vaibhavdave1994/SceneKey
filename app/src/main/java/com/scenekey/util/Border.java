package com.scenekey.util;

public class Border extends android.graphics.drawable.Drawable {
            public android.graphics.Paint paint;
           public android.graphics.Rect bounds_rect;
           int returnValue = 0;

           public Border(int colour, int width)
           {
             this.paint = new android.graphics.Paint();
            this.paint.setColor(colour);
            this.paint.setStrokeWidth(width);
            this.paint.setStyle(android.graphics.Paint.Style.STROKE);
           }

           @Override
   public void onBoundsChange(android.graphics.Rect bounds)
           {
             this.bounds_rect = bounds;
         }

           public void draw(android.graphics.Canvas c)
           {
             c.drawRect(this.bounds_rect, this.paint);
           }

           public void setAlpha(int a)
           {
             // TODO: Implement this method
           }

          public void setColorFilter(android.graphics.ColorFilter cf)
           {
             // TODO: Implement this method
           }

           public int getOpacity()
          {
            // TODO: Implement this method
             return returnValue;
           }
         }
