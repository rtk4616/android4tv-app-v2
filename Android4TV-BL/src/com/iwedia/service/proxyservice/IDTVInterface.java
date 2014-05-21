package com.iwedia.service.proxyservice;

/**
 * This interface holds declaration of system important event functions.
 * 
 * @author Marko Zivanovic.
 */
public interface IDTVInterface {
    /**
     * Channel zapping event
     * 
     * @param status
     *        true if successful zapped, else false.
     */
    void channelZapping(boolean status);
}
