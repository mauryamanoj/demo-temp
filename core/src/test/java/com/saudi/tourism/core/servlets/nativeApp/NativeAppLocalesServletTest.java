package com.saudi.tourism.core.servlets.nativeApp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppLocaleModel;
import com.saudi.tourism.core.services.v2.NativeAppLocaleService;
import com.saudi.tourism.core.servlets.nativeapp.NativeAppLocalesServlet;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NativeAppLocalesServletTest {

  private SlingHttpServletRequest request ;

  private SlingHttpServletResponse response;

  private NativeAppLocalesServlet nativeAppLocalesServlet;

  private NativeAppLocaleModel nativeAppLocaleModel;

  @Mock
  private NativeAppLocaleService nativeAppLocaleService;

  private final AemContext context = new AemContext();
  @BeforeEach
  void setUp() {
    request = context.request();
    response = context.response();
    context.load().json("/servlets/datasource/native-app-locale.json","/nativeAppLocales");
    nativeAppLocalesServlet = new NativeAppLocalesServlet();
    Utils.setInternalState(nativeAppLocalesServlet, "nativeAppLocaleService", nativeAppLocaleService);
  }

  @Test
  void getNativeAppLocaleTypeShouldReturn200IfProvidedAppLocaleConfigIsAuthored() throws IOException {
   //Arrange
    Resource res = context.resourceResolver().getResource("/nativeAppLocales");
    nativeAppLocaleModel  = res.adaptTo(NativeAppLocaleModel.class);
    Mockito.when(nativeAppLocaleService.getAllLocaleInfo(any(),anyString())).thenReturn(nativeAppLocaleModel);
    //Act
    nativeAppLocalesServlet.doGet(request,response);
    //Assert
    Assertions.assertEquals(200,context.response().getStatus());
  }
  @Test
  void getNativeAppLocaleTypeShouldReturn404IfAppLocaleConfigIsMissing() throws IOException {
    //Arrange
    nativeAppLocaleModel = null;
    Mockito.when(nativeAppLocaleService.getAllLocaleInfo(any(),anyString())).thenReturn(nativeAppLocaleModel);

    //Act
    nativeAppLocalesServlet.doGet(request, response);
    //Assert
    Assertions.assertEquals(404,context.response().getStatus());
  }

  @Test
  void getNativeAppLocaleTypeShouldReturnIfProvidedResourceIsNull() throws IOException {
    //Arrange
    doThrow(NullPointerException.class).when(nativeAppLocaleService).getAllLocaleInfo(any(),anyString());

    //Act
    nativeAppLocalesServlet.doGet(request, response);

    //Assert
    Assertions.assertEquals(500,context.response().getStatus());
    Assertions.assertTrue(context.response().getOutputAsString().contains("InternalServerError While fetching the App Locales"));
  }

}