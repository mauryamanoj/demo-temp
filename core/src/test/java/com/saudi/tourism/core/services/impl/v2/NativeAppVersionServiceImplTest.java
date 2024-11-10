package com.saudi.tourism.core.services.impl.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppVersion;
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

import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NativeAppVersionServiceImplTest {

  private NativeAppVersionServiceImpl nativeAppVersionServiceImpl;

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletResponse response;

  private String path = "/content/sauditourism/app1/en";

  private NativeAppVersion nativeAppVersion;
  private final AemContext aemContext = new AemContext();
  @BeforeEach
  void setUp() {
    nativeAppVersionServiceImpl = new NativeAppVersionServiceImpl();
    Map<String, Object> props = new HashMap<>();
    props.put("immediate", "true");
    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void getNativeAppVersionTypeShouldReturnAppVersionListIfProvidedAppVersionConfigIsAuthored() {

    //Arrange
    Resource resource = aemContext.load().json("/servlets/datasource/native-app-version.json","/content/sauditourism/app1/en/jcr:content/nativeAppVersion");
    aemContext.currentResource(resource);

    //Act
    nativeAppVersion = nativeAppVersionServiceImpl.getAllVersionInfo(request,path);

    //Assert
    Assertions.assertEquals(2,nativeAppVersion.getAppVersion().size());
  }

  @Test
  void getNativeAppVersionTypeShouldReturnNullIfProvidedAppVersionConfigIsMissing() {
    //Act
    nativeAppVersion = nativeAppVersionServiceImpl.getAllVersionInfo(request,path);

    //Assert
    Assertions.assertEquals(null,nativeAppVersion);
  }
}