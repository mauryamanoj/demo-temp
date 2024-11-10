package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.saudi.tourism.core.models.components.guide.Card;
import com.saudi.tourism.core.models.components.guide.GuideCards;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class PageBannerModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/page-banner/content.json", CONTENT_PATH);
  }

  @Test
  public void testPageBannerModel(AemContext context) {

    //Given
    String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/page_banner";


    // When
    PageBannerModel pageBanner =
        context.currentResource(RESOURCE_PATH).adaptTo(PageBannerModel.class);

    // Then
    assertEquals(1, pageBanner.getCards().size());
    assertEquals("true", pageBanner.getHideImageBrush());
    assertEquals(false, pageBanner.isNotOnTop());
    assertEquals("HomeBanner", pageBanner.getType());
    assertEquals("title card", pageBanner.getCards().get(0).getTitle());
    assertEquals("description card", pageBanner.getCards().get(0).getDescription());
    assertEquals("/content/dam/image.jpg", pageBanner.getCards().get(0).getImage().getFileReference());
    assertEquals("/content/dam/image.jpg", pageBanner.getCards().get(0).getLogo().getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/image:crop-1920x768?defaultImage=image", pageBanner.getCards().get(0).getImage().getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/image:crop-460x620?defaultImage=image", pageBanner.getCards().get(0).getImage().getMobileImage());
    assertEquals("https://scth.scene7.com/is/image/scth/image",
        pageBanner.getCards().get(0).getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/image",
      pageBanner.getCards().get(0).getLogo().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/video.mp4", pageBanner.getCards().get(0).getVideo().getS7videoFileReference());
    assertEquals("/content/dam/sauditourism/video.mp4", pageBanner.getCards().get(0).getVideo().getVideoFileReference());
    assertEquals("altImage", pageBanner.getCards().get(0).getImage().getAlt());
    assertEquals(true, pageBanner.getCards().get(0).getVideo().isAutorerun());
    assertEquals(true, pageBanner.getCards().get(0).getVideo().isAutoplay());
  }

}
