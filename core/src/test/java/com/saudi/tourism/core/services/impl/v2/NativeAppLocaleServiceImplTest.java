package com.saudi.tourism.core.services.impl.v2;

import com.saudi.tourism.core.models.nativeApp.page.AppLocale;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppLocaleModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NativeAppLocaleServiceImplTest {

  private NativeAppLocaleServiceImpl nativeAppLocaleServiceImpl;

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletResponse response;

  private AemContext aemContext = new AemContext();

  private NativeAppLocaleModel nativeAppLocaleModel;
  private Resource resource;
  private Resource res;
  private String path = "/content/sauditourism/app1/en";

  @BeforeEach
  void setUp() {
    nativeAppLocaleServiceImpl = new NativeAppLocaleServiceImpl();
    request = aemContext.request();
    response = aemContext.response();
    aemContext.addModelsForClasses(AppLocale.class, NativeAppLocaleModel.class);

  }

  @Test
  void getNativeAppLocaleTypeShouldReturnAppLocaleListIfProvidedAppLocaleConfigPath() {
    //Arrange
    resource = aemContext.load().json("/servlets/datasource/native-app-locale.json", "/content/sauditourism/app1/en/jcr:content/nativeAppLocales");
    aemContext.currentResource(resource);
    //Act
    nativeAppLocaleModel=nativeAppLocaleServiceImpl.getAllLocaleInfo(request, path);
    //Assert
    Assertions.assertEquals(2, nativeAppLocaleModel.getAppLocale().size());
  }

  @Test
  void getNativeAppLocaleTypeShouldReturnNullIfProvidedAppLocaleConfigPathIsMissing() {

    //Act
    nativeAppLocaleModel = nativeAppLocaleServiceImpl.getAllLocaleInfo(request, path);

    //Assert
    Assertions.assertEquals(null,nativeAppLocaleModel );

  }
}