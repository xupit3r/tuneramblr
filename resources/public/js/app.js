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
	// iterate over the songs and
	// add them to the map
	for ( var idx in songs) {
		var song = songs[idx];
		var placedSong = SONGMAP.addSong(song);
		if (placedSong) {
			APP.songs[APP.songs.length] = placedSong;
			APP.recordFreqs(placedSong);
		}
	}
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
	// iterate over the metadata and
	// add it to the panel
	// we want to build a fragment and then
	// inject that fragment into the DOM
	// (less expensive)
	var frag = document.createDocumentFragment();
	var ful = frag.appendChild(document.createElement("ul"));
	ful.id = "meta-items";
	for ( var idx in metadata) {

		// build the UI element
		var freq = metadata[idx];
		var property = idx;
		var tmpLi = document.createElement("li");
		var liTxt = document.createTextNode(property);
		tmpLi.appendChild(liTxt);
		tmpLi.style.fontSize = APP.calcMetaFontSize(freq);
		tmpLi.onclick = HANDLERS.meta.item.click(property);
		ful.appendChild(tmpLi);
	}
	$("#metaside").append(frag);
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
