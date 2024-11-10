package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ExperiencesDetailsServletTest {

  @InjectMocks
  private ExperienceBookingOptionsServlet servlet = new ExperienceBookingOptionsServlet();

  @Mock
  private SaudiTourismConfigs saudiTourismConfig;

  @Mock
  private ExperienceService service;

  private String experienceId;


  @BeforeEach
  public void setUp(final AemContext context) throws IOException {
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
    context.registerService(ExperienceService.class, service);
    when(saudiTourismConfig.getHalayallaEndPointUrl()).thenReturn(String.format("http://dummy-url"));
    experienceId = "1";
  }

  @Test
  public void testExperienceSuggestionAPIPostiveCase(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Assertions.assertNotNull(saudiTourismConfig.getHalayallaEndPointUrl());
    Map<String, Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("city", "Riyadh");
    queryStrings.put("lang", "ar");
    queryStrings.put("experienceId", experienceId);
    context.request().setParameterMap(queryStrings);
    when(service.getExperienceBookingOptions(queryStrings, experienceId)).thenReturn(new Object());
    servlet.doGet(context.request(), context.response());
    Assertions.assertEquals(200, context.response().getStatus());

  }

  @Test
  public void testExperienceSuggestionAPIExceptionCase(AemContext context) throws ServletException, IOException {
    Assertions.assertNotNull(saudiTourismConfig.getHalayallaEndPointUrl());
    Map<String, Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("city", "Riyadh");
    queryStrings.put("lang", "ar");
    queryStrings.put("experienceId", experienceId);
    context.request().setParameterMap(queryStrings);
    when(service.getExperienceBookingOptions(queryStrings, experienceId)).thenThrow(NullPointerException.class);
    servlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

}
