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
	
	// setup the sidebar UI
	UI.metaside.setup();
	
	
	// register handlers
	
	// this will always be called 
	// on initial page load
	HANDLERS.functions.setup();

	// setup the sidebar handlers
	HANDLERS.metaside.setup();
});