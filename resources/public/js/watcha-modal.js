/* logic for the watcha doing dialog. */
$("#watcha-modal-form").submit(function (ev) {
    // TODO: maybe add some simple validation that it is not empty
    LISTEN.prepareSession($("#w-doing").val(), $("#pmode").is(":checked"));
    LISTEN.hideWatchaDoing();
    return false;
});
