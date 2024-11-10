package com.saudi.tourism.core.models.components.bigslider.v1;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.saudi.tourism.core.services.weather.WeatherConstants.CITY_NAME;
import static com.saudi.tourism.core.utils.Constants.FEATURE_IMAGE;
import static com.saudi.tourism.core.utils.PrimConstants.PN_REGION;
import static com.saudi.tourism.core.utils.PrimConstants.PN_REGION_ID;
import static com.saudi.tourism.core.utils.PrimConstants.PN_CATEGORY;
import static com.saudi.tourism.core.utils.PrimConstants.PN_PAGE_CATEGORY;
import static com.saudi.tourism.core.utils.PrimConstants.PN_CITY_ID;
/**
 * Slide data build from BigSlideModel or EventDetail model.
 */
@Getter
public class BigSlide {

  /**
   * Desktop crop for big slider.
   */
  @Expose
  public static final String BIG_SLIDER_DESKTOP_CROP = DynamicMediaUtils.DM_CROP_1920_1080;

  /**
   * Mobile crop for big slider.
   */
  @Expose
  public static final String BIG_SLIDER_MOBILE_CROP = DynamicMediaUtils.DM_CROP_375_280;

  /**
   * Field 'locationType'.
   */
  @Expose
  private static final String FIELD_LOCATION_TYPE = "locationType";

  /**
   * Title.
   */
  @Expose
  private String title;

  /**
   * Label Text.
   */
  @Expose
  private String labelText;

  /**
   * Description.
   */
  @Expose
  private String description;

  /**
   * Image.
   */
  @Expose
  private Image image;

  /**
   * Link.
   */
  @Expose
  private Link link;

  /**
   * The event path for Favorites.
   */
  @Expose
  private String favoritePath;

  /**
   * The event id for Favorites.
   */
  @Expose
  private String favoriteId;

  /**
   * The ornamentId.
   */
  @Expose
  private String ornamentId;

  /**
   * Model parameters.
   */
  @Expose
  private Map<String, Object> parameters = new HashMap<>();

  /**
   * Static constructor using EventDetail instance.
   *
   * @param resolver resolver
   * @param eventDetail  event detail
   * @param isPublish    isPublish
   * @param i18n         i18n
   * @param currentResource currentResource
   * @return instance of BigSlide
   */
  public static BigSlide ofEventDetail(ResourceResolver resolver, EventDetail eventDetail, boolean isPublish,
      ResourceBundle i18n, Resource currentResource) {
    BigSlide result = new BigSlide();
    result.title = eventDetail.getTitle();
    result.description = eventDetail.getShortDescription();

    result.favoritePath = eventDetail.getFavId();
    result.favoriteId = eventDetail.getFavId();

    Image eventImage = new Image();
    eventImage.setFileReference(eventDetail.getFeatureEventImage());
    eventImage.setS7fileReference(eventDetail.getS7featureEventImage());
    result.image = eventImage;
    DynamicMediaUtils.setAllImgBreakPointsInfo(result.image, BIG_SLIDER_DESKTOP_CROP,
        BIG_SLIDER_MOBILE_CROP, "1280", "420",
        resolver, currentResource);
    if (Objects.nonNull(eventDetail.getPath())) {
      String url = LinkUtils.getAuthorPublishUrl(resolver, eventDetail.getPath(), isPublish);
      result.link = new Link(url, i18n.getString("Discover"), false);
    }

    result.parameters.put("displayStartDate", eventDetail.getDisplayedStartDate());
    result.parameters.put("displayEndDate", eventDetail.getDisplayedEndDate());
    result.parameters.put(PN_REGION, eventDetail.getRegion());
    return result;
  }

  /**
   * Static constructor using Slide instance.
   * @param slideModel slide
   * @param resourceResolver resource resolver
   * @param currentResource resource
   * @return instance of BigSlide
   */
  public static BigSlide slideToBigSlideModel(Slide slideModel, ResourceResolver resourceResolver,
                                              Resource currentResource) {
    if (slideModel.isHideSlide()) {
      return null;
    }
    BigSlide result = new BigSlide();
    result.title = slideModel.getTitle();
    result.description = slideModel.getDescription();
    result.image = slideModel.getImage();
    result.link = slideModel.getLink();
    result.labelText = slideModel.getLabelText();
    result.favoritePath = slideModel.getFavoritePath();
    result.ornamentId = slideModel.getOrnamentId();
    DynamicMediaUtils.setAllImgBreakPointsInfo(result.image, "crop-1920x1080",
        "crop-375x667", "1280", "420",
        resourceResolver, currentResource);
    return result;
  }

  /**
   * Static constructor using map item instance.
   * @param mapItem mapItem
   * @param regionCityService regionCityService
   * @param lang lang
   * @param scene7Domain scene7Domain
   * @param isCnServer isCnServer
   * @param currentResource currentResource
   * @param resourceResolver resourceResolver
   * @return slide
   */
  public static BigSlide ofMap(final Resource mapItem, RegionCityService regionCityService,
      String lang, String scene7Domain, boolean isCnServer, ResourceResolver resourceResolver,
                               Resource currentResource) {
    Slide slide = mapItem.adaptTo(Slide.class);
    BigSlide bigSlide = slideToBigSlideModel(slide, resourceResolver, currentResource);
    if (Objects.isNull(bigSlide)) {
      return null;
    }

    ValueMap valueMap = mapItem.getValueMap();
    String regionId = slide.getRegion();
    String cityId = valueMap.get(CITY_NAME, String.class);

    if (StringUtils.isNotBlank(cityId)) {
      bigSlide.parameters.put(FIELD_LOCATION_TYPE, CITY_NAME);
      bigSlide.parameters.put(PN_CITY_ID, cityId);
    } else {
      bigSlide.parameters.put(FIELD_LOCATION_TYPE, PN_REGION);
    }
    bigSlide.parameters.put(PN_REGION_ID, regionId);

    bigSlide.title = Optional.ofNullable(bigSlide.parameters.get(PN_CITY_ID))
        .map(cId -> regionCityService.getRegionCityById((String) cId, lang))
        .map(RegionCity::getName).orElse(null);
    if (Objects.isNull(bigSlide.title)) {
      bigSlide.title = Optional.ofNullable(regionCityService.getRegionCityById(regionId, lang))
          .map(RegionCity::getName).orElse(null);
    }
    return bigSlide;
  }

  /**
   * Convert path to Category slide.
   * @param path path
   * @param regionCityService regionCityService
   * @param resolver resolver
   * @param isPublish isPublish
   * @param i18n i18n
   * @param currentResource currentResource
   * @return BigSlide
   */
  public static BigSlide ofCategory(final String path, RegionCityService regionCityService,
      final @NotNull ResourceResolver resolver, boolean isPublish,
      ResourceBundle i18n, Resource currentResource) {
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Resource res =
        Optional.ofNullable(pageManager.getPage(path)).map(Page::getContentResource).orElse(null);
    if (Objects.isNull(res)) {
      return null;
    }
    BigSlide slide = new BigSlide();
    ValueMap valueMap = res.getValueMap();
    slide.title = valueMap.get(JCR_TITLE, String.class);
    slide.description = valueMap.get(JCR_DESCRIPTION, String.class);
    slide.image = new Image();
    slide.image.setFileReference(valueMap.get(FEATURE_IMAGE, String.class));
    slide.image.setS7fileReference(valueMap.get("s7featureImage", String.class));
    DynamicMediaUtils.setAllImgBreakPointsInfo(slide.image, BIG_SLIDER_DESKTOP_CROP,
        BIG_SLIDER_MOBILE_CROP, "1280", "420",
        resolver, currentResource);

    slide.favoritePath = LinkUtils.getFavoritePath(path);
    final String lang = CommonUtils.getLanguageForPath(path);
    String cityRegionId = Optional.ofNullable(valueMap.get(CITY_NAME, String.class))
        .orElse(valueMap.get(PN_REGION, String.class));
    if (Objects.nonNull(cityRegionId)) {
      slide.parameters.put(PN_REGION,
          Optional.ofNullable(regionCityService.getRegionCityById(cityRegionId, lang))
              .map(RegionCity::getName).orElse(null));
    }
    String[] tags = valueMap.get(PN_PAGE_CATEGORY, String[].class);
    if (Objects.nonNull(tags) && tags.length > 0) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      List<String> list = new ArrayList<>(2);
      for (int i = 0; i < tags.length && i < 2; i++) {
        list.add(
            Optional.ofNullable(tagManager.resolve(tags[i])).map(t -> t.getTitle(new Locale(lang)))
                .orElse(StringUtils.EMPTY));
      }
      slide.parameters.put(PN_CATEGORY, String.join(", ", list));
    }
    String url = LinkUtils.getAuthorPublishUrl(resolver, path, isPublish);
    slide.link = new Link(url, i18n.getString("Discover"), false);

    return slide;
  }
}
