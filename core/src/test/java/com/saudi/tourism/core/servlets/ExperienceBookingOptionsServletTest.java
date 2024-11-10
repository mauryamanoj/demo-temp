package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class ExperienceBookingOptionsServletTest {

  private ExperienceDetailsServlet servlet;

  private SaudiTourismConfigs saudiTourismConfig;
  private ExperienceService service;
  private String testResponse;
  private String experienceId;
  
 
  public final AemContext context = new AemContext();


  @BeforeEach public void setUp() throws IOException {
    saudiTourismConfig = Mockito.mock(SaudiTourismConfigs.class);
    service = Mockito.mock(ExperienceService.class);
    when(saudiTourismConfig.getHalayallaEndPointUrl()).thenReturn(String.format("http://dummy-url"));
    experienceId="1";
    servlet = new ExperienceDetailsServlet();
  }

  @Test public void testExperienceSuggestionAPIPostiveCase()
      throws IllegalArgumentException, ServletException, IOException {
	    Assertions.assertNotNull(saudiTourismConfig.getHalayallaEndPointUrl());
	    Map<String,Object> queryStrings = new HashMap<String, Object>();
		queryStrings.put("city", "Riyadh");
		queryStrings.put("lang", "ar");
		queryStrings.put("experienceId", experienceId);
		context.request().setParameterMap(queryStrings);
		when(service.getExperienceDetails(queryStrings,experienceId)).thenReturn(new Object());
		servlet.doGet(context.request(), context.response());
	    Assertions.assertEquals(200 , context.response().getStatus());
  }
  
  @Test public void testExperienceSuggestionAPIExceptionCase() throws ServletException, IOException {
	  	Assertions.assertNotNull(saudiTourismConfig.getHalayallaEndPointUrl());
	    Map<String,Object> queryStrings = new HashMap<String, Object>();
		queryStrings.put("city", "Riyadh");
		queryStrings.put("lang", "ar");
		queryStrings.put("experienceId", experienceId);
		context.request().setParameterMap(queryStrings);
		when(service.getExperienceDetails(queryStrings,experienceId)).thenThrow(NullPointerException.class);
		servlet.doGet(context.request(), context.response());
	    Assertions.assertNotNull(context.response());
	  }

}
