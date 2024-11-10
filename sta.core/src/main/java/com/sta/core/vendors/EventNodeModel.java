package com.sta.core.vendors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The type Card grid model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
@Data
public class EventNodeModel {


  private String id;
  @ValueMapValue
  private String title;
  @ValueMapValue
  private String subtitle;
  @ValueMapValue
  private String description;
  @ValueMapValue
  private String category;
  @ValueMapValue
  private String targetGroup;
  @ValueMapValue
  private String isFree;
  @ValueMapValue
  private String startDate;
  @ValueMapValue
  private String endDate;
  @ValueMapValue
  private String endTime;
  @ValueMapValue
  private String bookingCtaTitle;
  @ValueMapValue
  private String eventLink;
  @ValueMapValue
  private String city;
  @ValueMapValue
  private String season;
  @ValueMapValue
  private String eventLocation;
  @ValueMapValue
  private String Street;
  @ValueMapValue
  private String number;
  @ValueMapValue
  private String zipCode;
  @ValueMapValue
  private String latitude;
  @ValueMapValue
  private String longitude;
}
