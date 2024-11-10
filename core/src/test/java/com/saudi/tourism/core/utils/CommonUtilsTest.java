package com.saudi.tourism.core.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.exceptions.MissingRequestParameterException;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import org.junit.jupiter.api.Disabled;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Test Class to test link utils.
 */
class CommonUtilsTest {

  private static final String TEST_PATH = "/content/sauditourism/en/test";

  private static final String EXTENSION_TEST_PATH = TEST_PATH + Constants.HTML_EXTENSION;
  public static final String PARAMETER_NAME = "parameterName";
  public static final String PARAMETER_VALUE = "parameterValue";

  private ResourceResolver resourceResolver;
  private TagManager tagManager;
  private Tag mockTag;

  @Test void testIsInternalLinkWithNullPath() {
    Assertions.assertEquals("11-21", CommonUtils
        .convertDateToSTring("2019-11-21T00:00:00.000+01:00", Constants.FORMAT_STARTDATE));
  }

  @Test void convertDateToSTringNull() {
    Assertions.assertEquals(null, CommonUtils.convertDateToSTring(null, Constants.FORMAT_STARTDATE));
  }

  @Test void testDateToStringLanguage() {
    Assertions.assertEquals("Mon 08, Feb", CommonUtils
        .dateToString(CommonUtils.dateToLocalDate("2021-02-08T00:00:00.000+01:00"),
            Constants.FMT_WD_D_M));
  }

  @Disabled
  @Test void testDateToStringRu() {
    Assertions.assertEquals("пн 08, фев", CommonUtils
        .dateToString(CommonUtils.dateToLocalDate("2021-02-08T00:00:00.000+01:00"),
            Constants.FMT_WD_D_M, "ru"));
  }

  @Test void getDateMonth() {
    Assertions.assertEquals("11",
        CommonUtils.getDateMonth("2019-11-21T00:00:00.000+01:00", Constants.FORMAT_MONTH));
  }

  @Test void getCategoryFromTagNameNull() {
    resourceResolver = Mockito.mock(ResourceResolver.class);
    Assertions.assertEquals(new ArrayList<>(),
        CommonUtils.getCategoryFromTagName(null, resourceResolver, "en"));
  }

  @Test void getCategoryFromTagName() {
    resourceResolver = Mockito.mock(ResourceResolver.class);
    tagManager = Mockito.mock(TagManager.class);
    mockTag = Mockito.mock(Tag.class);
    when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    when(tagManager.resolve("/content/cq:tags/sauditourism:tennis")).thenReturn(mockTag);
    when(mockTag.getTitle()).thenReturn("Tennis");
    Assertions.assertEquals(Arrays.asList("Tennis", "Music"), CommonUtils
        .getCategoryFromTagName(new String[] {"sauditourism:tennis", "sauditourism:music"},
            resourceResolver, "en"));
  }

  @Test void isDateBetweenStartEnd() {
    Assertions.assertFalse(CommonUtils.isDateBetweenStartEnd("2020-01-05", "2020-01-15","2020-01"
            + "-21",
        null));
  }

  @Test void isDateBetweenStartEdgecase() {
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-05", null,"2020-01-05T00:00:00.000Z", "2020-01-17T00:00:00"
            + ".000Z"));
   Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-06",null, "2020-01-05T00:00:00.000Z", "2020-01-17T00:00:00"
            + ".000Z"));
   Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-17",null, "2020-01-05T00:00:00.000Z", "2020-01-17T00:00:00"
            + ".000Z"));
  }
  @Test void isDateBetweenStartEndPositive() {
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-21", null,"2019-11-21T00:00:00.000+01:00", null));
     Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-11", null,"2019-11-21T00:00:00.000+01:00", null));
    Assertions.assertFalse(CommonUtils
        .isDateBetweenStartEnd("2019-11-24", null,"2019-11-21T00:00:00.000+01:00", null));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd(null, "2019-11-24","2019-11-21T00:00:00.000+01:00", null));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd(null, "2019-11-24","2019-11-01T00:00:00.000+01:00",
            "2019-11-21T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd(null, "2019-11-22","2019-11-01T00:00:00.000+01:00",
            "2019-11-21T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd(null, "2019-11-09","2019-11-01T00:00:00.000+01:00",
            "2019-11-21T00:00:00.000+01:00"));
  }

  @Test void isDateBetweenStartsec2() {
    Assertions.assertFalse(CommonUtils
        .isDateBetweenStartEnd("2019-11-24",null, "2019-11-21T00:00:00.000+01:00",
            "2019-11-23T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-20",null, "2019-11-21T00:00:00.000+01:00",
            "2019-11-29T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-24",null, "2019-11-21T00:00:00.000+01:00",
            "2020-01-29T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-12-07",null, "2019-12-05T00:00:00.000+01:00",
            "2025-01-29T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-10-03",null, "2019-12-05T00:00:00.000+01:00",
            "2025-01-29T00:00:00.000+01:00"));
  }

  @Test void isDateBetweenStartEndDate() {
    Assertions.assertFalse(CommonUtils
        .isDateBetweenStartEnd("2019-11-24","2019-11-24", "2019-11-21T00:00:00.000+01:00",
            "2019-11-23T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-27","2019-11-28", "2019-11-27T00:00:00.000+01:00",
            "2019-11-27T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-27","2019-11-27", "2019-11-27T00:00:00.000+01:00",
            "2019-11-27T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-24","2019-11-24", "2019-11-21T00:00:00.000+01:00",
            "2019-11-29T00:00:00.000+01:00"));

    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-10-10","2020-01-14", "2019-10-24T00:00:00.000+01:00",
            "2020-01-14T00:00:00.000+01:00"));
  }
  @Test void isDateBetweenStartEndDateBoundary() {
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-11-20","2019-11-29", "2019-11-21T00:00:00.000+01:00",
            "2020-01-29T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-03","2020-01-30", "2020-01-04T00:00:00.000+01:00",
            "2020-01-24T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-02","2020-01-24", "2020-01-04T00:00:00.000+01:00",
            "2020-01-24T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-03","2020-01-24", "2020-01-04T00:00:00.000+01:00",
            "2020-01-24T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-01-04","2020-01-25", "2020-01-04T00:00:00.000+01:00",
            "2020-01-24T00:00:00.000+01:00"));
  }
  @Test void filterDateBetweenStartEndDateBoundary() {
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-12-10","2019-12-20", "2019-11-21T00:00:00.000+01:00",
            "2020-01-29T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2020-02-14","2020-03-14", "2019-12-29T00:00:00.000+01:00",
            "2020-03-07T00:00:00.000+01:00"));
    Assertions.assertTrue(CommonUtils
        .isDateBetweenStartEnd("2019-10-09","2020-01-09", "2019-12-29T00:00:00.000+01:00",
            "2020-03-07T00:00:00.000+01:00"));
  }

  @Test void getPageNameByIndex() {
    Assertions.assertEquals("en",
        CommonUtils.getPageNameByIndex("/content/sauditourism/en/mm/c04-full-bleed-slider", 3));
    Assertions.assertEquals("/content/sauditourism",
        CommonUtils.getPageNameByIndex("/content/sauditourism", 3));
  }

  @Test
  @DisplayName("Test getMandatoryParameter method when parameter is not set")
  void testGetMandatoryParameterWhenNoParameter() {
    SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
    Assertions.assertThrows(MissingRequestParameterException.class, () ->
      CommonUtils.getMandatoryParameter(request, "parameterName"),
        "Mandatory parameterName request parameter is missing.");
  }

  @Test
  @DisplayName("Test getMandatoryParameter method when parameter is set to empty string")
  void testGetMandatoryParameterWhenEmptyString() {
    SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
    when(request.getParameter(PARAMETER_NAME)).thenReturn(StringUtils.EMPTY);
    Assertions.assertThrows(MissingRequestParameterException.class, () ->
            CommonUtils.getMandatoryParameter(request, "parameterName"),
        "Mandatory parameterName request parameter is missing.");
  }

  @Test
  @DisplayName("Test getMandatoryParameter method when parameter is set to value")
  void testGetMandatoryParameterWhenNonEmpty() {
    SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
    when(request.getParameter(PARAMETER_NAME)).thenReturn(PARAMETER_VALUE);
    Assertions.assertEquals(PARAMETER_VALUE, CommonUtils.getMandatoryParameter(request,
        PARAMETER_NAME));
  }

  @Test
  @DisplayName("test toAbsolutePath method with relative path and absolute path inputs")
  void testToAbsolutePath() {
    final String relativePath = "/some/relative/path";
    final String absolutePath = Constants.ROOT_CONTENT_PATH + relativePath;
    String relativePathResult = CommonUtils.toAbsolutePath(relativePath);
    Assertions.assertEquals(absolutePath, relativePathResult);
    Assertions.assertEquals(relativePathResult, CommonUtils.toAbsolutePath(absolutePath));
  }

  @Test
  @DisplayName("Test getListString method with default delimiter parameter")
  void testGetListStringWithDefaultDelimiter() {
    List<AppFilterItem> filterItems = Arrays.asList(
            new AppFilterItem("jeddah", "Jeddah"),
            new AppFilterItem("dammam", "Dammam")
    );
    Assertions.assertEquals("Jeddah, Dammam", CommonUtils.getListString(filterItems));
  }

  @Test
  @DisplayName("Test getListString method with comma delimiter parameter")
  void testGetListStringWithCommaDelimiter() {
    List<AppFilterItem> filterItems = Arrays.asList(
            new AppFilterItem("jeddah", "Jeddah"),
            new AppFilterItem("dammam", "Dammam")
    );
    Assertions.assertEquals("Jeddah,Dammam", CommonUtils.getListString(Constants.COMMA,
            filterItems));
  }

  @Test
  @DisplayName("test getLinkInPageProperties when resource is null")
  void testGetLinkInPagePropertiesWhenResourceIsNull() {
    Assertions.assertNull(CommonUtils.getLinkInPageProperties(null, Link.class));
  }

  @Test
  @DisplayName("test getLinkInPageProperties when resource has no content")
  void testGetLinkInPagePropertiesWhenResourceHasNoContent() {
    Resource resource = mock(Resource.class);
    when(resource.getChild(JcrConstants.JCR_CONTENT)).thenReturn(null);
    Assertions.assertNull(CommonUtils.getLinkInPageProperties(resource, Link.class));
  }

  @Test
  @DisplayName("test getLinkInPageProperties when resource is not adaptable")
  void testGetLinkInPagePropertiesWhenResourceIsNotAdaptable() {
    Resource resource = mock(Resource.class);
    Resource jcrContent = mock(Resource.class);
    when(resource.getChild(JcrConstants.JCR_CONTENT)).thenReturn(jcrContent);
    when(jcrContent.adaptTo(Link.class)).thenReturn(null);
    Assertions.assertNull(CommonUtils.getLinkInPageProperties(resource, Link.class));
  }

  @Test
  @DisplayName("test getLinkInPageProperties when resource is adaptable")
  void testGetLinkInPagePropertiesWhenResourceIsAdaptable() {
    Resource resource = mock(Resource.class);
    Resource jcrContent = mock(Resource.class);
    Link expected = mock(Link.class);
    when(resource.getChild(JcrConstants.JCR_CONTENT)).thenReturn(jcrContent);
    when(jcrContent.adaptTo(Link.class)).thenReturn(expected);
    Link actual = CommonUtils.getLinkInPageProperties(resource, Link.class);
    Assertions.assertNotNull(actual);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("test getFirstSentence")
  void testGetFirstSentence() {
    final String expected = "Some test sentence.";
    Assertions.assertEquals(expected, CommonUtils.getFirstSentence("Some test sentence"));
    Assertions.assertEquals(expected,
        CommonUtils.getFirstSentence("Some test sentence. Second sentence."));
  }

  @Test
  @DisplayName("test stripHtml")
  void testStripHtml() {
    final String expected = "Some test& sentence";

    Assertions.assertEquals(expected, CommonUtils.stripHtml(
        "<p>\r\n<blockquote><b>Some</b>&nbsp;test&amp;<br><i>sentence</i></blockquote>\t</p>"));
  }

  @DisplayName("test toAbsolutePath")
  @Test
  void toAbsolutePath() {
    assertEquals("/content/sauditourism", CommonUtils.toAbsolutePath("/content/sauditourism"));
    assertEquals("/content/sauditourism/", CommonUtils.toAbsolutePath("/content/sauditourism/"));

    final String expected = "/content/sauditourism/en/some/page";
    assertEquals(expected, CommonUtils.toAbsolutePath("en/some/page"));
    assertEquals(expected, CommonUtils.toAbsolutePath("/en/some/page"));
    assertEquals(expected, CommonUtils.toAbsolutePath("/content/sauditourism/en/some/page"));
  }

  @DisplayName("test toAbsolutePath method with language param")
  @Test
  void toAbsolutePath_locale() {
    final String locale = Constants.ARABIC_LOCALE;

    assertEquals("/content/sauditourism/ar",
        CommonUtils.toAbsolutePath("/content/sauditourism", locale));

    final String expected = "/content/sauditourism/ar/some/page";

    assertEquals(expected, CommonUtils.toAbsolutePath("ar/some/page", locale));
    assertEquals(expected, CommonUtils.toAbsolutePath("/ar/some/page", locale));
    assertEquals(expected,
        CommonUtils.toAbsolutePath("/content/sauditourism/ar/some/page", locale));

    assertEquals(expected, CommonUtils.toAbsolutePath("en/some/page", locale));
    assertEquals(expected, CommonUtils.toAbsolutePath("/en/some/page", locale));
    assertEquals(expected,
        CommonUtils.toAbsolutePath("/content/sauditourism/en/some/page", locale));
  }

  @Test
  @DisplayName("correct full path locale")
  void correctFullPathLocale() {
    final String ar = "/content/sauditourism/ar/some/page";
    final String en = "/content/sauditourism/en/some/page";
    assertEquals(ar, CommonUtils.correctFullPathLocale(en, "ar"));
    assertEquals(en, CommonUtils.correctFullPathLocale(ar, "en"));
    assertEquals(en, CommonUtils.correctFullPathLocale(en, "en"));
  }

  @Test
  @DisplayName("Test Encoded S7 URL") void testEncodedURL() {
    final String dam = "/content/dam/saudi-tourism/media/articles/a211-taif-rose-fields/660x370"
        + "/A211 Taif Rose Fields_2.jpg";
    final String s7 =
        "https://scth.scene7.com/is/image/scth/A211 Taif Rose Fields_2-4:crop-760x570?defaultImage=A211 Taif Rose Fields_2-4";
    final String s7Raw = "https://scth.scene7.com/is/image/scth/A211 Taif Rose Fields_2-4";
    final String s7RawEnc = "https://scth.scene7.com/is/image/scth/A211%20Taif%20Rose%20Fields_2-4";
    final String s7Enc = "https://scth.scene7.com/is/image/scth/A211%20Taif%20Rose%20Fields_2-4"
        + ":crop-760x570?defaultImage=A211%20Taif%20Rose%20Fields_2-4";
    final String s7RawResult =
        "https://scth.scene7.com/is/image/scth/A211%20Taif%20Rose%20Fields_2-4";
    final String s7Result = "https://scth.scene7.com/is/image/scth/A211%20Taif%20Rose%20Fields_2-4"
        + ":crop-760x570?defaultImage=A211%20Taif%20Rose%20Fields_2-4";

    assertEquals(dam, CommonUtils.getS7EncodedUrl(dam));
    assertEquals(s7Result, CommonUtils.getS7EncodedUrl(s7));
    assertEquals(s7RawResult, CommonUtils.getS7EncodedUrl(s7Raw));
    assertEquals(s7Result, CommonUtils.getS7EncodedUrl(s7Enc));
    assertEquals(s7RawResult, CommonUtils.getS7EncodedUrl(s7RawEnc));
  }
}
