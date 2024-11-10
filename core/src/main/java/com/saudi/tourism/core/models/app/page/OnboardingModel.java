package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;
import java.util.List;

/**
 * OnboardingModel model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class OnboardingModel implements Serializable {
  /**
   * onboardingCarouselList.
   */
  @ChildResource(name = "onboardingCarouselList")
  @Expose
  @Getter
  private List<OnboardingCard> onboardingCarouselList;

}
