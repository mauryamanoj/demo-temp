(function ($) {
    var MAX_TAGS_VALIDATOR = "tags.count.validator",
        MAX_TAGS = "maxTags",
        foundationReg = $(window).adaptTo("foundation-registry");

    foundationReg.register("foundation.validation.validator", {
        selector: "[data-foundation-validation='" + MAX_TAGS_VALIDATOR + "']",
        validate: function(el) {
            var $tagsPicker = $(el),
                maxTagsAllowed = el.dataset[MAX_TAGS.toLowerCase()];

            if(!maxTagsAllowed){
                console.log(MAX_TAGS + " number not set");
                return;
            }

            var $tagList = $tagsPicker.find(".coral3-TagList");
 
            return ($tagList.find(".coral3-Tag").length > maxTagsAllowed
                            ? "Max exceeded, allowed : " + maxTagsAllowed : undefined);
        }
    });
}(jQuery));
