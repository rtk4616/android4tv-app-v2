package android.dtv.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Class for managing rendering of graphic elements got from MW.
 *
 * @author Milan Vidakovic
 *
 */
public class GraphicsRendererNative {
    static {
        System.loadLibrary("GraphicsRenderer");
    }

    /**
     * Draw bitmap on Canvas in native code . This method is called internaly by
     * apropriate dialog.
     */
    public static native boolean draw(int type/*GraphicsType*/, Canvas can, Paint p, int width, int height, int x, int y);

    /** Scale graphic element */
    public static native boolean scale(int type/*GraphicsType*/, int size/*GraphicsType*/);

    public static native boolean drawVideoFrameOnCanvas(Canvas can, Paint p, int width, int height);

}
