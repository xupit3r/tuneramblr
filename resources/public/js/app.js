var USERS = {};

/* the tracks table */
USERS.tracks = {};

/* session info */
USERS.session = {};

/**
 * Requests the user's track information.
 */
USERS.setupUserSessionTracks = function() {
	$.ajax({
		type : "POST",
		url : "/user/base/tracks",
		dataType : "json",
		success : USERS.handleUserSessionTracks
	});
};

/**
 * Handles the response for the user tracks
 * 
 * @param resp
 *            the response object from the tracks request
 */
USERS.handleUserSessionTracks = function(resp) {
	/* setup the table */
	USERS.initTable(resp.songs);

	/* store the rest of the track info */
	USERS.session.freqs = resp.freqs;
	USERS.session.imgs = resp.imgs;
};

/**
 * Initializes and builds the track table display
 * 
 * @param tracks
 *            an object containing the track data to be displayed in the table
 */
USERS.initTable = function(tracks) {
	/* only carry out these actions if we have songs to display */
	if (!TUNERAMBLR.util.isEmpty(tracks)) {
		/* get a handle on the tracks table */
		var tracksTab = $("#tracks_table");
		tracksTab.append(USERS.buildTracksHead());
		tracksTab.append(USERS.buildTracksBody(tracks));
		tracksTab.find("tbody tr").click(function(e) {
			USERS.tableRowClick(this);
		});

	}
};

/**
 * Builds the tracks table header.
 */
USERS.buildTracksHead = function() {
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
USERS.buildTracksBody = function(tracks) {
	var tbody = document.createElement("tbody");
	for ( var i = 0; i < tracks.length; i++) {
		var id = "row_" + i;
		var track = tracks[i];
		tbody.appendChild(USERS.buildTrackRow(track, id));
		USERS.tracks[id] = track;
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
USERS.buildTrackRow = function(track, id) {

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
USERS.tableRowClick = function(el) {
	var trackInfo = USERS.tracks[el.id];
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

/* executes when the DOM is ready */
$(document).ready(function() {

	/* is this a logged in user? */
	if (TUNERAMBLR.isUserLoggedIn()) {
		USERS.setupUserSessionTracks();
	} else {
		$('.carousel').carousel();
	}
});