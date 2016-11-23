package com.app.web.utils;

//**************************
// The UAgentInfo class encapsulates information about
//   a browser's connection to your web site. 
//   The object's detection methods return booleans: true or false.
public class UAgentInfo2 {
	// Stores some info about the browser and device.
	private String userAgent;

	// Stores info about what content formats the browser can display.
	private String httpAccept;

	// A long list of strings which provide clues
	// about devices and capabilities.
	public static final String deviceIphone = "iphone";

	// [ SNIP! Other variables snipped out ]

	// **************************
	// The constructor. Initializes several default variables.
	public UAgentInfo2(String userAgent, String httpAccept) {
		if (userAgent != null) {
			this.userAgent = userAgent.toLowerCase();
		}
		if (httpAccept != null) {
			this.httpAccept = httpAccept.toLowerCase();
		}
	}

	// **************************
	// Returns the contents of the User Agent value, in lower case.
	public String getUserAgent() {
		return userAgent;
	}

	// **************************
	// Detects if the current device is an iPhone.
	public boolean detectIphone() {
		// The iPod touch says it's an iPhone! So let's disambiguate.
		if (userAgent.indexOf(deviceIphone) != -1) {
			return true;
		}
		return false;
	}

	// [ SNIP! Other functions snipped out ]

}
