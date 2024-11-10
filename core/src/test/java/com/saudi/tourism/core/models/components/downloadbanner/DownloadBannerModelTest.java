package com.saudi.tourism.core.models.components.downloadbanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Image;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class DownloadBannerModelTest {
  private static final String CONTENT_PATH = "/download-banner";

  private final Gson gson = new GsonBuilder().create();


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/downloadbanner/content"
        + ".json", CONTENT_PATH);
  }

  @Test
  public void testDownloadBannerModel(AemContext context) {
    //Arrange


    //Act

    final DownloadBannerModel model =
        context.currentResource(CONTENT_PATH).adaptTo(DownloadBannerModel.class);
    final String json = model.getJson();

    //Assert
    // Banner Data
    final DownloadBannerModel data = gson.fromJson(json, DownloadBannerModel.class);
    assertEquals("Live the full Visit Saudi experience", data.getTitle());
    assertEquals("Discover everything on the go.", data.getSubtitle());
    assertEquals("Download the App", data.getAppstitle());
    assertEquals("/content/dam/saudi-tourism/media/donwload-iphoneX-mockup-desktop-full.png", data.getBanner().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/donwload-iphoneX-mockup-mobile-full.png", data.getBanner().getMobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/donwload-iphoneX-mockup-desktop-full", data.getBanner().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/donwload-iphoneX-mockup-mobile-full", data.getBanner().getS7mobileImageReference());
    // Apps Data
    assertEquals(2, data.getApplications().size());
    assertEquals("Apple", data.getApplications().get(0).getApptitle());
    assertEquals("https://apps.apple.com/us/app/visit-saudi/id818179871", data.getApplications().get(0).getAppurl());
    assertEquals("/content/dam/App-store-desktop.png", data.getApplications().get(0).getImage().getFileReference());
    assertEquals("/content/dam/App-store-desktop.png", data.getApplications().get(0).getImage().getS7fileReference());
    assertEquals("App-store-desktop", data.getApplications().get(1).getApptitle());
    assertEquals("https://play.google.com/store/apps/details?id=sa.gov.apps.sauditourism", data.getApplications().get(1).getAppurl());
    assertEquals("/content/dam/google-play-blanco-desktop.png", data.getApplications().get(1).getImage().getFileReference());
    assertEquals("/content/dam/google-play-blanco-desktop.png", data.getApplications().get(1).getImage().getS7fileReference());
  }
}
