package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * The Class AccountPropertiesModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class AccountPropertiesModel implements Serializable {

  /**
   * name.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String name;

  /**
   * label.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String label;

  /**
   * placeholder.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String placeholder;

  /**
   * type.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String type;

  /**
   * options.
   */
  @ValueMapValue
  @Setter
  @Expose
  private List<InputOptionsModel> options;

}
