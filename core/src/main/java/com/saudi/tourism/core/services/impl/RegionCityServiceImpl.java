package com.saudi.tourism.core.services.impl;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.ImmutableList;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.MemHolder;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.services.impl.CalendarServiceImpl.CALENDAR_SERVICE_DESCRIPTION;
import static com.saudi.tourism.core.utils.Constants.RT_APP_REGION_PAGE;

/**
 * Implementation of the RegionCityService, is used to retrive cities and regions.
 */
@Component(service = RegionCityService.class,
           immediate = true,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + CALENDAR_SERVICE_DESCRIPTION})
@Slf4j
public class RegionCityServiceImpl implements RegionCityService {
  /**
   * The constant CITY_FIXED_CONTENT_UTILS_PATH.
   */
  private static final String CITY_FIXED_CONTENT_UTILS_PATH =
      "sauditourism/components/content/utils/city-fixed";
  /**
   * Cities memory cache key.
   */
  private static final String ALL_MEM_CACHE_KEY_PREFIX = "regioncity-all-dic:";

  /**
   * Field categoryTags.
   */
  private static final String DESTINATION_TAGS = "destinationFeatureTags";
  /**
   * Field bestFeature.
   */
  private static final String BEST_FEATURE = "bestFeature";
  /**
   * Field preferredTripDaysCount.
   */
  private static final String PREFERRED_TRIP_DAYS_COUNT = "preferredTripDaysCount";
  /**
   * ResourceBundleProvider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;
  /**
   * The User service.
   */
  @Reference
  private UserService userService;
  /**
   * The cache.
   */
  @Reference
  private Cache memCache;

  @Override public List<RegionCity> getAll(final String locale) {
    return getAllFromCache(locale);
  }

  @Override public List<RegionCity> getCities(final String locale) {
    return getAll(locale).stream()
        .filter(regionCity -> !regionCity.getId().endsWith(Constants.REGION_SUFFIX))
        .collect(Collectors.toList());
  }

  @Override public RegionCity getRegionCityById(String id, String locale) {
    String memKey = ALL_MEM_CACHE_KEY_PREFIX + locale;
    RegionCityHolder holder = (RegionCityHolder) memCache.get(memKey);
    if (Objects.isNull(holder)) {
      getAll(locale);
      holder = (RegionCityHolder) memCache.get(memKey);
    }
    return holder.getMap().get(id);
  }

  @Override public RegionCityExtended getRegionCityExtById(String id, String locale) {
    String memKey = Constants.EXT_CITY_MEM_CACHE_KEY_PREFIX + locale;
    RegionCityExtHolder holder = (RegionCityExtHolder) memCache.get(memKey);
    if (Objects.isNull(holder)) {
      getCitiesExt(locale);
      holder = (RegionCityExtHolder) memCache.get(memKey);
    }
    assert holder != null;
    return Optional.ofNullable(holder.getMap().get(id)).map(RegionCityExtended::getCloned)
        .orElse(null);
  }

  @Override public List<RegionCity> getRegions(final String locale) {
    return getAll(locale).stream()
        .filter(regionCity -> regionCity.getId().endsWith(Constants.REGION_SUFFIX))
        .collect(Collectors.toList());
  }

  @Override public List<RegionCityExtended> getCitiesExt(final String locale) {
    String memKey = Constants.EXT_CITY_MEM_CACHE_KEY_PREFIX + locale;
    final RegionCityExtHolder holder = (RegionCityExtHolder) memCache.get(memKey);
    if (Objects.nonNull(holder)) {
      return holder.getList();
    }
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      // Additional data from the city-fixed dictionary
      final Resource dictionaryResource = resolver.getResource(CITY_FIXED_CONTENT_UTILS_PATH);
      if (Objects.isNull(dictionaryResource)) {
        throw new IllegalArgumentException("The path is not accessible");
      }
      final Map<String, ValueMap> additionalPropertiesMap = new HashMap<>();
      CommonUtils.iteratorToStream(dictionaryResource.listChildren()).map(ResourceUtil::getValueMap)
          .forEach(valueMap -> {
            final String id = valueMap.get(Constants.VALUE, String.class);
            if (StringUtils.isNotBlank(id)) {
              additionalPropertiesMap.put(id, valueMap);
            }
          });

      // Get data from configuration page
      final Resource configPageContentResource =
          resolver.getResource(Constants.GLOBAL_CITIES_EXT_DATA_CONTENT);
      Map<String, Resource> childMap;
      if (Objects.nonNull(configPageContentResource)) {
        childMap = CommonUtils.iteratorToStream(configPageContentResource.listChildren())
            .collect(Collectors.toMap(Resource::getName, Function.identity()));
      } else {
        childMap = Collections.emptyMap();
      }
      List<RegionCityExtended> list =
          createRegionCityExtList(resolver, childMap, locale, additionalPropertiesMap);
      Map<String, RegionCityExtended> map =
          list.stream().collect(Collectors.toMap(RegionCityExtended::getId, Function.identity()));
      memCache.add(memKey, new RegionCityExtHolder(list, map));
      return list;
    }
  }

  /**
   * Create immutable list of cities.
   * @param resolver resolver
   * @param childMap childMap
   * @param locale locale
   * @param additionalPropertiesMap additionalPropertiesMap
   *
   * @return immutable list of cities
   */
  private List<RegionCityExtended> createRegionCityExtList(ResourceResolver resolver, Map<String,
      Resource> childMap, String locale, Map<String, ValueMap> additionalPropertiesMap) {
    Locale loc = new Locale(locale);
    final ResourceBundle i18n = i18nProvider.getResourceBundle(loc);
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    List<RegionCity> cityList = getAll(locale);
    final ImmutableList.Builder<RegionCityExtended> builder = ImmutableList.builder();
    for (RegionCity city : cityList) {
      final String cityId = city.getId();
      final ValueMap fixedProperties = additionalPropertiesMap.get(cityId);

      final Resource child = childMap.get(cityId);
      if (Objects.isNull(child)) {
        builder
            .add(RegionCityExtended.createWithAdditionalProperties(city, fixedProperties, i18n));
      } else {
        ValueMap valueMap = child.getValueMap();
        String[] categoryTag = valueMap.get(DESTINATION_TAGS, String[].class);
        List<CategoryTag> destinationTags =
            CommonUtils.buildCategoryTagsList(categoryTag, resolver, locale);
        String bestFeatureTagPath = valueMap.get(BEST_FEATURE, String.class);
        if (Objects.nonNull(bestFeatureTagPath)) {
          bestFeatureTagPath = CommonUtils.getTagName(bestFeatureTagPath, tagManager, loc);
        }

        // Media for this city (image child resource)
        Image cityImage = null;
        final Resource imageResource = child.getChild(Constants.PN_IMAGE);
        if (imageResource != null) {
          cityImage = imageResource.adaptTo(Image.class);
        }

        builder.add(RegionCityExtended.createWithAdditionalProperties(city,
            valueMap.get(PREFERRED_TRIP_DAYS_COUNT, Long.class), cityImage, destinationTags,
            bestFeatureTagPath, fixedProperties, i18n));
      }
    }
    return builder.build();
  }

  @Override
  public RegionCityExtended searchRegionCityByCoords(final String locale, final String latitude,
      final String longitude) {
    return getCitiesExt(locale).parallelStream().filter(
        regionCity -> StringUtils.equals(latitude, regionCity.getLatitude()) && StringUtils
            .equals(longitude, regionCity.getLongitude())).findAny().orElse(null);
  }

  /**
   * Get all cities and regions from cache.
   *
   * @param locale locale
   * @return list of cities and regions
   */
  private List<RegionCity> getAllFromCache(final String locale) {
    String memKey = ALL_MEM_CACHE_KEY_PREFIX + locale;
    RegionCityHolder holder = (RegionCityHolder) memCache.get(memKey);
    if (Objects.nonNull(holder)) {
      return holder.getList();
    }
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    List<RegionCity> list = loadAll();
    List<RegionCity> transList = new ArrayList<>();
    for (RegionCity regionCity : list) {
      transList.add(
          new RegionCity(regionCity.getId(), CommonUtils.getI18nString(i18n, regionCity.getId()),
              regionCity.getText()));
    }
    list = ImmutableList.copyOf(transList);
    Map<String, RegionCity> map =
        list.stream().collect(Collectors.toMap(RegionCity::getId, Function.identity()));
    memCache.add(memKey, new RegionCityHolder(list, map));
    return list;
  }

  /**
   * Load all cities and regions from CRX.
   *
   * @return cities and regions from CRX
   */
  @NotNull
  private List<RegionCity> loadAll() {
    String memKey = ALL_MEM_CACHE_KEY_PREFIX + "crx";
    List<RegionCity> list = (List<RegionCity>) memCache.get(memKey);
    if (Objects.nonNull(list)) {
      return list;
    }
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      Resource root = resolver.getResource(CITY_FIXED_CONTENT_UTILS_PATH);
      if (Objects.isNull(root)) {
        throw new IllegalArgumentException("The path is not accessible");
      }
      final ImmutableList.Builder<RegionCity> builder = ImmutableList.builder();
      CommonUtils.iteratorToStream(root.listChildren()).map(res -> {
        final RegionCity regionCity = res.adaptTo(RegionCity.class);
        if (regionCity == null) {
          LOGGER.error("Region/city resource adapting error, path: {}", res.getPath());
        }
        return regionCity;
      }).filter(Objects::nonNull).forEach(builder::add);
      list = builder.build();
      memCache.add(memKey, list);
      return list;
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't load city dictionary", e);
    }
  }


  /**
   * RegionCity memory holder class for handling cities and regions. It's used instead of Generic
   * to avoid 'Unchecked Cast' Intellij warning.
   */
  private static final class RegionCityHolder extends MemHolder<RegionCity> {
    /**
     * Default constructor with a list and a map.
     *
     * @param list list
     * @param map  map
     */
    RegionCityHolder(@NotNull final List<RegionCity> list,
        @NotNull final Map<String, RegionCity> map) {
      super(list, map);
    }
  }


  /**
   * RegionCityExtended holder. It's used instead of Generic to avoid 'Unchecked Cast' Intellij
   * warning.
   */
  private static final class RegionCityExtHolder extends MemHolder<RegionCityExtended> {
    /**
     * Default constructor with a list and a map.
     *
     * @param list list
     * @param map  map
     */
    RegionCityExtHolder(@NotNull final List<RegionCityExtended> list,
        @NotNull final Map<String, RegionCityExtended> map) {
      super(list, map);
    }
  }

  @Override
  public List<AppRegion> loadAllAppRegionsPages(SlingHttpServletRequest request, String path) {
    ResourceResolver resolver = request.getResourceResolver();
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page root = pageManager.getPage(path);
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    List<AppRegion> list = new ArrayList<>();
    for (@NotNull Iterator<Page> it = root.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_REGION_PAGE)) {
        list.add(page.getContentResource().adaptTo(AppRegion.class));
      }

    }
    return list;
  }
}
