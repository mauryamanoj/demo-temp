package com.saudi.tourism.core.models.components;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Class for footer component.
 */
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AlertSlingModel {

  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * The request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * The Admin page alert.
   */
  @Getter
  private AdminPageAlert adminPageAlert;

  /**
   * Flag to enable/disable Alert.
   */
  @Getter
  private boolean enableAlertToast;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    try {
      Resource currentResource = currentPage.getContentResource();
      String path = Optional.ofNullable(currentPage).map(Page::getPath).orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      adminPageAlert = AdminUtil.getAdminAlert(language, CommonUtils.getSiteName(currentResource.getPath()));
      if (currentPage.getContentResource() != null && currentPage.getContentResource()
          .isResourceType(Constants.EVENT_DETAIL_RES_TYPE)) {
        enableAlertToast = false;
      } else {
        enableAlertToast = adminPageAlert.isEnableAlert();
      }
    } catch (Exception e) {
      LOGGER.error("Error in AlertSlingModel {0}", e);
    }
  }
}
