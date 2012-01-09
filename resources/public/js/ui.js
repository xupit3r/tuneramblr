var UI = {};

// UI for each section
// of the site
UI.functions = {};
UI.metaside = {};
UI.tags = {};
UI.playlists = {};
UI.songs = {};
UI.locations = {};

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
	// the elements that make up this 
	// section are going to dictate
	// what goes in here...
	// not sure yet
	
	$("#metaside").accordion();
	
	// should probably setup the content
	// within the accordion as well
	// e.g. handlers for links in the
	// content

};

UI.metaside.reload = function() {

};