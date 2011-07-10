// general application stuff
var APP = {};

// executes when the DOM is ready
$(document).ready(function() {
	// setup the map
	SONGMAP.initMap("map", 40.00, 79.00, 6);
	
	
	// setup UI
	
	// this will always be called 
	// on initial page load
	UI.functions.setup();
	
	// this should probably only 
	// be called when this data 
	// is loaded, which might not 
	// be at this time
	UI.playlists.setup();
	
	// this should probably only 
	// be called when this data 
	// is loaded, which might not 
	// be at this time
	UI.songs.setup();
	
	
	// register handlers
	
	// this will always be called 
	// on initial page load
	HANDLERS.functions.setup();
	
	// this should probably only 
	// be called when this data 
	// is loaded, which might not 
	// be at this time
	HANDLERS.playlists.setup();
	
	// this should probably only 
	// be called when this data 
	// is loaded, which might not 
	// be at this time
	HANDLERS.songs.setup();
});