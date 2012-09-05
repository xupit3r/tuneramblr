var TUNERAMBLR = {};

/**
 * Determines if the user is currently logged in or not.
 * 
 * @returns true if the user is logged in, false otherwise
 */
TUNERAMBLR.isUserLoggedIn = function() {
	return $("#home_content").length > 0;
};

/* utility methods */

TUNERAMBLR.util = {};

/**
 * Helper function to determine if a maps is empty or not.
 * 
 * @param map
 *            the map to check
 * @returns true if the map is empty, false otherwise
 */
TUNERAMBLR.util.isEmpty = function(map) {
	for ( var key in map) {
		if (map.hasOwnProperty(key)) {
			return false;
		}
	}
	return true;
};

/**
 * Converts an array to a hashmap.
 * 
 * @param arr
 *            the array to translate
 * @returns an hashmap representation of the array
 */
TUNERAMBLR.util.arrToMap = function(arr) {
	var map = {};
	for ( var i = 0; i < arr.length; i++) {
		map[arr[i]] = arr[i];
	}
	return map;
};

/**
 * Requests the user's location from the browser.
 * 
 * @param hLocation
 *            the callback function for a successful request to the location API
 * @param hNoApi
 *            the callback function for browsers that have no location API
 * @param hError
 *            the callback function when an error occurs
 */
TUNERAMBLR.util.getUserLocation = function(hLocation, hNoApi, hError) {
	/* does this browser expose a geolocation API? */
	if (navigator.geolocation) {
		/* call the browser's location API */
		navigator.geolocation.getCurrentPosition(hLocation, hError);
	} else {
		/* we have no API, we shall carry on! */
		hNoApi();
	}
};

/* About */

/**
 * Requests the about information for the page.
 */
TUNERAMBLR.about = function() {
	$.ajax({
		type : "GET",
		url : "/about",
		dataType : "json",
		success : APP.displayAbout
	});
};

/**
 * Handles and displays the about response information
 */
TUNERAMBLR.displayAbout = function(response) {
	$.prompt(response.about);
};