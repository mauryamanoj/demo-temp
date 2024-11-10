package com.sta.core.vendors.models;

import com.google.gson.Gson;
import com.saudi.tourism.core.utils.CommonUtils;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Package entry model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class PackageListEntry implements Serializable {

  private String id;

  @ValueMapValue
  private String title;

  @ValueMapValue(name = "categoryTags")
  private String category;

  @ValueMapValue
  private String city;

  @ValueMapValue
  private String bannerImage;
  @ValueMapValue
  private Calendar modifiedDate;

  private ImageEntry image;

  @ValueMapValue
  private String statusType;
  private String statusMessage;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Constructor.
   * @param currentResource current resource
   */
  @Inject
  public PackageListEntry(@SlingObject @Named("currentResource") Resource currentResource) {
    this.id = currentResource.getName();
  }

  /**
   * init method for slingModel.
   */
  @PostConstruct
  private void init() {
    image = new ImageEntry();
    if (Objects.nonNull(bannerImage)) {
      Gson gson = new Gson();
      image = gson.fromJson(bannerImage, ImageEntry.class);
      if (!image.getValue().contains("ugc")) {
        image.setValue(
            image.getValue() + ":crop-667x375?defaultImage=progress&wid=667&" + System.currentTimeMillis());
      }
    }

    if (Objects.isNull(statusType)) {
      statusType = "pending";
      statusMessage = "Pending";
    }
    if (Objects.nonNull(i18nProvider)) { // update city to i18n value
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale("en"));
      statusMessage = CommonUtils.getI18nString(i18nBundle, statusType);
    }
  }


}
