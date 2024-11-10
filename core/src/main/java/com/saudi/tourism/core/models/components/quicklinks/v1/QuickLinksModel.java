package com.saudi.tourism.core.models.components.quicklinks.v1;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

/**
 * C-01 Quick Links Model.
 *
 * @author cbarrios
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class QuickLinksModel {

  /**
   * Sling ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Component Title.
   */
  @ValueMapValue
  @Setter
  private String componentTitle;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  private String weight;

  /**
   * List of resource links.
   */
  @ChildResource(name = Constants.LINKS)
  @Setter
  private List<Link> links;

  /**
   * Resource Object.
   */
  @SlingObject
  private Resource resource;

  /**
   * init method for slingModel.
   */
  @PostConstruct protected void init() {
    processPageTitles();
  }

  /**
   * Get the page titles.
   */
  protected void processPageTitles() {
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    if (this.links != null && Objects.nonNull(pageManager)) {
      for (Link currentLink : this.getLinks()) {
        if (LinkUtils.isInternalLink(currentLink.getUrl())) {
          Page page = pageManager.getPage(currentLink.getUrl());
          String vendorName = CommonUtils.getVendorName(page);
          String packageName = CommonUtils.getPackageName(page);
          if (StringUtils.isEmpty(currentLink.getCopy())) {
            currentLink.setCopy(page.getTitle());
          }
          //append .html to internal links
          currentLink.setUrl(LinkUtils.getUrlWithExtension(currentLink.getUrl()));
          currentLink.setAppEventData(CommonUtils
              .getAnalyticsEventData(Constants.DEFAULT_TOUR_PACKAGE_HERO_EVENT,
                  LinkUtils.getAppFormatUrl(currentLink.getUrl()),
                  StringUtils.defaultIfBlank(currentLink.getTitle(), currentLink.getCopy()),
                  vendorName, packageName));
        }
      }
    }
  }

}
