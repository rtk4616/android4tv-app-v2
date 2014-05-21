package com.iwedia.gui.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class PatternView extends View {
    private static PatternView PatternView;
    Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Config.ARGB_8888);
    Paint paint = new Paint();

    public PatternView(Context context) {
        super(context);
        PatternView = this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*
         * Paint paint = new Paint();
         * GraphicsRendererNative.draw(GraphicsType.TYPE_MHEG, canvas, paint, 0,
         * 0, 0, 0);
         */
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(0);
        canvas.drawRect(0, 0, 1920, 1080, paint);
    }

    public void invalidatePatternView() {
        if (PatternView != null) {
            PatternView.postInvalidate();
        } else {
            Log.i("Java-Bane", "PatternView = null");
        }
    }
}
