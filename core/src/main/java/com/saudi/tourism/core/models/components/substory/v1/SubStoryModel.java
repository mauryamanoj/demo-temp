package com.saudi.tourism.core.models.components.substory.v1;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.story.SubStoryCFModel;
import com.saudi.tourism.core.utils.CommonUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Story Component Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class SubStoryModel {

  /** ResourceBundleProvider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /** DISPLAYED ITEMS BY DEFAULT. */
  private static final Integer MAX_DISPLAYED_ITEMS = 3;

  /** Resource Resolver. */
  @Inject
  private transient ResourceResolver resourceResolver;

  /** Current Resource. */
  @Self
  private transient Resource currentResource;

  /** Title. */
  @Expose
  private String title;

  /** Description. */
  @Expose
  private String description;

  /** More Label. */
  @Expose private String moreLabel;

  /** CTA Link. */
  @ChildResource
  @Expose
  private Link link;

  /** Get Directions Link. */
  @ChildResource
  @Expose
  private Link directionsLink;

  /** Story CF Path. */
  @ValueMapValue
  private String storyCFPath;

  /** Gallery Items. */
  @Expose
  private List<ImageBanner> gallery;

  @PostConstruct
  void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (Objects.isNull(currentPage)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(storyCFPath);
    if (Objects.isNull(cfResource)) {
      return;
    }

    final var cfModel = cfResource.adaptTo(SubStoryCFModel.class);
    if (Objects.isNull(cfModel)) {
      return;
    }

    final var language = CommonUtils.getLanguageForPath(currentPage.getPath());
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));

    final var getDirectionsLabel = CommonUtils.getI18nString(i18n, I18nConstants.I18_GET_DIRECTIONS);

    if (StringUtils.isNotBlank(getDirectionsLabel)) {
      directionsLink.setText(getDirectionsLabel);
    }

    title = cfModel.getTitle();
    description = cfModel.getAboutDescription();
    gallery = cfModel.getImageBanners();

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
                      "crop-375x210",
                      "1280",
                      "420",
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
