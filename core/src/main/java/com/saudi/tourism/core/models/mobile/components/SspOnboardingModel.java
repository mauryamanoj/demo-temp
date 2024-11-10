package com.saudi.tourism.core.models.mobile.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;
import java.util.List;

/**
 * Ssp OnboardingModel model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SspOnboardingModel implements Serializable {
  /**
   * onboardingCarouselList.
   */
  @ChildResource(name = "screens")
  private List<SspOnboardingCard> screens;
}
