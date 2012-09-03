var LISTEN = {};

/**
 * Requests the user session metadata. This includes the qualitative user
 * location, weather, and qualitative time.
 * 
 * @param locinfo
 *            an object containing the lat and lng information for the user
 *            location
 */
LISTEN.setupUserSessionMeta = function(locinfo) {
	var data = {};
	if (locinfo) {
		data = {
			lat : locinfo.lat,
			lng : locinfo.lng,
		}
	}

	$.ajax({
		type : "POST",
		url : "/user/base/meta",
		dataType : "json",
		data : data,
		success : APP.handleUserSessionMeta
	});
};

/**
 * Handles the server response for the user meta data request
 * 
 * @param resp
 *            the response object from the request
 */
LISTEN.handleUserSessionMeta = function(resp) {
	APP.session.weather = resp.weather;
	APP.session.address = resp.address;
	APP.session.time = resp.time;
};

/**
 * builds the user meta data section (location, time, weather).
 * 
 * @param meta
 *            an object containing the expected metadata (location, time, and
 *            weather)
 */
LISTEN.buildMetaSection = function(meta) {
	/* build the new elements */
	var timeEl = document.createElement("p");
	var weatherEl = document.createElement("p");
	var addressEl = document.createElement("p");

	/* build the text nodes */
	var timeTxt = document.createTextNode(meta.time);
	var weatherTxt = document.createTextNode(meta.weather);
	var addressTxt = document.createTextNode(meta.address);

	/* append the text nodes to the elements */
	weatherEl.appendChild(weatherTxt);
	addressEl.appendChild(addressTxt);
	timeEl.appendChild(timeTxt)

	/* shove those elements into the DOM */
	$("#autogen").append([ timeEl, weatherEl, addressEl ]);
};

/* executes when the DOM is ready */
$(document).ready(function() {

	/* is this a logged in user? */
	if (TUNERAMBLR.isUserLoggedIn()) {

		/* get the user's location and metadata about that location */
		TUNERAMBLR.util.getUserLocation(function(position) {
			/*
			 * I read that FF sometimes calls this multiple times let's avoid
			 * that, if we have already set a location, just return
			 */
			if (USERS.userLocation.isSet) {
				return;
			}

			/* record the user's location */
			USERS.userLocation.lat = position.coords.latitude;
			USERS.userLocation.lng = position.coords.longitude;
			USERS.userLocation.isSet = true;

			/* now, request the songs */
			USERS.setupUserSessionMeta({
				lat : position.coords.latitude,
				lng : position.coords.longitude
			});

		}, function() {
			USERS.setupUserSession(USERS.defaults.location);
		}, function(error) {
			/* do something */
		});
	} else {
		$('.carousel').carousel();
	}
});