package com.saudi.tourism.core.servlets.nativeApp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppFeatureModel;
import com.saudi.tourism.core.services.v2.ShowingFeatureService;
import com.saudi.tourism.core.servlets.nativeapp.ShowingFeaturesServlet;
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
class ShowingFeaturesServletTest {

  private ShowingFeaturesServlet showingFeaturesServlet;

  private SlingHttpServletRequest request;

  private SlingHttpServletResponse response;

  @Mock
  private Resource resource;

  @Mock
  private ShowingFeatureService showingFeatureService;
  private NativeAppFeatureModel nativeAppFeatureModel;
  private final AemContext context = new AemContext();

  @BeforeEach
  void setUp(AemContext context) {
    request = context.request();
    response = context.response();
    showingFeaturesServlet = new ShowingFeaturesServlet();
    resource = context.load().json("/servlets/datasource/showing-feature-app-version.json", "/nativeAppFeatureApi");
    Utils.setInternalState(showingFeaturesServlet, "showingFeatureService", showingFeatureService);
  }

  @Test
  void getNativeAppFeatureModelTypeShouldReturn200IfProvidedAppFeatureConfigIsAuthored() throws IOException {
    //Arrange
    Resource res = context.resourceResolver().getResource("/nativeAppFeatureApi");
    nativeAppFeatureModel = res.adaptTo(NativeAppFeatureModel.class);
    Mockito.when(showingFeatureService.getAllFeaturesInfo(any(),anyString())).thenReturn(nativeAppFeatureModel);
    //Act
    showingFeaturesServlet.doGet(request, response);
    //Assert
    Assertions.assertEquals(21,nativeAppFeatureModel.getFeaturesList().size());
  }

  @Test
  void getNativeAppFeatureModelTypeShouldReturn400IfProvidedAppFeatureConfigIsMissing(AemContext context) throws IOException {
    //Arrange
    nativeAppFeatureModel=null;
    Mockito.when(showingFeatureService.getAllFeaturesInfo(any(),anyString())).thenReturn(nativeAppFeatureModel);

    //Act
    showingFeaturesServlet.doGet(request, response);

    //Assert
    Assertions.assertEquals(400,context.response().getStatus());
  }

  @Test
  void getNativeAppFeatureTypeShouldReturnIfProvidedResourceIsNull() throws IOException {
    //Arrange
    doThrow(NullPointerException.class).when(showingFeatureService).getAllFeaturesInfo(any(),anyString());

    //Act
    showingFeaturesServlet.doGet(request, response);

    //Assert
    Assertions.assertEquals(500,context.response().getStatus());
    Assertions.assertTrue(context.response().getOutputAsString().contains("Internal Server Exception"));
  }
}