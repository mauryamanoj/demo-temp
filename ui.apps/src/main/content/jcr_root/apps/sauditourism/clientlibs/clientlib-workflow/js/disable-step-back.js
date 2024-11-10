(function ($, $document, gAuthor) {
  "use strict";

  var _ = window._,
      EDITOR_URL = "/editor.html",
      WF_ACTION_STEP_BACK = "workflow-stepback",
      WF_ACTION_DELEGATE = "workflow-delegate",
      WF_STATUS_TYPE = "workflow";

  if(isAuthoring()){
    $document.on("cq-editor-statusbar-loaded", disableStepBackOnPage);
  }

  function disableStepBackOnPage(){
    if(!gAuthor){
      return;
    }

    var statusBar = gAuthor.ui.statusBarManager.getStatusBar(),
        $status, $stepBack, $delegate;

    _.each(statusBar.statuses, function(status){
      if(status.statusType == WF_STATUS_TYPE){
        if (status.actionIds) {
          _.remove(status.actionIds, function(action) {
            return (action == WF_ACTION_STEP_BACK);
          });
          _.remove(status.actionLabels, function(label) {
            return (label == 'Step back');
          });
          $status = $(statusBar.status);
          $stepBack = $status.find("[data-status-action-id='" + WF_ACTION_STEP_BACK + "']");
          $delegate = $status.find("[data-status-action-id='" + WF_ACTION_DELEGATE + "']");
          if($stepBack) {
            $stepBack.hide();
            $delegate.hide();
          }
        }
      }
    });

  }

  function isAuthoring() {
    return (window.location.pathname.indexOf(EDITOR_URL) === 0);
  }
}(jQuery, jQuery(document), Granite.author));