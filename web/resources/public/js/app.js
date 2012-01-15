// general application stuff
var APP = {};

// executes when the DOM is ready
$(document).ready(function() {
	
	// set the user's current 
	// location on the map
	APP.setUserLocation();
	
	// initialize the map
	SONGMAP.initMap("map", 12);
	
	// get songs
	APP.getSongs();
	
	// get the metadata
	APP.getMetadata(57);
	
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

/* handle songs */

APP.getSongs = function() {
	// we will use the user's current 
	// location as means of determing 
	// which songs to load...
	if (APP.userLocation) {
		$.ajax({
			type: "POST",
			url: "/songs/get",
			dataType: "json",
			data: {lat: APP.userLocation.coords.latitude,
				   lng: APP.userLocation.coords.longitude},
			success: APP.placeSongs});
	} else {
		setTimeout(APP.getSongs,1000);
	}
	
};

APP.placeSongs = function(songs) {
	// iterate over the songs and
	// add them to the map
	for (var idx in songs) {
		var song = songs[idx];
		SONGMAP.addLocation(song.lat, song.lng);
	}
};

/* handle metadata */

APP.getMetadata = function(id) {
	// id will be a valid user id
	// or some arbitrary id if no 
	// user is logged in
	$.ajax({type: "POST",
			url: "/metadata/get",
			dataType: "json",
			data: {id: id},
			success: APP.fillMetadata});
	
};

APP.fillMetadata = function(metadata) {
	// iterate over the metadata and 
	// add it to the panel
	if (metadata.length > 0) {
		var frag = document.createDocumentFragment();
		var fdiv = frag.appendChild(document.createElement("div"));
		fdiv.id = "meta-items";
		for (var idx in metadata) {
			var metaItem = metadata[idx];
			var tmpInp = document.createElement("input");
			var tmpLbl = document.createElement("label");
			var lblTxt = document.createTextNode(metaItem);
			tmpInp.type = "checkbox";
			tmpInp.name = metaItem;
			tmpInp.value = metaItem;
			tmpLbl.appendChild(tmpInp);
			tmpLbl.appendChild(lblTxt);
			fdiv.appendChild(tmpLbl);
		}
		$("#metaside").append(frag);
	}
};