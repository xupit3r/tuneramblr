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
APP.defaults.center = {lat: 40.37858996679397, lng: -80.04364013671875};

// executes when the DOM is ready
$(document).ready(function() {
	
	// setup tabs
	var bodyTabs = $("#body_tabs");
	if(bodyTabs) {
		bodyTabs.tabs();
	}

	// gets the user's location and sets up the map
	// retrieve the user location and setup the map
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

	// retrieve playlists (only returns something if
	// this is for a logged in user)
	APP.getPlaylists();

	// setup the function handlers
	HANDLERS.functions.setup();
});

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

APP.handleUserSession = function(resp) {
	
	// build autogen section
	APP.buildAutogenSection(resp.auto);

	// build the word cloud
	APP.fillMetadata(resp.freqs);
	
	// build image grid
	IMG.buildImgGrid(resp.imgs);
};

APP.buildAutogenSection = function(auto) {
	// build the new elements
	var weatherEl = document.createElement("p");
	var addressEl = document.createElement("p");
	var weatherTxt = document.createTextNode(auto.weather);
	var addressTxt = document.createTextNode(auto.address);
	
	// append the text to the elements
	weatherEl.appendChild(weatherTxt);
	addressEl.appendChild(addressTxt);
	
	// shove those elements into the DOM
	$("#tab-autogen").append([weatherEl, addressEl]);
};

APP.handleUserSongs = function(resp) {
	// process the songs
	APP.processSongs(resp.songs);
};

// returns a key that will be used to store
// and later lookup the song within the model
APP.getSongKey = function(song) {
	// i am going to need to get to these songs later. to facilitate
	// that, I am going to use the combination of title, artist, and
	// album as the key
	var key = song.title + song.artist + song.album;
	return key;
};

APP.processSongs = function(songs) {
	// if we are getting songs, we 
	// will likely just be building a 
	// table
	APP.buildTrackTable(songs);
};

APP.buildTrackTable = function(songs) {

	// only carry out these actions if we have songs to display
	if (!APP.util.isEmpty(songs)) {
		// add the songs to the map and the table
		var tableData = {};
		tableData.aaData = [];

		var row = 0;
		for ( var idx in songs) {
			var song = songs[idx];
			var key = APP.getSongKey(song);
			APP.songs.placed[key] = song;
			tableData.aaData[row] = APP.buildTrackRow(song);
			row++;
		}

		/* initialize the table */

		// set the table's headers
		tableData.aoColumns = APP.buildTrackCols();

		// set the height of the table to 300px
		tableData.sScrollY = "200px";

		// only include the table! (p.s. get to know sDom, it is pretty useful)
		tableData.sDom = "t";

		// don't paginate the table
		tableData.bPaginate = false;

		// don't display any info at the bottom of the table
		tableData.bInfo = false;

		// initialize the dataTable
		APP.songs.table = $("#tracks_table").dataTable(tableData);

		// setup the row click handler
		$("#tracks_table tbody tr")
				.live('click', HANDLERS.songs.table.rowClick);
	}
};

// build a definition of the table's column headers
APP.buildTrackCols = function() {
	var cols = [];
	cols[0] = {};
	cols[0].sTitle = "Title";
	cols[1] = {};
	cols[1].sTitle = "Artist";
	cols[2] = {};
	cols[2].sTitle = "Album";
	return cols;
};

// build a representation of the track's row in the table
APP.buildTrackRow = function(song) {
	var row = [];
	row[0] = song.title;
	row[1] = song.artist;
	row[2] = song.album;
	return row;
};

/* metadata */

// calculates the font size of the
// meta property text
APP.calcMetaWeight = function(freq) {
	// just return the frequency count, for now...
	// may want to change this in future...
	return freq;
};

APP.fillMetadata = function(freqs) {

	// if there are no songs, freqs comes back
	// as null
	if (freqs != null) {
		var phrases = [];
		var idx = 0;
		for ( var phrase in freqs) {
			phrases[idx] = {};
			phrases[idx].weight = APP.calcMetaWeight(freqs[phrase]);
			phrases[idx].text = phrase;
			phrases[idx].handlers = HANDLERS.meta.getMetaHandlers(phrase);
			phrases[idx].customClass = "metaword";
			idx++;
		}

		// store these in the scope for possible use later
		APP.meta.freqs = freqs;

		// empty out the container (this is done to avoid overlap)
		$("#cloud_holder").empty();

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


