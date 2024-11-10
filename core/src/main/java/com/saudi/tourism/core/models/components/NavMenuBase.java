package com.saudi.tourism.core.models.components;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Nav Menu base to adapt properties form the jcr node.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavMenuBase {

  /**
   * Variable for navigationRoot information.
   */
  @Getter
  @ValueMapValue(name = "navigationConfigRoot")
  private String navigationConfigRoot;

  /**
   * Variable for multiSiteConfigRoot information.
   */
  @Getter
  @ValueMapValue(name = "multiSiteConfigRoot")
  private String multiSiteConfigRoot;

  /**
   * Variable for topLinksConfigRoot information.
   */
  @Getter
  @ValueMapValue(name = "topLinksConfigRoot")
  private String topLinksConfigRoot;

  /**
   * Variable for eVisa config information.
   */
  @Getter
  @ValueMapValue(name = "eVisaConfig")
  private String eVisaConfig;

  /**
   * Variable for learnMoreLabel.
   */
  @Getter
  @ValueMapValue(name = "learnMoreLabel")
  private String learnMoreLabel;

  /**
   * Variable for homePageUrl.
   */
  @Getter
  @ValueMapValue(name = "homePageUrl")
  private String homePageUrl;


}
