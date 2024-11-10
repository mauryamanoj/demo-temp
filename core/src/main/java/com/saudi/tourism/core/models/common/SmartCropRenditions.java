package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.PN_HEI;
import static com.saudi.tourism.core.utils.Constants.PN_WID;

/**
 * Smart Crop Renditions Model.
 */
@Data
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SmartCropRenditions {


  /**
   * Separator for sizes.
   */
  public static final String SIZES_SEPARATOR = ", ";

  /**
   * Sizes List.
   */
  @ChildResource
  private List<Size> sizes;
  /**
   * Breakpoint.
   */
  @ValueMapValue
  private String breakpoint;

  /**
   * Mode: '' : Thumbnail / Preview.
   */
  @ValueMapValue
  private String mode;

  /**
   * Renditions.
   */
  @ValueMapValue
  private String rendition;

  /**
   * Image width page relative.
   */
  @ValueMapValue
  private String imgRelativeWidth;

  /**
   * Image widths.
   */
  @ValueMapValue
  private String widths;

  /**
   * sceSet List.
   */
  private List<String> srcsetList;

  /**
   * property map list.
   */
  private List<Map<String, String>> propsList;

  /**
   * Model initializer.
   */
  @PostConstruct
  public void init() {
    if (StringUtils.isNotBlank(widths)) {
      propsList = new ArrayList<>();
      float aspectRatio = getAspectRatio();
      String[] widArray = StringUtils.split(StringUtils.deleteWhitespace(widths), Constants.COMMA);
      for (String wid : widArray) {
        int hei = (int) (aspectRatio * Integer.parseInt(wid));
        Map<String, String> dimMap = new HashMap<>();
        dimMap.put(PN_WID, wid);
        dimMap.put(PN_HEI, String.valueOf(hei));
        propsList.add(dimMap);
      }
    }
  }

  /**
   * Get Aspect Ratio. AR = Crop Height / Crop Width
   * rendition = crop-1920x1080 | crop height: 1080 , crop width: 1920
   *
   * @return aspect ratio
   */
  private float getAspectRatio() {
    String[] dimArray = rendition.split("[-x]");
    if (dimArray.length == NumberConstants.THREE) {
      return Float.parseFloat(dimArray[2]) / Float.parseFloat(dimArray[1]);
    }
    return NumberConstants.CONST_ONE;
  }

  /**
   * Generates a formatted sizes string for srcset attributes.
   *
   * @return A string representation like "(max-width: 600px) 480px, 800px".
   */
  public String getAggregateSizes() {

    if (CollectionUtils.isEmpty(sizes)) {
      return null;
    }
    return sizes.stream()
        .map(Size::getFormattedSize)
        .collect(Collectors.joining(SIZES_SEPARATOR));
  }
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Size {
    /**
     * Maximum width.
     */
    @ValueMapValue
    private String maxWidth;

    /**
     * Width.
     */
    @ValueMapValue
    private String width;

    /**
     * Get formatted size string.
     *
     * @return formatted size string.
     */
    public String getFormattedSize() {
      if (StringUtils.isNotBlank(maxWidth)) {
        return String.format("(max-width: %spx) %s", maxWidth, width);
      } else {
        return String.format("%s", width);
      }
    }
  }
}
