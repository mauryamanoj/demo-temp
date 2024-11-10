package com.saudi.tourism.core.models.components.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * The Class BaseFilterModel.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
@Slf4j
public class BaseFilterModel implements Serializable {

  /**
   * The filterTitle.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "label")
  @Expose
  private String filterTitle;

  /**
   * The date.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "name")
  @Expose
  private String type;
  /**
   * The typeGroup.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "type")
  @Expose
  private String typeGroup;
  /**
   * The icon.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = Constants.PN_ICON)
  @Expose
  private String icon;
  /**
   * The showAllButton.
   */
  @Getter
  @Expose
  private ButtonsModel showAllButton;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  @Getter
  private transient ResourceBundleProvider i18nProvider;

  /**
   * currentResource.
   */
  @Self
  @Getter
  private transient Resource currentResource;
}
