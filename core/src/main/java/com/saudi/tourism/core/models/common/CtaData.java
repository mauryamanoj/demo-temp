package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * CtaData  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class CtaData {

  /**
   * Card cta Event Name.
   */
  @ValueMapValue
  @Expose
  private String ctaEventName;

  /**
   * Card page Category.
   */
  @ValueMapValue
  @Expose
  private String pageCategory;

  /**
   *  Card  page Sub category.
   */
  @ValueMapValue
  @Expose
  private String pageSubCategory;

  /**
   *  Card section Name.
   */
  @ValueMapValue
  @Expose
  private String sectionName;

}
