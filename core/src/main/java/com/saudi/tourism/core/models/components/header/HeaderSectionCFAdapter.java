package com.saudi.tourism.core.models.components.header;

import org.apache.sling.api.adapter.Adaptable;

/**
 * HeaderSectionCFAdapter.
 */
public interface HeaderSectionCFAdapter {
  /**
   * Verify is the current adaptable is supported by the current adapater.
   *
   * @param adaptable Current adaptable
   * @return False if the adaptable is not supported, true otherwise
   */
  boolean supports(Adaptable adaptable);

  /**
   * Adapte the current adaptable to HeaderSectionModel.
   *
   * @param adaptable Current adaptable
   * @return an HeaderSectionModel instance
   */
  HeaderSectionCFModel adaptTo(Adaptable adaptable);
}
