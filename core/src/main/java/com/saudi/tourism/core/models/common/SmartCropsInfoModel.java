package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.CONST_AMPERSAND;
import static com.saudi.tourism.core.utils.Constants.EQUAL;
import static com.saudi.tourism.core.utils.Constants.PN_HEI;
import static com.saudi.tourism.core.utils.Constants.PN_WID;
import static com.saudi.tourism.core.utils.Constants.QUESTION_MARK;
import static com.saudi.tourism.core.utils.Constants.SPACE;

/**
 * Smart Crop Info Model.
 */
@Slf4j
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SmartCropsInfoModel {

  /**
   * Image Source.
   */
  @RequestAttribute
  private String imageSrc;

  /**
   * IsPreview Mode.
   */
  @RequestAttribute
  private boolean isPreviewImage;

  /**
   * ResourceResolver sling object.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;
  /**
   * SlingHttpServletRequest object.
   */
  @Self
  private SlingHttpServletRequest request;

  /**
   * Smart crop renditions.
   */
  @Getter
  private List<SmartCropRenditions> smartCropRenditions;

  /**
   * Default image source.
   */
  @Setter
  @Getter
  private String defImgSrc;

  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  /**
   * Model Initializer.
   */
  @PostConstruct
  protected void init() {
    String resourcePath = request.getRequestPathInfo().getResourcePath();
    LOGGER.debug("SmartCropInfoModel :: Resource path -> " + resourcePath);
    smartCropRenditions = CommonUtils.getSmartCropInfoFromContentPolicy(resourceResolver,
        resourcePath, "crops");
    try {
      List<SmartCropRenditions> removeItemList = new ArrayList<>();
      if (!smartCropRenditions.isEmpty() && StringUtils.isNotBlank(imageSrc) && !DynamicMediaUtils
          .isDamImage(imageSrc)) {
        for (SmartCropRenditions model : smartCropRenditions) {
          if (isConditionPass(model.getMode())) {
            model.setSrcsetList(getSourceUrlList(model));
          } else {
            removeItemList.add(model);
          }
        }
        if (!removeItemList.isEmpty()) {
          smartCropRenditions.removeAll(removeItemList);
        }
      }
    } catch (Exception exp) {
      LOGGER.error("Exception Occurred at SmartCropInfoModel " + exp.getMessage());
    }

  }

  /**
   * check requirement. default view mode is thumbnail empty
   * if mode is preview check the crop item is also preview
   *
   * @param mode view mode
   * @return true if mode is empty and isNotPreviewImage or mode is preview and cropItem is also
   * preview
   */
  private boolean isConditionPass(final String mode) {
    return (isPreviewImage && "preview".equals(mode)) || (StringUtils.isBlank(mode)
        && !isPreviewImage);
  }

  /**
   * Prepare source url list.
   *
   * @param model model
   * @return srcSetList
   */
  private List<String> getSourceUrlList(final SmartCropRenditions model) {
    List<String> srcList = new ArrayList<>();
    for (Map<String, String> map : model.getPropsList()) {
      String url = DynamicMediaUtils.getScene7ImageWithDefaultImage(imageSrc, model.getRendition(),
          DynamicMediaUtils.isCnServer(settingsService));
      String srcUrl = getFormedSrcUrl(url, map.get(PN_WID), map.get(PN_HEI));
      srcList.add(srcUrl + SPACE + map.get(PN_WID) + "w");
      if (StringUtils.isBlank(defImgSrc)) {
        setDefImgSrc(srcUrl);
      }
    }
    return srcList;
  }

  /**
   * Get Formed String URL.
   * using the method as S7 url can have space in file name. so encoding may result in exception.
   *
   * @param url URL
   * @param wid width
   * @param hei height
   * @return complete URL
   */
  private String getFormedSrcUrl(final String url, final String wid, final String hei) {
    StringBuilder builder = new StringBuilder(url);
    String paramString =
        PN_WID + EQUAL + wid + CONST_AMPERSAND + PN_HEI + EQUAL + hei;
    if (url.contains(DynamicMediaUtils.QUERY_PARAM_DEFAULT_IMAGE)) {
      builder.append(CONST_AMPERSAND).append(paramString);
    } else {
      builder.append(QUESTION_MARK).append(paramString);
    }
    return (builder.toString()).replace(SPACE, "%20");
  }

}
