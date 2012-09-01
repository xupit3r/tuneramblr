var APP = {};

/* model storage for stuff about songs */
APP.session = {};

/* the tracks table */
APP.tracks = {};

/* store the user location for later use */
APP.userLocation = {};

/* app defaults */
APP.defaults = {};
APP.defaults.location = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

/**
 * Determines if the user is currently logged in or not.
 * 
 * @returns true if the user is logged in, false otherwise
 */
APP.isUserLoggedIn = function() {
	return $("#stats").length > 0;
};

/**
 * Requests the user session metadata. This includes the qualitative user
 * location, weather, and qualitative time.
 * 
 * @param locinfo
 *            an object containing the lat and lng information for the user
 *            location
 */
APP.setupUserSessionMeta = function(locinfo) {
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
APP.handleUserSessionMeta = function(resp) {
	APP.session.weather = resp.weather;
	APP.session.address = resp.address;
	APP.session.time = resp.time;
};

/**
 * Requests the user's track information.
 */
APP.setupUserSessionTracks = function() {
	$.ajax({
		type : "POST",
		url : "/user/base/tracks",
		dataType : "json",
		success : APP.handleUserSessionTracks
	});
};

/**
 * Handles the response for the user tracks
 * 
 * @param resp
 *            the response object from the tracks request
 */
APP.handleUserSessionTracks = function(resp) {
	/* setup the table */
	APP.initTable(resp.songs);

	/* store the rest of the track info */
	APP.session.freqs = resp.freqs;
	APP.session.imgs = resp.imgs;
};

/**
 * builds the user meta data section (location, time, weather).
 * 
 * @param meta
 *            an object containing the expected metadata (location, time, and
 *            weather)
 */
APP.buildMetaSection = function(meta) {
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

/**
 * Initializes and builds the track table display
 * 
 * @param tracks
 *            an object containing the track data to be displayed in the table
 */
APP.initTable = function(tracks) {
	/* only carry out these actions if we have songs to display */
	if (!APP.util.isEmpty(tracks)) {
		/* get a handle on the tracks table */
		var tracksTab = $("#tracks_table");
		tracksTab.append(APP.buildTracksHead());
		tracksTab.append(APP.buildTracksBody(tracks));
		tracksTab.find("tbody tr").click(function(e) {
			APP.tableRowClick(this);
		});

	}
};

/**
 * Builds the tracks table header.
 */
APP.buildTracksHead = function() {
	var thead = document.createElement("thead");
	var row = thead.appendChild(document.createElement("tr"));

	var titleCell = row.appendChild(document.createElement("th"));
	var artistCell = row.appendChild(document.createElement("th"));
	var albumCell = row.appendChild(document.createElement("th"));

	titleCell.appendChild(document.createTextNode("Title"));
	artistCell.appendChild(document.createTextNode("Artist"));
	albumCell.appendChild(document.createTextNode("Album"));

	return thead;
};

/**
 * Builds the body of the tracks table.
 * 
 * @param tracks
 *            an object containing the track data to be displayed in the body of
 *            the track table
 */
APP.buildTracksBody = function(tracks) {
	var tbody = document.createElement("tbody");
	for ( var i = 0; i < tracks.length; i++) {
		var id = "row_" + i;
		var track = tracks[i];
		tbody.appendChild(APP.buildTrackRow(track, id));
		APP.tracks[id] = track;
	}
	return tbody;
};

/**
 * Builds a row in the track table.
 * 
 * @param track
 *            an object containing the track properties to be displayed in the
 *            table row
 * @param id
 *            a unique identifier for the row
 */
APP.buildTrackRow = function(track, id) {

	var row = document.createElement("tr");

	var titleCell = row.appendChild(document.createElement("td"));
	var artistCell = row.appendChild(document.createElement("td"));
	var albumCell = row.appendChild(document.createElement("td"));

	titleCell.appendChild(document.createTextNode(track.title));
	artistCell.appendChild(document.createTextNode(track.artist));
	albumCell.appendChild(document.createTextNode(track.album));

	if (id) {
		row.setAttribute("id", id);
	}

	return row;
};

/**
 * Event handler for the row click event.
 * 
 * @param el
 *            the row element that was clicked
 */
APP.tableRowClick = function(el) {
	var trackInfo = APP.tracks[el.id];
	var trackInfoDialogBody = $("#ti-dialog_body");
	var tiMeta = trackInfo.metadata;
	var chartData = [];
	var idx = 0;
	for ( var item in tiMeta) {
		chartData[idx] = [ item, tiMeta[item] ];
		idx++;
	}

	$("#ti-dialog").modal();

	$.jqplot("ti_chart_div", [ chartData ], {
		seriesDefaults : {
			renderer : $.jqplot.PieRenderer,
			rendererOptions : {
				showDataLabels : true
			}
		},
		legend : {
			show : true,
			location : 'e'
		}
	});
};

/* utility methods */
APP.util = {};

/**
 * Helper function to determine if a maps is empty or not.
 * 
 * @param map
 *            the map to check
 * @returns true if the map is empty, false otherwise
 */
APP.util.isEmpty = function(map) {
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
APP.util.arrToMap = function(arr) {
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
APP.util.getUserLocation = function(hLocation, hNoApi, hError) {
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
APP.about = function() {
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
APP.displayAbout = function(response) {
	$.prompt(response.about);
};

/* executes when the DOM is ready */
$(document).ready(function() {

	/* is this a logged in user? */
	if (APP.isUserLoggedIn()) {
		/* get the user's tracks */
		APP.setupUserSessionTracks();

		/* get the user's location and metadata about that location */
		APP.util.getUserLocation(function(position) {
			/*
			 * I read that FF sometimes calls this multiple times let's avoid
			 * that, if we have already set a location, just return
			 */
			if (APP.userLocation.isSet) {
				return;
			}

			/* record the user's location */
			APP.userLocation.lat = position.coords.latitude;
			APP.userLocation.lng = position.coords.longitude;
			APP.userLocation.isSet = true;

			/* now, request the songs */
			APP.setupUserSessionMeta({
				lat : position.coords.latitude,
				lng : position.coords.longitude
			});

		}, function() {
			APP.setupUserSession(APP.defaults.location);
		}, function(error) {
			/* do something */
		});
	} else {
		$('.carousel').carousel();
	}
});