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
	
	// TODO: if a user is logged in, retrieve his/her playlists
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

APP.handleUserSongs = function(resp) {
	// process the songs (build the table and add the locations to the map)
	APP.processSongs(resp.songs);

	// build the word cloud
	APP.fillMetadata(resp.freqs);
};

//returns a key that will be used to store
//and later lookup the song within the model
APP.getSongKey = function(song) {
	// i am going to need to get to these songs later. to facilitate
	// that, I am going to use the combination of title, artist, and
	// album as the key
	var key = song.title + song.artist + song.album;
	return key;
};

APP.processSongs = function(songs) {

	// only carry out these actions if we have songs to display
	if (!APP.util.isEmpty(songs)) {
		// add the songs to the map and the table
		var tableData = {};
		tableData.aaData = [];
		var row = 0;
		for ( var idx in songs) {
			var song = songs[idx];
			var placedSong = SONGMAP.addSong(song);
			if (placedSong) {
				var key = APP.getSongKey(song); 
				APP.songs.placed[key] = placedSong;
				tableData.aaData[row] = APP.buildTrackRow(placedSong);
				row++;
			}
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
};

/* playlist */
APP.generatePlaylist = function(title, songs) {
	// if we have some songs, make a call to generate the playlist
	if(songs != null) {
		$.ajax({
			type : "POST",
			url : "/playlist/gen",
			dataType : "json",
			data : {
				title: title,
				songs: songs
			},
			success : APP.handlePlaylist
		});
	} else {
		console.log("hmmm, that playlist should have had some songs.");
	}
};

APP.handlePlaylist = function(resp) {
	// utilize the data URI and create a link to the response data
	if(resp.added == true) {
		var listLink = document.createElement("a");
		$(listLink).attr("href", APP.buildPlaylistLink(resp));
		$(listLink).addClass("lnk_playlist");
		$(listLink).append(document.createTextNode(resp.title));
		$("#playlists").append(listLink);
	}
};

APP.buildPlaylistLink = function(listData) {
	var pname = listData.pname;
	var link = "/playlist/get/"+pname;
	return link;
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
