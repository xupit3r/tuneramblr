var LISTEN = {};

LISTEN.session = {};
LISTEN.userLocation = {};

/* app defaults */
LISTEN.defaults = {};
LISTEN.defaults.location = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

/**
 * Requests the user session audio information.
 * 
 * @param locinfo
 *            an object containing the lat and lng information for the user
 *            location
 * @param sHandler
 *            a callback function to handle a successful Ajax request.
 */
LISTEN.getUserSessionAudio = function(locinfo, sHandler) {
	var data = {};
	if (locinfo) {
		data = {
			lat : locinfo.lat,
			lng : locinfo.lng,
			curtime : new Date().getTime(),
			tz : jstz.determine().name()
		}
	}

	$.ajax({
		type : "GET",
		url : "/user/listen/get/audio",
		dataType : "json",
		data : data,
		success : sHandler
	});
};

/**
 * Store the metadata for the currently playing track
 * 
 * @param meta
 *            an object containing the meta data to be stored
 */
LISTEN.storeAudioMeta = function(meta) {
	/* store the data */
	LISTEN.session.weather = meta.weather;
	LISTEN.session.location = meta.location;
	LISTEN.session.time = meta.time;
	LISTEN.session.track = meta.track;
};

/**
 * Handles the server response for the user session audio data
 * 
 * @param resp
 *            the response object from the request
 */
LISTEN.initUserSessionAudio = function(resp) {
	LISTEN.storeAudioMeta(resp);
	LISTEN.buildAudioSectionMeta(resp);
	LISTEN.setupAudioPlayer(resp);
};

/**
 * Handles the server response for the audio update request
 * 
 * @param resp
 *            the response object from the request
 */
LISTEN.updateUserSessionAudio = function(resp) {
	LISTEN.storeAudioMeta(resp);
	LISTEN.buildAudioSectionMeta(resp);
	LISTEN.updateAudioPlayer(resp);
};

/**
 * builds the user meta data section (location, time, weather).
 * 
 * @param meta
 *            an object containing the expected metadata (location, time, and
 *            weather)
 */
LISTEN.buildAudioSectionMeta = function(meta) {
	/* build the new elements */
	var timeEl = $("#metad_time_val span.metad_text");
	var weatherEl = $("#metad_weather_val span.metad_text");
	var locationEl = $("#metad_location_val span.metad_text");
	var trackTitleEl = $("#jp-track-title");
	var trackArtistEl = $("#jp-track-artist");

	/* append the text nodes to the elements */
	timeEl.text(meta.time);
	weatherEl.text(meta.weather)
	locationEl.text(meta.location);

	var track = meta.track;
	trackTitleEl.text(track.title);
	trackArtistEl.text(track.artist);
};

/**
 * Sets up the audio player.
 * 
 * @param audioInfo
 *            the information necessary to build the audio player (needs more
 *            description)
 */
LISTEN.setupAudioPlayer = function(audioInfo) {

	var ap = $("#jquery_jplayer_1");

	ap.jPlayer({
		ready : function() {
			/* load audio and play */
			$(this).jPlayer("setMedia", {
				mp3 : audioInfo.url
			}).jPlayer("play");
		},
		ended : function() {
			/* request next song when track is done */
			LISTEN.getNextTrack();
		},
		swfPath : "/script/Jplayer.swf",
		supplied : "mp3"
	});

};

/**
 * Loads and starts playing the next track
 * 
 * @param audioInfo
 *            the audio information (containing the stream url) for the next
 *            track
 */
LISTEN.updateAudioPlayer = function(audioInfo) {
	var ap = $("#jquery_jplayer_1");
	ap.jPlayer("clearMedia");
	ap.jPlayer("setMedia", {
		mp3 : audioInfo.url
	}).jPlayer("play");
};

/**
 * Handles the retrieval of the next audio track
 */
LISTEN.getNextTrack = function() {
	LISTEN.getUserSessionAudio({
		lat : LISTEN.userLocation.lat,
		lng : LISTEN.userLocation.lng
	}, LISTEN.updateUserSessionAudio);
};

LISTEN.initLoading = function() {
	/* loading div for ajax calls */
	$("#loading_div")
	.ajaxStart(function() {
		$("#jp-track-title").text("");
		$("#jp-track-artist").text("");
		$(this).show();
	}).ajaxStop(function() {
		$(this).hide();
	});
};

/* executes when the DOM is ready */
$(document).ready(function() {
	
	LISTEN.initLoading();

	/* get the user's location and metadata about that location */
	TUNERAMBLR.util.getUserLocation(function(position) {
		/*
		 * I read that FF sometimes calls this multiple times let's avoid that,
		 * if we have already set a location, just return
		 */
		if (LISTEN.userLocation.lat && LISTEN.userLocation.lng) {
			return;
		}

		/* record the user's location */
		LISTEN.userLocation.lat = position.coords.latitude;
		LISTEN.userLocation.lng = position.coords.longitude;

		/* now, setup audio playback */
		LISTEN.getUserSessionAudio({
			lat : position.coords.latitude,
			lng : position.coords.longitude
		}, LISTEN.initUserSessionAudio);

	}, function() {
		LISTEN.setupUserSession(LISTEN.defaults.location);
	}, function(error) {
		/* do something */
	});
});