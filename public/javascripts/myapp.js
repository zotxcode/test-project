var MyApp = function () {
    // Creating modal dialog's DOM
    var $dialog = $(
                        '<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-hidden="true" style="padding-top:15%; overflow-y:visible;">' +
                        '<div class="modal-dialog modal-m">' +
                        '<div class="modal-content">' +
                            '<div class="modal-header"><h3 style="margin:0;"></h3></div>' +
                            '<div class="modal-body">' +
                                '<div class="progress progress-striped active" style="margin-bottom:0;"><div class="progress-bar" style="width: 100%"></div></div>' +
                            '</div>' +
                        '</div></div></div>');
    return {
        loadingOverlay : {
			el: 'body',
			content : null,
			create: function(){
				this.content = $('<div class="overlay"></div>').appendTo('body').css({
					position: 'fixed',
					top: $(this.el).offset()['left'],
					left: $(this.el).offset()['left'],
					width: $(window).width() + "px",
					height: $(window).height() + "px"
				});

				$('<div class="loader"><span class="ajax-progress" style="display:none"></span></div>').appendTo('.overlay');
			},
			show: function (message, options) {
                // Assigning defaults
                if (typeof options === 'undefined') {
                    options = {};
                }
                if (typeof message === 'undefined') {
                    message = 'Loading';
                }
                var settings = $.extend({
                    dialogSize: 'm',
                    progressType: '',
                    onHide: null // This callback runs after the dialog was hidden
                }, options);

                // Configuring dialog
                $dialog.find('.modal-dialog').attr('class', 'modal-dialog').addClass('modal-' + settings.dialogSize);
                $dialog.find('.progress-bar').attr('class', 'progress-bar');
                if (settings.progressType) {
                    $dialog.find('.progress-bar').addClass('progress-bar-' + settings.progressType);
                }
                $dialog.find('h3').text(message);
                // Adding callbacks
                if (typeof settings.onHide === 'function') {
                    $dialog.off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
                        settings.onHide.call($dialog);
                    });
                }
                // Opening dialog
                $dialog.modal();
            },
            /**
             * Closes dialog
             */
            hide: function () {
                $dialog.modal('hide');
            }
		} /* END loadingOverlay */
    };

}();

toastr.options = {
  "closeButton": true,
  "newestOnTop": true,
  "progressBar": true,
  "positionClass": "toast-bottom-right",
  "preventDuplicates": false,
  "showDuration": "300",
  "hideDuration": "1000",
  "timeOut": "5000",
  "extendedTimeOut": "1000",
  "showEasing": "swing",
  "hideEasing": "linear",
  "showMethod": "fadeIn",
  "hideMethod": "fadeOut"
}