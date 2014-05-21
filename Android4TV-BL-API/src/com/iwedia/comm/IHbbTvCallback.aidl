package com.iwedia.comm;
/**
 * The HBB related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface IHbbTvCallback {
	 void showApplication();
	 void hideApplication();
	 void createApplication(String uri);
	 void setKeyMask(int mask);
	 void destroyApplication();
}