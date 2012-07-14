/* 
 * This file will contain functionality for handling images 
 * This includes: requesting images, organizing images, and sending responses
 */


/* I am going to be using jquery.masonry and the imagesLoaded plugin 
 * to handle layout of the images.
 */

// image namespace
var IMG = {};

// image URL
IMG.url = APP.img.url;

// build the image grid
// NOTE: this is not yet hooked up
IMG.buildImgGrid = function(tracks) {

	var content = "";
	for ( var el in tracks) {
		// if this song has an image, display it
		var track = tracks[el];
		if (track.img) {
			content += "<img class='img_grid_item' src='" + IMG.url + track.img
					+ "'/>";
		}
	}

	var grid_container = $("#img_grid");

	alert(content);

	grid_container.append(content);

	grid_container.masonry({
		itemSelector: '.img_grid_item',
	});
};