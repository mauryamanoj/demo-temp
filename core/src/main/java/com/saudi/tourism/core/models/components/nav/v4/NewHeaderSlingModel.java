package com.saudi.tourism.core.models.components.nav.v4;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.LanguageLink;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import com.saudi.tourism.core.services.NavigationService;
import com.saudi.tourism.core.services.SaudiModeConfig;
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
import javax.inject.Inject;

import java.util.List;

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
   * Saudi Mode Config.
   */
  @Inject
  private SaudiModeConfig saudiModeConfig;
  /**
   * Inject PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * The Navigation header.
   */
  @Expose
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
  @Expose
  private String language;
  /**
   * sitename.
   */
  private String site;

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
        site = CommonUtils.getSiteName(currentResource.getPath());
        AdminPageOption adminPageOption = AdminUtil.getAdminOptions(language, site);
        navigationHeader = getNavigationService()
            .getNavigationHeader(request, getResourceResolver(), language,
                adminPageOption.getHeaderPath(), site);
        navigationHeader.setSearchPage(
            CommonUtils.isResourceOfResourceType(currentResource, SEARCH_RESULT_RESOURCE_TYPE));
        if (null == navigationHeader.getCurrentLanguage()) {
          navigationHeader.setCurrentLanguage(new Link());
        }
        final Link curLanguage = navigationHeader.getCurrentLanguage();
        curLanguage.setCode(language);
        final List<LanguageLink> languages = navigationHeader.getLanguages();
        if (null != languages && !languages.isEmpty()) {
          languages.forEach(lang -> {
            if (lang.getCopy().equals(curLanguage.getCode())) {
              curLanguage.setName(lang.getText());
            }
          });
        }
        navigationHeader.setCurrentLanguage(curLanguage);
      }
    } catch (Exception ex) {
      LOGGER.error("Error at init method", ex);
    }
  }

  @JsonIgnore
  @PostConstruct
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

}
