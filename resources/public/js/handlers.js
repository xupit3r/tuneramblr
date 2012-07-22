var HANDLERS = {};

// handlers for each section
// of the site
HANDLERS.functions = {};
HANDLERS.meta = {};
HANDLERS.map = {};
HANDLERS.songs = {};
HANDLERS.playlist = {};

/* function buttons (top) */
HANDLERS.functions.setup = function() {
	// add the click handler for the generate playlist button
	$("#btn_playlist").click(HANDLERS.playlist.genClick);
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

	// pull the song (NOTE: if a song is record multiple times, this is going
	// to grab the last instance to be added to this map... going to fix this)
	var song = APP.songs.placed[key];

	// TODO: highlight related properties

};

// space for the table filter handlers
HANDLERS.songs.table.filters = {};

// table filtering (extends datatables). follows the data tables API. RETURNS:
// true if row should be kept, false otherwise
HANDLERS.songs.table.filters.meta = function(oSettings, oData, iDataIndex) {

	// pull the song and the global meta data
	var key = oData[0] + oData[1] + oData[2];
	var song = APP.songs.placed[key];
	var selectedPhrases = APP.select.phrases;
	var selectedImages = APP.select.images;

	// only keep songs that possess one of the selected images
	var hasImg = APP.util.isEmpty(selectedImages) ? true : false;
	for ( var img in selectedImages) {
		if (song.img === img) {
			hasImg = true;
		}
	}

	// only keep songs that possess all the selected phrases
	var hasPhrases = true;
	for ( var phrase in selectedPhrases) {
		if (!song.metadata.hasOwnProperty(phrase)) {
			// it doesn't possess the current selected metadata, filter it out!
			hasPhrases = false;
		}
	}

	// this song possessed all the selected metadata, keep it!
	return (hasImg && hasPhrases);
};

// add the filtering extension to the datatables plugin
$.fn.dataTableExt.afnFiltering.push(HANDLERS.songs.table.filters.meta);

/* metadata */

// space for metadata item handlers
HANDLERS.meta.phrases = {};
HANDLERS.meta.images = {};

HANDLERS.meta.getMetaHandlers = function(metaProp) {
	var mhs = {
		click : HANDLERS.meta.phrases.click(metaProp)
	};

	return mhs;
};

// the onclick handler for metadata items
HANDLERS.meta.phrases.click = function(metaProp) {
	var metaClick = function(ev) {
		if (APP.select.phrases[metaProp]) {
			// remove this property from the model and remove the selection from
			// the cloud
			delete APP.select.phrases[metaProp];
			$(this).removeClass("mselect");

		} else {
			// add this property to the model and select it in the cloud
			APP.select.phrases[metaProp] = metaProp;
			$(this).addClass("mselect");
		}

		// re-draw the table as the selection/deselection of meta data will
		// affect the contents of the table
		APP.songs.table.fnDraw();
	};

	return metaClick;
};

// the onclick handler for metadata items
HANDLERS.meta.images.click = function(metaProp) {
	var metaClick = function(ev) {
		if (APP.select.images[metaProp]) {
			// remove this property from the model and remove the selection from
			// the cloud
			delete APP.select.images[metaProp];
			$(this).removeClass("mselect");

		} else {
			// add this property to the model and select it in the cloud
			APP.select.images[metaProp] = metaProp;
			$(this).addClass("mselect");
		}

		// re-draw the table as the selection/deselection of meta data will
		// affect the contents of the table
		APP.songs.table.fnDraw();
	};

	return metaClick;
};

/* playlist */
HANDLERS.playlist.genClick = function() {
	// if the table is not yet available, just
	// silently fail
	if (APP.songs.table != null) {
		var inHtml = "Enter a title: <input type='text' id='listTitle' name='listTitle' value='' />"
		$.prompt(inHtml, {
			submit : HANDLERS.playlist.finishGen,
			buttons : {
				Ok : true
			}
		});
	}
};

HANDLERS.playlist.finishGen = function(e, v, m, f) {

	// check that a playlist name was actually entered
	// if not, keep the dialog open and highlight the
	// text field
	if (f.listTitle == "") {
		var inp = m.children("#listTitle");
		inp.css("border", "solid #ff0000 1px");
		return false;
	}

	// pull the title from the dialog
	var title = f.listTitle;

	// retrieve the songs that are currently in the table
	var rows = APP.songs.table.$("tbody tr");

	// build a song list from the rows
	var songs = [];
	for ( var i = 0; i < rows.length; i++) {
		var row = rows[i];
		var cells = $("td", row);
		songs[i] = {};
		songs[i].title = $(cells[0]).text();
		songs[i].artist = $(cells[1]).text();
		songs[i].album = $(cells[2]).text();
	}

	// make a call to generate the playlist
	APP.generatePlaylist(title, songs);

	// true will close the dialog
	return true;
};
