package com.saudi.tourism.core.models.components;


import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Season Keys Model.
 */
@Model(adaptables = { Resource.class,
    SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class SeasonKeys {
  /**
   * name.
   */
  @ValueMapValue
  private String name;
  /**
   * key.
   */
  @Getter
  @Setter
  private String key;
  /**
   * value.
   */
  @Getter
  @Setter
  private String value;

  /**
   * generate key and values.
   */
  @PostConstruct
  protected void init() {
    if (null != name) {
      key = name;
      if (name.contains("Seasons") || name.contains("seasons") || name.contains("Season")
          || name.contains("season") && name.contains(Constants.SPACE)) {
        value = name.replace(Constants.SPACE, Constants.MINUS).toLowerCase();
      } else if (name.contains(Constants.SPACE)) {
        value = name.replace(Constants.SPACE, Constants.MINUS).concat("-season").toLowerCase();
      } else {
        value = name.concat("-season").toLowerCase();
      }
    }
  }
}
