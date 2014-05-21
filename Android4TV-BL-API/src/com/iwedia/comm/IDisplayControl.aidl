package com.iwedia.comm;

import android.view.Surface;
import com.iwedia.dtv.display.SurfaceBundle;

import com.iwedia.dtv.display.SurfaceBundle;

interface IDisplayControl {

	Surface getVideoLayerSurface(int layer);
	
	int setVideoLayerSurface(int layer, in SurfaceBundle handle);
	
	void scaleWindow(int x, int y, int width, int height);

}
