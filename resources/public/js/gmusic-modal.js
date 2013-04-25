/* logic for the google music modal dialog.  most of this will just be immediately evaluated logic. */

/**
 * Handle the form submission
 */
$("#gmusic-modal-form").submit(function (ev) {
    // TODO: maybe add some simple form validation here
    // TODO: add some indication that form has been submitted (spinning gif or something)
    $.ajax({
	type : "POST",
	url : "/user/gmusic/login/modal/submit",
	dataType : "json",
	data : $(this).serialize(),
	success : LISTEN.handlePrepareSession
    });
    return false;
});
