package com.saudi.tourism.core.utils;

import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for DynamicMediaUtils.
 */
class
DynamicMediaUtilsTest {

  @Test
  void getScene7Path() {
    // TODO
    assertTrue(true);
  }

  @Test
  void isDamImage() {
    assertFalse(DynamicMediaUtils.isDamImage(StringUtils.EMPTY));
    assertFalse(DynamicMediaUtils.isDamImage(StringUtils.EMPTY));

    assertFalse(DynamicMediaUtils.isDamImage(
        "https://scth.scene7.com/is/image/scth/192A6448-1_1920x1080%20(1):crop-1920x1080"));
    assertFalse(DynamicMediaUtils.isDamImage("http://some/image.jpg"));
    assertFalse(DynamicMediaUtils.isDamImage("//some/image.jpg"));
    assertFalse(DynamicMediaUtils.isDamImage("/some/image.jpg"));
    assertFalse(DynamicMediaUtils.isDamImage("image.jpg"));

    assertTrue(DynamicMediaUtils.isDamImage("/content/dam/some/image.jpg"));
  }

  @Test
  void testGetScene7ImageWithDefaultImage() {
    // "https://scth.scene7.com/is/image/scth/192A6448-1_1920x1080%20(1):crop-1920x1080"
    final String imageName = "192A6448-1_1920x1080";
    final String originalS7Image0 = "https://scth.scene7.com/is/image/scth/";
    final String originalS7Image1 = originalS7Image0 + imageName;
    final String originalS7Image2 = originalS7Image1 + ":crop-1920x1080";

    final String expectedNoDefaultImage = "https://scth.scene7.com/is/image/scth/:crop-profile";
    final String expectedNoDefaultImageSharpen =
        expectedNoDefaultImage + "?" + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;

    final String expectedDefaultImage =
        originalS7Image1 + ":crop-profile?defaultImage=" + imageName;
    final String expectedMobileImage =
        originalS7Image1 + ":crop-mobile-profile?defaultImage=" + imageName;
    final String expectedDefaultSharpen =
        expectedDefaultImage + "&" + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;
    final String expectedMobileSharpen =
        expectedMobileImage + "&" + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;
    final String expectedTransparentImage =
      expectedDefaultImage + "&fmt=png-alpha";

    final String originalS7Mobile2 = "//some-server.com/some/mobile/image.jpg";
    final String expectedMobile2Sharpen =
        "//some-server.com/some/mobile/image.jpg:crop-mobile-profile?defaultImage=image.jpg" + "&"
            + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;

    assertEquals(expectedNoDefaultImage,
        DynamicMediaUtils.getScene7ImageWithDefaultImage(originalS7Image0, "crop-profile", false));
    assertEquals(expectedNoDefaultImage, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image0, "crop-profile", false, false));
    assertEquals(expectedNoDefaultImageSharpen, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image0, "crop-profile", true, false));

    assertEquals(expectedDefaultImage,
        DynamicMediaUtils.getScene7ImageWithDefaultImage(originalS7Image1, "crop-profile", false));
    assertEquals(expectedDefaultImage, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image1, "crop-profile", false, false));
    assertEquals(expectedDefaultSharpen, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image1, "crop-profile", true, false));
    //assertEquals(expectedTransparentImage, DynamicMediaUtils
     // .getScene7ImageWithDefaultImage(originalS7Image1, "crop-profile", false, false, true));


    // Test CN server
    final String originalCnS7Image = "https://assets.visitsaudi.cn/is/image/scth/";
    final String cnImageServerNoDefaultImage = originalCnS7Image + ":crop-profile";
    final String cnImageServerNoDefaultImageSharp =
        originalCnS7Image + ":crop-profile?" + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;
    final String expectedCNDefaultImage =
        originalCnS7Image + imageName + ":crop-profile?defaultImage=192A6448-1_1920x1080";
    final String expectedCNDefaultImageSharp =
        expectedCNDefaultImage + "&" + Constants.DEFAULT_S7_IMAGE_SHARP_PARAM;
    assertEquals(cnImageServerNoDefaultImage,
        DynamicMediaUtils.getScene7ImageWithDefaultImage(originalS7Image0, "crop-profile", true));
    assertEquals(cnImageServerNoDefaultImageSharp, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image0, "crop-profile", true, true));
    assertEquals(expectedCNDefaultImage, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image0 + imageName, "crop-profile", false, true));
    assertEquals(expectedCNDefaultImageSharp, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(originalS7Image0 + imageName, "crop-profile", true, true));

    final String cdnS7ImageBase = "https://s7g10.scene7.com/is/image/scth/";
    final String cdnS7NoDefaultImage = cdnS7ImageBase + ":crop-profile";

    assertEquals(expectedCNDefaultImage, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(cdnS7ImageBase + imageName, "crop-profile", false, true));
    assertEquals(expectedCNDefaultImageSharp, DynamicMediaUtils
        .getScene7ImageWithDefaultImage(cdnS7ImageBase + imageName, "crop-profile", true, true));

    //
    // Test prepareDMImages method
    //
    final Image img = new Image();
    img.setS7fileReference(originalS7Image2);
    img.setTransparent(false);

    DynamicMediaUtils.prepareDMImages(img, "crop-profile", "crop-mobile-profile", false, false);
    assertEquals(expectedDefaultImage, img.getDesktopImage());
    assertEquals(expectedMobileImage, img.getMobileImage());

    img.setS7mobileImageReference(originalS7Mobile2);
    DynamicMediaUtils.prepareDMImages(img, "crop-profile", "crop-mobile-profile", true, false);
    assertEquals(expectedDefaultSharpen, img.getDesktopImage());
    assertEquals(expectedMobile2Sharpen, img.getMobileImage());
  }

  @Test
  void testGetScene7ImageWithProfileAndHiWeiImage() {
    final String originalS7Image0 = "https://scth.scene7.com/is/image/scth/";
    final String expectedNoDefaultImage = "https://scth.scene7.com/is/image/scth/:crop-375x210?wid=750&hei=420";
    assertEquals(expectedNoDefaultImage,
      DynamicMediaUtils.getFormedSrcUrlWithProfileAndWidHei(originalS7Image0, false,"crop-375x210", "750","420"));

    final String originalS7Image1 = "https://scth.scene7.com/is/image/scth/";
    final String expectedNoDefaultImage1 = "https://scth.scene7.com/is/image/scth/:crop-260x195?wid=520&hei=390";
    assertEquals(expectedNoDefaultImage1,
      DynamicMediaUtils.getFormedSrcUrlWithProfileAndWidHei(originalS7Image1, false,"crop-260x195", "520","390"));
  }

  @Test
  void prepareDMImagesEmpty() {
    final Image img = new Image();
    // Test with nulls
    DynamicMediaUtils.prepareDMImages(img, "crop-profile", "crop-mobile", false);
    Assertions.assertNull(img.getDesktopImage());
    Assertions.assertNull(img.getMobileImage());

    // Test with empty and pre-defined desktop and mobile (value should remain)
    img.setS7fileReference(StringUtils.EMPTY);
    final String expectedDesktop = "desktop-image";
    img.setDesktopImage(expectedDesktop);
    final String expectedMobile = "desktop-mobile";
    img.setMobileImage(expectedMobile);
    DynamicMediaUtils.prepareDMImages(img, "crop-profile", "crop-mobile", false);
    Assertions.assertTrue(StringUtils.equals(expectedDesktop, img.getDesktopImage()));
    Assertions.assertTrue(StringUtils.equals(expectedMobile, img.getMobileImage()));
  }

  @Test
  void setAllImgBreakPointsInfo(){
    //Arrange
    final var mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
    mobileScRendition.setRendition(CropEnum.CROP_375x280.getValue());
    mobileScRendition.setWidths("353");
    mobileScRendition.setImgRelativeWidth("90vw");
    mobileScRendition.init();

    final var desktopScRendition = new SmartCropRenditions();

    desktopScRendition.setRendition(CropEnum.CROP_375x280.getValue());
    desktopScRendition.setWidths("604");
    desktopScRendition.setImgRelativeWidth("40vw");
    desktopScRendition.init();

    final var image = new Image();
    image.setFileReference("/content/dam/sauditourism/favicon.png");
    image.setS7fileReference("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider");
    image.setS7mobileImageReference("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider");

    final var scRenditions = Arrays.asList(new SmartCropRenditions[]{mobileScRendition, desktopScRendition});

    //Act
    DynamicMediaUtils.setAllImgBreakPointsInfo(
      image,
      scRenditions,
      CropEnum.CROP_375x280.getValue(),
      CropEnum.CROP_375x280.getValue(),
      BreakPointEnum.DESKTOP.getValue(),
      BreakPointEnum.MOBILE.getValue());

    //Assert
    assertEquals("/content/dam/sauditourism/favicon.png", image.getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", image.getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", image.getS7mobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", image.getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", image.getMobileImage());
    assertEquals("1280", image.getDeskTopBreakpoint());
    assertEquals("420", image.getMobileBreakpoint());

    assertTrue(CollectionUtils.isNotEmpty(image.getBreakpoints()));
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=353&hei=263 353w", image.getBreakpoints().get(0).getSrcset());
    assertEquals("(max-width: 420px)", image.getBreakpoints().get(0).getMedia());

    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=604&hei=450 604w", image.getBreakpoints().get(1).getSrcset());

  }
}
