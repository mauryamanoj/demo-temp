package com.saudi.tourism.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MobileUtilsTest {
  @Test
  void testFormatTimeToHHMM_ArabicLocale_HHMM() {
    String time = "14:30";
    String expected = "\u200F" + "02:30 مساء";
    String actual = MobileUtils.formatTimeToHHMM(time, "ar");
    assertEquals(expected, actual);
  }

  @Test
  void testFormatTimeToHHMM_ArabicLocale_HHMMSS() {
    String time = "14:30:00";
    String expected = "\u200F" + "02:30 مساء";
    String actual = MobileUtils.formatTimeToHHMM(time, "ar");
    assertEquals(expected, actual);
  }

  @Test
  void testFormatTimeToHHMM_NonArabicLocale() {
    String time = "14:30";
    String expected = "02:30 PM";
    String actual = MobileUtils.formatTimeToHHMM(time, "en");
    assertEquals(expected, actual);
  }

  @Test
  void testFormatTimeToHHMM_NonArabicLocale_HHMMSS() {
    String time = "14:30:56";
    String expected = "02:30 PM";
    String actual = MobileUtils.formatTimeToHHMM(time, "en");
    assertEquals(expected, actual);
  }

  @Test
  void testFormatTimeToHHMM_InvalidTimeString() {
    String time = "invalid time";
    String expected = "invalid time";
    String actual = MobileUtils.formatTimeToHHMM(time, "ar");
    assertEquals(expected, actual);
  }
}
