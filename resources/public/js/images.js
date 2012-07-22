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
	var imgEls = [];
	var idx = 0;
	for ( var el in tracks) {
		// if this song has an image, display it
		var track = tracks[el];
		if (track.img) {
			imgEls[idx] = document.createElement("img");
			$(imgEls[idx]).addClass("img_grid_item")
					      .attr("src", (IMG.url + track.img))
					      .attr("id", track.img)
					      .click(HANDLERS.meta.images.click(track.img));
			idx++;
		}
	}

	// insert the images into the DOM
	var grid_container = $("#img_grid");
	grid_container.append(imgEls);
	grid_container.masonry({
		itemSelector : '.img_grid_item',
	});
};