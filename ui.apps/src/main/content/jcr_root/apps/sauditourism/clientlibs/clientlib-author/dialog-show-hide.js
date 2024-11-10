/**
 * # Dialog Show-Hide
 * _(file: dialog-show-hide.js)_
 *
 *
 * Additional client library for dropdown/select, radio etc. components.
 * It enabled hiding / unhiding of other components based on the selection made in the
 * dropdown/select.
 *
 * Was adapted from OOTB dropdownshowhide.js file
 *     /libs/cq/gui/components/authoring/dialog/dropdownshowhide/clientlibs/dropdownshowhide
 *
 * ## How to use:
 *
 * - add class `js-dialog-showhide` to dropdown(select)/radiogroup etc. element
 * - add data attribute `showhide-target` to the dropdown/radiogroup element, value should be a
 *   selector, usually a specific class name, to find all possible target elements that must be
 *   shown/hidden
 * - add the same target class to each target component that must be shown/hidden
 * - add attribute `showhide-target-value` to each target component, the value of the attribute
 *   should equal to the value of select (radio) option that will unhide this element.
 *
 * ---
 *
 * **To switch from AEM OOB version to this one, just replace dialog' classes from
 * "cq-dialog-dropdown-showhide" to "js-dialog-showhide", data attribute name from
 * "cq-dialog-dropdown-showhide-target" to "showhide-target" and data attribute name
 * "showhidetargetvalue" to showhide-target-value.**
 *
 *  ---
 *
 * ### Configuration samples
 *
 *  <pre>
 *    <!-- Coral 3 -->
 *    <selection
 *         jcr:primaryType="nt:unstructured"
 *         sling:resourceType="granite/ui/components/coral/foundation/form/select"
 *         fieldLabel="Selection"
 *         granite:class="js-dialog-showhide"
 *         name="./selection">
 *         <granite:data
 *             jcr:primaryType="nt:unstructured"
 *             showhide-target=".target-class-to-hide"/>
 *         <items jcr:primaryType="nt:unstructured">
 *             <image
 *                 jcr:primaryType="nt:unstructured"
 *                 text="Option 1"
 *                 value="value1"/>
 *             <video
 *                 jcr:primaryType="nt:unstructured"
 *                 text="Option 2"
 *                 value="value2"/>
 *         </items>
 *     </selection>
 *     <!-- This element is displayed only when `Option 2` is selected in a combobox -->
 *     <some-element
 *         jcr:primaryType="nt:unstructured"
 *         jcr:title="Other element"
 *         granite:class="target-class-to-hide hide"
 *         sling:resourceType="granite/ui/components/coral/foundation/form/textfield"
 *         fieldLabel="Some text field"
 *         name="./text">
 *         <granite:data
 *             jcr:primaryType="nt:unstructured"
 *             showhide-target-value="value2"/>
 *     </some-element>
 *  </pre>
 *
 *  <pre>
 *     <!-- Coral 2 -->
 *     <selection
 *         jcr:primaryType="nt:unstructured"
 *         sling:resourceType="granite/ui/components/foundation/form/select"
 *         fieldLabel="Selection"
 *         class="js-dialog-showhide"
 *         showhide-target=".target-class-to-hide"
 *         name="./selection">
 *         <items jcr:primaryType="nt:unstructured">
 *             <image
 *                 jcr:primaryType="nt:unstructured"
 *                 text="Option 1"
 *                 value="value1"/>
 *             <video
 *                 jcr:primaryType="nt:unstructured"
 *                 text="Option 2"
 *                 value="value2"/>
 *         </items>
 *     </selection>
 *     <!-- This element is displayed only when `Option 2` is selected in a combobox -->
 *     <some-element
 *         jcr:primaryType="nt:unstructured"
 *         jcr:title="Some text field"
 *         showhide-target-value="value2"
 *         class="target-class-to-hide hide"
 *         sling:resourceType="granite/ui/components/foundation/form/textfield"/>
 *  </pre>
 *
 * @author yuriy.shestakov
 */
(function (document, $) {
  "use strict";

  const
      EVENT_DIALOG_LOAD = 'foundation-contentloaded.showhide',
      EVENT_CHANGE = 'change',
      EVENT_SELECTED = 'selected',
      EVENT_TYPE_LOAD = 'load',
      SHOWHIDE_COMPONENT_SELECTOR = '.js-dialog-showhide',
      /** data-showhide-target key */
      DATA_KEY_SHOWHIDE_TARGET = 'showhide-target',
      /** data-showhide-target-value key */
      DATA_KEY_SHOWHIDE_TARGET_VALUE = 'showhide-target-value',
      SELECTOR_CORAL_CHECKED_RADIO_ITEM = "coral-radio[checked]",
      SELECTOR_CORAL_SELECT = "coral-select",
      SELECTOR_CORAL_SELECTED_ITEM = "coral-select-item[selected]",
      SELECTOR_CORAL2_CHECKED_RADIO_BUTTON = 'input.coral-Radio-input:checked',
      SELECTOR_FIELD_WRAPPER = '.coral-Form-fieldwrapper',
      SELECTOR_RADIOGROUP = 'div.coral-RadioGroup',
      CLASS_HIDE = 'hide',
      SELECTOR_HIDDEN = '.' + CLASS_HIDE,
      JS_TYPE_FUNCTION = 'function',
      JS_TYPE_OBJECT = 'object',
      REGEXP_CORAL3 = /^coral-/i;

  var isMainDocumentProcessed = false;

  // When dialog gets injected
  $(document).off(EVENT_DIALOG_LOAD).on(EVENT_DIALOG_LOAD, function (eventObject) {
    var loadedContainerElement = eventObject.target || this;

    // Don't process main document twice (this event can be triggered twice in coral).
    if (loadedContainerElement === document) {
      if (isMainDocumentProcessed) {
        return;
      }

      isMainDocumentProcessed = true;
    }

    attachListeners(eventObject.target);
  });


  function attachListeners(context) {
    var $elements = $(SHOWHIDE_COMPONENT_SELECTOR, context);

    $elements.each(function (i, element) {
      var $element = $(element);

      // Check element tag starts with `<coral-`
      if (REGEXP_CORAL3.test(element.nodeName)) {
        // Handle Coral 3 element

        Coral.commons.ready(element, function (initializedElement) {
          // If there is already an initial value make sure the corresponding target element becomes
          // visible
          showHideHandler.apply(initializedElement);
          // Attach listener
          initializedElement.on(EVENT_CHANGE, showHideHandler);
        });

      } else if ($element.is(SELECTOR_RADIOGROUP)) {
        // Radio Group workaround - element has class `coral-RadioGroup` and contains several
        // <coral-radio> elements

        // If there is already an inital value make sure the according target element becomes
        // visible
        showHideHandler.apply($element);
        // Attach listener
        $element.on(EVENT_CHANGE, showHideHandler);

      } else {
        // Handle Coral2 based element

        console.log('SHOW/HIDE: Old versions of Coral components are not entirely supported and' +
            ' may work improperly. Use Granite Coral 3 instead. Element:');
        console.log(element);

        // If there is already an init шal value make sure the according target element becomes
        // visible
        showHideHandler.apply($element);
        // Attach listener
        $element.on(EVENT_SELECTED, showHideHandler);
      }
    });
  }

  function showHideHandler(event) {
    var $element = $(this),
        targetSelector, parentTargetSelector, parentTargetValue,
        targetValue;

    // get value to show
    targetValue = extractValueFromEvent(event);
    if (!targetValue) {
      targetValue = extractValueFromElement($element);
    }

    // get the selector to find the target elements, it's stored as data-showhide-target attribute
    targetSelector = $element.data(DATA_KEY_SHOWHIDE_TARGET);

    // if the element is also depend on parent widget
    if ($element.data(DATA_KEY_SHOWHIDE_TARGET_VALUE) != null) {
      this.classList.forEach(function (parentTarget) {
        if (parentTarget.indexOf(DATA_KEY_SHOWHIDE_TARGET) !== -1 &&
            $(SHOWHIDE_COMPONENT_SELECTOR).data(DATA_KEY_SHOWHIDE_TARGET).indexOf(parentTarget) !== -1) {
          parentTargetSelector = "." + parentTarget;
          var elem = document.querySelector("[data-showhide-target='"+  parentTargetSelector +"']");
          parentTargetValue = elem.value;
        }
      })
    }

    showHide($element, targetSelector, targetValue);
    if (parentTargetSelector && parentTargetValue && parentTargetSelector !== '' && parentTargetValue !== '') {
      showHide($element, parentTargetSelector, parentTargetValue);
    }
  }

  /**
   * Tries extract value from event
   * @param event jQuery / Granite UI event object
   * @returns {String|undefined} target value
   */
  function extractValueFromEvent(event) {
    if (!event || (event.type && event.type === EVENT_TYPE_LOAD)) {
      // Skip load events
      return;
    }

    var value;

    // For dropdowns (coral select) we can get the 'event.selected' value from the select event
    value = event.selected;

    // For radio buttons we can get the value directly from event's target from the change event
    if (!value && event.type === EVENT_CHANGE && event.target
        && $(event.target).is(SELECTOR_CORAL_CHECKED_RADIO_ITEM)) {
      value = event.target.value
    }

    return value;
  }

  function extractValueFromElement($element) {
    var value,
        isCheck = $element.is("coral-checkbox, coral-radio, coral-switch");

    // Checkbox / radio button workaround, especially when the 'uncheckedValue' is set
    if (isCheck && $element.length && !$element[0].checked) {
      // Look for unchecked value
      let fieldName = $element.attr('name');
      if (fieldName) {
        var $uncheckedEl = $element.closest(SELECTOR_FIELD_WRAPPER)
            .find('[name="' + fieldName + '@DefaultValue"]');
        if ($uncheckedEl.length) {
          return extractValueFromElement($uncheckedEl);
        }
      }

      return null;
    }

    if ($element.value) {
      value = $element.value;
    } else if ($element.val && JS_TYPE_FUNCTION === (typeof $element.val)) {
      value = $element.val();
    } else if ($element.getValue && JS_TYPE_FUNCTION === (typeof $element.getValue)) {
      value = $element.getValue();
    } else if ($element.selectedItem && JS_TYPE_OBJECT === (typeof $element.selectedItem)) {
      value = $element.selectedItem.value;
    }

    if (!value) {
      if ($element.is(SELECTOR_CORAL_SELECT)) {
        // Uninitialized combo box workaround - look for selected element
        value = $element.find(SELECTOR_CORAL_SELECTED_ITEM).val();
      } else if ($element.is(SELECTOR_RADIOGROUP)) {
        // Uninitialized radio group workaround - look or checked item
        value = $element.find(SELECTOR_CORAL_CHECKED_RADIO_ITEM).attr('value');
      } else {
        // Coral 2 Radio Group workaround (looks for the input field that stores selected value)
        var $radioInputField = $element.find(SELECTOR_CORAL2_CHECKED_RADIO_BUTTON);
        if ($radioInputField.length) {
          return extractValueFromElement($radioInputField);
        }
      }
    }

    if (!value) {
      if (value === '') {
        console.log('SHOW/HIDE: Empty value "" will be used for show/hide functionality');
      } else {
        console.log('SHOW/HIDE: Couldn\'t extract value to show from the component ');
        console.log($element);
      }
    }

    return value;
  }

  function showHide($element, targetSelector, targetValue) {
    if (!targetSelector) {
      console.log('SHOW/HIDE: Couldn\'t find target selector to hide or show elements. ' +
          'Check `data-showhide-target` attribute');
      return;
    }

    var $target;

    // Check for multifield.
    // If main element was placed into multifield, all actions should take place only for current
    // multifield section
    var $mf = $element.closest("coral-Multifield-item");
    if ($mf.length) {
      $target = $mf.find(targetSelector);
    } else {
      $target = $(targetSelector);
    }

    // Make sure all unselected target elements are hidden.
    $target.not(SELECTOR_HIDDEN).each(function () {
      $(this).addClass(CLASS_HIDE); // Hides the unhidden target
      // Hides the target field wrapper. Thus, hiding label, quick tip etc.
      // TODO CHECKME It works for text fields, need to check also for other elements
      //  (multifields, containers etc.)
      $(this).closest(SELECTOR_FIELD_WRAPPER).addClass(CLASS_HIDE);
    });

    // Show only the element which is corresponding to the selected value
    if (targetValue !== undefined) {
      // Unhide the target element that contains the selected value as data-showhide-target-value
      // attribute
      $target.filter('[data-' + DATA_KEY_SHOWHIDE_TARGET_VALUE + '~="' + targetValue + '"]').each(
          function () {
            $(this).removeClass(CLASS_HIDE);  // Unhides the target
            // Unhides the target field wrapper. Thus, displaying label, quick tip etc.
            $(this).closest(SELECTOR_FIELD_WRAPPER).removeClass(CLASS_HIDE);
          });
    }
  }

})(document, Granite.$);
