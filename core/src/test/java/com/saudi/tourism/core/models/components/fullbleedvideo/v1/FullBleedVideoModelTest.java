package com.saudi.tourism.core.models.components.fullbleedvideo.v1;


import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FullBleedVideoModelTest {

  private static final String RESOURCE_PATH = "/content/sauditourism/en/understand/test-page/jcr:content/root/responsivegrid/m01_full_bleed_video";

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/full-bleed-video/content.json", RESOURCE_PATH);
  }

  @Test
  void getProps(final AemContext aemContext) {
    final FullBleedVideoModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(FullBleedVideoModel.class);
    Assertions.assertEquals("Full Bleed", model.getTitle());
    Assertions.assertEquals("/content/dam/saudi-tourism/media/red-sea-route-/dmc-content/my-trip/summber-vibes-video.mp4", model.getVideoReference());
    Assertions.assertEquals("youtube", model.getVideoType());
    Assertions.assertEquals("QDWQDQW", model.getYoutubeReference());
    Assertions.assertEquals("alt", model.getAlt());
    Assertions.assertEquals("full-bleed", model.getLayout());
    Assertions.assertEquals("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", model.getImageReference());
    Assertions.assertEquals("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", model.getMobileImageReference());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/Saudi 2020_Images-1920x1080px7-1", model.getS7imageReference());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/Saudi 2020_Images-1920x1080px7-1", model.getS7mobileImageReference());
  }
}