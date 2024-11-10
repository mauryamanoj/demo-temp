package com.saudi.tourism.core.models.common;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * This model is used to retrieve configuration details from config pages.
 */
@Model(adaptables = {SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ConfigurationModel {

  /**
   * Variable resourceType is fetched form a Page.
   */
  @RequestAttribute
  private String resourceType;

  /**
   * Variable configPage which contains resource.
   */
  @RequestAttribute
  private String configPage;

  /**
   * Variable isRelativePath.
   */
  @RequestAttribute
  @Default(booleanValues = false)
  private boolean isRelativePath;

  /**
   * Variable currentPage.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * Variable ResourceResolver.
   */
  @ScriptVariable
  private transient ResourceResolver resolver;

  /**
   * Variable PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * Variable resourcePath.
   */
  @Getter
  private String resourcePath = "";

  /**
   * Init method.
   */
  @PostConstruct protected void init() {
    Resource resource = Optional.of(isRelativePath)
        .map(x -> currentPage.getAbsoluteParent(SpecialCharConstants.TWO)).map(home -> {
          if (home.getPath().startsWith(Constants.SITE_VERSION_HISTORY)) {
            return resolver.getResource(
                Constants.SITE_ROOT + Constants.FORWARD_SLASH_CHARACTER + CommonUtils
                    .getPageNameByIndex(currentPage.getPath(), NumberConstants.SIX));
          } else {
            return home.adaptTo(Resource.class);
          }
        }).map(res -> resolver.getResource(res, configPage))
        .orElse(resolver.getResource(configPage));

    Page containingPage = pageManager.getContainingPage(resource);
    if (Objects.nonNull(containingPage) && StringUtils.isNotBlank(resourceType)) {
      Resource contentResource = containingPage.getContentResource(Constants.RESPONSIVE_GRID);
      if (contentResource != null) {
        Iterable<Resource> children = contentResource.getChildren();
        for (Resource res : children) {
          if (res.isResourceType(resourceType)) {
            resourcePath = res.getPath();
            break;
          }
        }
      }
    }
  }

}
