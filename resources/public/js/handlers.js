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

// space for the table filter handlers
HANDLERS.songs.table.filters = {};

// table filtering (extends datatables). follows the data tables API. RETURNS:
// true if row should be kept, false otherwise
HANDLERS.songs.table.filters.meta = function(oSettings, oData, iDataIndex) {

	// pull the song and the global meta data
	var key = oData[0] + oData[1] + oData[2];
	var song = APP.songs.placed[key];
	var selectedMetadata = APP.select.meta;

	// only keep songs that possess all the selected properties
	for ( var meta in selectedMetadata) {
		if (!song.metadata.hasOwnProperty(meta)) {
			// it doesn't possess the current selected metadata, filter it out!
			return false;
		}
	}

	// this song possessed all the selected metadata, keep it!
	return true;
};

// add the filtering extension to the datatables plugin
$.fn.dataTableExt.afnFiltering.push(HANDLERS.songs.table.filters.meta);

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

		// re-draw the table as the selection/deselection of meta data will
		// affect the contents of the table
		APP.songs.table.fnDraw();
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
