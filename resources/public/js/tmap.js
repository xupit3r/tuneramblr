var TMAP = {};

TMAP.defaults = {};
TMAP.defaults.zoom = 13;
TMAP.defaults.maxZoom = 16;
TMAP.defaults.center = {
	lat : 40.37858996679397,
	lng : -80.04364013671875
};

TMAP.tiles = {};
TMAP.tiles.url = {};
TMAP.tiles.url.osm = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";

TMAP.map = null;
TMAP.zoom = TMAP.defaults.zoom;

/* initialize the map */
TMAP.initMap = function(elm, zm, center) {
	TMAP.zoom = zm;
	TMAP.map = new L.Map(elm, {
		center : new L.LatLng(center.lat, center.lng),
		zoom : zm
	});
	TMAP.map.addLayer(new L.TileLayer(TMAP.tiles.url.osm, {
		maxZoom : TMAP.defaults.maxZoom
	}));
	TMAP.map.scrollWheelZoom.disable()
	TMAP._registerMapListeners();

};

/* register all necessary map events */
TMAP._registerMapListeners = function() {
	TMAP.map.on('dragend', HANDLERS.map.dragend);
	TMAP.map.on('zoomend', HANDLERS.map.zoomend);
};

/* center the map at some location */
TMAP.center = function(lat, lng) {
	TMAP.map.setView(new L.LatLng(lat, lng), TMAP.zoom, true);
};

/* add a song to the map */
TMAP.addSong = function(songData) {
	var latlng = new L.LatLng(songData.lat, songData.lng);
	var marker = new L.Marker(latlng);
	var popup = TMAP.buildSongMarkerPopupContent(marker, songData);

	marker.bindPopup(popup);
	TMAP.map.addLayer(marker);

	songData.marker = marker;
	return songData;
};

/* builds the marker popup UI content */
TMAP.buildSongMarkerPopupContent = function(location, songData) {
	var content = "<div class='song_mrkr'>";
	
	if (songData.img) {
		content += "<img src='" + APP.img.url + songData.img + "'/>"
	}
	
	content += "<p class='mrkr_title'>" + songData.title + "</p>";
	content += "<p class='mrkr_artist'>" + songData.artist + "</p>";
	content += "</div>";
	return content;
};

/* gets the center of the map */
TMAP.getMapCenterInfo = function() {
	var center = TMAP.map.getCenter();
	var zoom = TMAP.map.getZoom();

	return {
		lat : center.lat,
		lng : center.lng,
		zoom : zoom
	};
};

/* pans to a latlng location on the map */
TMAP.panTo = function(latlng) {
	TMAP.map.panTo(latlng);
};

/* removes a location from the map */
TMAP.removeLocation = function(loc) {
	TMAP.map.removeLayer(loc);
};

TMAP.initMap("tracks_map", TMAP.defaults.zoom, TMAP.defaults.center);

TMAP.loadTracks = function(resp) {
	
};