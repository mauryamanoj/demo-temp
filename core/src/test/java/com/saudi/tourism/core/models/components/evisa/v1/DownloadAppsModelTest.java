package com.saudi.tourism.core.models.components.evisa.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DownloadAppsModelTest {

  private static final String RESOURCE_PATH = "/content/sauditourism/en/understand-test/test/jcr:content/root/responsivegrid/e_visa_download_apps";
  //private static String pagePath = "/content/sauditourism/en/understand-test/test";
  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/evisa-download-apps/content.json", RESOURCE_PATH);
  }

  @Test
  void getTitle(final AemContext aemContext){

    final DownloadAppsModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(DownloadAppsModel.class);
    //Assert
    Assertions.assertEquals("Download Apps", model.getTitle());
  }

  @Test
  void getEVisaApplications(final AemContext aemContext){
    final DownloadAppsModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(DownloadAppsModel.class);

    //Assert
    Assertions.assertEquals("/content/dam/icons/favicon-16x16.png", model.getEVisaApplications().get(0).getBrandIcon());
    Assertions.assertEquals("Tawakkalna", model.getEVisaApplications().get(0).getTitle());
    Assertions.assertEquals("Tawakkalna (Covid-19 KSA) is the official Saudi Contact Tracing app. approved by the Saudi Ministry of Health", model.getEVisaApplications().get(0).getDescription());
    Assertions.assertEquals("www.google.com", model.getEVisaApplications().get(0).getAppsList().get(0).getAppUrl());
    Assertions.assertEquals("/content/dam/icons/favicon-16x16.png", model.getEVisaApplications().get(0).getAppsList().get(0).getAppStoreIcon());

    Assertions.assertEquals("www.google.com", model.getEVisaApplications().get(0).getAppsList().get(1).getAppUrl());
    Assertions.assertEquals("/content/dam/icons/favicon-16x16.png", model.getEVisaApplications().get(0).getAppsList().get(1).getAppStoreIcon());

    Assertions.assertEquals("/content/dam/icons/favicon-16x16.png", model.getEVisaApplications().get(1).getBrandIcon());
    Assertions.assertEquals("Nusuk", model.getEVisaApplications().get(1).getTitle());
    Assertions.assertEquals("Nusuk (Eatmarna Previously) Based on the responsibility of the Ministry of Hajj and Umrah", model.getEVisaApplications().get(1).getDescription());
    Assertions.assertEquals("www.google.com", model.getEVisaApplications().get(1).getAppsList().get(0).getAppUrl());
    Assertions.assertEquals("/content/dam/icons/favicon-16x16.png", model.getEVisaApplications().get(1).getAppsList().get(0).getAppStoreIcon());

  }
}
