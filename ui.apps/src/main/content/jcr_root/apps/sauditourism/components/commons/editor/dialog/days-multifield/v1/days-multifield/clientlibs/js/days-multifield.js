/* global jQuery, Coral */
(function ($, Coral, Granite, ns, channel) {
  "use strict";

  const
      EVENT_DIALOG_LOADED = "dialog-loaded",
      EVENT_DIALOG_CLOSED = "dialog-closed",
      EVENT_SELECT_LIST_CHANGE = "coral-selectlist:change",
      EVENT_CLICK = "click",
      EVENT_CHANGE = "change",
      EVENT_ITEM_ORDER = "coral-multifield:itemorder",

      NS = ".cmp-childreneditor",
      NN_PREFIX = "day_",
      PN_NAME = "name",
      PN_PANEL_TITLE = "cq:panelTitle",
      PN_RESOURCE_TYPE = "sling:resourceType",
      ATTR_CHILDREN_EDITOR = "childrenEditor",
      ATTR_ADDED = "added",
      ATTR_REORDERED = "reordered",
      TITLE_DAY = Granite.I18n.get("Day"),
      SUFFIX_ADDED = " (" + Granite.I18n.get("Added") + ")",
      SUFFIX_REORDERED = " ( " + Granite.I18n.get("Reordered") + ")",

      selectors = {
        self: "[data-st-cmp-is='daysMultifield']",
        childrenEditor: "[data-cmp-is='childrenEditor']",
        add: "[data-cmp-hook-childreneditor='add']",
        insertComponentDialog: {
          self: "coral-dialog.InsertComponentDialog",
          selectList: "coral-selectlist"
        },
        dialogContent: ".cmp-tabs__editor",
        item: {
          icon: "[data-cmp-hook-childreneditor='itemIcon']",
          input: "[data-cmp-hook-childreneditor='itemTitle']",
          hiddenInput: "[data-cmp-hook-childreneditor='itemResourceType']"
        }
      };

  function addItem(editor, fullPathToComponent) {
    var components = ns.components.find(fullPathToComponent),
        component = components.length ? components[0] : {},
        item;

    if (!components.length) {
      // Component wasn't found.
      return;
    }

    item = editor._elements.self.items.add(new Coral.Multifield.Item());
    item.setAttribute(ATTR_ADDED, ATTR_ADDED);

    // next frame to ensure the item template is rendered in the DOM
    Coral.commons.nextFrame(function () {
      var name = NN_PREFIX + Date.now();
      item.dataset[PN_NAME] = name;

      var input = item.querySelectorAll(selectors.item.input)[0];
      input.name = "./" + name + "/" + PN_PANEL_TITLE;
      // Produce "Day <day number>" value to store as a panel title
      input.value = TITLE_DAY + " " + editor._elements.self.items.length;
      input.title = input.value + SUFFIX_ADDED;

      var hiddenInput = item.querySelectorAll(selectors.item.hiddenInput)[0];
      hiddenInput.value = component.getResourceType();
      hiddenInput.name = "./" + name + "/" + PN_RESOURCE_TYPE;

      var itemIcon = item.querySelectorAll(selectors.item.icon)[0];
      var icon = editor._renderIcon(component);
      itemIcon.appendChild(icon);

      editor._elements.self.trigger(EVENT_CHANGE);
    });
  }

  /**
   * Renames items after reordering.
   *
   * @param editor instance of Children Editor
   */
  function doRenameItems(editor) {
    var items = editor._elements.self.items.getAll();

    for (var i = 0; i < items.length; i++) {
      var item = items[i],
          input = item.querySelectorAll(selectors.item.input)[0];

      input.value = TITLE_DAY + " " + (i + 1);
    }

    editor._elements.self.trigger(EVENT_CHANGE);
  }

  /**
   * Updates one Children Editor to fit our needs.
   *
   * @param editor instance of Children Editor
   */
  function updateEditorInstance(editor) {
    var me = editor,
        addButton = me._elements.add;

    doRenameItems(editor);

    if (!addButton) {
      return;
    }

    if (ns) {
      Coral.commons.ready(addButton, function () {
        // Change existing button's onclick method
        addButton.off(EVENT_CLICK).on(EVENT_CLICK + NS, function () {
          var editable = ns.editables.find(me._path)[0],
              children = editable.getChildren() || [];

          if (children.length) {
            var allowedComponents = ns.components.computeAllowedComponents(editable, ns.pageDesign);

            // Check for the future - if several components are allowed to place
            // (for example trip-days v1 and trip-days v2)
            if (allowedComponents.length === 1) {
              // Only one component is allowed - just add it
              addItem(me, allowedComponents[0]);

            } else {
              // Create and display the insert component dialog relative to a child item
              // - against which allowed components are calculated
              ns.edit.ToolbarActions.INSERT.execute(children[0]);

              var insertComponentDialog = $(document).find(selectors.insertComponentDialog.self)[0];
              var selectList = insertComponentDialog.querySelectorAll(
                  selectors.insertComponentDialog.selectList)[0];

              // next frame to ensure we remove the default event handler
              Coral.commons.nextFrame(function () {
                selectList.off(EVENT_SELECT_LIST_CHANGE);
                selectList.on(EVENT_SELECT_LIST_CHANGE + NS, function (event) {
                  insertComponentDialog.hide();
                  // Add selected component
                  addItem(me, event.detail.selection.value);
                });
              });

              // unbind events on dialog close
              channel.one(EVENT_DIALOG_CLOSED, function () {
                selectList.off(EVENT_SELECT_LIST_CHANGE + NS);
              });
            }
          }
        });
      });
    } else {
      // editor layer unavailable, remove the insert component action
      addButton.parentNode.removeChild(addButton);
    }

    /**
     * Add reorder event handlers after vent is initialized
     */
    Coral.commons.ready(me._elements.self, function () {
      me._elements.self
          .off(EVENT_ITEM_ORDER + NS)
          .on(EVENT_ITEM_ORDER + NS, function (event) {
            var item = event.detail.item,
                isNew = item.getAttribute(ATTR_ADDED);

            if (!isNew) {
              item.setAttribute(ATTR_REORDERED, ATTR_REORDERED);
              var input = item.querySelectorAll(selectors.item.input)[0];
              input.title = input.value + SUFFIX_REORDERED;
            }

            doRenameItems(me);
          });
    });
  }

  $(document).on(EVENT_DIALOG_LOADED, function (e) {
    var $dialog = e.dialog,
        $dialogContent = $dialog.find(selectors.dialogContent),
        $dayMultifieldInstances = $dialogContent
            .find(selectors.childrenEditor + selectors.self);

    if (!$dayMultifieldInstances.length) {
      return;
    }

    Coral.commons.ready($dayMultifieldInstances[0], function () {
      $dayMultifieldInstances.each(function () {
        var me = this,
            childrenEditorInstance = $(me).data(ATTR_CHILDREN_EDITOR);

        // Check instance
        if (!childrenEditorInstance) {
          console.log("Error: ChildrenEditor class is not initialized yet.");
          return;
        }

        updateEditorInstance(childrenEditorInstance);
      });
    });
  });

})(jQuery, Coral, Granite, Granite.author, jQuery(document));
