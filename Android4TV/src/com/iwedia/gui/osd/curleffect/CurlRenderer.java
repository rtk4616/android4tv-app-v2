package com.iwedia.gui.osd.curleffect;

import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.iwedia.gui.config_handler.ConfigHandler;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Open GL renderer class.
 */
public class CurlRenderer implements GLSurfaceView.Renderer {
    private final String TAG = "CurlRenderer";
    /** Background fill color. */
    private int mBackgroundColor;
    /** Curl meshes used for static and dynamic rendering. */
    private Vector<CurlMesh> mCurlMeshes;
    private CurlRenderer.Observer mObserver;
    /** Page rectangle. */
    private RectF mPageRect;
    /** Screen size. */
    private int mViewportWidth, mViewportHeight;
    /** Rect for render area. */
    private RectF mViewRect = new RectF();

    /** Constructor */
    public CurlRenderer(CurlRenderer.Observer observer) {
        mObserver = observer;
        mCurlMeshes = new Vector<CurlMesh>();
        mPageRect = new RectF();
    }

    /** Adds CurlMesh to this renderer. */
    public synchronized void addCurlMesh(CurlMesh mesh) {
        removeCurlMesh(mesh);
        mCurlMeshes.add(mesh);
    }

    public RectF getPageRect() {
        return mPageRect;
    }

    public synchronized void onDrawFrame(GL10 gl) {
        mObserver.onDrawFrame();
        gl.glLoadIdentity();
        for (int i = 0; i < mCurlMeshes.size(); ++i) {
            mCurlMeshes.get(i).onDrawFrame(gl);
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        mViewportWidth = width;
        mViewportHeight = height;
        float ratio = (float) width / height;
        mViewRect.top = 1.0f;
        mViewRect.bottom = -1.0f;
        mViewRect.left = -ratio;
        mViewRect.right = ratio;
        updatePageRects();
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, mViewRect.left, mViewRect.right, mViewRect.bottom,
                mViewRect.top);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        if (ConfigHandler.CURL_GRAPHIC_QUALITY) {
            /** With MultisampleConfigChooser */
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
            gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
            gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
        }
        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_CULL_FACE);
        mObserver.onSurfaceCreated();
    }

    /** Removes CurlMesh from this renderer. */
    public synchronized void removeCurlMesh(CurlMesh mesh) {
        while (mCurlMeshes.remove(mesh)) {
            Log.i(TAG, "Removing Curl Mesh.");
        }
    }

    /** Change background/clear color. */
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    /** Translates screen coordinates into view coordinates. */
    public void translate(PointF pt) {
        pt.x = mViewRect.left + (mViewRect.width() * pt.x / mViewportWidth);
        pt.y = mViewRect.top - (-mViewRect.height() * pt.y / mViewportHeight);
    }

    /** Recalculates page rectangles. */
    private void updatePageRects() {
        if (0 == mViewRect.width() || 0 == mViewRect.height()) {
            return;
        } else {
            mPageRect.set(mViewRect);
            int bitmapW = (int) ((mPageRect.width() * mViewportWidth) / mViewRect
                    .width());
            int bitmapH = (int) ((mPageRect.height() * mViewportHeight) / mViewRect
                    .height());
            mObserver.onPageSizeChanged(bitmapW, bitmapH);
        }
    }

    /** Observer for waiting render engine/state updates. */
    public interface Observer {
        /**
         * Called from onDrawFrame called before rendering is started. This is
         * intended to be used for animation purposes.
         */
        public void onDrawFrame();

        /**
         * Called once page size is changed. Width and height tell the page size
         * in pixels making it possible to update textures accordingly.
         */
        public void onPageSizeChanged(int width, int height);

        public void onSurfaceCreated();
    }
}
