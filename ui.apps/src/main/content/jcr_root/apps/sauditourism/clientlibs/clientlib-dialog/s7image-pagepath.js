(function (ns, document, $, Coral, Granite) {
  "use strict";

  // Create page
  $(document).on("click", "button.foundation-wizard-control[type='submit']", function () {
    updateS7Property();
  });

  // page properties
  $(document).on("click", "button[type='submit'][form='cq-sites-properties-form']", function () {
    updateS7Property();
  });

  function updateS7Property() {
    var items = document.querySelectorAll('.s7-image-listener'),
        baseAPI = "/bin/api/v1/dynamic-image-reference",
        $form = $(this).closest("form.foundation-form"),
        count=items.length, index=0;

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
  }

  function getScene7FieldName(attrName){
    var baseFieldName = attrName.substr(0, attrName.lastIndexOf('/') + 1),
        fieldName = attrName.substr(attrName.lastIndexOf('/') + 1);
    return baseFieldName + "s7" + fieldName;
  }

})(window.aemTouchUIValidation = window.aemTouchUIValidation || {}, document, Granite.$, Granite);