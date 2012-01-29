// namespace for operations on the map
var SONGMAP = {};

SONGMAP.defaults = {};
SONGMAP.defaults.zoom = 9;

SONGMAP.map = null;
SONGMAP.infoBox = null;

SONGMAP.initMap = function(elm, zm) {
	var options = {
		zoom: zm,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		disableDefaultUI : true,
		scrollwheel : false
	};
	SONGMAP.map = new google.maps.Map(document.getElementById(elm), options);
	SONGMAP._registerMapListeners(SONGMAP.map);
};

// center the map at some location
SONGMAP.center = function(lat, lng) {
	SONGMAP.map.setCenter(new google.maps.LatLng(lat, lng));
};

// register all necessary map events
SONGMAP._registerMapListeners = function(map) {
	// when I figure out what listeners I want
	// then I will set them up here
};

// add a new song location to the map
// returns the song containing a handle to the 
// map marker
SONGMAP.addSong = function(songData) {
	var latlng = new google.maps.LatLng(songData.lat, songData.lng);
	var loc = new google.maps.Marker({
		map : SONGMAP.map,
		position : latlng
	});
	
	// setup a click listener for the marker
	var mcf = UI.buildInfoBox(SONGMAP, loc, songData);
	google.maps.event.addListener(loc, "click", mcf);

	// add the marker to the location data
	// and store that data in the SONGMAP
	// model
	songData.marker = loc;
	return songData;
};

SONGMAP.markerClick

// hide a location
SONGMAP.hideLocation = function(loc) {
	loc.setVisible(false);
};

// make visible a hidden location
SONGMAP.showLocation = function(loc) {
	loc.setVisible(true);
};

// move the location to the center of the map
SONGMAP.scrollToLocation = function(lat, lng) {
	SONGMAP.map.panTo(new google.maps.LatLng(lat, lng));
};

// close any open infoBox windows and
// remove the location
SONGMAP.removeLocation = function(loc) {
	// if there is an infoBox opened
	// for this location, we must close
	// it before removing the location
	if (SONGMAP.infoBox != null) {
		SONGMAP.infoBox.close();
	}
	loc.setMap(null);
};

SONGMAP.closeInfoBox = function() {
	if (SONGMAP.infoBox != null) {
		SONGMAP.infoBox.close();
	}
};

// opens an infoBox over a location on
// the map, and fills the infoBox with
// the provided content
SONGMAP.openinfoBox = function(loc, content) {
	if (SONGMAP.infoBox != null) {
		SONGMAP.infoBox.close();
	}
	SONGMAP.infoBox = new google.maps.InfoWindow({
		content : content
	});
	SONGMAP.infoBox.open(SONGMAP.map, loc);
};