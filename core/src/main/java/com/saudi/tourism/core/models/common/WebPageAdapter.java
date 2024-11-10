package com.saudi.tourism.core.models.common;

import org.apache.sling.api.adapter.Adaptable;

/**
 * Web Page Adapter.
 * @param <T> type
 */
public interface WebPageAdapter<T> {
  boolean supports(Adaptable adaptable);
  T adaptTo(Adaptable adaptable);
}
