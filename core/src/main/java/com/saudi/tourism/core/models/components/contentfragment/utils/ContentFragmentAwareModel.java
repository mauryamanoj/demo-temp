package com.saudi.tourism.core.models.components.contentfragment.utils;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;

public interface ContentFragmentAwareModel {
  default <T> T getElementValue(ContentFragment cf, String elementName, Class<T> type) {
    if (cf == null) {
      return null;
    }

    if (cf.hasElement(elementName)) {
      FragmentData elementData = cf.getElement(elementName).getValue();
      if (elementData != null) {
        return elementData.getValue(type);
      }
    }

    return null;
  }
}
