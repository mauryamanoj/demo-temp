package com.saudi.tourism.core.models.components.contentfragment.menu;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class MenuItemCFModel {

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfig;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /** The resource resolver. */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /** The Resource. */
  @Inject
  private Resource currentResource;

  /** sub items page path. */
  @ValueMapValue
  private List<String> subItemsPagePath;

  /** sub Menu Pages. */
  @ValueMapValue
  private List<String> subMenuPages;

  /** Menu Item Label. */
  @Expose
  @ValueMapValue
  private String title;

  /** Menu Item URL. */
  @Expose
  @ValueMapValue
  private String ctaLink;

  /** Menu Item CF image. */
  @ValueMapValue
  private String menuItemImage;

  /** Menu Item CF s7image. */
  @ValueMapValue
  private String s7menuItemImage;

  /** Menu Item image. */
  @Expose
  private Image image;

  /** View All Label. */
  @Expose
  @ValueMapValue
  private String viewAllLabel;

  /** View All Link. */
  @Expose
  @ValueMapValue
  private String viewAllLink;

  /** The links. */
  @Expose
  private List<SubMenuCF> data = new ArrayList<>();

  @PostConstruct
  public void init() {
    ctaLink =
        LinkUtils.getAuthorPublishUrl(
            resourceResolver,
            ctaLink,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
    viewAllLink =
        LinkUtils.getAuthorPublishUrl(
            resourceResolver,
            viewAllLink,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
    buildImage();
    buildSubMenus();
  }

  /**
   * Builds the image.
   */
  private void buildImage() {
    if (StringUtils.isNotEmpty(menuItemImage)) {
      image = new Image();
      image.setFileReference(menuItemImage);
      image.setMobileImageReference(menuItemImage);
      image.setS7fileReference(s7menuItemImage);
      image.setS7mobileImageReference(s7menuItemImage);
      image.setTransparent(true);
    }
  }

  private void buildSubMenus() {
    if (CollectionUtils.isNotEmpty(subMenuPages)) {
      PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
      data =
          subMenuPages.stream()
              .filter(StringUtils::isNotEmpty)
              .map(
                  i -> {
                    Page page = pageManager.getPage(i);
                    if (page == null) {
                      return null;
                    }

                    var cfReference =
                        page.getProperties()
                            .get(Constants.REFERENCED_FRAGMENT_REFERENCE, StringUtils.EMPTY);

                    var subPageCtaLink =
                        LinkUtils.getAuthorPublishUrl(
                            resourceResolver,
                            page.getPath(),
                            settingsService.getRunModes().contains(Externalizer.PUBLISH));
                    if (StringUtils.isEmpty(subPageCtaLink)) {
                      subPageCtaLink = StringUtils.EMPTY;
                    }

                    if (StringUtils.isNotEmpty(cfReference)) {
                      final var subMenuCF = resourceResolver.getResource(cfReference);
                      if (subMenuCF == null) {
                        return null;
                      }

                      final var subMenu = subMenuCF.adaptTo(SubMenuCF.class);
                      if (subMenu == null) {
                        return null;
                      }

                      subMenu.setCtaLink(subPageCtaLink);
                      return subMenu;
                    } else {
                      final var subPageCtaLabel = page.getNavigationTitle();
                      if (StringUtils.isEmpty(subPageCtaLabel)) {
                        return null;
                      }

                      final var navImage =
                          page.getProperties().get(Constants.NAV_IMAGE, StringUtils.EMPTY);
                      if (StringUtils.isEmpty(navImage)) {
                        return null;
                      }

                      final var s7navImage =
                          page.getProperties().get(Constants.S7_NAV_IMAGE, StringUtils.EMPTY);
                      if (StringUtils.isEmpty(s7navImage)) {
                        return null;
                      }

                      final var subMenuimage = new Image();
                      subMenuimage.setFileReference(navImage);
                      subMenuimage.setMobileImageReference(navImage);
                      subMenuimage.setS7fileReference(s7navImage);
                      subMenuimage.setS7mobileImageReference(s7navImage);
                      subMenuimage.setTransparent(true);

                      return SubMenuCF.builder()
                          .ctaLabel(subPageCtaLabel)
                          .ctaLink(subPageCtaLink)
                          .image(subMenuimage)
                          .build();
                    }
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }
  }
}
