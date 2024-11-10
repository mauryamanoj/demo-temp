package com.saudi.tourism.core.models.components;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;

/**
 * The Header Logo model.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class HeaderLogo {
  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * The adminService.
   */
  @OSGiService
  private AdminSettingsService adminService;

  /**
   * The image.
   */
  private String logo;

  /**
   * fetch logos paths.
   */
  @PostConstruct
  protected void init() {
    Resource currentResource = currentPage.getContentResource();
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    String site = CommonUtils.getSiteName(currentResource.getPath());
    if (null != adminService.getAdminOptions(lang, site).getHeaderLogo()) {
      logo = adminService.getAdminOptions(lang, site).getHeaderLogo();
    }
  }
}
