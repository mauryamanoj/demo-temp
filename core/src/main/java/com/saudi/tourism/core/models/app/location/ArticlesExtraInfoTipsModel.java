package com.saudi.tourism.core.models.app.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ArticlesExtraInfoTipsModel {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Items for activities.
   */
  private List<String> items;

  /**
   * Items for activities.
   */
  @JsonIgnore
  @ValueMapValue
  private String[] itemsAuthored;

  /**
   * PostConstruct method.
   */
  @PostConstruct private void init() {
    items = Arrays.asList(itemsAuthored);
  }
}
