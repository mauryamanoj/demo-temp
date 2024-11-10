(function($) {
    var registry = $(window).adaptTo("foundation-registry");
    var REGEX_URI_MODEL_SELECTOR = "regex.validation.key.space";
    registry.register("foundation.validation.validator", {
        selector: "[data-validation='" + REGEX_URI_MODEL_SELECTOR + "']",
        validate: function(el) {
            if (el.value !== "") {
                var regex_pattern = /^\S*$/;
                var error_message = "This field must not  contain white spaces, please remove the white space";
                var result = el.value.match(regex_pattern);

                if (result === null) {
                    return error_message;
                }
            }
        }
    });



        var REGEX_URI_MODEL_SELECTOR = "custom.validation.entertainer.polygons";
        registry.register("foundation.validation.validator", {
            selector: "[data-validation='" + REGEX_URI_MODEL_SELECTOR + "']",
            validate: function(el) {
                var locationId= $(el).closest('.cq-Dialog').find("[name='./locationId']").val();
                if(locationId != '')
                {
                    if (el.items.length == 0) {
                        var error_message = "If locationId selected, polygons should be set";
                        return error_message;

                    }
                }
            }
        });

       var REGEX_FLOAT_SELECTOR = "regex.validation.float";
        registry.register("foundation.validation.validator", {
            selector: "[data-validation='" + REGEX_FLOAT_SELECTOR + "']",
            validate: function(el) {
                if (el.value !== "") {
                    var regex_pattern =  /^([0-9]*[.])?[0-9]+$/
                    var error_message = "This field allows float only separated with a dot.";
                    var result = el.value.match(regex_pattern);
                    if (result === null) {
                        return error_message;
                    }
                }
            }
        });




})(jQuery);
