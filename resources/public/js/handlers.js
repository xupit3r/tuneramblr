var HANDLERS = {};

// handlers for each section
// of the site
HANDLERS.functions = {};
HANDLERS.meta = {};
HANDLERS.map = {};
HANDLERS.songs = {};

/* function buttons (top) */
HANDLERS.functions.setup = function() {
	// setup any handlers here...
};

HANDLERS.functions.reload = function() {
	// probably need this when a user logs in
};

/* songs */

// handlers for the song table
HANDLERS.songs.table = {};

// song row click
HANDLERS.songs.table.rowClick = function(ev) {
	// pull the data cells in this row
	var cells = $("td", this);

	// build the key
	var title = $(cells[0]).text();
	var artist = $(cells[1]).text();
	var album = $(cells[2]).text();
	var key = title + artist + album;

	// pull the song (NOTE: if a song is recorde multiple times, this is going
	// to grab the last instance to be added to this map... going to fix this)
	var song = APP.songs.placed[key];

	// pan to the songs location on the map
	SONGMAP.panTo(song.marker.getLatLng());

	// open the pop up so the user knows which marker represents the song
	song.marker.openPopup();

};

/* metadata */

// space for metadata item handlers
HANDLERS.meta.item = {};

HANDLERS.meta.getMetaHandlers = function(metaProp) {
	var mhs = {
		click : HANDLERS.meta.item.click(metaProp)
	};

	return mhs;
};

// the onclick handler for metadata items
HANDLERS.meta.item.click = function(metaProp) {
	var metaClick = function(ev) {
		if (APP.select.meta[metaProp]) {
			// remove this property from the model and remove the selection from
			// the cloud
			delete APP.select.meta[metaProp];
			$(this).removeClass("mselect");

		} else {
			// add this property to the model and select it in the cloud
			APP.select.meta[metaProp] = metaProp;
			$(this).addClass("mselect");
		}
	};

	return metaClick;
};

/* map */

// handle map dragging
HANDLERS.map.dragend = function(e) {
	// TODO: when necessary, grab new songs (be sure to clear all necessary data
	// structures: map, DOM scopes, tables, etc.)
};

// handle map zooming
HANDLERS.map.zoomend = function(e) {
	// TODO: when necessary, grab new songs (be sure to clear all necessary data
	// structures: map, DOM scopes, tables, etc.)
};
