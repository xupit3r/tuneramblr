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
		dataType : "html",
		success : TIMELINE.handleTimelineData
	});
};

/**
 * Handles the response from the timeline display request
 * 
 * @param resp
 *            the response (contains the timeline display data, hint: it is
 *            HTML!)
 */
TIMELINE.handleTimelineData = function(resp) {
	$("#timeline_container").append(resp);
	$("#loading_div").detach();
};

/* we want to do this immediately */
TIMELINE.setupUserTimeline();