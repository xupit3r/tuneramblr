var UI = {};

// UI for each section
// of the site
UI.functions = {};
UI.metaside = {};
UI.playlist = {};

UI.functions.setup = function() {
	$("#fn_login").button({
		icons: {
			primary: "ui-icon-locked"
		}
	});
	
	$("#fn_settings").button({
		icons: {
			primary: "ui-icon-gear"
		}
	});
	
	$("#fn_about").button({
		icons: {
			primary: "ui-icon-gear"
		}
	});
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