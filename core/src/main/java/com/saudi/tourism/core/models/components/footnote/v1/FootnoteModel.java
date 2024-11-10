package com.saudi.tourism.core.models.components.footnote.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * C-98 Footnote.
 *
 * @author jlentink
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class FootnoteModel implements Serializable {

  /**
   * Component heading.
   */
  @ValueMapValue
  @Expose
  private String heading;

  /**
   *  Heading weight.
   */
  @ValueMapValue
  @Expose
  private String titleWeight;

  /**
   * Apply link.
   */
  @ChildResource
  @Expose
  private Link link;

}
