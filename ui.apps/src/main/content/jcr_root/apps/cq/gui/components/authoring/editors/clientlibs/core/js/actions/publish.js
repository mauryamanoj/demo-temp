/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
;
(function ($, ns, channel, window, httpHelpers, undefined) {

    var DOCUMENT_REFERRER_KEY = "document.referrer";

    function navigateToPublishWizard(activator, path, editMode, schedule) {
        var params = "";
        if (path && typeof path === 'string' && path.length > 0) {
            params += "item=" + path;
        }

        if (params.length > 0) {
        // begin fix sauditourism VSW-6082
            location.href = activator.data('url') + "?" + params + (editMode ? ("&editmode") : "") + (schedule ? ("&later") : "");
        // end fix sauditourism VSW-6082
        }
    }

    function quickPublish(activator, path, editMode, schedule) {
        var referencesUrl = activator.data('references-url');

        $.ajax(referencesUrl, {
            "data": {path: path},
            "type": "POST",
            "cache": false,
            "dataType": "json"
        }).then(function (data) {
            if (data && data.assets && data.assets.length == 0) {
                if (schedule) {
                    navigateToPublishWizard(activator, path, editMode, schedule);
                    return;
                }
                // Publish directly as there is no asset
                $.ajax(activator.data('replication-url'), {
                    "type": "POST",
                    "data": {
                        "_charset_": "utf-8",
                        "cmd": "Activate",
                        "path": path
                    }
                }).then(function () {
                    ns.ui.helpers.notify({
                        content: Granite.I18n.get("The page has been published"),
                        type: ns.ui.helpers.NOTIFICATION_TYPES.INFO
                    });
                }, function (data) {
                    // Error while publishing
                    ns.ui.helpers.notify({
                        content: Granite.I18n.get("Failed to publish the selected page(s)."),
                        type: ns.ui.helpers.NOTIFICATION_TYPES.ERROR
                    });
                });
            } else {
                // Assets found then navigate to wizard
                navigateToPublishWizard(activator, path, editMode, schedule);
            }
        }, function () {
            // Error while getting assets
            ns.ui.helpers.notify({
                content: Granite.I18n.get("Failed to retrieve references for the selected page."),
                type: ns.ui.helpers.NOTIFICATION_TYPES.ERROR
            });
        });
    }

    channel.on("click.quickpublish", ".cq-authoring-actions-quickpublish-activator", function () {
        // Used by the publish wizard for redirection
        CUI.util.state.setSessionItem(DOCUMENT_REFERRER_KEY, location.href);

        var activator = $(this);
        var path = httpHelpers.getPath(activator.data("path"));
        var editMode = activator.data("edit") || false;
        var schedule = activator.data("later") || false;
        quickPublish(activator, path, editMode, schedule);
    });

    channel.on("cq-page-info-loaded", function (event) {
        var pageInfo = event.pageInfo;
        // "cq-page-published-message" is set by the publish wizard
        if (sessionStorage.getItem("cq-page-published-message")) {
            sessionStorage.removeItem("cq-page-published-message");
            var message = null;
            if (pageInfo.workflow.isRunning) {
                message = Granite.I18n.get("The page is pending approval");
            } else if (pageInfo.status.replication.action == "ACTIVATE") {
                message = Granite.I18n.get("The page has been published");
            } else if (pageInfo.status.replication.action == "DEACTIVATE") {
                message = Granite.I18n.get("The page has been unpublished");
            }
            if (message != null) {
                $('.pageinfo .popover').hide();
                ns.ui.helpers.notify({
                    content: message,
                    type: ns.ui.helpers.NOTIFICATION_TYPES.INFO
                });
            }
        }
    });


}(jQuery, Granite.author, jQuery(document), this, Granite.HTTP));
