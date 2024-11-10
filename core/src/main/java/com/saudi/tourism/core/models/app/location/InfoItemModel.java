package com.saudi.tourism.core.models.app.location;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * This model is used to handle one info item data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class InfoItemModel implements Serializable {

  /**
   * InfoItemModel constructor for packages.
   * @param label label
   * @param value value
   */
  public InfoItemModel(String label, String value) {
    this.label = label;
    this.value = value;
  }

  /**
   * InfoItemModel constructor for location servlet.
   */
  public InfoItemModel() {
  }

  /**
   * Item Label.
   */
  @ValueMapValue(name = Constants.CONST_ITEM_LABEL)
  @Expose
  private String label;

  /**
   * Item Value.
   */
  @ValueMapValue(name = Constants.CONST_ITEM_VALUE)
  @Expose
  private String value;
}
