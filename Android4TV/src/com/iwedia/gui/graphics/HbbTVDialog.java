package com.iwedia.gui.graphics;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;

/**
 * Android dialog for showing HbbTV. Dialog default resolution: 720p.
 */
public class HbbTVDialog extends Dialog {
    public static final String TAG = "HbbTVDialog";
    private static WebView hbbView;
    public static final int EVENT_BROWSER_INITIALIZED = 0;
    public static final int EVENT_BROWSER_IS_CLOSING = 1;
    public static final int EVENT_PAGE_LOADED = 82;
    public static final int EVENT_NETWORK_TIMEOUT = 80;

    public HbbTVDialog(Context context, int theme) {
        super(context, theme);
        setContentView(com.iwedia.gui.R.layout.hbb_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        hbbView = (WebView) findViewById(com.iwedia.gui.R.id.webViewHbb);
        hbbView.getSettings().setJavaScriptEnabled(true);
        // uncomment this if using Android in 1080p
        // hbbView.getSettings().setUseWideViewPort(true);
        // hbbView.getSettings().setLoadWithOverviewMode(true);
        hbbView.getSettings().setPluginsEnabled(true);
        hbbView.getSettings().setPluginsPath("/system/lib/plugins");
        hbbView.setWebViewClient(new HbbTVWebViewClient());
        hbbView.setBackgroundColor(Color.TRANSPARENT);
        hbbView.setOnKeyListener(new MainKeyListener(MainActivity.activity));
        hbbView.requestFocus();
        hbbView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
        hbbView.getSettings().setUserAgentString(
                "HbbTV/1.1.1 (;iWedia;Teatro;4.5;;)");
        hbbView.getSettings().setNeedInitialFocus(false);
    }

    String currentUrl;
    static boolean errorOccured = false;

    private class HbbTVWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            Log.d(TAG, "failing URL :" + failingUrl + " description: "
                    + description + " errorCode : " + errorCode);
            if (errorCode < 0) {
                Log.i(TAG, "Received error code: " + errorCode);
                view.setAlpha((float) 0.00);
                MainActivity.keySet = 0;
                try {
                    /*
                     * Notification for HbbTV application manager that url is
                     * not loaded successfully
                     */
                    // MainActivity.service.getHbbControl().notifyAppMngr(errorCode,
                    // description);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public WebView getHbbTVView() {
        return hbbView;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return hbbView.onKeyDown(keyCode, event);
    }
}
