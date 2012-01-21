// namespace for operations on the map
var SONGMAP = {};

SONGMAP.map = null;
SONGMAP.infoBox = null;
SONGMAP.locations = [];

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

// add a new location to the map
SONGMAP.addLocation = function(locData) {
	var latlng = new google.maps.LatLng(locData.lat, locData.lng);
	var loc = new google.maps.Marker({
		map : SONGMAP.map,
		position : latlng
	});
	
	var id = SONGMAP.locations.length;
	var mcf = function () {
		var content = "<div class='song_mrkr'>";
		content += "<p class='mrkr_artist'>Artist: "+locData.artist+"</p>";
		content += "<p class='mrkr_title'>Title: "+locData.title+"</p>";
		content += "<p class='mrkr_album'>Album: "+locData.album+"</p>";
		content += "</div>";
		SONGMAP.openinfoBox(loc, content);
	};
	google.maps.event.addListener(loc, "click", mcf);

	// add the marker to the location data
	// and store that data in the SONGMAP
	// model
	locData.marker = loc;
	SONGMAP.locations[id] = locData;
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