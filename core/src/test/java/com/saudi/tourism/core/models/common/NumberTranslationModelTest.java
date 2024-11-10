package com.saudi.tourism.core.models.common;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class) public class NumberTranslationModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism";
  private static final String EN_LOCALE_EVENT_PATH = CONTENT_PATH + "/en/event";
  private static final String AR_LOCALE_EVENT_PATH = CONTENT_PATH + "/ar/event";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/servlets/app/translation-content.json", CONTENT_PATH);
  }

  @Test public void testNonArabicNumberTranslation(AemContext context) {
    String input = "100125.79";
    NumberTranslationModel model = getModelForTest(input, "number", EN_LOCALE_EVENT_PATH,
        null, context);
    assertNotNull(model);
    assertEquals(input, model.getTranslatedNumber());
  }

  @Test public void testArabicNumberTranslation(AemContext context) {
    String input = "100125.79";
    NumberTranslationModel model = getModelForTest(input, "number", AR_LOCALE_EVENT_PATH,
        null, context);
    assertNotNull(model);
    assertNotEquals(input, model.getTranslatedNumber());
    assertEquals("١٠٠١٢٥.٧٩", model.getTranslatedNumber());
  }

  @Test public void testInvalidNumberTranslation(AemContext context) {
    String input = "Test";
    NumberTranslationModel model = getModelForTest(input, "number", EN_LOCALE_EVENT_PATH,
        null, context);
    assertNotNull(model);
    assertEquals(input, model.getTranslatedNumber());
  }

  @Test public void testNonArabicDateTranslation(AemContext context) {
    String input = "10/31";
    NumberTranslationModel model = getModelForTest(input, "date", EN_LOCALE_EVENT_PATH, "/",
        context);
    assertNotNull(model);
    assertEquals(input, model.getTranslatedNumber());
  }

  @Test public void testArabicDateTranslation(AemContext context) {
    String input = "10-31";
    NumberTranslationModel model = getModelForTest(input, "date", AR_LOCALE_EVENT_PATH, "-",
        context);
    assertNotNull(model);
    assertNotEquals(input, model.getTranslatedNumber());
    assertEquals("١٠-٣١", model.getTranslatedNumber());
  }

  @Test public void testArabicDateTranslation2(AemContext context) {
    String input = "05-08";
    NumberTranslationModel model = getModelForTest(input, "date", AR_LOCALE_EVENT_PATH, "-",
        context);
    assertNotNull(model);
    assertNotEquals(input, model.getTranslatedNumber());
    assertEquals("٥-٨", model.getTranslatedNumber());
  }

  @Test public void testInvalidDateTranslation(AemContext context) {
    String input = "Test";
    NumberTranslationModel model = getModelForTest(input, "date", AR_LOCALE_EVENT_PATH,
        null, context);
    assertNotNull(model);
    assertEquals(input, model.getTranslatedNumber());
  }

  @Test public void testNullType(AemContext context) {
    String input = "10-31";
    NumberTranslationModel model = getModelForTest(input, null, AR_LOCALE_EVENT_PATH,
        null, context);
    assertNotNull(model);
    assertEquals("١٠-٣١", model.getTranslatedNumber());
  }

  @Test public void testNullInput(AemContext context) {
    NumberTranslationModel model = getModelForTest(null, null, AR_LOCALE_EVENT_PATH,
        null, context);
    assertNull(model);
  }

  private NumberTranslationModel getModelForTest(final String input, final String type,
      final String localeEventPath, final String separator, AemContext context) {
    context.currentResource(localeEventPath);
    context.request().setAttribute("input", input);
    context.request().setAttribute("type", type);
    context.request().setAttribute("separator", separator);
    return context.request().adaptTo(NumberTranslationModel.class);
  }
}