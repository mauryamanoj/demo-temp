package com.sta.core.vendors.models;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.sta.core.vendors.ScriptConstants;
import com.saudi.tourism.core.utils.CommonUtils;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


/**
 * Event entry model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EventEntry implements Serializable {

  /**
   * Event name in English.
   */
  @ValueMapValue
  private String eventName;

  /**
   * ID.
   */
  private String id;

  /**
   * City.
   */
  @ValueMapValue
  private String city;

  /**
   * Title in English.
   */
  @ValueMapValue
  private String title;

  /**
   * Subtitle.
   */
  @ValueMapValue
  private String subtitle;
  /**
   * galleryImages.
   */
  @ValueMapValue
  private String galleryImages;

  /**
   * Title in Arabic.
   */
  @ValueMapValue
  private String title_ar;

  /**
   * bookingCtaTitle.
   */
  @ValueMapValue
  private String bookingCtaTitle;

  /**
   * Copy in English.
   */
  @ValueMapValue
  private String copy;

  /**
   * Copy in Arabic.
   */
  @ValueMapValue
  private String copy_ar;

  /**
   * Season.
   */
  @ValueMapValue
  private String season;

  /**
   * freePaid.
   */
  @ValueMapValue
  private String freePaid;

  /**
   * Longitude.
   */
  @ValueMapValue
  private String longitude;

  /**
   * eventLinkCopy.
   */
  @ValueMapValue
  private String eventLinkCopy;

  /**
   * eventLinkUrl.
   */
  @ValueMapValue
  private String eventLinkUrl;

  /**
   * eventLocation.
   */
  @ValueMapValue
  private String eventLocation;

  /**
   * eventPlanningStatus.
   */
  @ValueMapValue
  private String eventPlanningStatus;

  /**
   * street.
   */
  @ValueMapValue
  private String street;

  /**
   * number.
   */
  @ValueMapValue
  private int number;

  /**
   * Latitude.
   */
  @ValueMapValue
  private String latitude;

  /**
   * Vendor.
   */
  @ValueMapValue
  private String vendor;
  /**
   * Vendor.
   */
  @ValueMapValue
  private String googleMapLink;
  /**
   * Vendor.
   */
  @ValueMapValue
  private String featureEventImage;

  /**
   * zipCode.
   */
  @ValueMapValue
  private String zipCode;

  /**
   * zipCode.
   */
  @ValueMapValue
  private String eventType;

  /**
   * Category.
   */
  @ValueMapValue
  private String categoryTags;

  /**
   * Subcategory.
   */
  @ValueMapValue
  private String subCategoryTags;

  /**
   * Target.
   */
  @ValueMapValue
  private String[] targetGroupTags;
  /**
   * calendarEndDate.
   */
  @ValueMapValue
  private String calendarEndDate;

  /**
   * calendarStartDate.
   */
  @ValueMapValue
  private String calendarStartDate;
  /**
   * startTime.
   */
  @ValueMapValue
  private String startTime;

  /**
   * endTime.
   */
  @ValueMapValue
  private String endTime;

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * init method for slingModel.
   */
  @PostConstruct private void init() {
    this.id = currentResource.getName();
    calendarEndDate = CommonUtils.getDateMonth(calendarEndDate, ScriptConstants.FORM_FORMAT_DATE);
    calendarStartDate =
        CommonUtils.getDateMonth(calendarStartDate, ScriptConstants.FORM_FORMAT_DATE);
    if (Objects.nonNull(featureEventImage)) {
      Gson gson = new Gson();
      ImageEntry image = gson.fromJson(featureEventImage, ImageEntry.class);
      if (!image.getValue().contains("ugc")) {
        image.setValue(image.getValue() + ":crop-667x375?defaultImage=progress&wid=667&" + System
            .currentTimeMillis());
      }
      featureEventImage = gson.toJson(image);
    }
  }

}
