package com.saudi.tourism.core.models.common;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Smart Banner Model.
 */
@Data
@Model(adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SmartBannerModel {
  /**
   * AdminSettingsService.
   */
  @OSGiService
  private AdminSettingsService adminSettingsService;
  /**
   * enabled.
   */
  private String enabled;
  /**
   * language.
   */
  private String language;
  /**
   * Title.
   */
  private String sff;
  /**
   * current page.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * iosAppId.
   */
  private String iosAppId;
  /**
   * smartBannerTitle.
   */
  private String smartBannerTitle;

  /**
   * smartBannerAuthorLabel.
   */
  private String smartBannerAuthor;

  /**
   * smartBannerButtonLabel.
   */
  private String smartBannerButtonLabel;

  /**
   * iosLabel.
   */
  private String iosLabel;

  /**
   * androidLabel.
   */
  private String androidLabel;
  /**
   * iosIcon.
   */
  private String iosIcon;

  /**
   * androidIcon.
   */
  private String androidIcon;

  /**
   * iosAppUrl.
   */
  private String iosAppUrl;
  /**
   * androidAppUrl.
   */
  private String androidAppUrl;
  /**
   * getLabel.
   */
  @ValueMapValue
  private String getLabel;
  /**
   * closeLabel.
   */
  @ValueMapValue
  private String closeLabel;
  /**
   * return smart banner configurable values.
   */
  @PostConstruct
  protected void init() {

    language =
      CommonUtils.getPageNameByIndex(currentPage.getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    if (null != adminSettingsService) {
      enabled = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getEnableSmartBanner();
      if (null != enabled && enabled.equals("true")) {
        iosAppId = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getIosAppId();
        smartBannerTitle = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getSmartBannerTitle();
        smartBannerAuthor = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getSmartBannerAuthor();
        smartBannerButtonLabel = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getSmartBannerButtonLabel();
        iosLabel = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getIosLabel();
        androidLabel = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getAndroidLabel();
        iosIcon = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getIosIcon();
        androidIcon = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getAndroidIcon();
        iosAppUrl = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getIosAppUrl();
        androidAppUrl = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getAndroidAppUrl();
        getLabel = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getGetLabel();
        closeLabel = adminSettingsService.getAdminOptions(language,
          CommonUtils.getSiteName(currentPage.getPath())).getCloseLabel();
      }
    }
  }
}
