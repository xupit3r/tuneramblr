var APP = {};

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
};

/* executes when the DOM is ready */
$(document).ready(function() {
	/* is this a logged in user? */
	if (TUNERAMBLR.isUserLoggedIn()) {
		APP.loadHomeContent("/content/ttable");
		APP.setupSideLinks();
	} else {
		$('.carousel').carousel();
	}
});