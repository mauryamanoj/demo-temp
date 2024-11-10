package com.saudi.tourism.core.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * RegionCityExtended object.
 */
@Getter
public class RegionCityExtended extends RegionCity {

  /**
   * Property name for weather group request city id.
   */
  public static final String PN_WEATHER_CITY_ID = "weatherCityId";

  /**
   * Property name for the navigation value of the city displayed in navigation.
   */
  public static final String PN_NAVIGATION_VALUE = "navigationValue";

  /**
   * Preferred days count for trips into this city. Use setter for this field to avoid having
   * null value.
   */
  @JsonIgnore
  private transient Long preferredTripDaysCount;

  /**
   * Images for this city.
   */
  @Expose
  private Image image = new Image();

  /**
   * List of destination tags wrapped into the CategoryTag object.
   */
  @Expose
  private List<CategoryTag> destinationFeatureTags;

  /**
   * Variable to store bestFeature.
   */
  @Expose
  private String bestFeature;

  /**
   * Latitude coordinate for this city.
   */
  @Expose
  private String latitude;

  /**
   * Longitude coordinate for this city.
   */
  @Expose
  private String longitude;

  /**
   * City id for weather group requests.
   */
  @JsonIgnore
  @Expose
  private transient String weatherCityId;

  /**
   * Key for producing short name to be displayed in navigation.
   */
  @Expose
  private String navigationValue;

  /**
   * Short name to be displayed in navigation.
   */
  @Expose
  private String navigationTitle;

  /**
   * Constructor.
   *
   * @param id                     id
   * @param name                   name
   * @param preferredTripDaysCount preferredTripDaysCount
   * @param cityImage              image instance with media for this city
   * @param destinationTags        list of destination tags
   * @param bestFeature            bestFeature
   */
  public RegionCityExtended(final String id, final String name, final Long preferredTripDaysCount,
      final Image cityImage, final List<CategoryTag> destinationTags, final String bestFeature) {
    super(id, name);

    this.setPreferredTripDaysCount(preferredTripDaysCount);

    if (cityImage != null) {
      image = cityImage;
    }
    this.destinationFeatureTags = destinationTags;
    this.bestFeature = bestFeature;
  }

  /**
   * Constructor.
   *
   * @param city                   city
   * @param preferredTripDaysCount preferredTripDaysCount
   * @param cityImage              image instance with media for this city
   * @param destinationTags        list of destination tags
   * @param bestFeature            bestFeature
   */
  public RegionCityExtended(@NotNull final RegionCity city, final Long preferredTripDaysCount,
      final Image cityImage, final List<CategoryTag> destinationTags, final String bestFeature) {
    this(city.getId(), city.getName(), preferredTripDaysCount, cityImage, destinationTags,
        bestFeature);
  }

  /**
   * Setter for {@link #preferredTripDaysCount}.
   *
   * @param preferredTripDaysCount the preferred trip days count to set
   */
  private void setPreferredTripDaysCount(final Long preferredTripDaysCount) {
    this.preferredTripDaysCount = Optional.ofNullable(preferredTripDaysCount)
        .orElse(TripPlanConstants.DEFAULT_PREFERRED_TRIP_DAYS_COUNT);
  }

  /**
   * Constructor.
   *
   * @param city city
   */
  public RegionCityExtended(final RegionCity city) {
    super(city.getId(), city.getName());
  }

  /**
   * Factory constructor matching default with only simple RegionCity instance but with applying
   * additional properties from dictionary value map.
   *
   * @param city                 simple RegionCity instance to copy fields
   * @param additionalProperties value map to apply
   * @param i18n                 localization bundle
   * @return new RegionCityExtended instance
   */
  public static RegionCityExtended createWithAdditionalProperties(final RegionCity city,
      final ValueMap additionalProperties, @NotNull final ResourceBundle i18n) {
    final RegionCityExtended extended = new RegionCityExtended(city);
    applyAdditionalPropsFromValueMap(extended, additionalProperties, i18n);
    return extended;
  }

  /**
   * Factory constructor matching default but with applying additional properties from dictionary
   * value map.
   *
   * @param city                   simple RegionCity instance to copy fields
   * @param preferredTripDaysCount preferred trip days count for this city
   * @param cityImage              image instance for this city
   * @param destinationTags        list of destination tags
   * @param bestFeatureTagPath     best feature tag
   * @param additionalProperties   value map to apply
   * @param i18n                   localization bundle
   * @return new RegionCityExtended instance
   */
  public static RegionCityExtended createWithAdditionalProperties(final RegionCity city,
      final Long preferredTripDaysCount, final Image cityImage,
      final List<CategoryTag> destinationTags, final String bestFeatureTagPath,
      final ValueMap additionalProperties, @NotNull final ResourceBundle i18n) {
    final RegionCityExtended extended =
        new RegionCityExtended(city, preferredTripDaysCount, cityImage, destinationTags,
            bestFeatureTagPath);
    applyAdditionalPropsFromValueMap(extended, additionalProperties, i18n);
    return extended;
  }

  /**
   * Sets additional properties from the value map provided.
   *
   * @param regionCityExtended   the RegionCityExtended instance
   * @param additionalProperties value map with additional properties
   * @param i18n                 localization bundle
   */
  private static void applyAdditionalPropsFromValueMap(final RegionCityExtended regionCityExtended,
      final ValueMap additionalProperties, final @NotNull ResourceBundle i18n) {
    if (additionalProperties == null) {
      return;
    }

    regionCityExtended.setText(additionalProperties.get(Constants.TEXT, String.class));

    regionCityExtended.latitude = additionalProperties.get(Constants.LATITUDE, String.class);
    regionCityExtended.longitude = additionalProperties.get(Constants.LONGITUDE, String.class);
    regionCityExtended.weatherCityId = additionalProperties.get(PN_WEATHER_CITY_ID, String.class);

    final String shortNavTitleKey = additionalProperties.get(PN_NAVIGATION_VALUE, String.class);
    if (StringUtils.isNotBlank(shortNavTitleKey)) {
      regionCityExtended.navigationValue = shortNavTitleKey;
      regionCityExtended.navigationTitle =
          CommonUtils.getI18nStringOrKey(i18n, regionCityExtended.navigationValue);
    }
    if (StringUtils.isBlank(regionCityExtended.navigationTitle)) {
      regionCityExtended.navigationTitle = regionCityExtended.getName();
    }
  }

  /**
   * Clone RegionCityExtended to be able to update its data.
   *
   * @return clone of this instance
   */
  @JsonIgnore
  public RegionCityExtended getCloned() {
    final RegionCityExtended cloned = SerializationUtils.clone(this);
    // Copy transient fields
    cloned.preferredTripDaysCount = this.getPreferredTripDaysCount();
    cloned.weatherCityId = this.getWeatherCityId();
    return cloned;
  }
}
