/*
 * This file contains fixes for better handling time zones in Coral.DatePicker.
 */
(function (document, $, Coral, moment) {
  "use strict";

  const NS = '.coralDatepickerFix',
      ATTR_VALUE = 'value',
      ATTR_NAME = 'name',
      EVENT_CONTENT_LOADED = 'foundation-contentloaded' + NS,
      EVENT_SUBMIT = 'submit' + NS,
      JCR_TYPE_STRING = 'String',
      REG_CORAL_FIELD = 'foundation-field',
      REGEX_TZ = /[+\-]\d\d:\d\d$/,
      SELECTOR_CORAL_FORM = 'form.foundation-form',
      SELECTOR_DATE_PICKER_WITH_TIMEZONE = 'coral-datepicker[valueformat$=Z]',
      SELECTOR_TIMEZONE_MSG = 'p.granite-datepicker-timezone',
      TZ_LOCAL = moment().format('Z'),
      TZ_SA = '+03:00';

  /**
   * Convert time zone for all date picker fields in the current scope.
   *
   * @scopeEl {HTMLElement} parent element to look for date pickers
   * @isLoading {Boolean} true to convert to local timezone, false / undefined to convert to SA zone
   */
  function doConvertAllDateFields(scopeEl, isLoading) {
    var $scope = $(scopeEl),
        $dateFields = $scope.find(SELECTOR_DATE_PICKER_WITH_TIMEZONE);

    $dateFields.each(function () {
      var $dateField = $(this),
          $parentWrapper = $dateField.parent(),
          coralField = $dateField.adaptTo(REG_CORAL_FIELD),
          coralFieldValue = coralField.getValue(),
          dateValue = isLoading ? $dateField.attr(ATTR_VALUE) : coralFieldValue,
          fieldName = coralField.getName() || $dateField.attr(ATTR_NAME),
          $typeHintField = $parentWrapper.find('input[name="' + fieldName + '@TypeHint"]'),
          targetTimezone = isLoading ? TZ_LOCAL : TZ_SA;

      // Ignore if time zone difference message is displayed, or bad value, or is saved as string
      if (!dateValue
          || coralField.isInvalid()
          || $typeHintField.val() === JCR_TYPE_STRING
          || $parentWrapper.children(SELECTOR_TIMEZONE_MSG).length) {
        return;
      }

      var newValue = convertTimeZoneStr(dateValue, targetTimezone);
      if (dateValue === newValue) {
        return;
      }

      if (isLoading && coralFieldValue && coralFieldValue !== dateValue) {
        // Coral.DatePicker component was already processed here and has a wrong value
        // (this piece of code usually is executed only in the component's edit dialog)
        coralField.setValue(newValue);

      } else {
        // Update coral-datepicker value attribute. val() function works improperly here
        $dateField.attr(ATTR_VALUE, newValue);
        // Update hidden input for web form
        $dateField.find('input[name="' + fieldName + '"]').val(newValue);
      }
    });
  }

  /**
   * Convert the current date value to Arabic/Riyadh timezone or back to local.
   *
   * @dateValue {Date} date to convert
   * @toOffset {string} convert to this offset
   */
  function convertTimeZoneStr(dateValue, toOffset) {
    var tzRegexMatch = dateValue ? dateValue.match(REGEX_TZ) : undefined;

    if (!dateValue || !tzRegexMatch || !tzRegexMatch.index || tzRegexMatch[0] === toOffset) {
      return dateValue;
    }

    return dateValue.substring(0, tzRegexMatch.index) + toOffset;
  }

  //
  // Binding events
  //

  // On page / dialog is loaded event
  var isMainDocumentProcessed = false;
  $(document).off(EVENT_CONTENT_LOADED).on(EVENT_CONTENT_LOADED, function (event) {
    var loadedContainerElement = event.target || this;

    // Don't process main document twice (this event is triggered twice in coral).
    if (loadedContainerElement === document) {
      if (isMainDocumentProcessed) {
        return;
      }

      isMainDocumentProcessed = true;
    }

    // Update to local time zone during loading.
    doConvertAllDateFields(loadedContainerElement, true);

    // Bind form events only when container finishes loading.
    Coral.commons.ready(loadedContainerElement, function (component) {
      // On form submit event for all forms in this container (usually only one)
      $(component).find(SELECTOR_CORAL_FORM)
          .off(EVENT_SUBMIT)
          .on(EVENT_SUBMIT, function (event) {
            // Update to SA/Riyadh time zone during saving.
            doConvertAllDateFields(event.target || this, false);
          });
    });
  });

})(document, Granite.$, Coral, moment);
