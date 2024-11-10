package com.saudi.tourism.core.services.impl;

import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.Holidays;
import com.saudi.tourism.core.services.CalendarService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.TestCacheImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Unit test for CalendarServiceImpl class.
 */
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CalendarServiceTest {

  private static final String ANY_LANGUAGE = "any-language";
  private static final String SOME_DATE = "2020-12-31";
  private static final String SOME_HOLIDAY = "New Year";
  private final Holidays EMPTY_MAP = new Holidays();
  private Holidays TEST_MAP;
  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private TestCacheImpl cache;

  @Mock
  private UserService userService;

  private CalendarServiceImpl testService;

  @BeforeEach
  void setUp(final AemContext context) {
    context.registerService(UserService.class, userService);
    context.registerService(Cache.class, cache);

    context.registerInjectActivateService(mock(CalendarServiceImpl.class, CALLS_REAL_METHODS));
    testService = (CalendarServiceImpl) context.getService(CalendarService.class);
    assert testService != null;

    TEST_MAP = new Holidays();
    TEST_MAP.put(SOME_DATE, SOME_HOLIDAY);

    lenient().doReturn(context.resourceResolver()).when(userService).getResourceResolver();
    lenient().doReturn(TEST_MAP).when(testService).queryHolidaysMap(eq(ANY_LANGUAGE));

    cache.init();
  }

  @AfterEach
  void tearDown() {
    cache.clear();
  }

  @Test
  void getConfiguredHolidaysMap() {
    final String language = ANY_LANGUAGE;
    final String memCacheKey = Constants.KEY_PREFIX_ADMIN_HOLIDAYS + language;

    final Holidays holidaysMap = testService.getConfiguredHolidaysMap(language);
    assertSame(TEST_MAP, holidaysMap);

    assertTrue(cache.containsKey(memCacheKey));
  }

  @Test
  void getConfiguredHolidaysMap_empty() {
    final String language = ANY_LANGUAGE;
    final String memCacheKey = Constants.KEY_PREFIX_ADMIN_HOLIDAYS + language;

    doReturn(EMPTY_MAP).when(testService).queryHolidaysMap(eq(language));

    final Holidays holidays = testService.getConfiguredHolidaysMap(language);
    assertSame(EMPTY_MAP, holidays);

    // Shouldn't be stored into the cache
    assertFalse(cache.containsKey(memCacheKey));
  }

  @Test
  void getHolidayNameByDate_not_existing() {
    final String notExistingDate = "2020-01-01";
    final String holidayName = testService.getHolidayNameByDate(notExistingDate, ANY_LANGUAGE);
    assertNull(holidayName);
  }

  @Test
  void getHolidayNameByDate() {
    final String holidayName = testService.getHolidayNameByDate(SOME_DATE, ANY_LANGUAGE);
    assertSame(SOME_HOLIDAY, holidayName);
  }

  @Test
  void queryHolidaysMap_noResource() {
    doCallRealMethod().when(testService).queryHolidaysMap(anyString());

    final Holidays holidaysMap = testService.queryHolidaysMap("not-existing");
    // Should return new empty map
    assertNotSame(EMPTY_MAP, holidaysMap);
    assertEquals(EMPTY_MAP, holidaysMap);
  }

  @Test
  void queryHolidaysMap_Unknown_Resource(final AemContext context) throws PersistenceException {
    final String language = ANY_LANGUAGE;
    final String parentPath = "/content/sauditourism/" + language + "/Configs/admin/jcr:content";
    final String holidaysNode = "holidays";

    doCallRealMethod().when(testService).queryHolidaysMap(anyString());

    // Set holidays resource exists
    final Resource someParentResource = mock(Resource.class);
    doReturn(parentPath).when(someParentResource).getPath();
    context.resourceResolver().create(someParentResource, holidaysNode, Collections.singletonMap(
        // Any resource type except holidays
        JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, NameConstants.NT_PAGE));

    final Holidays holidaysMap = testService.queryHolidaysMap(language);
    // Should return new empty map
    assertNotSame(EMPTY_MAP, holidaysMap);
    assertEquals(EMPTY_MAP, holidaysMap);
  }

  /**
   * Tests both CalendarService.queryHolidaysMap and resource.adaptTo(Holidays.class).
   */
  @Test
  void queryHolidaysMap(final AemContext context) {
    final String language = "en";
    final String holidaysPath =
        "/content/sauditourism/" + language + "/Configs/admin/jcr:content/holidays";
    final String jsonPath = "/components/trip-plans/content-en-Configs-admin-holidays.json";

    doCallRealMethod().when(testService).queryHolidaysMap(anyString());

    // Load holidays resource
    context.load().json(jsonPath, holidaysPath);

    final Holidays holidaysMap = testService.queryHolidaysMap(language);
    assertNotNull(holidaysMap);

    // Test model is adapted correctly
    assertNotNull(holidaysMap.get("2020-03-20"));
    assertEquals("June Solstice", holidaysMap.get("2020-06-20"));
    assertEquals(24, holidaysMap.size());
  }
}
