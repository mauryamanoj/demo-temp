package com.saudi.tourism.core.services.stories.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.ImageRelativeWidthEnum;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

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

  /** Size for Large Desktop image width. */
  String SIZE_MEDIUM_IMAGE_WIDTH_600 = "600px";

  /** Size for Large Desktop image width. */
  String SIZE_MEDIUM_IMAGE_WIDTH_260 = "260px";

  /** Size for Large Mobile image width. */
  String SIZE_LARGE_IMAGE_WIDTH_900 = "900px";

  /** Size for Large Mobile image width. */
  String SIZE_LARGE_IMAGE_WIDTH_318 = "318px";

  /** Large Mobile image width. */
  String LARGE_MOBILE_IMAGE_WIDTH = "318";

  default List<SmartCropRenditions> buildSmartCropsRenditions(
      @NonNull final FetchStoriesRequest request) {

    SmartCropRenditions mobileScRendition = null;
    SmartCropRenditions desktopScRendition = null;
    List<SmartCropRenditions.Size> mobileSizesList = new ArrayList<>();
    List<SmartCropRenditions.Size> desktopSizesList = new ArrayList<>();


    if (StringUtils.equalsIgnoreCase("medium", request.getImagesSize())) {
      mobileScRendition = new SmartCropRenditions();
      mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE_1023.getValue());
      mobileScRendition.setRendition(CropEnum.CROP_600x600.getValue());
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

      desktopScRendition.setRendition(CropEnum.CROP_600x600.getValue());
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

    if (StringUtils.equalsIgnoreCase("large", request.getImagesSize())) {
      mobileScRendition = new SmartCropRenditions();
      mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE_1023.getValue());
      mobileScRendition.setRendition(CropEnum.CROP_375x210.getValue());
      mobileScRendition.setWidths(LARGE_MOBILE_IMAGE_WIDTH);
      SmartCropRenditions.Size mobileSize1 = new SmartCropRenditions.Size();
      mobileSize1.setMaxWidth(BreakPointEnum.MOBILE_1023.getValue());
      mobileSize1.setWidth(SIZE_LARGE_IMAGE_WIDTH_318);

      SmartCropRenditions.Size mobileSize2 = new SmartCropRenditions.Size();
      mobileSize2.setWidth(SIZE_LARGE_IMAGE_WIDTH_900);
      mobileSizesList.add(mobileSize1);
      mobileSizesList.add(mobileSize2);
      mobileScRendition.setSizes(mobileSizesList);
      mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
      mobileScRendition.init();

      desktopScRendition = new SmartCropRenditions();

      desktopScRendition.setRendition(CropEnum.CROP_1160x650.getValue());
      desktopScRendition.setWidths(LARGE_DESKTOP_IMAGE_WIDTH);
      desktopScRendition.setImgRelativeWidth(
          ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());

      SmartCropRenditions.Size desktopSize1 = new SmartCropRenditions.Size();
      desktopSize1.setWidth(SIZE_LARGE_IMAGE_WIDTH_900);
      desktopSizesList.add(desktopSize1);
      desktopScRendition.setSizes(desktopSizesList);
      desktopScRendition.init();

      return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});
    }

    mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE_1023.getValue());
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
