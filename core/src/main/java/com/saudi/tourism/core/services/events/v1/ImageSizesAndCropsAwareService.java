package com.saudi.tourism.core.services.events.v1;

import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.ImageRelativeWidthEnum;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Image sizes & crops interface.
 */
interface ImageSizesAndCropsAwareService {
  /** Small Desktop image width. */
  String SMALL_DESKTOP_IMAGE_WIDTH = "390";

  /** Small Mobile image width. */
  String SMALL_MOBILE_IMAGE_WIDTH = "260";

  /** Medium Desktop image width. */
  String MEDIUM_DESKTOP_IMAGE_WIDTH = "600";

  /** Medium Mobile image width. */
  String MEDIUM_MOBILE_IMAGE_WIDTH = "260";

  /** Large Desktop image width. */
  String LARGE_DESKTOP_IMAGE_WIDTH = "900";

  /** Large Mobile image width. */
  String LARGE_MOBILE_IMAGE_WIDTH = "318";

  default List<SmartCropRenditions> buildSmartCropsRenditions(
      @NonNull final FetchEventsRequest request) {

    SmartCropRenditions mobileScRendition = null;
    SmartCropRenditions desktopScRendition = null;

    if (StringUtils.equalsIgnoreCase("medium", request.getImagesSize())) {
      mobileScRendition = new SmartCropRenditions();
      mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
      mobileScRendition.setRendition(CropEnum.CROP_600x600.getValue());
      mobileScRendition.setWidths(MEDIUM_MOBILE_IMAGE_WIDTH);
      mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
      mobileScRendition.init();

      desktopScRendition = new SmartCropRenditions();

      desktopScRendition.setRendition(CropEnum.CROP_600x600.getValue());
      desktopScRendition.setWidths(MEDIUM_DESKTOP_IMAGE_WIDTH);
      desktopScRendition.setImgRelativeWidth(
          ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
      desktopScRendition.init();

      return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});
    }

    if (StringUtils.equalsIgnoreCase("large", request.getImagesSize())) {
      mobileScRendition = new SmartCropRenditions();
      mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
      mobileScRendition.setRendition(CropEnum.CROP_375x210.getValue());
      mobileScRendition.setWidths(LARGE_MOBILE_IMAGE_WIDTH);
      mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
      mobileScRendition.init();

      desktopScRendition = new SmartCropRenditions();

      desktopScRendition.setRendition(CropEnum.CROP_1160x650.getValue());
      desktopScRendition.setWidths(LARGE_DESKTOP_IMAGE_WIDTH);
      desktopScRendition.setImgRelativeWidth(
          ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
      desktopScRendition.init();

      return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});
    }

    mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
    mobileScRendition.setRendition(CropEnum.CROP_600x600.getValue());
    mobileScRendition.setWidths(SMALL_MOBILE_IMAGE_WIDTH);
    mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
    mobileScRendition.init();

    desktopScRendition = new SmartCropRenditions();

    desktopScRendition.setRendition(CropEnum.CROP_600x600.getValue());
    desktopScRendition.setWidths(SMALL_DESKTOP_IMAGE_WIDTH);
    desktopScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
    desktopScRendition.init();

    return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});
  }
}
