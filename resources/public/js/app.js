var APP = {};

APP.img = {};
APP.img.url = "/image/ugen/";

APP.loadHomeContent = function(contentUrl) {
	$.ajax({
		type : "GET",
		url : contentUrl,
		dataType : "html",
		success : function (content) {
			$("#home_content").html(content);
		}
	});
};

APP.clearNavSelection = function () {
	$("#nav-stack li").removeClass("active");
};

APP.setupSideLinks = function() {
	$("#user-timeline a").click(function (){
		APP.clearNavSelection();
		$(this).parent().addClass("active");
		APP.loadHomeContent("/content/timeline");
	});
	
	$("#user-tracks a").click(function (){
		APP.clearNavSelection();
		$(this).parent().addClass("active");
		APP.loadHomeContent("/content/ttable");
	});
	
	$("#user-map a").click(function (){
		APP.clearNavSelection();
		$(this).parent().addClass("active");
		APP.loadHomeContent("/content/tmap");
	});
	
	$("#user-settings a").click(function (){
		APP.clearNavSelection();
		$(this).parent().addClass("active");
		APP.loadHomeContent("/content/usettings");
	});
};

/* executes when the DOM is ready */
$(document).ready(function() {
	/* is this a logged in user? */
	if (TUNERAMBLR.isUserLoggedIn()) {
		APP.loadHomeContent("/content/timeline");
		APP.setupSideLinks();
	} else {
		$('.carousel').carousel();
	}
});