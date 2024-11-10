package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Dictionary data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
public class DictItem {

  /**
   * Variable to store the code.
   */
  @Expose
  private String code;

  /**
   * Variable to store the value.
   */
  @Expose
  private String value;

  /**
   * Constructor with injected fields.
   * @param code code
   * @param value value
   */
  @Inject
  public DictItem(
      @ValueMapValue @Named(Constants.CODE) String code,
      @ValueMapValue @Named(Constants.VALUE) String value
  ) {
    this.code = code;
    this.value = value;
  }
}
