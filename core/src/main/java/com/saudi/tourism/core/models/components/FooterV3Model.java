package com.saudi.tourism.core.models.components;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NavItemUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class for footer component.
 */
@Slf4j
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class FooterV3Model {

  /**
   * The winter-season title.
   */
  @ValueMapValue
  @Expose
  private String seasonLabel;

  /**
   * Variable to hold list of seasonItems.
   */
  @ChildResource
  @Expose
  private List<FooterSeasonLinksItems> seasonItems;

  /**
   * The Discovery Saudi Title.
   */
  @ValueMapValue
  @Expose
  private String discoveryLabel;

  /**
   * Variable to hold list of discoveryItems.
   */
  @ChildResource
  @Expose
  private List<FooterDiscoveryLinksItems> discoveryItems;

  /**
   * The useful information title.
   */
  @ValueMapValue
  @Expose
  private String usefulInfoLabel;

  /**
   * Variable to hold list of usefulItems.
   */
  @ChildResource
  @Expose
  private List<FooterUsefulLinksItems> usefulItems;

  /**
   * The multiSiteConfigRoot path.
   */
  @ValueMapValue
  @Expose
  private String multiSiteConfigRoot;

  /**
   * The preferenceLabel title.
   */
  @ValueMapValue
  @Expose
  private String preferenceLabel;

  /**
   * Variable to store all multi sites.
   */
  @Expose
  private List<NavItem> multiSiteItems;

  /**
   * Inject PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Variable to specify childDepth.
   */
  private int childDepth = SpecialCharConstants.THREE;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Bact to top .
   */
  @Expose
   private String backToTop;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * publish Mode .
   */
  private boolean publishMode;
  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

    Resource resourceMultiSiteConfig = getResourceResolver().getResource(getMultiSiteConfigRoot());
    multiSiteItems = NavItemUtils
      .generateChildListItems(resourceMultiSiteConfig, pageManager, childDepth, false, false);

    String path =  Optional.ofNullable(currentPage).map(Page::getPath)
        .orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    if (i18nBundle.getString("back-to-top") != null) {
      backToTop = i18nBundle.getString("back-to-top");
    }

  }
}
