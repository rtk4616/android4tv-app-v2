package com.iwedia.comm;

/**
 * The parental callback.
 *
 * @author Milan Vidakovic
 *
 */
 interface IParentalCallback {
 	void channelLocked(boolean locked);
 	void ageLocked(boolean locked);
}
