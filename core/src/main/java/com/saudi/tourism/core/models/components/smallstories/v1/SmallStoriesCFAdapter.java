package com.saudi.tourism.core.models.components.smallstories.v1;

import com.saudi.tourism.core.models.common.CFAdapter;
import org.apache.sling.api.adapter.Adaptable;
/** SmallStories CFModel adapter.  */
public interface SmallStoriesCFAdapter extends CFAdapter<CardModel> {
  /**
   * Verify is the current adaptable is supported by the current adapter.
   *
   * @param adaptable Current adaptable
   * @return False if the adaptable is not supported, true otherwise
   */
  boolean supports(Adaptable adaptable);

  /**
   * Adapt the current adaptable to WhatToBuyCFModel.
   *
   * @param adaptable Current adaptable
   * @return an WhatToBuyCFModel instance
   */
  CardModel adaptTo(Adaptable adaptable);

}
