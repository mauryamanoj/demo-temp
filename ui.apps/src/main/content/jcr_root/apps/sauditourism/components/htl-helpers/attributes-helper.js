"use strict";

/**
 * This helper produces attributes map for data-sly-attribute htl feature to add attributes by
 * name into htl. Must be used to add transition attributes (load-more, data-learn-cta etc.).
 *
 * Usage:
 * <sly
 *  data-sly-use.attributesHelper="${'/apps/sauditourism/components/htl-helpers/attributes-helper.js' @
 *    attributes=['any-attribute']}"/>
 * <tag data-sly-attribute="${attributesHelper.attributes}"></tag>
 * Will produce:
 * <tag any-attribute="any-attribute"></tag>
 *
 * Arguments:
 * @param attributes attributes array
 */
use(function () {
  var attributes = this.attributes || [];
  if (!attributes) {
    return {};
  }

  var result = {};
  if (attributes.getClass && attributes.getClass().isArray()) {
    // Array passed
    for (var i = 0; i < attributes.length; i++) {
      var attribute = attributes[i];
      result[attribute] = attribute;
    }

  } else {
    // String passed
    result[attributes] = attributes;
  }

  return {
    attributes: result
  };
});
