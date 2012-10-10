var TTABLE = {};

/* the tracks table */
TTABLE.tracks = {};

/* session info */
TTABLE.session = {};

/**
 * Requests the user's track information.
 */
TTABLE.setupUserSessionTracks = function() {
	$.ajax({
		type : "POST",
		url : "/user/base/tracks",
		dataType : "json",
		success : TTABLE.handleUserSessionTracks
	});
};

/**
 * Handles the response for the user tracks
 * 
 * @param resp
 *            the response object from the tracks request
 */
TTABLE.handleUserSessionTracks = function(resp) {
	/* setup the table */
	TTABLE.initTable(resp.songs);

	/* store the rest of the track info */
	TTABLE.session.freqs = resp.freqs;
	TTABLE.session.imgs = resp.imgs;
};

/**
 * Initializes and builds the track table display
 * 
 * @param tracks
 *            an object containing the track data to be displayed in the table
 */
TTABLE.initTable = function(tracks) {
	/* only carry out these actions if we have songs to display */
	if (!TUNERAMBLR.util.isEmpty(tracks)) {
		/* get a handle on the tracks table */
		var tracksTab = $("#tracks_table");
		tracksTab.append(TTABLE.buildTracksHead());
		tracksTab.append(TTABLE.buildTracksBody(tracks));
		tracksTab.find("tbody tr").click(function(e) {
			TTABLE.tableRowClick(this);
		}).popover({
			trigger: "hover",
			placement: "top",
			title: "Track Info",
			content: function () {
				
				/* setup the table */
				var table = document.createElement("table");
				table.setAttribute("class", "table table-condensed table-bordered");
				
				/* setup the header row */
				var thead = table.appendChild(document.createElement("thead"));
				var headRow = thead.appendChild(document.createElement("tr"));
				var itemHeader = headRow.appendChild(document.createElement("th"));
				var cntHeader = headRow.appendChild(document.createElement("th"));
				itemHeader.appendChild(document.createTextNode("Data"));
				cntHeader.appendChild(document.createTextNode("Frequency"));
				
				
				/* build the table body rows */
				var trackInfo = TTABLE.tracks[this.id];
				var tiMeta = trackInfo.metadata;
				var tbody = table.appendChild(document.createElement("tbody"));
				for ( var item in tiMeta) {
					var row = tbody.appendChild(document.createElement("tr"));
					
					var itemCell = row.appendChild(document.createElement("td"));
					var cntCell = row.appendChild(document.createElement("td"));

					itemCell.appendChild(document.createTextNode(item));
					cntCell.appendChild(document.createTextNode(tiMeta[item]));
				}
				
				return table;
			}
		});
	}
	
	$("#loading_div").detach();
};

/**
 * Builds the tracks table header.
 */
TTABLE.buildTracksHead = function() {
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
TTABLE.buildTracksBody = function(tracks) {
	var tbody = document.createElement("tbody");
	for ( var i = 0; i < tracks.length; i++) {
		var id = "row_" + i;
		var track = tracks[i];
		tbody.appendChild(TTABLE.buildTrackRow(track, id));
		TTABLE.tracks[id] = track;
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
TTABLE.buildTrackRow = function(track, id) {

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
TTABLE.tableRowClick = function(el) {
	var trackInfo = TTABLE.tracks[el.id];
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

/* this should be immediately invoked */
TTABLE.setupUserSessionTracks();