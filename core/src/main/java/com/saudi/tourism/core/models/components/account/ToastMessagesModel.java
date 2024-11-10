package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class ToastMessagesModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class ToastMessagesModel implements Serializable {

  /**
   * type.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String type;

  /**
   * copy.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String copy;

}
