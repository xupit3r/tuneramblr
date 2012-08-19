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
	// nadda
};

HANDLERS.functions.reload = function() {
	// probably need this when a user logs in
};

/* songs */

// handlers for the song table
HANDLERS.songs.table = {};

// song row click
HANDLERS.songs.table.rowClick = function(el) {
	
	// build the dialog content
	var trackInfo = APP.tracks[el.id];
	var trackInfoDialogBody = $("#ti-dialog_body");
	
	// build a pie chart of the meta data associated
	// with this track
	var tiMeta = trackInfo.metadata;
	var chartData = [];
	var idx = 0;
	for(var item in tiMeta) {
		chartData[idx] = [item, tiMeta[item]];
		idx++;
	}

	// launch a modal dialog with the tracks info in it
	$("#ti-dialog").modal();
	
	// display the char
	$.jqplot ("ti_chart_div", [chartData],{
		seriesDefaults: {	
			renderer: $.jqplot.PieRenderer,
			rendererOptions: {showDataLabels: true}
		},
		legend: { show:true, location: 'e' }
	});
};

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
