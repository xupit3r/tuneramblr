// namespace for operations on the map
var SONGMAP = {};

SONGMAP.map = null;
SONGMAP.infoBox = null;

SONGMAP.initMap = function(eln, lat, lng, zoom) {
	var options = {
		zoom : zoom,
		center : new google.maps.LatLng(lat, lng),
		mapTypeId : google.maps.MapTypeId.HYBRID,
		disableDefaultUI : true, // disable all UI features
		disableDoubleClickZoom : true, // disable click to zoom
		scrollwheel : false
	};
	SONGMAP.map = new google.maps.Map(document.getElementById(eln), options);
	SONGMAP._registerMapListeners(SONGMAP.map);
};

// register all necessary map events
SONGMAP._registerMapListeners = function(map) {
	// when I figure out what listeners I want
	// then I will set them up here
};

// add a new location to the map
SONGMAP.addLocation = function(lat, lng, type, name, size, owner, id) {
	var latlng = new google.maps.LatLng(lat, lng);
	var loc = new google.maps.Marker({
		map : SONGMAP.map,
		position : latlng
	});
	// do anything else that we might need
	// with the location variable
};

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
SONGMAP.openinfoBox = function(content, loc) {
	if (SONGMAP.infoBox != null) {
		SONGMAP.infoBox.close();
	}
	SONGMAP.infoBox = new google.maps.InfoWindow({
		content : content
	});
	SONGMAP.infoBox.open(SONGMAP.map, mrkr);
};