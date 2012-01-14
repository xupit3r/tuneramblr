// general application stuff
var APP = {};

// executes when the DOM is ready
$(document).ready(function() {
	
	// set the user's current 
	// location on the map
	APP.setUserLocation();
	
	// initialize the map
	SONGMAP.initMap("map", 12);
	
	// setup the function menu UI
	UI.functions.setup();
	
	// setup the sidebar UI
	UI.metaside.setup();

	// setup the sidebar UI
	UI.playlist.setup();
	
	// register handlers
	
	// this will always be called 
	// on initial page load
	HANDLERS.functions.setup();

	// setup the sidebar handlers
	HANDLERS.metaside.setup();
	
	// setup the playlist handlers
	HANDLERS.playlist.setup();
});

APP.userLocation = null;

APP.setUserLocation = function() {
	
	// does this browser expose a
	// geolocation API?
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(
				function(position) {
					if(!position)alert("null position");
					
					// I read that FF sometimes
					// calls this multiple times
					// let's avoid that, if we
					// have already set a location, 
					// just return
					if(APP.userLocation) {
						return;
					}
					
					// record the user's location
					APP.userLocation = position;
					
					// center the map at the user's
					// current location
					SONGMAP.center(position.coords.latitude,position.coords.longitude);
				},
				function(error) {
					console.log("Oh NOES! No location!")
				}
		);
	} else {
		// if not, maybe I can make 
		// a request to the server...
		// not sure what I want to do 
		// here...
	}
};