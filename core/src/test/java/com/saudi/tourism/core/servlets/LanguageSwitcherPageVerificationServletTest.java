package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.*;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.evisa.v1.EvisaConfigModel;
import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import com.saudi.tourism.core.models.components.packages.SimpleSliderModel;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.saudi.tourism.core.utils.PrimConstants.PN_LOCALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LanguageSwitcherPageVerificationServletTest {

  @Mock
  private AdminPageOption adminPageOption;
  @Mock
  private NavigationHeader navigationHeader;

  @Mock
  private ResourceResolver resolver;

  @InjectMocks
  private LanguageSwitcherPageVerificationServlet languageSwitcherPageVerificationServlet;

  @Test
  void doGetShouldReturnBadRequestIfParamsNotProvided(final AemContext context) throws ServletException, IOException {
    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();
    languageSwitcherPageVerificationServlet = new LanguageSwitcherPageVerificationServlet();
    languageSwitcherPageVerificationServlet.doGet(request, response);
    Assertions.assertEquals(400, response.getStatus());
  }

  @Test
  void doGetShouldReturnTrueIfPageAvailable(final AemContext context) throws ServletException, IOException {
    Map<String,Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("pagePath", "/ar/about-e-visa");
    queryStrings.put("currentLanguage", "ar");
    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();
    request.setParameterMap(queryStrings);
    String targetPagePath =  "/content/sauditourism/en/about-e-visa/jcr:content";
    context.load().json("/components/header-config-model/languages.json", Constants.LANGS_PATH);
    context.build().resource(targetPagePath, "jcr:title", "Tourist visas").commit();
    context.addModelsForClasses(Link.class);
    languageSwitcherPageVerificationServlet = new LanguageSwitcherPageVerificationServlet();
    languageSwitcherPageVerificationServlet.doGet(request, response);
    assertNotNull(context.currentResource(targetPagePath));
    assertNotNull(context.currentResource(Constants.LANGS_PATH));
    assertEquals("{\"pageUnavailableLocales\":[\"ar\"]}", response.getOutputAsString());
  }

}