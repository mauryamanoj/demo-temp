package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import com.saudi.tourism.core.utils.NavItemUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.SocialChannelModel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;

import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Class for footer component.
 */
@Slf4j
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterConfigModel {
    /**
     * Inject PageManager.
     */
  @ScriptVariable
  private PageManager pageManager;
  /**
  * Variable for footerLinksConfigsRoot root information.
  */
  @Getter
  @ValueMapValue(name = "footerLinksConfigsRoot")
  @Expose
  private String footerLinksConfigsRoot;

  /**
   * Variable for footerLinksConfigs secondary information.
   */
  @Getter
  @Expose
  @ValueMapValue(name = "footerLinksConfigsSecondary")
  private String footerLinksConfigsSecondary;

  /**
   * The homepage path.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String logoPath;

  /**
   * The copyright text.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String copyright;

  /**
   * The social links title.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String socialTitle;
  /**
   * The Footer logo icon.
   */
  @Getter
  @ValueMapValue
  private String logoIcon;
  /**
   * Hide ornament.
   */
  @Getter
  @ValueMapValue
  private String hideOrnament;

  /**
   * Variable to hold list of socialItems.
   */
  @Getter
  @Expose
  @ChildResource
  private List<SocialChannelModel> socialItems;

  /**
   * The call center title.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String callCenterTitle;

  /**
   * Variable to hold list of callCenterItems.
   */
  @Getter
  @Expose
  @ChildResource
  private List<DictItem> callCenterItems;

  /**
   * The download section title.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String downloadTitle;

  /**
   * Variable to hold list of download items.
   */
  @Getter
  @Expose
  @ChildResource
  private List<Link> downloadItems;

  /**
   * Authority link.
   */
  @Getter
  @Expose
  @ChildResource
  private Link authorityLink;

  /**
     * Variable for current resource.
     */
  @Getter
  @ScriptVariable
  private Page currentPage;

    /**
     * Variable Injecting resourceResolver.
     */
  @Getter
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Variable to specify childDepth.
   */
  @Getter
  @Expose
  private int childDepth = SpecialCharConstants.THREE;

  /**
   * Variable to store all footer links.
   */
  @Getter
  @Expose
  private List<NavItem> footerLinkItems;

  /**
   * Variable to store all footer links.
   */
  @Getter
  @Expose
  private List<NavItem> footerLinkSecondaryItems;

    /**
     * Variable to hold the current site locale code.
     */
  @Getter
  @Expose
  private String localeCode;

  /**
   * check Publish mode .
   */
  private Boolean publishMode;
  /**
   * Variable - componentHeading to hold footer 4th column title.
   */
  @Getter
  @Expose
  @ChildResource
  private ComponentHeading componentHeading;
  /**
   * I18n Resource Bundle provider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;
  /**
   * Variable to hold the current Language.
   */
  @Getter
  @Expose
  private String language;
  /**
   * Inject FooterV3Model .
   */
  @Self
  @Inject
  @Expose
  private FooterV3Model footerV3Model;

  /**
   * setting service.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;
     /**
     * init method of sling model.
     */
  @PostConstruct
  protected void init() {
    Resource resourceRootConfigs =
            getResourceResolver().getResource(getFooterLinksConfigsRoot());
    Resource resourceSecondaryConfigs =
        getResourceResolver().getResource(getFooterLinksConfigsSecondary());
    publishMode = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    footerLinkItems =
            NavItemUtils.generateChildListItems(resourceRootConfigs,
                        pageManager,
                        childDepth,
                        false, publishMode);
    footerLinkSecondaryItems =
        NavItemUtils.generateChildListItems(resourceSecondaryConfigs,
            pageManager,
            childDepth,
            false, publishMode);

    InheritanceValueMap ivm =
                new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
    localeCode = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);
    String lang = LanguageUtil.getLocale(localeCode).getDisplayLanguage();
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(localeCode));
    language = CommonUtils.getI18nString(i18n, lang);
    logoPath = LinkUtils.getAuthorPublishUrl(resourceResolver, logoPath,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
  /**
   *
   * @return json data.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
