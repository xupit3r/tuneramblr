var HANDLERS = {};

// handlers for each section
// of the site
HANDLERS.functions = {};
HANDLERS.meta = {};
HANDLERS.playlist = {};

/* function buttons (top) */
HANDLERS.functions.setup = function() {
	$("#fn_login").click(function () {
		// present login form
	});
	
	$("#fn_settings").click(function () {
		// bring up settings page
	});

};

HANDLERS.functions.reload = function() {
	// probably need this when a user logs in
};


/* metadata (sidepane) */

// space for metadata item handlers
HANDLERS.meta.item = {};


// the onclick handler for metadata items 
HANDLERS.meta.item.click = function (metaProp) {
	var metaClick = function (ev) {
		alert(metaProp);
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