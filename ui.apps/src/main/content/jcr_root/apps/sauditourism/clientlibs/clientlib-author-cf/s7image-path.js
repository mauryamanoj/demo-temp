(function (ns, document, $, Coral, Granite) {
  "use strict";
  $(document).on('foundation-contentloaded', function(e) {
    $(document).on("click", ".cq-dialog-submit", function () {
      updateS7Property();
    });
       $(document).on("click", ".savegroup-save", function () {
      updateS7Property();
    });
  });

  function updateS7Property() {
  //     var items = document.querySelectorAll("foundation-autocomplete[name='image']"),
    var newitems = document.querySelectorAll("[data-selector='s7-image-listener']"),
        itemsOld = document.querySelectorAll("foundation-autocomplete[name='image']"),
        items = [...newitems, ...itemsOld ],
        baseAPI = "/bin/api/v1/dynamic-image-reference",
        baseVideoAPI = "/bin/api/v1/dynamic-video-reference",
        $form = $(this).closest("form.foundation-form"),
        count=items.length, index=0,
        videoItems = document.querySelectorAll("[data-selector='s7-video-listener']"),
        countVideo=videoItems.length;

    [].forEach.call(items, function (item) {
      var field;
      if ($(item).is('foundation-autocomplete')) {
        field = $(item);
      } else {
        field = $(item).find("input[data-cq-fileupload-parameter='filereference']");
      }
      var imagePath = field.val(), api = baseAPI + "." + imagePath;
      var s7FieldName = getScene7FieldName($(field).attr("name"));
      $.ajax({
        url: api,
        cache: false,
        async: false
      }).done(handler).success(submit);
      function handler(mfData) {
        var s7Path = mfData["s7ImageReference"];
        if (s7Path !== null) {
          $(document.getElementsByName(s7FieldName)).val(s7Path);
        } else {
          $(document.getElementsByName(s7FieldName)).val(imagePath);
        }
        index++;
      }
      function submit() {
        if (count == index) {
          $form.submit();
        }
      }
    });

          [].forEach.call(videoItems, function (item) {
      var field;
      if ($(item).is('foundation-autocomplete')) {
        field = $(item);
      } else {
        field = $(item).find("input[data-cq-fileupload-parameter='filereference']");
      }
      var videoPath = field.val(), api = baseVideoAPI + "." + videoPath;
      var s7FieldName = getScene7FieldName($(field).attr("name"));
      $.ajax({
        url: api,
        cache: false,
        async: false
      }).done(handler).success(submit);
      function handler(mfData) {
        var s7Path = mfData["s7VideoReference"];
        if (s7Path !== null) {
          $(document.getElementsByName(s7FieldName)).val(s7Path);
        } else {
          $(document.getElementsByName(s7FieldName)).val(videoPath);
        }
        index++;
      }
      function submit() {
        if (count == index) {
          $form.submit();
        }
      }
    });
  }

  function getScene7FieldName(attrName){
    var baseFieldName = attrName.substr(0, attrName.lastIndexOf('/') + 1),
        fieldName = attrName.substr(attrName.lastIndexOf('/') + 1);
    return baseFieldName + "s7" + fieldName;
  }

})(window.aemTouchUIValidation = window.aemTouchUIValidation || {}, document, Granite.$, Granite);