package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Getter
class DestinationPageModelTest {

  private RegionCityService citiesService;
  private SlingSettingsService settingsService;
  private ResourceBundleProvider i18nProvider;
  private UserService userService;
  private Resource currentResource;
  private ResourceResolver resourceResolver;
  private ResourceResolver curresourceResolver;
  private TagManager tagManager;
  private Tag tag1;
  private Tag tag2;

  private static final String PATH = "/content/sauditourism/en/do/break-free/explore-tabuk";
  private static final String SHORT_PATH = "/en/do/break-free/explore-tabuk";
  private static final String REGION_ID = "riyadh-region";
  private static final String REGION_NAME = "Riyadh region";
  private static final String REGION_CODE = "Riyadh region code";

  private static final String CITY_ID = "riyadh";
  private static final String CITY_NAME = "Riyadh";
  private static final String CITY_CODE = "Riyadh code";

  static final String TAG_1 = "tag1";
  static final String TAG_2 = "tag2";
  static final String TAG_1_PATH = Constants.TAGS_URL + TAG_1;
  static final String TAG_2_PATH = Constants.TAGS_URL + TAG_2;
  static final String TAG_1_LOCALIZED = "localizedTag1";
  static final String TAG_2_LOCALIZED = "localizedTag2";

  @BeforeEach
  void setUp() {
    citiesService = mock(RegionCityService.class);
    userService = mock(UserService.class);
    currentResource = mock(Resource.class);
    resourceResolver = mock(ResourceResolver.class);
    curresourceResolver = mock(ResourceResolver.class);
    tagManager = mock(TagManager.class);
    tag1 = mock(Tag.class);
    tag2 = mock(Tag.class);
    i18nProvider = mock(ResourceBundleProvider.class);
    when(i18nProvider.getResourceBundle(new Locale("en"))).thenReturn(mock(ResourceBundle.class));
    when(currentResource.getPath()).thenReturn(PATH);
    when(curresourceResolver.map(PATH)).thenReturn(SHORT_PATH);
    when(currentResource.getResourceResolver()).thenReturn(curresourceResolver);
    when(userService.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    when(tagManager.resolve(TAG_1_PATH)).thenReturn(tag1);
    when(tagManager.resolve(TAG_2_PATH)).thenReturn(tag2);
    when(tag1.getTitle(any())).thenReturn(TAG_1_LOCALIZED);
    when(tag2.getTitle(any())).thenReturn(TAG_2_LOCALIZED);
  }

  @Test
  @DisplayName("Test getCode when city field is empty")
  void testCodeWhenRegion() {
    settingsService = mock(SlingSettingsService.class);
    Set<String> modes = new HashSet<>();
    modes.add(Externalizer.PUBLISH);
    doReturn(modes).when(settingsService).getRunModes();
    DestinationPageModel model =
        new DestinationPageModel(REGION_NAME, null, currentResource, citiesService,settingsService,
          i18nProvider);
    assertEquals(REGION_ID, model.getRegionId());
    assertNull(model.getCityId());
  }

  @Test
  @DisplayName("Test url is appended with html extension")
  void testUrlValue() {
    settingsService = mock(SlingSettingsService.class);
    Set<String> modes = new HashSet<>();
    modes.add(Externalizer.PUBLISH);
    doReturn(modes).when(settingsService).getRunModes();
    DestinationPageModel model = new DestinationPageModel(
        REGION_NAME,
        null,
        currentResource,
        citiesService,
        settingsService,
        i18nProvider
    );
    assertEquals(SHORT_PATH, model.getUrl());
  }

  @Test
  @DisplayName("Test bestFor contains empty list when no data is stored in JCR")
  void testBestForWhenNoData() {
    settingsService = mock(SlingSettingsService.class);
    Set<String> modes = new HashSet<>();
    modes.add(Externalizer.PUBLISH);
    doReturn(modes).when(settingsService).getRunModes();
    DestinationPageModel model = new DestinationPageModel(
        REGION_NAME,
        null,
        currentResource,
        citiesService,
        settingsService,
        i18nProvider
    );
    assertEquals(Collections.emptyList(), model.getBestFor());
  }

  @Test
  @DisplayName("Test getCode when city field is provided")
  void testCodeWhenCity() {
    settingsService = mock(SlingSettingsService.class);
    Set<String> modes = new HashSet<>();
    modes.add(Externalizer.PUBLISH);
    doReturn(modes).when(settingsService).getRunModes();
    DestinationPageModel model = new DestinationPageModel(
        REGION_NAME,
        CITY_NAME,
        currentResource,
        citiesService,
        settingsService,
        i18nProvider
    );
    assertEquals(REGION_ID, model.getRegionId());
    assertEquals(CITY_ID, model.getCityId());
  }

  @Test
  @DisplayName("Test getCode when city field is provided")
  void testCodeWhenCityIsProvided() {
    settingsService = mock(SlingSettingsService.class);
    Set<String> modes = new HashSet<>();
    modes.add(Externalizer.PUBLISH);
    doReturn(modes).when(settingsService).getRunModes();
    final RegionCityExtended mockCity = mock(RegionCityExtended.class);
    final CategoryTag ct1 = new CategoryTag(TAG_1, TAG_1_LOCALIZED);
    final CategoryTag ct2 = new CategoryTag(TAG_2, TAG_2_LOCALIZED);
    //noinspection ResultOfMethodCallIgnored
    doReturn(Arrays.asList(ct1, ct2)).when(mockCity).getDestinationFeatureTags();

    doReturn(mockCity).when(citiesService).getRegionCityExtById(eq(CITY_ID), anyString());

    DestinationPageModel model =
        new DestinationPageModel(REGION_NAME, CITY_NAME, currentResource, citiesService, settingsService,i18nProvider);
    assertEquals(2, model.getBestFor().size());
    assertEquals(TAG_1_LOCALIZED, model.getBestFor().get(0));
    assertEquals(TAG_2_LOCALIZED, model.getBestFor().get(1));
  }
}
