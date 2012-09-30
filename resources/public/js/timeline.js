/* stuff to create the timeline */

/* namespace for timeline logic */
var TIMELINE = {};

/**
 * Requests the user's timeline information.
 */
TIMELINE.setupUserTimeline = function() {
	$.ajax({
		type : "GET",
		url : "/user/base/timeline",
		dataType : "json",
		success : TIMELINE.handleTimelineData
	});
};

/**
 * Handles the response from the timeline data request
 * 
 * @param resp
 *            the response (contains the timeline data)
 */
TIMELINE.handleTimelineData = function(resp) {
	TIMELINE.tracks = resp.tracks;

	TIMELINE.buildTimeline(resp.tracks);
};

/**
 * Builds the timeline display.
 * 
 * @param tracks
 *            the tracks (in sorted order) to be displayed within the timeline
 */
TIMELINE.buildTimeline = function(tracks) {
	var tlTable = document.createElement("table");
	tlTable.setAttribute("class", "table");
	var tlBody = tlTable.appendChild(document.createElement("tbody"));
	
	for(var i = 0; i < tracks.length; i++) {
		var track = tracks[i];
		var trackRow = tlBody.appendChild(document.createElement("tr"));
		
		var dateTd = trackRow.appendChild(document.createElement("td"));
		var trackInfoTd = trackRow.appendChild(document.createElement("td"));
		var locationTd = trackRow.appendChild(document.createElement("td"));
		var weatherTd = trackRow.appendChild(document.createElement("td"));
		var imageTd = trackRow.appendChild(document.createElement("td"));
		
		var tDate = new Date(track.tstamp);
		var dateDisp = tDate.toLocaleDateString() + ", " + tDate.toLocaleTimeString();
		dateTd.appendChild(document.createTextNode(dateDisp));
		trackInfoTd.appendChild(document.createTextNode(track.title + " by " + track.artist));
		locationTd.appendChild(document.createTextNode(track.location));
		weatherTd.appendChild(document.createTextNode(track.weather));
		imageTd.appendChild(document.createTextNode(track.img));
		
	}
	
	$("#timeline_container").append(tlTable);
};

/* we want to do this immediately */
TIMELINE.setupUserTimeline();