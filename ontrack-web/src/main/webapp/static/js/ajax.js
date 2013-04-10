var AJAX = function () {

    /**
     * Performs a PUT request
     * @param config.*          See #call
     */
    function put (config) {
        call($.extend(config, { method: 'PUT' }));
    }

    /**
     * Performs a GET request
     * @param config.*          See #call
     */
    function get (config) {
        call($.extend(config, { method: 'GET' }));
    }

    /**
     * Performs an AJAX call
     * @param config.loading        Loading indicator configuration (see #showLoading)
     * @param config.method         HTTP method (default: 'POST')
     * @param config.url            URL to call (required)
     * @param config.responseType   Type of the response (default: 'json')
     * @param config.successFn      (data -> void) function that is called with the data returned by the AJAX call (required)
     * @param config.errorFn        (jqXHR, textStatus, errorThrown -> void) function that allows for the treatment of the error (default: #defaultAjaxErrorFn)
     */
    function call (config) {
        // Default settings
        var c = $.extend({
            method: 'POST',
            responseType: 'json',
            errorFn: defaultAjaxErrorFn
        }, config);
        // Starting to load
        showLoading(c.loading, true);
        // Performing the call
		$.ajax({
			type: c.method,
			url: c.url,
			dataType: c.responseType,
			success: function (data) {
			    // Loading ending
                showLoading(c.loading, false);
                // Success :)
		  		c.successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
			    // Loading ending
                showLoading(c.loading, false);
                // Error :(
                c.errorFn(jqXHR, textStatus, errorThrown);
			}
		});
    }

    /**
     * Shows a loading indicator on a element. If not element is defined, no loading occurs.
     * @param config.el     jQuery object or selector for the object which will contain the loading indicator (default: undefined)
     * @param config.mode   Way of displaying the loading indicator (default: append):
     *      'append' - appends a loading image at the end of the element
     *      'appendText' - appends a loading image and a text at the end of the element
     * @param show          Boolean that indicates if the the loading must be hidden or shown (default: false)
     */
    function showLoading (config, show) {
        if (config && config.el) {
            var c = $.extend({
                mode: 'append'
            }, config);
            if (c.mode == 'append') {
                if (show) {
                    $(config.el).addClass('disabled');
                    $(config.el).append('&nbsp;<img class="ajax-loader" src="static/images/ajax-loader.gif" />');
                } else {
                    $(config.el).removeClass('disabled');
                    $(config.el).find('.ajax-loader').remove();
                }
            } else if (c.mode == 'appendText') {
                if (show) {
                    $(config.el).addClass('disabled');
                    $(config.el).append('&nbsp;<span class="ajax-loader"><img src="static/images/ajax-loader.gif" /> {0}</span>'.format(loc('general.loading')));
                } else {
                    $(config.el).removeClass('disabled');
                    $(config.el).find('.ajax-loader').remove();
                }
            } else {
                throw 'ShowLoading: mode={0} not known'.format(c.mode);
            }
        }
    }

    /**
     * Default error function for an AJAX error handling
     */
    function defaultAjaxErrorFn (jqXHR, textStatus, errorThrown) {
        Application.displayError(getAjaxError(jqXHR, textStatus, errorThrown));
    }

    /**
     * Generates a AJAX error handler that just needs to deal
     * with a text message
     * @param errorMessageFn Function that takes the error message as a parameter
     * @return An AJAX error handler
     */
    function simpleAjaxErrorFn (errorMessageFn) {
        return function (jqXHR, textStatus, errorThrown) {
            var message = getAjaxError (jqXHR, textStatus, errorThrown);
            errorMessageFn(message);
        }
    }

    /**
     * Gets the AJAX error message
     */
	function getAjaxError (jqXHR, textStatus, errorThrown) {
		return '[{0}] {1}'.format(jqXHR.status, jqXHR.statusText);
	}

	/**
	 * Generates an error message handler that displays the message
	 * into an existing element.
	 * @param el Element that contains the error message
	 * @return An error message handler
	 */
	function elementErrorMessageFn (el) {
        return function (message) {
            if (message == null) {
                $(el).hide();
            } else {
                $(el).text(message);
                $(el).show();
            }
        }
	}

    return {
        // AJAX calls
        put: put,
        get: get,
        call: call,
        // Loading
        showLoading: showLoading,
        // Error management
        defaultAjaxErrorFn: defaultAjaxErrorFn,
        simpleAjaxErrorFn: simpleAjaxErrorFn,
        getAjaxError: getAjaxError,
        elementErrorMessageFn: elementErrorMessageFn
    };

} ();