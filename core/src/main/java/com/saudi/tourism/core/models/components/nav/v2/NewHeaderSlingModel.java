package com.saudi.tourism.core.models.components.nav.v2;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.NavigationService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

import static com.saudi.tourism.core.utils.Constants.SEARCH_RESULT_RESOURCE_TYPE;

/**
 * Class for Header Navigation component.
 */
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class NewHeaderSlingModel {

  /**
   * Inject PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * The Navigation header.
   */
  private NavigationHeader navigationHeader;

  /**
   * The Navigation service.
   */
  @OSGiService
  private NavigationService navigationService;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * Variable to hold the current Language.
   */
  private String language;

  /**
   * Variable Injecting resourceResolver.
   */
  @Getter
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * The Request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    try {
      Resource currentResource = currentPage.getContentResource();
      if (currentResource != null) {
        language = CommonUtils.getLanguageForPath(currentResource.getPath());
        AdminPageOption adminPageOption = AdminUtil.getAdminOptions(language,
            CommonUtils.getSiteName(currentResource.getPath()));
        navigationHeader = getNavigationService()
            .getNavigationHeader(request, getResourceResolver(), language,
                adminPageOption.getHeaderPath(), CommonUtils.getSiteName(currentResource.getPath()));
        navigationHeader.setSearchPage(
            CommonUtils.isResourceOfResourceType(currentResource, SEARCH_RESULT_RESOURCE_TYPE));
      }
    } catch (Exception ex) {
      LOGGER.error("Error at init method", ex);
    }
  }

}
