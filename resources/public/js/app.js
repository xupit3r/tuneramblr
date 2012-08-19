// general application stuff
var APP = {};

APP.img = {};
APP.img.url = "/image/ugen/";

// model storage for stuff about songs
APP.songs = {};

// reference to the tracks table
APP.songs.table;

// storage for songs placed on the map
APP.songs.placed = {}

// model storage for metadata
APP.meta = {};
APP.meta.freqs = {};

// model storage for user selections
APP.select = {};
APP.select.phrases = {};
APP.select.images = {};
APP.select.songs = {};

// default center location
APP.defaults = {};
APP.defaults.center = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

// executes when the DOM is ready
$(document).ready(function() {

	$('.carousel').carousel();

	// is this a logged in user?
	if ($("#stats").length > 0) {

		// get the user's location
		APP.getUserLocation(function(position) {
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

			// setup the centering info
			loccenter = {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			};

			// now, request the songs
			APP.setupUserSession(loccenter);

		}, function() {
			// if we have no location API,
			// just use the default location (it
			// is in Pittsburgh :)
			APP.setupUserSession(APP.defaults.center);
		}, function(error) {
			console.log("Oh NOES! No location!");
		});
	}
});

// setup all of the pertinent session information
APP.setupUserSession = function(locinfo) {
	var data = {};
	if ((typeof locinfo != "undefined") && (locinfo != null)) {
		data = {
			lat : locinfo.lat,
			lng : locinfo.lng,
		}
	}

	// make the call
	$.ajax({
		type : "POST",
		url : "/user/base",
		dataType : "json",
		data : data,
		success : APP.handleUserSession
	});
};

APP.handleUserSession = function(resp) {
	// setup the table
	APP.initTable(resp.songs);
};

APP.buildAutogenSection = function(auto) {
	// build the new elements
	var timeEl = document.createElement("p");
	var weatherEl = document.createElement("p");
	var addressEl = document.createElement("p");

	// build the text nodes
	var timeTxt = document.createTextNode(auto.time);
	var weatherTxt = document.createTextNode(auto.weather);
	var addressTxt = document.createTextNode(auto.address);

	// append the text nodes to the elements
	weatherEl.appendChild(weatherTxt);
	addressEl.appendChild(addressTxt);
	timeEl.appendChild(timeTxt)

	// shove those elements into the DOM
	$("#autogen").append([ timeEl, weatherEl, addressEl ]);
};

// store the user location for later use
APP.userLocation = {};
APP.getUserLocation = function(hLocation, hNoApi, hError) {
	// does this browser expose a
	// geolocation API?
	if (navigator.geolocation) {
		// call the browser's location API
		navigator.geolocation.getCurrentPosition(hLocation, hError);
	} else {
		// we have no API, we shall carry on!
		hNoApi();
	}
};

/* songs */

APP.getSongs = function(centerinfo) {
	var data = {};
	if ((typeof centerinfo != "undefined") && (centerinfo != null)) {
		data = {
			lat : centerinfo.lat,
			lng : centerinfo.lng,
		}
	}

	// make the call
	$.ajax({
		type : "POST",
		url : "/songs/get",
		dataType : "json",
		data : data,
		success : APP.handleUserSongs
	});

};

APP.handleUserSongs = function(resp) {
	// process the songs
	APP.initTable(resp.songs);
};

APP.initTable = function(tracks) {
	// only carry out these actions if we have songs to display
	if (!APP.util.isEmpty(tracks)) {
		// get a handle on the tracks table
		APP.songs.table = $("#tracks_table");
		APP.songs.table.append(APP.buildTracksHead());
		APP.songs.table.append(APP.buildTracksBody(tracks));

	}
};

// build a definition of the table's column headers
APP.buildTracksHead = function() {
	var thead = document.createElement("thead");
	// the row to return
	var row = thead.appendChild(document.createElement("tr"));
	
	// build the cells
	var titleCell = row.appendChild(document.createElement("th"));
	var artistCell = row.appendChild(document.createElement("th"));
	var albumCell = row.appendChild(document.createElement("th"));
	
	// build the cell text
	titleCell.appendChild(document.createTextNode("Title"));
	artistCell.appendChild(document.createTextNode("Artist"));
	albumCell.appendChild(document.createTextNode("Album"));
	
	return thead;
};

// build a representation of the track's row in the table
APP.buildTracksBody = function(tracks) {
	var tbody = document.createElement("tbody");
	for (var i = 0; i < tracks.length; i++) {
		tbody.appendChild(APP.buildTrackRow(tracks[i]));
	}
	return tbody;
};

APP.buildTrackRow = function(track) {
	
	// the row to return
	var row = document.createElement("tr");
	
	// build the cells
	var titleCell = row.appendChild(document.createElement("td"));
	var artistCell = row.appendChild(document.createElement("td"));
	var albumCell = row.appendChild(document.createElement("td"));
	
	// build the cell text
	titleCell.appendChild(document.createTextNode(track.title));
	artistCell.appendChild(document.createTextNode(track.artist));
	albumCell.appendChild(document.createTextNode(track.album));
	
	return row;
};

/* metadata */

APP.fillMetadata = function(freqs) {

	// if there are no songs, freqs comes back
	// as null
	if (freqs != null) {
		var phrases = [];
		var idx = 0;
		for ( var phrase in freqs) {
			phrases[idx] = {};
			phrases[idx].weight = freqs[phrase];
			phrases[idx].text = phrase;
			phrases[idx].handlers = HANDLERS.meta.getMetaHandlers(phrase);
			phrases[idx].customClass = "metaword";
			idx++;
		}

		// store these in the scope for possible use later
		APP.meta.freqs = freqs;

		// empty out the container (this is done to avoid overlap)
		// $("#cloud_holder").empty();

		// build a word cloud using JQCloud
		$("#cloud_holder").jQCloud(phrases, {
			width : 960,
			height : 150
		});
	}
};

/* playlist */
APP.generatePlaylist = function(title, songs) {
	// if we have some songs, make a call to generate the playlist
	if (songs != null) {
		$.ajax({
			type : "POST",
			url : "/playlists/gen",
			dataType : "json",
			data : {
				title : title,
				songs : songs
			},
			success : APP.handleAddedPlaylist
		});
	} else {
		console.log("hmmm, that playlist should have had some songs.");
	}
};

APP.getPlaylists = function() {
	$.ajax({
		type : "POST",
		url : "/playlists/ulists",
		dataType : "json",
		success : APP.handleUserPlaylists
	});
};

APP.handleAddedPlaylist = function(resp) {
	// utilize the data URI and create a link to the response data
	if (resp.added == true) {
		var link = APP.buildPlaylistLink(resp);
		$("#playlists").append(link);
	}
};

APP.handleUserPlaylists = function(resp) {
	// if this is an anon user, the response
	// will be null
	if (resp != null) {
		var frag = document.createDocumentFragment();
		for ( var i = 0; i < resp.length; i++) {
			frag.appendChild(APP.buildPlaylistLink(resp[i]));
		}
		$("#playlists").append(frag);
	}
};

// just note that pObj has to have a minimum
// of pname and title attributes
APP.buildPlaylistLink = function(pObj) {
	var listLink = document.createElement("a");
	$(listLink).attr("href", "/playlists/get/" + pObj.pname);
	$(listLink).addClass("lnk_playlist");
	$(listLink).append(document.createTextNode(pObj.title));
	return listLink;
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

APP.util.arrToMap = function(arr) {
	var map = {};
	for ( var i = 0; i < arr.length; i++) {
		map[arr[i]] = arr[i];
	}
	return map;
};

/* About */
APP.about = function() {
	$.ajax({
		type : "GET",
		url : "/about",
		dataType : "json",
		success : APP.displayAbout
	});
};

APP.displayAbout = function(response) {
	$.prompt(response.about);
};
