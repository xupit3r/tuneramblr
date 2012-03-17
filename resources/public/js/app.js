// general application stuff
var APP = {};

// model storage of the songs on the map
APP.songs = [];

// model storage for metadata
APP.meta = {};
APP.meta.freqs = {};

// model storage for user selections
APP.select = {};
APP.select.meta = {};
APP.select.songs = {};

// executes when the DOM is ready
$(document).ready(function() {

	// gets the user's location and sets up the map
	APP.setupMap();

	// retrieve the songs
	APP.getSongs();

	// setup the function handlers
	HANDLERS.functions.setup();
});

// store the user location for later use
APP.userLocation = {};

// sets up the map (this also includes attempting to retrieve the user's
// location, for centering purposes)
APP.setupMap = function() {

	// does this browser expose a
	// geolocation API?
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {

			// I read that FF sometimes
			// calls this multiple times
			// let's avoid that, if we
			// have already set a location,
			// just return
			if (APP.userLocation.isSet) {
				return;
			}

			// record the user's location
			APP.userLocation.lat = position.coords.latitude;
			APP.userLocation.lng = position.coords.longitude;
			APP.userLocation.isSet = true;

			// initialize the map and center it at the user's location
			SONGMAP.initMap("map", SONGMAP.defaults.zoom, {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			});
		}, function(error) {
			console.log("Oh NOES! No location!");
			setTimeout(APP.setUserLocation, 500);
		});
	} else {
		// initialize the map (uses the default center since we could not
		// retrieve user location
		SONGMAP.initMap("map", SONGMAP.defaults.zoom, SONGMAP.defaults.center);
	}
};

/* songs */

APP.getSongs = function() {
	if (APP.userLocation.isSet) {
		var centerInfo = SONGMAP.getMapCenterInfo();
		$.ajax({
			type : "POST",
			url : "/songs/get",
			dataType : "json",
			data : {
				user : APP.username,
				lat : centerInfo.lat,
				lng : centerInfo.lng,
				zoom : centerInfo.zoom
			},
			success : APP.handleUserSongs
		});
	} else {
		setTimeout(APP.getSongs, 1000);
	}

};

APP.handleUserSongs = function(songs) {
	APP.processSongs(songs);
	APP.fillMetadata(APP.meta.freqs);
};

APP.processSongs = function(songs) {

	if (!APP.util.isEmpty(songs)) {
		// iterate over the songs and
		// add them to the map, as
		// we are doing that, build
		// a document fragment that will
		// later be injected into the DOM
		var frag = document.createDocumentFragment();
		var tableHead = APP.buildTrackHead();
		var tableBody = document.createElement("tbody");
		frag.appendChild(tableHead);
		frag.appendChild(tableBody);
		for ( var idx in songs) {
			var song = songs[idx];
			var placedSong = SONGMAP.addSong(song);
			if (placedSong) {
				APP.songs[APP.songs.length] = placedSong;
				APP.recordFreqs(placedSong);
				tableBody.appendChild(APP.buildTrackNode(placedSong));
			}
		}
		$("#tracks_table").append(frag);
	}
};

APP.buildTrackHead = function() {
	var tableHead = document.createElement("thead");
	var headRow = tableHead.appendChild(document.createElement("tr"));

	// track title
	var titleHead = tableHead.appendChild(document.createElement("th"));
	titleHead.appendChild(document.createTextNode("Title"));

	// track artist
	var artistHead = tableHead.appendChild(document.createElement("th"));
	artistHead.appendChild(document.createTextNode("Artist"));

	// track album
	var albumHead = tableHead.appendChild(document.createElement("th"));
	albumHead.appendChild(document.createTextNode("Album"));

	return tableHead;
};

APP.buildTrackNode = function(song) {
	var trackNode = document.createElement("tr");

	// title cell
	var trackTitle = document.createElement("td");
	trackTitle.appendChild(document.createTextNode(song.title));
	trackNode.appendChild(trackTitle);

	// artist cell
	var trackArtist = document.createElement("td");
	trackArtist.appendChild(document.createTextNode(song.artist));
	trackNode.appendChild(trackArtist);

	// album cell
	var trackAlbum = document.createElement("td");
	trackAlbum.appendChild(document.createTextNode(song.album));
	trackNode.appendChild(trackAlbum);

	// TODO: marker interaction
	// TODO: metadata interaction

	return trackNode;
};

/* metadata */

// calculates the font size of the
// meta property text
APP.calcMetaFontSize = function(freq) {
	// we want a value between 1 and 2
	// this means our fonts will have
	// a range between the current font
	// size and double the current font
	// size
	return (freq / 10 < 1) ? (freq / 10 + 1) + "em" : (freq / 10 > 2) ? "2em"
			: (freq / 10) + "em";
};

// calculates the font size of the
// meta property text
APP.calcMetaWeight = function(freq) {
	// just return the frequency count, for now...
	// may want to change this in future...
	return freq;
};

APP.recordFreqs = function(song) {
	var weather = song.weather ? song.weather.split(",") : [];
	var userdef = song.userdef ? song.userdef.split(",") : [];
	var metadata = userdef.concat(weather);
	for ( var idx in metadata) {
		var metaitem = metadata[idx];
		if (!APP.meta.freqs[metaitem]) {
			APP.meta.freqs[metaitem] = 1;
		} else {
			APP.meta.freqs[metaitem] += 1
		}
	}
};

APP.fillMetadata = function(metadata) {
	var phrases = [];
	var idx = 0;
	for ( var phrase in metadata) {
		phrases[idx] = {};
		phrases[idx].weight = APP.calcMetaWeight(metadata[phrase]);
		phrases[idx].text = phrase;
		phrases[idx].handlers = HANDLERS.meta.getMetaHandlers(phrase);
		phrases[idx].customClass = "metaword";
		idx++;
	}

	// build a word cloud using JQCloud
	$("#cloud_holder").jQCloud(phrases, {
		width: 960,
		height: 150
	});
};

/* user management */

APP.updateUserUi = function(userdata) {
	if (userdata && userdata.user) {
		$("#fn_acct_create").hide();
		$("#fn_login").text("Logout as " + user);
	} else {
		alert("problem with user login!");
	}
};

/* utility methods */
APP.util = {};

// check if a collection is empty or not
APP.util.isEmpty = function(map) {
	for ( var key in map) {
		if (map.hasOwnProperty(key)) {
			return false;
		}
	}
	return true;
};
