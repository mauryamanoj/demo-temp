package com.saudi.tourism.core.services.attractions.v1;

import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.ImageRelativeWidthEnum;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Image sizes & crops interface.
 */
interface ImageSizesAndCropsAwareService {

  /**
   * Medium Desktop image width.
   */
  String MEDIUM_DESKTOP_IMAGE_WIDTH = "600";

  /**
   * Medium Mobile image width.
   */
  String MEDIUM_MOBILE_IMAGE_WIDTH = "260";


  /**
   * Size for Large Desktop image width.
   */
  String SIZE_MEDIUM_IMAGE_WIDTH_600 = "600px";

  /**
   * Size for Large Desktop image width.
   */
  String SIZE_MEDIUM_IMAGE_WIDTH_260 = "260px";


  default List<SmartCropRenditions> buildSmartCropsRenditions(
      @NonNull final FetchAttractionsRequest request) {

    SmartCropRenditions mobileScRendition = null;
    SmartCropRenditions desktopScRendition = null;
    List<SmartCropRenditions.Size> mobileSizesList = new ArrayList<>();
    List<SmartCropRenditions.Size> desktopSizesList = new ArrayList<>();


    mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE_1023.getValue());
    mobileScRendition.setRendition(CropEnum.CROP_760x570.getValue());
    mobileScRendition.setWidths(MEDIUM_MOBILE_IMAGE_WIDTH);
    mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
    SmartCropRenditions.Size mobileSize1 = new SmartCropRenditions.Size();
    mobileSize1.setMaxWidth(BreakPointEnum.MOBILE_1023.getValue());
    mobileSize1.setWidth(SIZE_MEDIUM_IMAGE_WIDTH_260);

    SmartCropRenditions.Size mobileSize2 = new SmartCropRenditions.Size();
    mobileSize2.setWidth(SIZE_MEDIUM_IMAGE_WIDTH_600);
    mobileSizesList.add(mobileSize1);
    mobileSizesList.add(mobileSize2);
    mobileScRendition.setSizes(mobileSizesList);

    mobileScRendition.init();

    desktopScRendition = new SmartCropRenditions();

    desktopScRendition.setRendition(CropEnum.CROP_660x337.getValue());
    desktopScRendition.setWidths(MEDIUM_DESKTOP_IMAGE_WIDTH);
    desktopScRendition.setImgRelativeWidth(
        ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());

    SmartCropRenditions.Size desktopSize1 = new SmartCropRenditions.Size();
    desktopSize1.setWidth(SIZE_MEDIUM_IMAGE_WIDTH_600);
    desktopSizesList.add(desktopSize1);
    desktopScRendition.setSizes(desktopSizesList);
    desktopScRendition.init();

    return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});


  }
}
