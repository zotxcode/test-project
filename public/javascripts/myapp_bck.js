var MyApp = function () {
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
			show: function(){
				if(this.content == null){
					this.create();
				}
				else{
					$(this.content).show();
				}
			},
			hide: function(){
				$(this.content).hide();
				$(this.content).find('.ajax-progress').text('').hide();
			},
			showProgress: function(percent){
				$(this.content).find('.ajax-progress').text(percent + '%').show();
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