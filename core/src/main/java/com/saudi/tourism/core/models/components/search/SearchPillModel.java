package com.saudi.tourism.core.models.components.search;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * The Search pill model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SearchPillModel {

  /**
   * Runmode service to get sling run modes.
   */
  @Inject
  private transient RunModeService runModeService;

  /**
   * Variable to store title of pills block.
   */
  @ValueMapValue
  @Default(values = Constants.BLANK)
  private String solrKey;

  /**
   * type of suggestions.
   */
  @ValueMapValue
  @Default(values = "keyword")
  private String type;

  /**
   * url of suggestions if direct link for web.
   */
  @ValueMapValue
  private String urlWeb;

  /**
   * url of suggestions if direct link for app.
   */
  @ValueMapValue
  private String urlApp;

  /**
   * App type.
   */
  private String appType;

  /**
   * resource resolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  private void init() {
    // Add .html or remove /content/sauditourism according to sling run mode for internal link
    if (StringUtils.isNotBlank(this.urlWeb)) {
      this.urlWeb = LinkUtils.getAuthorPublishUrl(resourceResolver, this.urlWeb,
            runModeService.isPublishRunMode());
    }

    if (StringUtils.isNotBlank(this.urlApp)) {

      final PageManager pageManager;
      if (resourceResolver != null) {
        pageManager = resourceResolver.adaptTo(PageManager.class);
        this.appType = AppUtils.getAppTypeFromLink(this.urlApp, pageManager);
      }
      this.urlApp = LinkUtils.getAuthorPublishUrl(resourceResolver, this.urlApp,
          runModeService.isPublishRunMode());

    }
  }
}
