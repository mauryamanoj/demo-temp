package com.saudi.tourism.core.models.components.brands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * The BrandHomePageList to display homepage brands.
 */
@Data
public class BrandHomePageList implements Serializable {

  /**
   * IsNew.
   */
  @Getter
  @Setter
  private List<BrandListModel> isNew;

  /**
   * IsPopular.
   */
  @Getter
  @Setter
  private List<BrandListModel> isPopular;

  /**
   * IsCLoseToYou.
   */
  @Getter
  @Setter
  private List<BrandListModel> isCloseToYou;

}
