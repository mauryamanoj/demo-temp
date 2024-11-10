package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HeroDataModel implements Serializable {

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;

}
