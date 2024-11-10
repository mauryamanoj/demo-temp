package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.NavItemUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class for Header Navigation component.
 */
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderConfigModel extends NavMenuBase {
  /**
   * Inject PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  private SaudiModeConfig saudiModeConfig;


  /**
   * Variable for current resource.
   */
  @Getter
  @ScriptVariable
  private Page currentPage;

  /**
   * Variable Injecting resourceResolver.
   */
  @Getter
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Variable to specify childDepth.
   */
  @Getter
  private int childDepth = SpecialCharConstants.THREE;

  /**
   * Variable to store all Navigation Links.
   */
  @Getter
  private List<NavItem> navItems;

  /**
   * Variable to store all multi sites.
   */
  @Getter
  private List<NavItem> multiSiteItems;

  /**
   * Variable to store all top links.
   */
  @Getter
  private List<NavItem> topLinkItems;

  /**
   * Variable to store eVisaItems Configs.
   */
  @Getter
  private List<NavItem> eVisaItems;

  /**
   * Variable to hold the current Language.
   */
  @Getter
  private String language;

  /**
   * Variable for searchResultsPageUrl.
   */
  @Getter
  @ValueMapValue(name = "searchResultsPageUrl")
  private String searchResultsPageUrl;
  /**
   * Variable for favoritesResultsPageUrl.
   */
  @Getter
  @ValueMapValue
  private String favoritesResultsPageUrl;

  /**
   * Inject Externalizer Service.
   */
  @Inject
  private Externalizer externalizer;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * publish mode .
   */
  private Boolean publishMode;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    publishMode = false;
    Resource resourceNavigationConfig =
        getResourceResolver().getResource(getNavigationConfigRoot());
    navItems = NavItemUtils
        .generateChildListItems(resourceNavigationConfig, pageManager, childDepth, false, publishMode);

    Resource resourceMultiSiteConfig = getResourceResolver().getResource(getMultiSiteConfigRoot());
    multiSiteItems = NavItemUtils
        .generateChildListItems(resourceMultiSiteConfig, pageManager, childDepth, false, publishMode);

    Resource resourceTopLinksConfig = getResourceResolver().getResource(getTopLinksConfigRoot());
    topLinkItems =
        NavItemUtils.generateChildListItems(resourceTopLinksConfig, pageManager, childDepth, false, publishMode);

    Resource resourceEVisaConfig = getResourceResolver().getResource(getEVisaConfig());
    eVisaItems =
        NavItemUtils.generateChildListItems(resourceEVisaConfig, pageManager, childDepth, false, publishMode);

    String path = Optional.ofNullable(currentPage).map(Page::getPath)
        .orElse(StringUtils.EMPTY);
    language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);

    if (Objects.nonNull(saudiModeConfig) && Objects.nonNull(searchResultsPageUrl)) {
      searchResultsPageUrl =
          LinkUtils.getAuthorPublishUrl(resolver, searchResultsPageUrl, saudiModeConfig.getPublish());
      favoritesResultsPageUrl =
          LinkUtils.getAuthorPublishUrl(resolver, favoritesResultsPageUrl, saudiModeConfig.getPublish());
    }

  }

}
