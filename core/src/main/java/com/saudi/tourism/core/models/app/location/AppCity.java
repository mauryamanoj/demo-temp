package com.saudi.tourism.core.models.app.location;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.entertainer.EntertainerOffer;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.saudi.tourism.core.utils.Constants.ROOT_CONTENT_PATH;

/**
 * App city.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.RT_APP_CITY_PAGE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})

/**
 * Instantiates a new app city.
 */
@Data
@Slf4j
public class AppCity extends LocationItemModel {

  /**
   * The Event service.
   */
  @JsonIgnore
  @Inject
  private transient EventService eventService;

  /**
   * City.
   */
  @JsonIgnore
  @ValueMapValue
  private String city;
  /**
   * Id.
   */
  @ValueMapValue(name = "city")
  private String id;

  /**
   * region.
   */
  @ValueMapValue(name = "region")
  private String regionId;

  /**
   * Nearest Areas.
   */
  @JsonIgnore
  @ChildResource
  private List<AreaModel> nearestAreas;

  /**
   * Areas around.
   */
  private List<String> areasAround;

  /**
   * Photo Collection.
   */
  @JsonIgnore
  @ChildResource
  private List<PhotoModel> photoCollection;

  /**
   * Photo Gallery.
   */
  private List<String> photoGallery;

  /**
   * List of package ids.
   */
  private List<String> packages;

  /**
   * previewImage.
   */
  @ValueMapValue
  private String previewImage;

  /**
   * mapBackgroundImage.
   */
  @ValueMapValue
  private String mapBackgroundImage;

  /**
   * bestFor.
   */
  @ValueMapValue
  @Named("bestForTags")
  private String[] bestFor;
  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * Empty Package Banner detail.
   */
  @ChildResource
  private EmptyPackageBannerModel emptyPackagesBanner;

  /**
   * is Promoted.
   */
  @ValueMapValue
  private boolean isPromote;

  /**
   * city rank.
   */
  @JsonIgnore
  @ValueMapValue
  private Integer rank;

  /**
   * events.
   */
  @Getter
  @Setter
  private List<EventDetail> events;

  /**
   * The entertainerOffer.
   */
  @ChildResource
  private EntertainerOffer entertainerOffer;

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  public void init() {
    convertTagsToNames(bestFor, currentResource);
    if (StringUtils.isNotBlank(city)) {
      id = AppUtils.pathToID(city);
    }
    PageManager pageManager = currentResource.getResourceResolver().adaptTo(PageManager.class);
    LocationRecommendationsModel recommendations = getRecommendations();
    if (Objects.nonNull(recommendations) && Objects.nonNull(recommendations.getItems())) {
      for (LocationRecommendationModel item: recommendations.getItems()) {
        if (Objects.nonNull(item) && Objects.nonNull(item.getLinks())) {
          convertLinkList(item.getLinks(), pageManager);
        }
      }
    }
    setAreasAround(convertObjToStrList(nearestAreas));
    setPhotoGallery(convertObjToStrPhotos(photoCollection));
    EventsRequestParams eventParams = new EventsRequestParams();
    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    if (StringUtils.isNotBlank(path)) {
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);
      eventParams.setLocale(language);
    } else {
      eventParams.setLocale("en");
    }
    eventParams.setCity(Arrays.asList(new String[]{id}));
    eventParams.setFeatured("true");
    if (null != eventService) {
      try {
        setEvents(eventService
            .getFilteredEvents(eventParams).getData());
      } catch (RepositoryException ex) {
        LOGGER.error("Could not get list of featured events in cities api", ex);
      }
    }
    if (null == rank) {
      rank = Integer.MAX_VALUE;
    }
  }

  /**
   * Convert obj to str photos.
   *
   * @param photoCollections the photo collections
   * @return the list
   */
  private List<String> convertObjToStrPhotos(List<PhotoModel> photoCollections) {
    List<String> photoList = new ArrayList<>();
    if (null != photoCollections) {
      photoCollections.forEach(a -> photoList.add(a.getPhoto()));
    }
    return photoList;
  }

  /**
   * Convert obj to str list.
   *
   * @param areas Areas.
   * @return list of areas.
   */
  private List<String> convertObjToStrList(List<AreaModel> areas) {
    List<String> areaList = new ArrayList<>();
    if (null != areas) {
      areas.forEach(a -> areaList.add(a.getArea()));
    }
    return areaList;
  }

  /**
   * Convert LocationLink to AppCityLocaltionLink.
   * @param links list of links
   * @param pageManager pageManager
   */
  static void convertLinkList(List<LocationLink> links, PageManager pageManager) {
    for (int i = 0; i < links.size(); i++) {
      LocationLink link = links.get(i);
      if (Objects.nonNull(link)) {
        links.set(i, new AppCityLocaltionLink(link, pageManager));
      }
    }
  }

  /**
   * ConvertTagsToNames.
   * @param tags tags
   * @param resource resource
   */
  static void convertTagsToNames(String[] tags, Resource resource) {
    if (Objects.nonNull(tags)) {
      String path = Optional.ofNullable(resource.getParent()).map(Resource::getPath)
          .orElse(StringUtils.EMPTY);
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);
      Locale locale = new Locale(language);
      TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
      for (int i = 0; i < tags.length; i++) {
        if (Objects.nonNull(tags[i])) {
          tags[i] = CommonUtils.getTagName(tags[i], tagManager, locale);
        }
      }
    }
  }

  /**
   * AppCityLocaltionLink.
   */
  /**
   * To string.
   *
   * @return the java.lang. string
   */
  @Data
  private static class AppCityLocaltionLink extends LocationLink {

    /**
     * id.
     */
    private String id;

    /**
     * path.
     */
    private String type;

    /**
     * Constructor from LocationLink.
     * @param locationLink LocationLink
     * @param pageManager pageManager
     */
    AppCityLocaltionLink(LocationLink locationLink,
        final PageManager pageManager) {
      setLink(locationLink.getLink());
      setTitle(locationLink.getTitle());
      setExternalMode(locationLink.getExternalMode());
      if (Objects.nonNull(getLink())) {
        type = AppUtils.getAppTypeFromLink(getLink(), pageManager);
        if (getLink().startsWith(ROOT_CONTENT_PATH)) {
          Page page = pageManager.getPage(getLink());
          if (Objects.nonNull(page) && Objects.nonNull(page.getContentResource())) {
            id = AppUtils.getPageID(page.getContentResource().getValueMap(), getLink());
          }
        }
      }

      if (StringUtils.isNotBlank(locationLink.getId())) {
        id = locationLink.getId();
      }
    }
  }
}
