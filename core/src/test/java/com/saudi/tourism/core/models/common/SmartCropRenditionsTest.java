package com.saudi.tourism.core.models.common;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.saudi.tourism.core.utils.PrimConstants.PN_HEI;
import static com.saudi.tourism.core.utils.PrimConstants.PN_WID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SmartCropRenditionsTest {

  @InjectMocks
  private SmartCropRenditions modelUnderTest;

  @Mock
  private Resource resource;



  @Test
  public void testAspectRatioCalculation() throws Exception {

    modelUnderTest.setRendition("crop-1920x1080");


    Method method = SmartCropRenditions.class.getDeclaredMethod("getAspectRatio");
    method.setAccessible(true);

    float actualRatio = (float) method.invoke(modelUnderTest);
    float expectedRatio = 1080f / 1920f;

    assertEquals(expectedRatio, actualRatio, 0.001);
  }

  @Test
  public void testAggregateSizesGeneration() {
    SmartCropRenditions.Size size1 = new SmartCropRenditions.Size();
    size1.setMaxWidth("600");
    size1.setWidth("480");

    SmartCropRenditions.Size size2 = new SmartCropRenditions.Size();
    size2.setWidth("800");

    modelUnderTest.setSizes(Arrays.asList(size1, size2));

    String expected = "(max-width: 600px) 480, 800";
    assertEquals(expected, modelUnderTest.getAggregateSizes());
  }

 @Test
  public void testPostConstructWithValidWidths(AemContext context) {

   // Create a test resource with properties
   final String testPath = "/content/test";
   context.create().resource(testPath, "rendition", "crop-1920x1080", "widths", "1920,1440");

   // Adapt the created resource to our model
   SmartCropRenditions modelUnderTest = context.resourceResolver().getResource(testPath).adaptTo(SmartCropRenditions.class);



   assertEquals(2, modelUnderTest.getPropsList().size());
   assertEquals("1920", modelUnderTest.getPropsList().get(0).get(PN_WID));
   assertEquals("1080", modelUnderTest.getPropsList().get(0).get(PN_HEI));
   assertEquals("1440", modelUnderTest.getPropsList().get(1).get(PN_WID));
  }
}
