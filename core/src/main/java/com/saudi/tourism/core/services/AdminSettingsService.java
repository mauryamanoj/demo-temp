package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.AdminPageAlert;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.CalendarAppModel;
import com.saudi.tourism.core.models.components.ChatbotConfigModel;
import com.saudi.tourism.core.models.components.EventsFiltersSettings;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.models.components.RoadTripSettings;
import com.saudi.tourism.core.models.components.SeasonKeys;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.models.components.UserFeedbackConfigModel;
import com.saudi.tourism.core.models.components.greenTaxi.DownloadAppModel;

import java.util.List;

/**
 * Admin settings service is used to obtain some settings from admin config page.
 */
public interface AdminSettingsService {

  /**
   * Returns admin page options.
   *
   * @param language language to get options
   * @param site site for get options
   * @return AdminPageOption instance
   */
  AdminPageOption getAdminOptions(String language, String site);

  /**
   * Returns admin page alert configurations.
   * @param site site name.
   * @param language language to get options
   * @return AdminPageAlert instance
   */
  AdminPageAlert getAdminAlert(String language, String site);

 /**
   * Returns event filters configurations.
   *
   * @param language language to get options
   * @return EventsFiltersSettings instance
   */
  EventsFiltersSettings getEventsFilters(String language);

  /**
   * Returns things to do filters configurations.
   *
   * @param language language to get options
   * @return List of FiltersSettings instance
   */
  List<FiltersSettings> getThingsToDoFilters(String language);

  /**
   * Returns stories filters configurations.
   *
   * @param language language to get options
   * @return FiltersSettings instance
   */
  FiltersSettings getStoriesFilters(String language);

  /**
   * Fetches whitelisted attraction categories.
   *
   * @param language language to get options
   * @return List of FiltersSettings instance
   */
  List<String> fetchWhitelistedAttractionCategories(String language);

  /**
   * Returns calendar configurations.
   *
   * @param language language to get options
   * @return EventsFiltersSettings instance
   */
  CalendarAppModel getCalendarAppData(String language);
  /**
   * Returns calendar configurations.
   *
   * @param language language to get options
   * @return Chatbot configuration values.
   */
  ChatbotConfigModel getChatbotData(String language);

  /**
   * Returns user feedbacks configurations.
   *
   * @param language language to get options
   * @return User feedback configuration values.
   */
  UserFeedbackConfigModel getUserFeedbackConfig(String language);
  /**
   * Returns package details page configuration.
   *
   * @param language language to get options
   * @return PackagePageSettings instance
   */
  String getPackageDetailsPage(String language);

  /**
   * Returns package page configurations.
   *
   * @param language language to get options
   * @return PackagePageSettings instance
   */
  PackagePageSettings getPackagePageSettings(String language);

  /**
   * Returns road trip page configurations.
   *
   * @param language language to get options
   * @return RoadTripSettings instance
   */
  RoadTripSettings getRoadTripSettings(String language);

  /**
   * get Download app settings for green taxi.
   * @param language locale.
   * @return download app settings.
   */
  DownloadAppModel getGreenTaxiDownloadAppSettings(String language);
  /**
   * get the seasons list for events details page properties.
   * @param language locale.
   * @return download app settings.
   */
  List<SeasonKeys> getSeasonsList(String language);
}
