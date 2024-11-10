package com.saudi.tourism.core.models.common;

import org.apache.sling.api.adapter.Adaptable;

public interface CFAdapter<T> {
  boolean supports(Adaptable adaptable);
  T adaptTo(Adaptable adaptable);
}
