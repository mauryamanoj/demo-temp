package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.essentiallinks.EssentialLinksModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;
import java.util.List;

/**
 * The Class PackageBase.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
@Getter
class PackageBase implements Serializable {

  /**
   * The Additional information.
   */
  @ChildResource(name = "important-info")
  private PackageInfo importantInformation;

  /**
   * The packageInclude.
   */
  @ChildResource(name = "package-include")
  private PackageInfo packageInclude;
  /**
   * The packageExclude.
   */
  @ChildResource(name = "package-exclude")
  private PackageInfo packageExclude;
  /**
   * The priceInfo.
   */
  @ChildResource(name = "price-info")
  private PackageInfo priceInfo;
  /**
   * The packageDaysModels.
   */
  @ChildResource(name = "days-info")
  private PackageDaysModel packageDaysModels;

  /**
   * The essential-links.
   */
  @ChildResource(name = "essential-links")
  @Setter
  private EssentialLinksModel essentialLinksModel;
  /**
   * The bookingInfo.
   */
  @Expose
  @Setter
  private List<PackageInfo> additionalInformation;
  /**
   * The days.
   */
  @Expose
  @Setter
  private List<PackageDayModel> days;
  /**
   * The days.
   */
  @Expose
  @Setter
  private VendorList vendors;
  /**
   * The days.
   */
  @Expose
  @Setter
  private LeadFormSuccessError lead;

}
