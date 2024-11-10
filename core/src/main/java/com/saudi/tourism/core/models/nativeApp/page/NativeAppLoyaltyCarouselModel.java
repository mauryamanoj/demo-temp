package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * NativeAppLoyaltyCarouselModel Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppLoyaltyCarouselModel implements Serializable {

  /**
   * loyaltyCarouselList List .
   */
  @ChildResource(name = "loyaltyCarouselList")
  @Expose
  @Getter
  private List<LoyaltyCarouselDetails> loyaltyCarouselList;

  /**
   * termsLink .
   */
  @ValueMapValue
  @Expose
  private String termsLink;

  /**
   * flag .
   */
  @ValueMapValue
  @Expose
  private boolean flag;
}
