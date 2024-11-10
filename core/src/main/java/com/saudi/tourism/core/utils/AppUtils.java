package com.saudi.tourism.core.utils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.page.ComponentInfo;
import com.saudi.tourism.core.utils.mobile.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.ROOT_CONTENT_PATH;
import static com.saudi.tourism.core.utils.Constants.TYPE_EXTERNAL;
import static com.saudi.tourism.core.utils.Constants.TYPE_INTERNAL;

/**
 * App servlet utils.
 */
public final class AppUtils {

  /**
   * Component text type.
   */
  public static final String COMPONENT_TEXT = "component-text";

  /**
   * Component image type.
   */
  public static final String COMPONENT_IMAGE = "component-image";

  /**
   * Define the list of deeplink types as a constant.
   */
  public static final List<String> MOBILE_DEEPLINK_TYPES = Arrays.asList(
      "VISA_REGULATIONS", "TRAVEL_SAVE", "MAP", "EXPERIENCES", "SAUDI_REWARDS"
                                                                         );

  /**
   * Function checking if type is deepLink.
   *
   * @param value value
   * @return boolean
   */
  public static boolean isDeepLinkType(String value) {
    return MOBILE_DEEPLINK_TYPES.contains(value);
  }

  /**
   * Default constructor.
   */
  private AppUtils() {
  }

  /**
   * Gets the component info from parsys.
   *
   * @param resourceResolver resource resolver
   * @param path             resource path
   * @return list of components
   */
  public static List<ComponentInfo> getComponentInfo(ResourceResolver resourceResolver,
      String path) {

    List<ComponentInfo> output = new ArrayList<>();

    Resource resource = resourceResolver.getResource(
        path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
            + Constants.FORWARD_SLASH_CHARACTER + Constants.RESPONSIVE_GRID);

    if (resource != null) {
      Iterator<Resource> iteratorResources = resource.listChildren();

      while (iteratorResources.hasNext()) {
        Resource currentComponent = iteratorResources.next();
        ComponentInfo component = currentComponent.adaptTo(ComponentInfo.class);

        if (currentComponent.getResourceType()
            .equals(Constants.APP_CONTENT_COMPONENT_RESOURCE_TYPE)) {
          component.setType(COMPONENT_TEXT);
        }
        if (currentComponent.getResourceType().equals(Constants.APP_CONTENT_IMAGE_RESOURCE_TYPE)) {
          component.setType(COMPONENT_IMAGE);
        }
        output.add(component);
      }
    }
    return output;
  }

  /**
   * Gets the component info from parsys.
   *
   * @param request request
   * @param path    resource path
   * @return list of components
   */
  public static List<ComponentInfo> getComponentInfo(SlingHttpServletRequest request, String path) {
    return getComponentInfo(request.getResourceResolver(), path);
  }

  /**
   * path to id string.
   *
   * @param path the label
   * @return the id
   */
  public static String pathToID(String path) {
    // for ID with out language, get code from history commit
    return path.replace("/jcr:content", "");
  }

  /**
   * String to id string.
   *
   * @param label the label
   * @return the string
   */
  public static String stringToID(String label) {
    return stringToID(label, false);
  }

  /**
   * String to CamelCase id string.
   *
   * @param label     the label
   * @param camelCase the camel case
   * @return the string
   */
  public static String stringToID(String label, boolean camelCase) {
    if (label == null || label.contains("sauditourism:")) {
      return label;
    }
    String id = label.replace("/", "-");
    if (!camelCase) {
      id = label.toLowerCase();
    }
    return id.replace(" ", "-").replace("'", StringUtils.EMPTY);
  }

  /**
   * Detect page type.
   *
   * @param valueMap value map
   * @param path     the path
   * @return page type
   */
  public static String getPageID(ValueMap valueMap, String path) {
    String resourceType =
        valueMap.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);
    switch (resourceType) {
      case Constants.RT_APP_CITY_PAGE:
        return valueMap.get("city", path);
      default:
        return path;
    }
  }

  /**
   * Detect page type.
   *
   * @param valueMap value map
   * @return page type
   */
  public static String getPageType(ValueMap valueMap) {
    String resourceType =
        valueMap.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);
    switch (resourceType) {
      case Constants.APP_LOCATION_RESOURCE_TYPE:
        if (valueMap.containsKey(Constants.APP_LOCATION_TYPE)) {
          return valueMap.get(Constants.APP_LOCATION_TYPE, String.class);
        } else {
          return StringUtils.EMPTY;
        }
      case Constants.APP_CONTENT_RESOURCE_TYPE:
        return Constants.CONTENT;
      case Constants.MOBILE_ITEM_RESOURCE_TYPE:
        return Constants.ITEM;
      case Constants.RT_APP_REGION_PAGE:
        return "region";
      case Constants.RT_APP_CITY_PAGE:
        return "city";
      case Constants.APP_LEGAL_PAGE_RESOURCE_TYPE:
        return "legal";
      case Constants.APP_PACKAGE_RESOURCE_TYPE:
        return "tourPackage";
      case Constants.APP_CONTACTS_PAGE_RESOURCE_TYPE:
        return "contact";
      case Constants.PACKAGE_DETAIL_RES_TYPE:
        return "package";
      case Constants.RT_APP_SEASON_CAMPAIGN_PAGE:
        return Constants.APP_SEASONAL_CAMPAIGN_TYPE;
      case Constants.RT_APP_SUMMER_CATEGORIES:
        return Constants.APP_SUMMER_CATEGORIES_TYPE;
      case Constants.RT_APP_CRUISE_PAGE:
        return Constants.PV_CRUISE;
      case Constants.EVENT_RES_TYPE:
        return "events";
      case Constants.ATTRACTIONS_RES_TYPE:
        return "attractions";
      case Constants.STORIES_RES_TYPE:
        return "stories";
      case Constants.ACTIVITIES_RES_TYPE:
        return "activities";
      case Constants.TOURS_RES_TYPE:
        return "tours";
      default:
        return StringUtils.EMPTY;
    }
  }

  /**
   * Detect page type based mon mobile item type.
   *
   * @param itemType itemType
   * @return page type
   */
  public static String getPageTypeFromMobileItemType(String itemType) {

    try {
      ItemType type = ItemType.valueOf(itemType.toUpperCase());
      switch (type) {
        case EVENT:
          return "events";
        case ATTRACTION:
          return "attractions";
        case STORY:
          return "stories";
        case ACTIVITY:
          return "activities";
        case TOUR:
          return "tours";
        case POI:
          return "pois";
        case EXPERIENCE:
          return "experiences";
        default:
          return itemType;
      }
    } catch (IllegalArgumentException e) {
      // Handle the case when itemType is not part of the enum
      return itemType;
    }
  }


  /**
   * Detect page type.
   * @param link link
   * @param pageManager pageManager
   * @return page type
   */
  public static String getAppTypeFromLink(
      String link,
      final PageManager pageManager) {

    String type = TYPE_EXTERNAL;

    if (Objects.nonNull(link)) {
      if (link.startsWith(ROOT_CONTENT_PATH)) {
        Page page = pageManager.getPage(link);
        if (Objects.nonNull(page) && Objects.nonNull(page.getContentResource())) {
          type = AppUtils.getPageType(page.getContentResource().getValueMap());
          if (type.equals(StringUtils.EMPTY)) {
            type = TYPE_INTERNAL;
          }
        }
      } else {
        type = TYPE_EXTERNAL;
      }
    }

    return type;
  }

  /**
   * Detect default search Category type.
   *
   * @param resourceType resourceType
   * @return page type
   */
  public static String getDefaultSearchCategory(final String resourceType) {
    switch (resourceType) {
      case Constants.APP_LOCATION_RESOURCE_TYPE:
        return Constants.SEE_DO_SEARCH_CATEGORY;
      case Constants.APP_CONTENT_RESOURCE_TYPE:
        return Constants.MEET_SAUDI_SEARCH_CATEGORY;
      case Constants.RT_APP_REGION_PAGE:
        return Constants.DESTINATIONS_SEARCH_CATEGORY;
      case Constants.RT_APP_CITY_PAGE:
        return Constants.DESTINATIONS_SEARCH_CATEGORY;
      case Constants.APP_LEGAL_PAGE_RESOURCE_TYPE:
        return Constants.MEET_SAUDI_SEARCH_CATEGORY;
      case Constants.APP_PACKAGE_RESOURCE_TYPE:
        return Constants.PACKAGES_SEARCH_CATEGORY;
      case Constants.APP_CONTACTS_PAGE_RESOURCE_TYPE:
        return Constants.MEET_SAUDI_SEARCH_CATEGORY;
      case Constants.PACKAGE_DETAIL_RES_TYPE:
        return Constants.PACKAGES_SEARCH_CATEGORY;
      case Constants.RT_APP_SEASON_CAMPAIGN_PAGE:
        return Constants.CAMPAIGN_SEARCH_CATEGORY;
      case Constants.RT_APP_SUMMER_CATEGORIES:
        return Constants.CAMPAIGN_SEARCH_CATEGORY;
      case Constants.RT_APP_CRUISE_PAGE:
        return Constants.MEET_SAUDI_SEARCH_CATEGORY;
      case Constants.EVENT_RES_TYPE:
        return "events";
      case Constants.ATTRACTIONS_RES_TYPE:
        return "attractions";
      case Constants.STORIES_RES_TYPE:
        return "stories";
      case Constants.ACTIVITIES_RES_TYPE:
        return "activities";
      case Constants.TOURS_RES_TYPE:
        return "tours";
      default:
        return StringUtils.EMPTY;
    }
  }
}
