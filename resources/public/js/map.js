// namespace for operations on the map
var SONGMAP = {};

SONGMAP.defaults = {};
SONGMAP.defaults.zoom = 9;
SONGMAP.defaults.maxZoom = 16;

SONGMAP.tiles = {};

// holds the Open Street Map tile URLS
SONGMAP.tiles.url = {};
SONGMAP.tiles.url.osm = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";

SONGMAP.map = null;
SONGMAP.zoom = SONGMAP.defaults.zoom;

// initialize the map
SONGMAP.initMap = function(elm, zm) {
	SONGMAP.zoom = zm;
	SONGMAP.map = new L.Map(elm, {
		zoom : zm
	});
	SONGMAP.map.addLayer(new L.TileLayer(SONGMAP.tiles.url.osm, {
		maxZoom : SONGMAP.defaults.maxZoom
	}));
	
	// disable the scroll wheel Zoom (i find it annoying)
	SONGMAP.map.scrollWheelZoom.disable()
};

// center the map at some location
SONGMAP.center = function(lat, lng) {
	SONGMAP.map.setView(new L.LatLng(lat, lng), SONGMAP.zoom, true);
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
	var latlng = new L.LatLng(songData.lat, songData.lng);
	var marker = new L.Marker(latlng);
	var popup = UI.buildSongMarkerPopupContent(marker, songData);
	
	// add the popup to the marker and add the marker to the map
	marker.bindPopup(popup);
	SONGMAP.map.addLayer(marker);

	// add the marker to the location data
	// and store that data in the SONGMAP
	// model
	songData.marker = marker;
	return songData;
};

// hide a location
SONGMAP.hideLocation = function(loc) {
	//TODO: implement for OSM
};

// make visible a hidden location
SONGMAP.showLocation = function(loc) {
	//TODO: implement for OSM
};

// move the location to the center of the map
SONGMAP.scrollToLocation = function(lat, lng) {
	//TODO: implement for OSM
};

// close any open infoBox windows and
// remove the location
SONGMAP.removeLocation = function(loc) {
	//TODO: implement for OSM
};