var HANDLERS = {};

// handlers for each section
// of the site
HANDLERS.functions = {};
HANDLERS.meta = {};
HANDLERS.playlist = {};

/* function buttons (top) */
HANDLERS.functions.setup = function() {
	$("#fn_acct_create").click(function () {
		// we are going to go to a 
		// separate page for this, so
		// that we can do all the necessary 
		// form validation and re-directs
	});
	
	$("#fn_login").click(function () {
		$.ajax({type: "GET",
			url: "/user/login/page",
			success: function(content) {
		        //create and add the background div
		        $('<div/>').addClass('modal_back').appendTo('body').show();
		        //add modal window
		        $('<div/>').html(content).addClass('modal').appendTo('body');	
			}});
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
