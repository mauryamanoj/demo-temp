package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.nativeApp.page.HomeSections;
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
 * NativeAppRewardsOrder model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppRewardsOrder implements Serializable {

  /**
   * Page title.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String pageTitle;

  /**
   * sections.
   */
  @ChildResource(name = "homeSections")
  @Expose
  @Getter
  private List<HomeSections> sections;


}
