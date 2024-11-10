package com.saudi.tourism.core.models.components.brands;

import lombok.Data;

/**
 * The Class BrandBaseRequestParams.
 */
@Data
public class BrandBaseRequestParams {

  /**
   * The language.
   */
  private String locale;

  /**
   * Flag to retrieve all the brands.
   */
  private boolean all = false;

  /**
   * Flag to retrieve all homepage brands.
   */
  private boolean homepage = false;

  /**
   * The limit.
   */
  private Integer limit = 0;

}
