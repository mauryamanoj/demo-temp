package com.saudi.tourism.core.models.common;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.inject.Inject;

/**
 * Helper model to provide admin options in HTL.
 */
@Getter
@Model(adaptables = SlingHttpServletRequest.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdminSlingModel {
  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * The Admin options.
   */
  @Getter
  private AdminPageOption adminOptions;

  /**
   * Injection constructor.
   *
   * @param currentPage auto injected current page variable
   */
  @Inject
  public AdminSlingModel(@ScriptVariable(name = "currentPage") final Page currentPage) {
    Resource currentResource = currentPage.getContentResource();
    adminOptions = AdminUtil.getAdminOptions(CommonUtils.getLanguageForPath(currentPage.getPath()),
        CommonUtils.getSiteName(currentResource.getPath()));
  }
}
