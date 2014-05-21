package com.iwedia.comm.system.application;

/**
 * The applications controller. This interface manages applications, their details and settings.
 *
 * @author Nikola Radakovic
 *
 */

interface IApplicationRestart{

   /**
	* Restart application.
	*/
	void binderRestart(String componentName);
}