var HANDLERS = {};

// handlers for each section
// of the site
HANDLERS.functions = {};
HANDLERS.meta = {};
HANDLERS.map = {};

/* function buttons (top) */
HANDLERS.functions.setup = function() {
  // setup any handlers here...
};

HANDLERS.functions.reload = function() {
	// probably need this when a user logs in
};


/* metadata */

// space for metadata item handlers
HANDLERS.meta.item = {};


// the onclick handler for metadata items 
HANDLERS.meta.item.click = function (metaProp) {
	var metaClick = function (ev) {
		if (APP.select.meta[metaProp]) {
			// remove this property from the model
			delete APP.select.meta[metaProp];
			
			// update the styling
			this.style.color = "black";
			
		} else {
			// add this property to the model
			APP.select.meta[metaProp] = metaProp;
			this.style.color = "red";
		}
	};
	
	return metaClick;
};


/* map */


// handle map dragging
HANDLERS.map.dragend = function(e) {
	// FIXME: we only want to make this call when necessary
	APP.getSongs();
};


// handle map zooming
HANDLERS.map.zoomend = function(e) {
	// FIXME: we only want to make this call when necessary
	APP.getSongs();
};
