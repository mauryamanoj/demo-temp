package com.saudi.tourism.core.models.components.hotels;

import com.saudi.tourism.core.models.components.events.BaseRequestParams;
import lombok.Data;

import java.util.List;

/**
 * The Class HotelsRequestParams.
 */
@Data
public class HotelsRequestParams extends BaseRequestParams {

  /**
   * The budget.
   */
  private String hotelName;

  /**
   * The city.
   */
  private List<String> area;

  /**
   * The image.
   */
  private String hotelImage;

  /**
   * The CTA URL.
   */
  private String ctaUrl;

  /**
   * The path.
   */
  private String path;

  /**
   * The hotel chain.
   */
  private List<String> hotelChain;

  /**
   * The articleId.
   */
  private String articleId;
}
