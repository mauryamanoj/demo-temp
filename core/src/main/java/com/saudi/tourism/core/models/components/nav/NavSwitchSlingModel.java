package com.saudi.tourism.core.models.components.nav;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

/**
 * Class for Switch Navigation Header component.
 */
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class NavSwitchSlingModel {
  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * enabledHeaderV4.
   */
  private boolean enabledHeaderV4;

  /**
   * The Request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    Resource currentResource = request.getResource();
    Resource currentResourcePath = currentPage.getContentResource();
    if (currentResource != null) {
      String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
      AdminPageOption adminPageOption = AdminUtil.getAdminOptions(lang,
          CommonUtils.getSiteName(currentResourcePath.getPath()));
      enabledHeaderV4 = adminPageOption.isEnabledHeaderV4();
    }
  }
}
