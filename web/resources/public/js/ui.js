var UI = {};

// UI for each section
// of the site
UI.functions = {};
UI.metaside = {};
UI.playlist = {};

UI.functions.setup = function() {
	// setup function menu UI
};

UI.functions.reload = function() {

};

UI.metaside.setup = function() {
	// setup the side pane for 
	// all of the yummy meta-data
};

UI.metaside.reload = function() {

};

UI.playlist.setup = function() {
	// setup the playlist
};

UI.playlist.reload = function() {

};

// UI Event Handler Builders //

UI.buildInfoBox = function (map, location, songData) {
	// build up the content to be displayed in the info box
	var content = "<div class='song_mrkr'>";
	content += "<p class='mrkr_artist'>Artist: "+songData.artist+"</p>";
	content += "<p class='mrkr_title'>Title: "+songData.title+"</p>";
	content += "<p class='mrkr_album'>Album: "+songData.album+"</p>";
	content += "</div>";
	
	// return a function to handle the opening of the box
	return (function () { map.openinfoBox(location, content);});
}