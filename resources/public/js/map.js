// namespace for operations on the map
var SONGMAP = {};

SONGMAP.defaults = {};
SONGMAP.defaults.zoom = 13;
SONGMAP.defaults.maxZoom = 16;
SONGMAP.defaults.center = {lat: 40.37858996679397,
						   lng: -80.04364013671875};
/* FYI: that is a Lat/Lng from the Pittsburgh area! */

SONGMAP.tiles = {};

// holds the Open Street Map tile URLS
SONGMAP.tiles.url = {};
SONGMAP.tiles.url.osm = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";

SONGMAP.map = null;
SONGMAP.zoom = SONGMAP.defaults.zoom;

// initialize the map
SONGMAP.initMap = function(elm, zm, center) {
	SONGMAP.zoom = zm;
	SONGMAP.map = new L.Map(elm, {
		center: new L.LatLng(center.lat, center.lng), 
		zoom: zm
	});
	
	// add the tile layer to the map (seems pretty important to me! :) ).
	SONGMAP.map.addLayer(new L.TileLayer(SONGMAP.tiles.url.osm, {
		maxZoom : SONGMAP.defaults.maxZoom
	}));

	// disable the scroll wheel Zoom (i find it annoying)
	SONGMAP.map.scrollWheelZoom.disable()

	// register the map listeners
	SONGMAP._registerMapListeners();

};

// register all necessary map events
SONGMAP._registerMapListeners = function() {
	// drag and zoom listeners (to load new songs)
	SONGMAP.map.on('dragend', HANDLERS.map.dragend);
	SONGMAP.map.on('zoomend', HANDLERS.map.zoomend);
};

// center the map at some location
SONGMAP.center = function(lat, lng) {
	SONGMAP.map.setView(new L.LatLng(lat, lng), SONGMAP.zoom, true);
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

// returns information related to the center point of the map this includes:
// center point latitude and logitude and map zoom
SONGMAP.getMapCenterInfo = function() {
	// this is a leaflet latlng object
	var center = SONGMAP.map.getCenter();

	// gives us the current zoom factor for the map
	var zoom = SONGMAP.map.getZoom();

	return {
		lat : center.lat,
		lng : center.lng,
		zoom : zoom
	};
};

// pans the map to some location specified by the latitude and longitude of the
// location
SONGMAP.panTo = function(lat, lng) {
	SONGMAP.map.panTo(new L.LatLng(lat, lng));
};

// removes a location from the map
SONGMAP.removeLocation = function(loc) {
	// loc needs to be an L.Marker obj
	SONGMAP.map.removeLayer(loc);
};