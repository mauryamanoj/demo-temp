package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.saudi.tourism.core.beans.bestexperience.Experience;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

@Component(service = AutoItemsAdapter.class, immediate = true, property = {"type=experience"})
public class AutoItemsExperienceAdapter implements AutoItemsAdapter {

  /**
   * Constant OFFSET.
   */
  private static final Integer OFFSET = 0;
  /**
   * Constant LIMIT.
   */
  private static final Integer LIMIT = -1;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /** ThingsToDo CF Service. */
  @Reference
  private transient ThingsToDoCFService thingsToDoCFService;

  /**
   * Experience Service.
   */
  @Reference
  private transient ExperienceService experienceService;

  @Override
  public List<ItemResponseModel> processItems(ResourceResolver resourceResolver, SlingSettingsService settingsService,
      @NonNull MobileRequestParams request, @NonNull List<String> types) {

    List<ItemResponseModel> items = new ArrayList<>();
    Experience experience = new Experience();
    FetchThingsToDoRequest fetchThingsToDoRequest = request.getThingsToDoRequest();
    fetchThingsToDoRequest.setType(types);

    Map<String, Object> queryStrings = buildExperienceQueryParameters(fetchThingsToDoRequest, request);
    JsonElement halaYJsonResponse = fetchAllExperiences(queryStrings);
    if (halaYJsonResponse != null) {
      experience = new Gson().fromJson(halaYJsonResponse.toString(), Experience.class);
    }

    if (Objects.nonNull(experience) && CollectionUtils.isNotEmpty(experience.getData())) {
      items =
          experience.getData().stream()
              .filter(Objects::nonNull)
              .map(
                  experienceData -> {
                    try {
                      return MobileUtils.buildItemFromExperience(
                          experienceData,
                          resourceResolver,
                          settingsService,
                          request.getLocale(),
                          i18nProvider);
                    } catch (ParseException e) {
                      LOGGER.warn("Couldn't parse date into calendar date");
                      return null;
                    }
                  })
              .collect(Collectors.toList());
    }
    return items;
  }

  /**
   * Build Experience Query Parameters.
   *
   * @param request FetchThingsToDoRequest
   * @param mobileParams MobileRequestParams
   * @return Map<String, Object>
   */
  private Map<String, Object> buildExperienceQueryParameters(
      FetchThingsToDoRequest request, MobileRequestParams mobileParams) {
    Map<String, Object> queryStrings = new HashMap<>();

    // To get all the experiences
    queryStrings.put("per_page", "-1");

    Optional.ofNullable(request.getLocale()).ifPresent(locale -> queryStrings.put("lang", locale));
    Optional.ofNullable(request.getDestination())
        .ifPresent(destination -> queryStrings.put("city", destination));
    Optional.ofNullable(request.getFreeOnly())
        .ifPresent(freeOnly -> queryStrings.put("free_only", freeOnly));
    Optional.ofNullable(request.getStartDate())
        .ifPresent(startDate -> queryStrings.put("start_date", startDate));
    Optional.ofNullable(request.getEndDate())
        .ifPresent(endDate -> queryStrings.put("end_date", endDate));
    Optional.ofNullable(mobileParams.getDiscounted())
        .ifPresent(endDate -> queryStrings.put("discounted", mobileParams.getDiscounted()));
    Optional.ofNullable(mobileParams.getMinPrice())
        .ifPresent(endDate -> queryStrings.put("minPrice", mobileParams.getMinPrice()));
    Optional.ofNullable(mobileParams.getMaxPrice())
        .ifPresent(endDate -> queryStrings.put("maxPrice", mobileParams.getMaxPrice()));
    return queryStrings;
  }

  /**
   * Fetch All Experiences. Call HalaYalla API to get all experiences.
   *
   * @param queryStrings HalaYalla queryStrings
   * @return All Experiences JSON Format
   */
  private JsonElement fetchAllExperiences(Map<String, Object> queryStrings) {
    try {
      return (JsonElement) experienceService.getAllExperiences(queryStrings);
    } catch (IOException e) {
      LOGGER.error("Error while getting All Experiences", e);
      return null;
    }
  }
}
