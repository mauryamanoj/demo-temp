(function(window, $, URITemplate) {
  "use strict";

  var ui = $(window).adaptTo("foundation-ui");
  var DIFFRESOURCES_PATH = "/mnt/overlay/wcm/core/content/sites/diffresources.html";
  var VERSIONHISTORY_PATH = "/mnt/overlay/wcm/core/content/sites/versionhistory/_jcr_content.txt";
  var VERSION_SERVLET_PATH = "/bin/api/v1/version.";

  function compareVersionCallback(currentPath, versionId, redirectTo, success) {
    if (success) {
      var url = DIFFRESOURCES_PATH + currentPath + "?item=" + redirectTo + "&sideBySide&versionId=" + versionId;

      window.open(Granite.HTTP.externalize(url), '_blank');

    } else {
      ui.alert(Granite.I18n.get("Preview"), Granite.I18n.get("Failed to compare version"), "error");
    }
  }

  function openCompareVersionPage(currentPath, vid) {
    $.ajax({
      url: Granite.HTTP.externalize(VERSIONHISTORY_PATH),
      type: "post",
      dataType: "text",
      data: { versionId: vid, wcmmode: "disabled" },
      success: function(redirectTo) {
        compareVersionCallback(currentPath, vid, redirectTo, true);
      },
      error: function(redirectTo) {
        compareVersionCallback(currentPath, vid, redirectTo, false);
      }
    });
  }

  $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
    name: "cq.inbox.openversion",
    handler: function(name, el, config, collection, selections) {
      var dataAttributeName = config.data.linkAttributeName;
      if (dataAttributeName) {
        var payloadLink = $(selections).data(dataAttributeName);

        var startInd = payloadLink.indexOf('path=') + 5;
        var currentPath = payloadLink.substring(startInd, payloadLink.indexOf('&', startInd));
        currentPath = decodeURIComponent(currentPath);
        $.ajax({
          url: Granite.HTTP.externalize(VERSION_SERVLET_PATH + currentPath),
          type: "get",
          success: function(response) {
            if (response.status == 'success') {
              openCompareVersionPage(currentPath, response.message);
            } else {
              compareVersionCallback(currentPath, '', '', false);
            }
          },
          error: function(response) {
            compareVersionCallback(currentPath, '', '', false);
          }
        });

      }
    }
  });
})(window, Granite.$, Granite.URITemplate);