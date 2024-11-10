package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.saudi.tourism.core.models.common.BannerCard;
import com.saudi.tourism.core.models.common.ImageBanner;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** PageBanner CF Model. */
@Getter
@Builder
public class PageBannerCFModel {
  /**
   * List of  Banner Cards.
   */
  @Getter
  private List<BannerCard> cards;

  /**
   * hideImageBrush.
   */
  private String hideImageBrush;

  /** Assets. */
  private List<ImageBanner> assets;

}
