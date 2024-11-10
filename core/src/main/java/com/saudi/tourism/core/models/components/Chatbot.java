package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ChatbotConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

/**
 * Chatbot.
 */
@Data
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Chatbot {

  /**
   * ENABLE_ALL.
   */
  public static final String ENABLE_ALL = "enable-all";
  /**
   * DISABLE_ALL.
   */
  public static final String DISABLE_ALL = "disable-all";
  /**
   * siteWideCheck.
   */
  private boolean siteWideCheck;
  /**
   * chatBotConfigs.
   */
  @OSGiService
  private ChatbotConfig chatBotConfigs;
  /**
   * adminService.
   */
  @OSGiService
  private AdminSettingsService adminService;
  /**
   * request object.
   */
  @Inject
  private SlingHttpServletRequest request;
  /**
   * List of pages where chatbot will be disabled.
   */
  private List<ChatbotDisabledModel> disabledPages;
  /**
   *Boolean value for disabling page.
   */
  private Boolean enablePage;
  /**
   *List of pages where chatbot will be enabled.
   */
  private List<ChatbotEnabledModel> enabledPages;
  /**
   * Chatbot Title.
   */
  private String chatbotTitle;
  /**
   * returns true or false based on the configurations.
   */
  @PostConstruct
  protected void init() {
    if (null != chatBotConfigs && null != chatBotConfigs.getChatbotEnableDisableSitewide()
          && chatBotConfigs.getChatbotEnableDisableSitewide()) {
      String currentPagePath = request.getRequestPathInfo().getResourcePath();
      currentPagePath = currentPagePath.replace("/jcr:content", StringUtils.EMPTY);
      ChatbotConfigModel chatbotConfigModel = adminService.getChatbotData(request.getLocale().getLanguage());
      if (null != chatbotConfigModel) {
        chatbotTitle = chatbotConfigModel.getChatbotTitle();
        String chatbotSelectionType = chatbotConfigModel.getEnableOrDisable();
        if (chatbotSelectionType.equals(ENABLE_ALL)) {
          disabledPages = chatbotConfigModel.getDisabledPages();
          if (null != disabledPages) {
            enablePage = true;
            for (ChatbotDisabledModel pagePath : disabledPages) {
              if (currentPagePath.equals(pagePath.getDisablePagePath())) {
                enablePage = false;
                break;
              }
            }
          }
        } else if (chatbotSelectionType.equals(DISABLE_ALL)) {
          enabledPages = chatbotConfigModel.getEnabledPages();
          if (null != enabledPages) {
            enablePage = false;
            for (ChatbotEnabledModel pagePath : enabledPages) {
              if (currentPagePath.equals(pagePath.getEnablePagePath())) {
                enablePage = true;
                break;
              }
            }
          }
        }
      }
    } else {
      enablePage = false;
    }
  }
}
