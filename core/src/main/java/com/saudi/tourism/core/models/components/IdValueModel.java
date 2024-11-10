package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * This class contains the id value details.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdValueModel implements Serializable {

  /**
   * id.
   */
  @ValueMapValue
  @Expose
  private String id;

  /**
   * value.
   */
  @ValueMapValue
  @Expose
  private String value;
}
