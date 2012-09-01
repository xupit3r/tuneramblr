/* 
 * This file will contain functionality for handling images 
 * This includes: requesting images, organizing images, and sending responses
 */

/* image namespace */
var IMG = {};

/* image URL */
IMG.url = "/image/ugen/";

/**
 * Builds an image gride on the page given a set of images.
 * 
 * @param imgs
 *            the set of images to display in the grid
 */
IMG.buildImgGrid = function(imgs) {

	var content = "";
	var imgEls = [];
	var idx = 0;
	for ( var el in imgs) {
		var img = imgs[el];
		if (img) {
			imgEls[idx] = document.createElement("img");
			$(imgEls[idx]).addClass("img_grid_item").attr("src",
					(IMG.url + img)).attr("id", img).click(
					HANDLERS.meta.images.click(img));
			idx++;
		}
	}

	/* insert the images into the DOM */
	var grid_container = $("#img_grid");
	grid_container.append(imgEls);
};