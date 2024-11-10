package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;

import com.saudi.tourism.core.utils.MobileUtils;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class SectionResponseModelTest {

  @Test
  void testExtractSectionIdWithSectionPath() {
    String path = "/content/sauditourism/mobile/en/sections/section1";

    String id = MobileUtils.extractSectionId(path);

    assertEquals("sections/section1", id);
  }

  @Test
  void testExtractSectionIdWithItemSpecificSectionPath() {
    String path = "/content/sauditourism/mobile/en/items/item1/specificSection1";

    String id = MobileUtils.extractSectionId(path);

    assertEquals("items/item1/specificSection1", id);
  }

  @Test
  void testExtractSectionIdWithInvalidPathPrefix() {
    String path = "/content/someotherprefix/mobile/en/sections/section1";

    String id = MobileUtils.extractSectionId(path);

    assertNull(id);
  }

  @Test
  void testExtractSectionIdWithBlankPath() {
    String path = "";

    String id = MobileUtils.extractSectionId(path);

    assertNull(id);
  }

  @Test
  void testExtractSectionIdWithNullPath() {
    String id = MobileUtils.extractSectionId(null);

    assertNull(id);
  }
}
