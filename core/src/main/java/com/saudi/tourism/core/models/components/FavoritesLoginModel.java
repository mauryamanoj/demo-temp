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
public class FavoritesLoginModel {

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
   * The Favorite login button title.
   */
  @Getter
  private String favLoginTitle;

  /**
   * The Favorite login copy.
   */
  @Getter
  private String favLoginCopy;

  /**
   * The Favorite login button copy.
   */
  @Getter
  private String favLoginButtonCopy;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    Resource currentResource = currentPage.getContentResource();
    try {
      String path = Optional.ofNullable(currentPage).map(Page::getPath).orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      AdminPageOption adminPageOption = AdminUtil.getAdminOptions(language,
          CommonUtils.getSiteName(currentResource.getPath()));
      favLoginTitle = StringUtils.defaultIfBlank(adminPageOption.getFavLoginTitle(),
          StringUtils.EMPTY);
      favLoginCopy = StringUtils.defaultIfBlank(adminPageOption.getFavLoginCopy(),
          StringUtils.EMPTY);
      favLoginButtonCopy = StringUtils.defaultIfBlank(adminPageOption.getFavLoginButtonCopy(),
          StringUtils.EMPTY);
    } catch (Exception e) {
      LOGGER.error("Error in Init {}", e);
    }
  }

}
