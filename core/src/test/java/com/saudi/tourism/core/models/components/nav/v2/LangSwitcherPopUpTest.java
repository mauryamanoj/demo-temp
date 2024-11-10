package com.saudi.tourism.core.models.components.nav.v2;

import com.saudi.tourism.core.models.common.LanguageLink;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LangSwitcherPopUpTest {

  private static final String RESOURCE_PATH = "/content/sauditourism/languages/english/jcr:content";

  @BeforeEach
  void setUp(final AemContext context) {
    context.load().json("/components/langSwitchPopUp/content.json", RESOURCE_PATH);
  }
  @Test
  void getProps(final AemContext aemContext) {
    final LanguageLink langSwitcherPopUp = aemContext.currentResource(RESOURCE_PATH).adaptTo(LanguageLink.class);
    Assertions.assertEquals("Current Language Text En", langSwitcherPopUp.getCurrentLangMsg());
    Assertions.assertEquals("Go to Home Page En", langSwitcherPopUp.getCtaLabelTwo());
  }
}