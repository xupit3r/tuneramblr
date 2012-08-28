var APP = {};

/* model storage for stuff about songs */
APP.session = {};

/* the tracks table */
APP.tracks = {};

/* store the user location for later use */
APP.userLocation = {};

/* app defaults */
APP.defaults = {};
APP.defaults.location = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

/* executes when the DOM is ready */
$(document).ready(function() {

	/* is this a logged in user? */
	if (APP.isUserLoggedIn()) {
		/* get the user's tracks */
		APP.setupUserSessionTracks();
		
		/* get the user's location and metadata about that location */
		APP.util.getUserLocation(function(position) {
			/*
			 * I read that FF sometimes calls this multiple times let's avoid
			 * that, if we have already set a location, just return
			 */
			if (APP.userLocation.isSet) {
				return;
			}

			/* record the user's location */
			APP.userLocation.lat = position.coords.latitude;
			APP.userLocation.lng = position.coords.longitude;
			APP.userLocation.isSet = true;

			/* now, request the songs */
			APP.setupUserSessionMeta({
				lat : position.coords.latitude,
				lng : position.coords.longitude
			});

		}, function() {
			APP.setupUserSession(APP.defaults.location);
		}, function(error) {
			/* do something */
		});
	} else {
		$('.carousel').carousel();
	}
});

APP.setupUserSessionMeta = function(locinfo) {
	var data = {};
	if (locinfo) {
		data = {
			lat : locinfo.lat,
			lng : locinfo.lng,
		}
	}

	$.ajax({
		type : "POST",
		url : "/user/base/meta",
		dataType : "json",
		data : data,
		success : APP.handleUserSessionMeta
	});
};

APP.handleUserSessionMeta = function(resp) {
	APP.session.weather = resp.weather;
	APP.session.address = resp.address;
	APP.session.time = resp.time;
};

APP.setupUserSessionTracks = function() {
	$.ajax({
		type : "POST",
		url : "/user/base/tracks",
		dataType : "json",
		success : APP.handleUserSessionTracks
	});
};

APP.handleUserSessionTracks = function(resp) {
	/* setup the table */
	APP.initTable(resp.songs);

	/* store the rest of the track info */
	APP.session.freqs = resp.freqs;
	APP.session.imgs = resp.imgs;
};

APP.buildAutogenSection = function(auto) {
	/* build the new elements */
	var timeEl = document.createElement("p");
	var weatherEl = document.createElement("p");
	var addressEl = document.createElement("p");

	/* build the text nodes */
	var timeTxt = document.createTextNode(auto.time);
	var weatherTxt = document.createTextNode(auto.weather);
	var addressTxt = document.createTextNode(auto.address);

	/* append the text nodes to the elements */
	weatherEl.appendChild(weatherTxt);
	addressEl.appendChild(addressTxt);
	timeEl.appendChild(timeTxt)

	/* shove those elements into the DOM */
	$("#autogen").append([ timeEl, weatherEl, addressEl ]);
};

APP.initTable = function(tracks) {
	/* only carry out these actions if we have songs to display */
	if (!APP.util.isEmpty(tracks)) {
		/* get a handle on the tracks table */
		var tracksTab = $("#tracks_table");
		tracksTab.append(APP.buildTracksHead());
		tracksTab.append(APP.buildTracksBody(tracks));
		tracksTab.find("tbody tr").click(function(e) {
			APP.tableRowClick(this);
		});

	}
};

APP.buildTracksHead = function() {
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

APP.buildTracksBody = function(tracks) {
	var tbody = document.createElement("tbody");
	for ( var i = 0; i < tracks.length; i++) {
		var id = "row_" + i;
		var track = tracks[i];
		tbody.appendChild(APP.buildTrackRow(track, id));
		APP.tracks[id] = track;
	}
	return tbody;
};

APP.buildTrackRow = function(track, id) {

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

APP.tableRowClick = function(el) {
	var trackInfo = APP.tracks[el.id];
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

/* utility methods */
APP.util = {};

APP.util.isEmpty = function(map) {
	for ( var key in map) {
		if (map.hasOwnProperty(key)) {
			return false;
		}
	}
	return true;
};

APP.util.arrToMap = function(arr) {
	var map = {};
	for ( var i = 0; i < arr.length; i++) {
		map[arr[i]] = arr[i];
	}
	return map;
};

APP.util.getUserLocation = function(hLocation, hNoApi, hError) {
	/* does this browser expose a geolocation API? */
	if (navigator.geolocation) {
		/* call the browser's location API */
		navigator.geolocation.getCurrentPosition(hLocation, hError);
	} else {
		/* we have no API, we shall carry on! */
		hNoApi();
	}
};

APP.isUserLoggedIn = function() {
	return $("#stats").length > 0;
};

/* About */
APP.about = function() {
	$.ajax({
		type : "GET",
		url : "/about",
		dataType : "json",
		success : APP.displayAbout
	});
};

APP.displayAbout = function(response) {
	$.prompt(response.about);
};
