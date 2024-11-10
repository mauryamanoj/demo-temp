package com.saudi.tourism.core.models.components.mediagallery.v1;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/** Media Gallery Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
@Slf4j
public class MediaGalleryModel {

  /** ResourceBundleProvider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /** DISPLAYED ITEMS BY DEFAULT. */
  private static final Integer MAX_DISPLAYED_ITEMS = 3;

  /** Resource Resolver. */
  @Inject private ResourceResolver resourceResolver;

  /** Current Resource. */
  @Self private Resource currentResource;

  /** More Label. */
  @Expose private String moreLabel;

  /** Gallery. */
  @Expose private List<ImageBanner> gallery;

  @PostConstruct
  protected void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return;
    }

    final var cfPath =
        currentPage.getProperties().get(Constants.REFERENCED_FRAGMENT_REFERENCE, String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfPath);
    if (cfResource == null) {
      return;
    }

    final var cfModel = cfResource.adaptTo(MediaGalleryCFModel.class);
    if (cfModel == null) {
      return;
    }

    final var language = CommonUtils.getLanguageForPath(currentPage.getPath());
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));

    gallery = cfModel.getImages();

    moreLabel = StringUtils.EMPTY;

    if (CollectionUtils.isNotEmpty(gallery)
        && CollectionUtils.size(gallery) > MAX_DISPLAYED_ITEMS) {
      int additionalItems = gallery.size() - MAX_DISPLAYED_ITEMS;
      moreLabel =
          "+" + additionalItems + " " + CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_MORE);
    }

    if (CollectionUtils.isNotEmpty(gallery)) {
      gallery.stream()
          .forEach(
              i -> {
                if (StringUtils.equals("image", i.getType())) {
                  i.setTypeLabel(CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_IMAGE));
                }

                if (StringUtils.equals("video", i.getType())) {
                  i.setTypeLabel(CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_VIDEO));
                }

                if (i.getImage() != null) {
                  DynamicMediaUtils.setAllImgBreakPointsInfo(
                      i.getImage(),
                      "crop-1920x1080",
                      "crop-760x570",
                      "",
                      "1023",
                      currentResource.getResourceResolver(),
                      currentResource);
                }
              });
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
