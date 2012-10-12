/* stuff to create the timeline */

/* namespace for timeline logic */
var TIMELINE = {};

TIMELINE.DEFAULT_TRACKS_TO_DISPLAY = 10;

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
	var timelineDiv = document.createElement("div");
	var entriesDiv = timelineDiv.appendChild(document.createElement("div"));
	
	var maxTracks = TIMELINE.DEFAULT_TRACKS_TO_DISPLAY;
	for(var i = 0; i < maxTracks; i++) {
		var track = tracks[i];
		var trackEntryDiv = entriesDiv.appendChild(document.createElement("div"));
		trackEntryDiv.setAttribute("class", "track_entry");
		
		var dateDiv = trackEntryDiv.appendChild(document.createElement("div"));
		dateDiv.setAttribute("class", "track_date");
		
		var trackInfoDiv = trackEntryDiv.appendChild(document.createElement("div"));
		trackInfoDiv.setAttribute("class", "track_name");
		
		var trackMetaDiv = trackEntryDiv.appendChild(document.createElement("div"));
		var locationDiv = trackMetaDiv.appendChild(document.createElement("div"));
		var weatherDiv = trackMetaDiv.appendChild(document.createElement("div"));
		var imageDiv = trackMetaDiv.appendChild(document.createElement("div"));
		trackMetaDiv.setAttribute("class", "track_meta");
		
		dateDiv.appendChild(document.createTextNode(new Date(track.tstamp).toLocaleDateString()));
		trackInfoDiv.appendChild(document.createTextNode(track.title + " by " + track.artist));
		locationDiv.appendChild(document.createTextNode(track.location));
		weatherDiv.appendChild(document.createTextNode(track.weather));
		imageDiv.appendChild(document.createTextNode(track.img));
		
	}
	
	$("#timeline_container").append(timelineDiv);
	$("#loading_div").detach();
};

/* we want to do this immediately */
TIMELINE.setupUserTimeline();