package com.saudi.tourism.core.services.impl.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppFeatureModel;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ShowingFeatureServiceImplTest {

  private ShowingFeatureServiceImpl showingFeatureServiceImpl;
  @Mock
  private SlingHttpServletRequest request ;

  @Mock
  private SlingHttpServletResponse response;

  private NativeAppFeatureModel nativeAppFeatureModel;

  private String path = "/content/sauditourism/app1/en";

  private final AemContext aemContext = new AemContext();

  @BeforeEach
  void setUp() {
    showingFeatureServiceImpl = new ShowingFeatureServiceImpl();
    Map<String, Object> props = new HashMap<>();
    props.put("immediate", "true");
    request = aemContext.request();
    response = aemContext.response();

  }

  @Test
  void getNativeAppFeatureModelTypeShouldReturnAppFeatureListIfProvideFeaturedConfigIsAuthored() {
    //Arrange
    Resource resource = aemContext.load().json("/servlets/datasource/showing-feature-app-version.json", "/content/sauditourism/app1/en/jcr:content/nativeAppFeatureApi");
    aemContext.currentResource(resource);
    //Act
     nativeAppFeatureModel = showingFeatureServiceImpl.getAllFeaturesInfo(request,path);
    //Assert
     Assertions.assertEquals(true,nativeAppFeatureModel.getFeaturesList().size()>2);
  }

  @Test
  void getNativeAppFeatureTypeShouldReturnNullIfProvidedAppFeatureResourceIsMissing() {
    //Act
    nativeAppFeatureModel = showingFeatureServiceImpl.getAllFeaturesInfo(request,path);

    //Assert
    Assertions.assertEquals(null,nativeAppFeatureModel);
  }
}