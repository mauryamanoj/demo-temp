package com.saudi.tourism.core.models.components.anchors.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;


/** Link Model pour links compnenetÒ. */
@Model(
    adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class LinkWithNumber extends Link implements Serializable {
  /**
   * the position for the link in links component.
   */
  @ValueMapValue
  @Expose
  private String number;

  /**
   * the Id used to link to component to scroll to.
   */
  @ValueMapValue
  @Expose
  private Boolean scroll;
}
