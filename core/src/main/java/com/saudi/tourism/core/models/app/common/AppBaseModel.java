package com.saudi.tourism.core.models.app.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * App page model.
 */
@Getter
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@ToString
public class AppBaseModel implements Serializable {

  /**
   * favId for app to map to web pages.
   */
  @JsonProperty("favId")
  @SerializedName("favId")
  @ValueMapValue
  @Setter
  private String webMappingPath;

}
