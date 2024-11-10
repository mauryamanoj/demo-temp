package com.saudi.tourism.core.models.components;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Class for Announcement Banner.
 */
@Data
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AnnouncementBannerAlertModel {
  /**
   * Constant SELF target.
   */
  public static final String SELF = "_self";
  /**
   * Constant BLANK target.
   */
  public static final String BLANK = "_blank";
  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * adminPageAlert.
   */
  @Getter
  private AdminPageAlert adminPageAlert;
  /**
   * title.
   */
  private String title;
  /**
   * bannerAlert.
   */
  private Boolean bannerAlert;
  /**
   * url.
   */
  private String url;
  /**
   * target.
   */
  private String target;
  /**
   * init.
   */
  @PostConstruct
  protected void init() {
    Resource currentResource = currentPage.getContentResource();
    String path = Optional.ofNullable(currentPage).map(Page::getPath).orElse(StringUtils.EMPTY);
    String language = CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    adminPageAlert = AdminUtil.getAdminAlert(language, CommonUtils.getSiteName(currentResource.getPath()));
    if (null != adminPageAlert && null != adminPageAlert.getEnableBannerAlert()
        && adminPageAlert.getEnableBannerAlert()) {
      bannerAlert = adminPageAlert.getEnableBannerAlert();
      title = adminPageAlert.getAnnBannerDesktopTitle();
      url = adminPageAlert.getAnnBannerUrl();
      if (url.contains(Constants.ROOT_CONTENT_PATH)) {
        url = url.concat(Constants.HTML_EXTENSION);
        target = SELF;
      } else {
        target = BLANK;
      }
    } else {
      bannerAlert = false;
    }
  }
}
