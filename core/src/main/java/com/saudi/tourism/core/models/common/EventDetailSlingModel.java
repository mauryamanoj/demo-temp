package com.saudi.tourism.core.models.common;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.NavItem;
import com.saudi.tourism.core.models.components.NavMenuBase;
import com.saudi.tourism.core.models.components.events.StickyEventModel;
import com.saudi.tourism.core.services.NavigationService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NavItemUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.saudi.tourism.core.utils.SpecialCharConstants.THREE;

/**
 * The type Event detail model.
 */
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class EventDetailSlingModel {

  /**
   * Variable resourceType is fetched form a Page.
   */
  @RequestAttribute
  private String navResourcePath;

  /**
   * Inject PageManager.
   */
  @ScriptVariable
  private PageManager pageManager;

  /**
   * Variable to store all multi sites.
   */
  @Getter
  private List<NavItem> multiSiteItems;

  /**
   * Variable to store eVisaItems Configs.
   */
  @Getter
  private List<NavItem> eVisaItems;

  /**
   * Variable to eVisa.
   */
  @Getter
  private Link eVisa;

  /**
   * The EventDetail.
   */
  @Getter
  private StickyEventModel eventDetail;
  /**
   * The Navigation service.
   */
  @Getter
  @Inject
  private NavigationService navigationService;
  /**
   * The Resource resolver.
   */
  @Getter
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * The Request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * publish mode .
   */
  private Boolean publishMode;
  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {
    publishMode = false;
    try {
      Resource currentResource = currentPage.getContentResource();
      if (Objects.nonNull(currentResource)) {
        eventDetail = currentResource.adaptTo(StickyEventModel.class);
        if (Objects.nonNull(eventDetail)) {
          NavMenuBase navMenuBase =
              Optional.ofNullable(navResourcePath).map(path -> resourceResolver.getResource(path))
                  .map(res -> res.adaptTo(NavMenuBase.class)).orElse(null);
          if (Objects.nonNull(navMenuBase)) {
            Resource resMultiSiteConfig =
                getResourceResolver().getResource(navMenuBase.getMultiSiteConfigRoot());
            multiSiteItems =
                NavItemUtils.generateChildListItems(resMultiSiteConfig, pageManager, THREE, false, publishMode);
            Resource resourceEVisaConfig =
                getResourceResolver().getResource(navMenuBase.getEVisaConfig());
            eVisaItems =
                NavItemUtils.generateChildListItems(resourceEVisaConfig, pageManager, THREE, false, publishMode);
            String language = CommonUtils.getPageNameByIndex(currentResource.getPath(),
                Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
            AdminPageOption adminPageOption =
                AdminUtil.getAdminOptions(language, CommonUtils.getSiteName(currentResource.getPath()));
            eVisa = getNavigationService()
                .getNavigationHeader(request, getResourceResolver(), language,
                    adminPageOption.getHeaderPath(), CommonUtils.getSiteName(currentResource.getPath())).getEvisa();
          }
        }
      }
    } catch (Exception ex) {
      LOGGER.error(" Error in reading event details from page properties");
    }
  }

}
