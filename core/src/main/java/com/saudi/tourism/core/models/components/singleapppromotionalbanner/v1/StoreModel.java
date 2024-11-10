package com.saudi.tourism.core.models.components.singleapppromotionalbanner.v1;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class StoreModel {

  /**
   * text.
   */
  @ValueMapValue
  @Expose
  private String text;

  /**
   * url.
   */
  @ValueMapValue
  @Expose
  private String url;

  /**
   * icon.
   */
  @ValueMapValue
  @Expose
  private String icon;

}
