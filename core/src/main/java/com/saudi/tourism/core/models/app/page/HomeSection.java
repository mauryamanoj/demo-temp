package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.io.Serializable;

/**
 * HomeSection .
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class HomeSection implements Serializable {

  /**
   * Section Class as a variable .
   */
  @Setter
  @Expose
  private Section section;

}
