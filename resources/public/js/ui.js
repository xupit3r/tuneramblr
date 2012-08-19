var UI = {};

// UI for each section
// of the site
UI.functions = {};
UI.meta = {};
UI.playlist = {};

UI.functions.setup = function() {
	// setup function menu UI
};

UI.functions.reload = function() {

};

UI.meta.setup = function() {
	// setup the side pane for
	// all of the yummy meta-data
};

UI.meta.reload = function() {

};

UI.playlist.setup = function() {
	// setup the playlist
};

UI.playlist.reload = function() {

};

// General UI elements //

// builds the marker popup UI content
UI.buildSongMarkerPopupContent = function (location, songData) {
	// build up the content to be displayed in the info box
	var content = "<div class='song_mrkr'>";
	
	// if this song has an image, display it
	if (songData.img) {
		content += "<img src='"+APP.img.url+songData.img+"'/>"
	}
	
	// setup the other meta data
	content += "<p class='mrkr_title'>"+songData.title+"</p>";
	content += "<p class='mrkr_artist'>"+songData.artist+"</p>";
	content += "</div>";
	return content;
};

// UI Event Handler Builders //
