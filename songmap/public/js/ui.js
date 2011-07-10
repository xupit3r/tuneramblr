var UI = {};

// UI for each section
// of the site
UI.functions = {};
UI.metaside = {};
UI.tag_cloud = {};

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
};

UI.functions.reload = function() {

};

UI.playlists.setup = function() {
	// the elements that make up this 
	// section are going to dictate
	// what goes in here...
	// not sure yet
	
	$("#playlists").accordion();
	
	// should probably setup the content
	// within the accordion as well
	// e.g. handlers for links in the
	// content

};

UI.playlists.reload = function() {

};

UI.songs.setup = function() {
	// the elements that make up this 
	// section are going to dictate
	// what goes in here...
	// not sure yet
	
	// this seems likely to be links
	// only, but not sure what I am going 
	// to do with them

};

UI.songs.reload = function() {

};