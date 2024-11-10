package com.sta.core.vendors.models;

import lombok.Getter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.sta.core.vendors.ScriptConstants;
import com.saudi.tourism.core.utils.CommonUtils;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Package entry model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EventListEntry implements Serializable {

  private String id;
  @ValueMapValue
  private String title;
  @ValueMapValue(name = "categoryTags")
  private String category;
  @ValueMapValue(name = "calendarStartDate")
  private String startDate;
  @ValueMapValue(name = "calendarEndDate")
  private String endDate;
  @ValueMapValue
  private Calendar modifiedDate;
  @ValueMapValue
  private String city;
  @ValueMapValue
  private String featureEventImage;

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
   *
   * @param currentResource current resource
   */
  @Inject public EventListEntry(@SlingObject @Named("currentResource") Resource currentResource) {
    this.id = currentResource.getName();
  }

  /**
   * init method for slingModel.
   */
  @PostConstruct private void init() {
    endDate = CommonUtils.getDateMonth(endDate, ScriptConstants.FORM_FORMAT_DATE);
    startDate = CommonUtils.getDateMonth(startDate, ScriptConstants.FORM_FORMAT_DATE);
    image = new ImageEntry();
    if (Objects.nonNull(featureEventImage)) {
      Gson gson = new Gson();
      image = gson.fromJson(featureEventImage, ImageEntry.class);
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
