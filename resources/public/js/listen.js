var LISTEN = {};

LISTEN.session = {};
LISTEN.userLocation = {};

/* app defaults */
// TODO: probably should store this under the TUNERAMBLR namespace
LISTEN.defaults = {};
LISTEN.defaults.location = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

/**
 * Prepares the user audio session. If the user session is bad, it will take the
 * steps necessary to mend it.
 */
LISTEN.prepareSession = function(watcha) {
	LISTEN.session.watcha = watcha;
	$.ajax({
		type : "GET",
		url : "/user/listen/check/session",
		dataType : "json",
		data : {},
		success : LISTEN.handlePrepareSession
	});
};

/**
 * Handle the prepare session request: either music will begin playing or the
 * user will be asked to re-authenticate with gmusic
 */
LISTEN.handlePrepareSession = function(resp) {
	if (resp.gsession) {
		// TODO: maybe add some indication that we have successfully logged in
		// to gmusic
		LISTEN.hideGmusicLogin();
		LISTEN.kickOffAudioSession(LISTEN.session.watcha);
	} else {
		LISTEN.showGmusicLogin();
	}
};

/**
 * Hides the modal dialog to login to gmusic
 */
LISTEN.hideWatchaDoing = function() {
	$("#track-lookup").show();
	$("#watcha-modal_body").empty();
	$("#watcha-modal").modal("hide");
};

/**
 * Show the modal dialog to login to gmusic
 */
LISTEN.showWatchaDoing = function() {
	$.ajax({
		type : "GET",
		url : "/user/listen/watcha/modal",
		dataType : "html",
		data : {},
		success : function(content) {
			$("#watcha-modal_body").empty().append(content);
			$("#watcha-modal").modal({
				backdrop : "static",
				keyboard : false,
				show : true
			});
		}
	});
};

/**
 * Hides the modal dialog to login to gmusic
 */
LISTEN.hideGmusicLogin = function() {
	$("#gmusic-modal_body").empty();
	$("#gmusic-modal").modal("hide");
};

/**
 * Show the modal dialog to login to gmusic
 */
LISTEN.showGmusicLogin = function() {
	$.ajax({
		type : "GET",
		url : "/user/gmusic/login/modal",
		dataType : "html",
		data : {},
		success : function(content) {
			$("#gmusic-modal_body").empty().append(content);
			$("#gmusic-modal").modal({
				backdrop : "static",
				keyboard : false,
				show : true
			});
		}
	});
};

/**
 * Retrieves and begins the user audio session
 * 
 * @param locinfo
 *            an object containing the lat and lng information for the user
 *            location
 * @param sHandler
 *            a callback function to handle a successful Ajax request.
 */
LISTEN.getUserSessionAudio = function(locinfo, doingWhat, sHandler) {
	var data = {};
	if (locinfo) {
		data = {
			lat : locinfo.lat,
			lng : locinfo.lng,
			curtime : new Date().getTime(),
			tz : jstz.determine().name(),
			watcha : doingWhat
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
	LISTEN.session.track = meta.track;
	LISTEN.session.weather = meta.weather;
	LISTEN.session.location = meta.location;
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
	$("#track-lookup").remove();
	$("#jp_container_1").fadeIn("slow", function() {
		LISTEN.setupAudioPlayer(resp);
	});
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
 * builds the audio metadata section
 * 
 * @param meta
 *            an object containing the expected metadata/trackinfo
 */
LISTEN.buildAudioSectionMeta = function(meta) {
	$("#jp-track-title").text(meta.track.title);
	$("#jp-track-artist").text(meta.track.artist);
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

	/* setup the love/hate stuff */
	$("#love-track").click(function(ev) {
		if(!$(this).hasClass("selected")) {
			$(this).addClass("selected");
			LISTEN.recordLoveHate("user_like")
		}
	});

	$("#hate-track").click(function(ev) {
		// TODO: indicate in the UI that you click the "hate" icon
		LISTEN.recordLoveHate("skip");
		$("#jquery_jplayer_1").jPlayer("stop");
		LISTEN.getNextTrack();
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
 * Submits a love/hate request for the currently playing track.
 */
LISTEN.recordLoveHate = function(lh) {
	var data = {
		lat : LISTEN.userLocation.lat,
		lng : LISTEN.userLocation.lng,
		curtime : new Date().getTime(),
		tz : jstz.determine().name(),
		watcha : LISTEN.session.watcha,
		weather : LISTEN.session.weather,
		location : LISTEN.session.location,
		artist : LISTEN.session.track.artist,
		title : LISTEN.session.track.title,
		album : LISTEN.session.track.album,
		lovehate : lh
	};

	$.ajax({
		type : "POST",
		url : "/user/listen/lovehate",
		dataType : "json",
		data : data,
		success : LISTEN.handleLoveHateResp
	});
};

/**
 * Handles the response from the love/hate track request. Handling will probably
 * mean displaying some sort of notification that we have stored the info.
 * 
 * @param resp
 *            the response from the love/hate request
 */
LISTEN.handleLoveHateResp = function(resp) {
	$("#track-alert").text(resp.message).fadeIn("slow", function() {
		$(this).fadeOut(5000, function() {
			$(this).text("");
		});
	});
	
};

/**
 * Handles the retrieval of the next audio track
 */
LISTEN.getNextTrack = function() {

	// clear the current track information
	LISTEN.showTrackLoading();
	LISTEN.getUserSessionAudio({
		lat : LISTEN.userLocation.lat,
		lng : LISTEN.userLocation.lng
	}, LISTEN.session.watcha, LISTEN.updateUserSessionAudio);
};

/**
 * show the track loading div
 */
LISTEN.showTrackLoading = function() {
	$("#jp-track-title").text("");
	$("#jp-track-artist").text("");
	$("#love-track").removeClass("selected");
	$("#loading_div").show();
};

LISTEN.kickOffAudioSession = function(doingWhat) {

	/* set the doing what? in the local cache */
	LISTEN.session.watcha = doingWhat;

	/* get the user's location and metadata about that location */
	TUNERAMBLR.util.getUserLocation(function(position) {

		// TODO: update the location retrieval to check on the "freshness" of
		// the user's location. see todo note in TUNERAMBLR.util.getUserLocation

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
		}, doingWhat, LISTEN.initUserSessionAudio);

	}, function() {
		LISTEN.getUserSessionAudio(LISTEN.defaults.location, doingWhat,
				LISTEN.initUserSessionAudio);
	}, function(error) {
		/* do something */
	});
};

/* executes when the DOM is ready */
$(document).ready(function() {
	LISTEN.showWatchaDoing();
	$("#loading_div").hide().ajaxStop(function() {
		$(this).hide();
	});;
	$("#jp_container_1").hide();
	$("#track-lookup").hide();
	$("#track-alert").hide();
});