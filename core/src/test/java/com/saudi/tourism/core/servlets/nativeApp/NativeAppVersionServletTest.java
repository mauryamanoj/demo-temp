package com.saudi.tourism.core.servlets.nativeApp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppVersion;
import com.saudi.tourism.core.services.v2.NativeAppVersionService;
import com.saudi.tourism.core.servlets.nativeapp.NativeAppVersionServlet;
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
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NativeAppVersionServletTest {

  private NativeAppVersionServlet nativeAppVersionServlet;

  private SlingHttpServletRequest request ;

  private SlingHttpServletResponse response;

  private NativeAppVersion nativeAppVersion;


  @Mock
  private NativeAppVersionService nativeAppVersionService;

  private final  AemContext context = new AemContext();


  @BeforeEach
  void setUp() {
    request = context.request();
    response = context.response();
    context.load().json("/servlets/datasource/native-app-version.json","/nativeAppVersion");
    nativeAppVersionServlet = new NativeAppVersionServlet();
    Utils.setInternalState(nativeAppVersionServlet, "nativeAppVersionService", nativeAppVersionService);
  }

  @Test
  public void getNativeAppVersionTypeShouldReturn200IfProvidedAppVersionConfigIsAuthored() throws IOException {
    //Arrange
    Resource res = context.resourceResolver().getResource("/nativeAppVersion");
    nativeAppVersion  = res.adaptTo(NativeAppVersion.class);
    Mockito.when(nativeAppVersionService.getAllVersionInfo(any(),anyString())).thenReturn(nativeAppVersion);
   //Act
    nativeAppVersionServlet.doGet(request,response);
    //Assert
    Assertions.assertEquals(2,nativeAppVersion.getAppVersion().size());
    Assertions.assertEquals(200,context.response().getStatus());
  }

  @Test
  void getNativeAppVersionTypeShouldReturn404IfProvidedAppVersionConfigIsMissing() throws IOException {
    //Arrange
    nativeAppVersion = null;
    Mockito.when(nativeAppVersionService.getAllVersionInfo(any(),anyString())).thenReturn(nativeAppVersion);
    //Act
    nativeAppVersionServlet.doGet(request, response);

    //Assert
    Assertions.assertEquals(404,context.response().getStatus());
  }

  @Test
  void getNativeAppVersionTypeShouldReturnIfProvidedResourceIsNull() throws IOException {
    //Arrange
    doThrow(NullPointerException.class).when(nativeAppVersionService).getAllVersionInfo(any(),anyString());

    //Act
    nativeAppVersionServlet.doGet(request, response);

    //Assert
    Assertions.assertEquals(500,context.response().getStatus());
    Assertions.assertTrue(context.response().getOutputAsString().contains("InternalServerError While fetching the App Version"));
  }

}