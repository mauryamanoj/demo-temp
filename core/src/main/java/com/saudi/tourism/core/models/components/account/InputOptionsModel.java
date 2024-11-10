package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class AccountInputsModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
@Getter
@Setter
public class InputOptionsModel implements Serializable {


  /**
   * Instantiates a new Input options model.
   *
   * @param label the label
   * @param value the value
   */
  public InputOptionsModel(final String label, final String value) {
    this.label = label;
    this.value = value;
  }

  /**
   * label.
   */
  @ValueMapValue(name = "text")
  @Expose
  private String label;

  /**
   * value.
   */
  @ValueMapValue
  @Expose
  private String value;

  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image image;


}
