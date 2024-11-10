package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
* LoyaltyCarouselDetails Model.
*/
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class LoyaltyCarouselDetails {

 /**
  * image .
  */
  @ValueMapValue
  @Expose
 private String image;

 /**
  * title .
  */
  @ValueMapValue
  @Expose
 private String title;

 /**
  * description . .
  */
  @ValueMapValue
  @Expose
 private String description;

 /**
  * ctaTitle .
  */
  @ValueMapValue
  @Expose
 private String ctaTitle;
}
