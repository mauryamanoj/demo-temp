package com.saudi.tourism.core.models.mobile.components.autosections.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerImage;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Objects;

public final class AtomsBuilder {
  /** Atoms Builder. */
  private AtomsBuilder() {
  }

  /**
   * This method aims to prepare item categories tags.
   *
   * @param categoryTags categoryTags
   * @param resolver resource resolver
   * @param settingsService settingsService
   * @param language locale
   * @return categories
   */
  public static List<Category> getCategoriesFromCFTags(
      String[] categoryTags,
      ResourceResolver resolver,
      SlingSettingsService settingsService,
      String language) {
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    if (ArrayUtils.isNotEmpty(categoryTags)) {
      return Arrays.stream(categoryTags)
          .flatMap(p -> TagUtils.getTagOrChildren(p, tagManager, resolver, language).stream())
          .peek(tag -> TagUtils.processTagsForMobile(tag, resolver, settingsService))
          .map(
              category -> {
                if (StringUtils.isNotBlank(category.getTitle())) {
                  category.setTitle(category.getTitle().toUpperCase());
                }
                return category;
              })
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * This method aims to prepare item categories tags.
   *
   * @param categoryTags categoryTags
   * @param resolver resource resolver
   * @param settingsService settingsService
   * @param language locale
   * @return categories
   */
  public static List<Category> getCategoriesFromThingsToDo(
      List<String> categoryTags,
      ResourceResolver resolver,
      SlingSettingsService settingsService,
      String language) {
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    if (CollectionUtils.isNotEmpty(categoryTags)) {
      return categoryTags.stream()
          .flatMap(p -> TagUtils.getTagOrChildren(p, tagManager, resolver, language).stream())
          .peek(tag -> TagUtils.processTagsForMobile(tag, resolver, settingsService))
          .map(
              category -> {
                if (StringUtils.isNotBlank(category.getTitle())) {
                  category.setTitle(category.getTitle().toUpperCase());
                }
                return category;
              })
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * This method aims to prepare item Poi tags.
   *
   * @param tag tag
   * @param resolver resource resolver
   * @param language locale
   * @return poi tag
   */
  public static String getPoiTypeFromThingsToDo(
      String tag,
      ResourceResolver resolver,
      String language) {
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    if (StringUtils.isNotBlank(tag)) {
      Locale locale = new Locale(language);
      return CommonUtils.getTagName(tag, tagManager, locale);
    }
    return StringUtils.EMPTY;
  }

  /**
   * This method aims to build Location for aut item.
   *
   * @param lat latitude
   * @param lng language
   * @param cfModel destination CF model
   * @return location
   */
  public static Location buildLocation(String lat, String lng, DestinationCFModel cfModel) {
    Location location = new Location();
    Destination destination = null;
    String title = cfModel.getTitle();
    if (StringUtils.isNotBlank(title)) {
      title = cfModel.getTitle().toUpperCase();
    }
    if (Objects.nonNull(cfModel)) {
      destination = Destination.builder()
              .id(cfModel.getId())
              .title(title)
              .lat(cfModel.getLatitude())
              .lng(cfModel.getLongitude())
              .build();
    }
    location.setLat(lat);
    location.setLng(lng);
    location.setDestination(destination);

    return location;
  }

  /**
   * This method aims to build customAction for auto item.
   *
   * @param cta cta
   * @return customAction
   */
  public static CustomAction buildCustomAction(Link cta) {
    CustomAction customAction = new CustomAction();
    if (Objects.nonNull(cta)) {
      customAction.setTitle(cta.getText());
      customAction.setCta(new Cta("WEB", cta.getUrl(), null));
    }
    customAction.setShow(true);
    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
    return customAction;
  }

  /**
   * This method aims to build media gallery for auto item.
   *
   * @param bannerImages banner images
   * @param resolver resource resolver
   * @param settingsService settings service
   * @return mediaGallery
   */
  public static MediaGallery buildMediaGallery(
      List<BannerImage> bannerImages,
      ResourceResolver resolver,
      SlingSettingsService settingsService) {
    MediaGallery gallery = new MediaGallery();
    if (CollectionUtils.isNotEmpty(bannerImages)) {
      bannerImages.stream()
          .filter(Objects::nonNull)
          .map(BannerImage::getImage)
          .filter(Objects::nonNull)
          .map(Image::getFileReference)
          .filter(Objects::nonNull)
          .findFirst()
          .ifPresent(
              fileReference -> {
                gallery.setUrl(
                    LinkUtils.getAuthorPublishAssetUrl(
                        resolver,
                        fileReference,
                        settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                gallery.setType("image");
              });
    }

    return gallery;
  }

  /**
   * This method aims to build media gallery atom from things to do.
   *
   * @param bannerImages banner images
   * @param resolver resource resolver
   * @param settingsService settings service
   * @return mediaGallery
   */
  public static MediaGallery buildMediaGalleryFromImages(
      List<Image> bannerImages, ResourceResolver resolver, SlingSettingsService settingsService) {
    MediaGallery gallery = new MediaGallery();
    if (CollectionUtils.isNotEmpty(bannerImages)) {
      bannerImages.stream()
          .filter(Objects::nonNull)
          .map(Image::getFileReference)
          .filter(Objects::nonNull)
          .findFirst()
          .ifPresent(
              fileReference -> {
                gallery.setUrl(
                    LinkUtils.getAuthorPublishAssetUrl(
                        resolver,
                        fileReference,
                        settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                gallery.setType("image");
              });
    }

    return gallery;
  }

  /**
   * This method aims to build season atom for item response model.
   *
   * @param cfModel season CF model
   * @return season
   */
  public static ItemResponseModel.Season buildSeason(SeasonCFModel cfModel) {
    if (Objects.nonNull(cfModel)) {
      return ItemResponseModel.Season.builder()
          .id(cfModel.getContentFragment().getName())
          .title(cfModel.getTitle())
          .build();
    }
    return null;
  }

}
