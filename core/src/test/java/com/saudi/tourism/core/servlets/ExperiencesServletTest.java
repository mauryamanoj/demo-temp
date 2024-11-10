package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.AdminUtil;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ExperiencesServletTest {

  @InjectMocks
  private ExperiencesServlet experienceServlet = new ExperiencesServlet();

  @Mock
  private SaudiTourismConfigs saudiTourismConfig;

  @Mock
  private ExperienceService service;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private AdminPageOption adminPageOption;

  @BeforeEach
  public void setUp(AemContext context) throws IOException {
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
    context.registerService(AdminSettingsService.class, adminSettingsService);
    context.registerService(ExperienceService.class, service);

    AdminUtil.setAdminSettingsService(adminSettingsService);

    when(adminSettingsService.getAdminOptions(anyString(), anyString())).thenReturn(adminPageOption);
    when(adminPageOption.isDisableWebHalayallaPackages()).thenReturn(false);
  }

  @Test
  public void testExperienceAPIPostiveCase(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("city", "Riyadh");
    queryStrings.put("lang", "ar");
    context.request().setParameterMap(queryStrings);
    when(service.getAllExperiences(queryStrings)).thenReturn(new Object());
    experienceServlet.doGet(context.request(), context.response());
    Assertions.assertEquals(200, context.response().getStatus());
  }

  @Test
  public void testExperienceAPIExceptionCase(AemContext context) throws ServletException, IOException {
    Map<String, Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("city", "Riyadh");
    queryStrings.put("lang", "ar");
    context.request().setParameterMap(queryStrings);
    when(service.getAllExperiences(queryStrings)).thenThrow(NullPointerException.class);
    experienceServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

}
