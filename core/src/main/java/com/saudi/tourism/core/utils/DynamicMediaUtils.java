package com.saudi.tourism.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.saudi.tourism.core.models.common.Breakpoint;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageSrcSetListModel;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.CN_SERVER_RUN_MODE;
import static com.saudi.tourism.core.utils.Constants.CONST_AMPERSAND;
import static com.saudi.tourism.core.utils.Constants.EQUAL;
import static com.saudi.tourism.core.utils.Constants.PARTIAL_CROP;
import static com.saudi.tourism.core.utils.Constants.PN_HEI;
import static com.saudi.tourism.core.utils.Constants.PN_WID;
import static com.saudi.tourism.core.utils.Constants.QUESTION_MARK;
import static com.saudi.tourism.core.utils.Constants.SPACE;
/**
 * Dynamic Media Utility class.
 */
@Slf4j
public final class DynamicMediaUtils {

  /**
   * Dynamic media cropping profile for 260x195.
   */
  public static final String DM_CROP_260_195 = "crop-260x195";

  /**
   * Dynamic media cropping profile for 315x236.
   */
  public static final String DM_CROP_315_236 = "crop-315x236";

  /**
   * Dynamic media cropping profile for 360x480.
   */
  public static final String DM_CROP_360_480 = "crop-360x480";

  /**
   * Desktop crop for 375x210.
   */
  public static final String DM_CROP_375_210 = "crop-375x210";

  /**
   * Desktop crop for 375x280.
   */
  public static final String DM_CROP_375_280 = "crop-375x280";

  /**
   * Dynamic media cropping profile for 375x667.
   */
  public static final String DM_CROP_375_667 = "crop-375x667";

  /**
   * Dynamic media cropping profile for 460x620.
   */
  public static final String DM_CROP_460_620 = "crop-460x620";

  /**
   * Dynamic media cropping profile for 1920x1080.
   */
  public static final String DM_CROP_660_337 = "crop-660x337";

  /**
   * Desktop crop for 760x570.
   */
  public static final String DM_CROP_760_570 = "crop-760x570";

  /**
   * Dynamic media cropping profile for 1160x650.
   */
  public static final String DM_CROP_1160_650 = "crop-1160x650";

  /**
   * Dynamic media cropping profile for 1920x1080.
   */
  public static final String DM_CROP_1920_1080 = "crop-1920x1080";

  /**
   * Dynamic media cropping profile for 667x375.
   */
  public static final String DM_CROP_667_375 = "crop-667x375";

  /**
   * Dynamic media cropping profile for 600x600.
   */
  public static final String DM_CROP_600_600 = "crop-600x600";

  /**
   * Dynamic media cropping profile for 1920x280.
   */
  public static final String DM_CROP_1920_280 = "crop-1920x280";

  /**
   * Query Param string default image.
   */
  public static final String QUERY_PARAM_DEFAULT_IMAGE = "defaultImage";

  /**
   * Mobile breakpoint.
   */
  public static final Integer MOBILE_BREAKPOINT = 420;

  /**
   * Instantiates a new utils.
   */
  private DynamicMediaUtils() {
  }

  /**
   * Build and GET scene 7 path. s7Domain/is/image/s7filename
   *
   * @param asset  asset
   * @param domain Scene7 domain
   * @return https://scth.scene7.com/is/image/scth/file
   */
  public static String getScene7Path(final Asset asset, final String domain) {
    if (isFileAvailableAtSceneServer(asset) && StringUtils
        .isNotBlank(asset.getMetadataValue(Scene7Constants.PN_S7_DOMAIN)) && StringUtils
        .isNotBlank(asset.getMetadataValue(Scene7Constants.PN_S7_FILE))) {
      return domain + Constants.S7_IMAGE_CONTENT + asset.getMetadata(Scene7Constants.PN_S7_FILE);
    }
    return asset.getPath();
  }

  /**
   * Build and GET scene 7 path. s7Domain/is/content/s7filename
   *
   * @param asset  asset
   * @param domain Scene7 domain
   * @return https://scth.scene7.com/is/content/scth/file
   */
  public static String getScene7VideoPath(final Asset asset, final String domain) {
    if (isFileAvailableAtSceneServer(asset) && StringUtils
        .isNotBlank(asset.getMetadataValue(Scene7Constants.PN_S7_DOMAIN)) && StringUtils
        .isNotBlank(asset.getMetadataValue(Scene7Constants.PN_S7_FILE))) {
      return domain + Constants.S7_VIDEO_CONTENT + asset.getMetadata(Scene7Constants.PN_S7_FILE);
    }
    return asset.getPath();
  }

  /**
   * Check if Metadata, contains dam:Scene7FileStatus property and status is PublishComplete or not.
   *
   * @param asset asset
   * @return true if status == PublishComplete else false.
   */
  private static boolean isFileAvailableAtSceneServer(final Asset asset) {
    return StringUtils.isNotBlank(asset.getMetadataValue(Scene7Constants.PN_S7_FILE_STATUS))
        && asset.getMetadataValue(Scene7Constants.PN_S7_FILE_STATUS)
        .equals(Scene7Constants.PV_S7_PUBLISH_COMPLETE);
  }

  /**
   * Checks that image path contains `/content/dam/`.
   *
   * @param imagePath image path to check
   * @return {@code true} if this image path is for dam
   */
  @Generated
  public static boolean isDamImage(@NotNull final String imagePath) {
    return StringUtils
        .contains(imagePath, DamConstants.MOUNTPOINT_ASSETS + Constants.FORWARD_SLASH_CHARACTER);
  }

  /**
   * Get Default Image name to return as Fall Back Image, without image sharpen param.
   *
   * @param s7ImageSrc image Source
   * @param profile    swatch profile
   * @param isChinaServer if run mode contains CN
   * @return Default Image Name
   */
  public static String getScene7ImageWithDefaultImage(final String s7ImageSrc,
      final String profile, final boolean isChinaServer) {
    return getScene7ImageWithDefaultImage(s7ImageSrc, profile, false, isChinaServer);
  }
  /**
   * Get Default Image name to return as Fall Back Image, without image sharpen param.
   *
   * @param s7ImageSrc image Source
   * @param profile    swatch profile
   * @param isChinaServer if run mode contains CN
   * @param enableImageSharpen to enable Image Sharpen
   * @return Default Image Name
   */
  public static String getScene7ImageWithDefaultImage(final String s7ImageSrc,
                                                      final String profile,
                                                      final boolean enableImageSharpen,
                                                      final boolean isChinaServer) {
    return getScene7ImageWithDefaultImage(s7ImageSrc, profile, enableImageSharpen, isChinaServer, false);
  }

  /**
   * Get Default Image name to return as Fall Back Image. If the image is DAM image (contains
   * /content/dam) it is returned not modified.
   *
   * @param s7ImageSrc         image Source
   * @param profile            swatch profile
   * @param enableImageSharpen enable image sharpen
   * @param isCnServer is china server
   * @param isTransparent handle trans
   * @return Default Image Name
   */
  public static String getScene7ImageWithDefaultImage(final String s7ImageSrc, final String profile,
      final boolean enableImageSharpen, final boolean isCnServer, final boolean isTransparent) {
    if (StringUtils.isBlank(s7ImageSrc) || isDamImage(s7ImageSrc)) {
      return s7ImageSrc;
    }
    String s7ImageUrl = s7ImageSrc;
    if (isCnServer) {
      s7ImageUrl = replaceImageDomain(s7ImageSrc);
    }

    final String imageSrcWithoutCrop = getS7ImageWithoutCropProfile(s7ImageUrl);
    final String fileName = getS7ImageFileName(imageSrcWithoutCrop);
    final String imageWIthCropProfile = getS7ImageWithCropProfile(imageSrcWithoutCrop, profile);

    try {
      // Should encode url in case imageWIthCropProfile contains special characters
      // Otherwise URIBuilder will raise
      final var resultImgBuilder =
          new URIBuilder(URLEncoder.encode(imageWIthCropProfile, StandardCharsets.UTF_8.name()));
      if (StringUtils.isNotBlank(fileName)) {
        resultImgBuilder.addParameter(QUERY_PARAM_DEFAULT_IMAGE, fileName);
      }

      if (enableImageSharpen) {
        resultImgBuilder.addParameter("fmt", "jpg");
        resultImgBuilder.addParameter("qlt", "90,0");
        resultImgBuilder.addParameter("resMode", "sharp2");
        resultImgBuilder.addParameter("op_usm", "1.75,0.3,2,0");
      }
      if (isTransparent) {
        resultImgBuilder.addParameter("fmt", "png-alpha");
      }

      // Should decode the url to return to the browser decoder urls
      // Otherwise pictures are not showing
      return  URLDecoder.decode(resultImgBuilder.toString(), StandardCharsets.UTF_8.name());
    } catch (URISyntaxException e) {
      LOGGER.error("Error when calculating scene7 image url", e);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Error when encoding/decoding scene7 image url", e);
    }
    return StringUtils.EMPTY;
  }

  /**
   * Replace image domain.
   *
   * @param s7ImageSrc s7 image source
   * @return replace domain
   */
  private static String replaceImageDomain(final String s7ImageSrc) {
    return StringUtils.replace(s7ImageSrc, Constants.SCENE7_DOMAIN, Constants.SCENE7_CN_DOMAIN)
        .replace(Constants.SCENE7_AKAMAI_DOMAIN, Constants.SCENE7_CN_DOMAIN);
  }

  /**
   * Get S7 image reference without crop profile.
   *
   * @param imageSource image source url
   * @return image source without crop
   */
  private static String getS7ImageWithoutCropProfile(final String imageSource) {
    return StringUtils.substringBefore(imageSource, PARTIAL_CROP);
  }

  /**
   * Get S7 image reference with crop profile.
   *
   * @param imageSource image source url ex: https://scth.scene7.com/is/image/scth/al-jouf
   * @param profile     crop profile | crop-660x337
   * @return image source without crop | https://scth.scene7.com/is/image/scth/al-jouf:crop-660x337
   */
  private static String getS7ImageWithCropProfile(final String imageSource, final String profile) {
    if (StringUtils.isNotBlank(profile) && !imageSource.contains(PARTIAL_CROP)) {
      return imageSource + Constants.COLON_SLASH_CHARACTER + profile;
    }
    return imageSource;
  }

  /**
   * Get S7Image file name.
   *
   * @param s7ImageSource imageSource ex: https://scth.scene7.com/is/image/scth/al-jouf
   * @return ex: al-jouf
   */
  private static String getS7ImageFileName(final String s7ImageSource) {
    String fileName =
        StringUtils.substringAfterLast(s7ImageSource, Constants.FORWARD_SLASH_CHARACTER);
    if (fileName.equals("scth")) {
      return StringUtils.EMPTY;
    }
    return fileName;
  }

  /**
   * Prepare desktop and mobile images from/in image instance.
   *
   * @param image       image obj
   * @param desktopCrop desktop crop
   * @param mobileCrop  mobile crop
   * @param isCnServer  isCnServer
   */
  @Generated public static void prepareDMImages(@NotNull final Image image,
      @NotNull String desktopCrop, @NotNull String mobileCrop, boolean isCnServer) {
    prepareDMImages(image, desktopCrop, mobileCrop, false, isCnServer);
  }

  /**
   * Prepare desktop and mobile images from/in image instance.
   *
   * @param image              image obj
   * @param desktopCrop        desktop crop
   * @param mobileCrop         mobile crop
   * @param enableImageSharpen enable image sharpen
   * @param isCnServer         isCnServer
   */
  public static void prepareDMImages(@NotNull final Image image, @NotNull String desktopCrop,
      String mobileCrop, final boolean enableImageSharpen, boolean isCnServer) {
    final String s7fileReference = image.getS7fileReference();

    // Checking s7mobileImageReference here is the workaround when some old components have
    // two images - desktop and mobile. Suggested to switch all those components to only one image,
    // remove mobile image references from Image class and use only s7fileReference everywhere.
    final String s7mobileImageReference =
        StringUtils.defaultIfBlank(image.getS7mobileImageReference(), s7fileReference);

    boolean isTransparent = image.isTransparent();

    if (StringUtils.isNotBlank(s7fileReference)) {
      image.setDesktopImage(
          getScene7ImageWithDefaultImage(s7fileReference, desktopCrop, enableImageSharpen,
              isCnServer, isTransparent));
    }
    if (StringUtils.isNotBlank(s7mobileImageReference)) {
      image.setMobileImage(
          getScene7ImageWithDefaultImage(s7mobileImageReference, mobileCrop, enableImageSharpen,
              isCnServer, isTransparent));
    }
  }

  /**
   * Calculate desktop and mobile images.
   * TODO Check if this method can be removed (use prepareDMImages instead)
   *
   * @param image              image obj
   * @param desktopCrop        desktop crop
   * @param mobileCrop         mobile crop
   * @param enableImageSharpen enable image sharpen
   * @param scene7Domain       scene7Domain
   * @param isCnServer isCnServer
   */
  public static void calculateDMImages(final Image image, String desktopCrop, String mobileCrop,
      final boolean enableImageSharpen, String scene7Domain, final boolean isCnServer) {
    String s7featureEventImage = image.getS7fileReference();
    if (Objects.nonNull(s7featureEventImage) && s7featureEventImage.startsWith(scene7Domain)
        && !s7featureEventImage.contains(Constants.SCENE7_DOMAIN_FRAGMENT)) {
      // add crop if and only if s7banner image start with scene7 domain
      image.setDesktopImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featureEventImage, desktopCrop, enableImageSharpen,
              isCnServer, image.isTransparent()));
      image.setMobileImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featureEventImage, mobileCrop, enableImageSharpen,
              isCnServer, image.isTransparent()));
    } else {
      // If no s7 image use featureEventImage(dam image)
      String featureEventImage = Optional.ofNullable(image.getFileReference())
          .filter(imageUrl -> imageUrl.contains(Constants.SCENE7_DOMAIN_FRAGMENT))
          .map((String imageUrl) -> {
            try {
              URIBuilder uriBuilder = new URIBuilder(imageUrl);
              uriBuilder.setScheme(Constants.HTTPS_SCHEME);
              uriBuilder.addParameter("scl", "1");
              return uriBuilder.toString();
            } catch (URISyntaxException e) {
              LOGGER.error("Unable to build URI from \"{}\": ", imageUrl, e);
              return imageUrl;
            }
          }).orElse(image.getFileReference());
      image.setDesktopImage(featureEventImage);
      image.setMobileImage(featureEventImage);
    }
  }

  /**
   * Is China Server.
   *
   * @param slingSettingsService slingSettings
   * @return true if instance contains CN run mode.
   */
  public static boolean isCnServer(final SlingSettingsService slingSettingsService) {
    return Objects.nonNull(slingSettingsService) && slingSettingsService.getRunModes()
        .contains(CN_SERVER_RUN_MODE);
  }
  /**
   * Sets All image breakpoints info.
   *
   * @param image
   * @param deskTopCrop
   * @param mobileCrop
   * @param deskTopBreakpoint
   * @param mobileBreakPoint
   * @param resourceResolver
   * @param currentResource
   *
   */
  public static void setAllImgBreakPointsInfo(final Image image, final String deskTopCrop,
                                              final String  mobileCrop,
                                              final String deskTopBreakpoint, final String mobileBreakPoint,
                                              final ResourceResolver resourceResolver, final Resource currentResource) {
    if (image != null) {
      DynamicMediaUtils.prepareDMImages(image, deskTopCrop, mobileCrop, false);
      image.setDeskTopBreakpoint(deskTopBreakpoint);
      image.setMobileBreakpoint(mobileBreakPoint);
      ImageSrcSetListModel srcModel =
          DynamicMediaUtils.getBreakPointsInfo(image, resourceResolver, currentResource);
      if (null != srcModel) {
        image.setBreakpoints(srcModel.getBreakpoints());
        image.setDefaultImage(srcModel.getDefaultImage());
      }
    }
  }

  /**
   * Sets All image breakpoints info.
   * @param image
   * @param scRenditions
   * @param deskTopCrop
   * @param mobileCrop
   * @param deskTopBreakpoint
   * @param mobileBreakPoint
   */
  public static void setAllImgBreakPointsInfo(
      final Image image,
      final List<SmartCropRenditions> scRenditions,
      final String deskTopCrop,
      final String mobileCrop,
      final String deskTopBreakpoint,
      final String mobileBreakPoint) {
    if (Objects.nonNull(image)) {
      // Clearing srcsetList to avoid duplicate entries
      org.apache.commons.collections4.CollectionUtils.emptyIfNull(scRenditions)
          .forEach(sc -> sc.setSrcsetList(new ArrayList<>()));
      DynamicMediaUtils.prepareDMImages(image, deskTopCrop, mobileCrop, false);
      image.setDeskTopBreakpoint(deskTopBreakpoint);
      image.setMobileBreakpoint(mobileBreakPoint);
      final var srcModel = DynamicMediaUtils.getBreakPointsInfo(image, scRenditions);
      if (null != srcModel) {
        image.setBreakpoints(srcModel.getBreakpoints());
        image.setDefaultImage(srcModel.getDefaultImage());
      }
    }
  }

  /**
   *
   * @param image
   * @param resolver
   * @param resource
   * @return list of breakpoints.
   */
  public static ImageSrcSetListModel getBreakPointsInfo(final Image image,
                                                        ResourceResolver resolver,
                                                        Resource resource) {
    ImageSrcSetListModel srcListModel = new ImageSrcSetListModel();
    String deskTopImageSrc = StringUtils.defaultIfBlank(image.getS7fileReference(), image.getFileReference());
    String mobileImageSrc = StringUtils.defaultIfBlank(image.getS7mobileImageReference(),
        image.getMobileImageReference());
    if (resource != null) {
      List<SmartCropRenditions> scList = getSmartCropRenditions(resolver, resource, deskTopImageSrc,
          mobileImageSrc, srcListModel, image.isTransparent());
      if (null != scList && !scList.isEmpty()) {
        List<Breakpoint> breakpoints = new ArrayList<>();
        scList.forEach(sc -> {
          if (CollectionUtils.isNotEmpty(sc.getSrcsetList())) {
            Breakpoint bt = new Breakpoint();
            bt.setSrcset(StringUtils.join(sc.getSrcsetList(), ","));
            if (StringUtils.isNotBlank(sc.getBreakpoint())) {
              bt.setMedia("(max-width: " + sc.getBreakpoint() + "px)");
            }

            bt.setSizes(sc.getAggregateSizes());
            breakpoints.add(bt);
          }
        });
        srcListModel.setBreakpoints(breakpoints);
      }
    }
    return srcListModel;
  }

  /**
   * Get Break Points Info.
   *
   * @param image
   * @param scRenditions
   * @return Break Points Info
   */
  public static ImageSrcSetListModel getBreakPointsInfo(
      final Image image, final List<SmartCropRenditions> scRenditions) {
    ImageSrcSetListModel srcListModel = new ImageSrcSetListModel();
    String deskTopImageSrc =
        StringUtils.defaultIfBlank(image.getS7fileReference(), image.getFileReference());
    String mobileImageSrc =
        StringUtils.defaultIfBlank(
            image.getS7mobileImageReference(), image.getS7fileReference());

    final var scList = getSmartCropRenditions(scRenditions, deskTopImageSrc, mobileImageSrc);

    if (CollectionUtils.isNotEmpty(scList)) {
      final var breakpoints = scList.stream()
              .map(
                  sc -> {
                    if (CollectionUtils.isEmpty(sc.getSrcsetList())) {
                      return null;
                    }

                    final var bt = new Breakpoint();
                    bt.setSrcset(StringUtils.join(sc.getSrcsetList(), ","));
                    if (StringUtils.isNotBlank(sc.getBreakpoint())) {
                      bt.setMedia("(max-width: " + sc.getBreakpoint() + "px)");
                    }
                    bt.setSizes(sc.getAggregateSizes());

                    return bt;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      srcListModel.setBreakpoints(breakpoints);
    }
    return srcListModel;
  }

  private static List<SmartCropRenditions> getSmartCropRenditions(
      final List<SmartCropRenditions> scRenditions,
      final String deskTopImageSrc,
      final String mobileImageSrc) {
    try {
      if (CollectionUtils.isNotEmpty(scRenditions)) {
        for (SmartCropRenditions model : scRenditions) {
          if (StringUtils.isNotBlank(model.getBreakpoint())
              && Integer.parseInt(model.getBreakpoint()) <= MOBILE_BREAKPOINT) {
            if (StringUtils.isNotBlank(mobileImageSrc)
                && !DynamicMediaUtils.isDamImage(mobileImageSrc)) {
              model.setSrcsetList(getSourceUrlList(model, mobileImageSrc));
            }
          } else {
            if (StringUtils.isNotBlank(deskTopImageSrc)
                && !DynamicMediaUtils.isDamImage(deskTopImageSrc)) {
              model.setSrcsetList(getSourceUrlList(model, deskTopImageSrc));
            }
          }
        }
      }
    } catch (Exception exp) {
      LOGGER.warn("Exception Occurred at SmartCropInfo", exp);
    }
    return scRenditions;
  }

  private static List<SmartCropRenditions> getSmartCropRenditions(
      ResourceResolver resolver,
      Resource resource,
      String deskTopImageSrc,
      String mobileImageSrc,
      ImageSrcSetListModel listModel,
      boolean isTransparent) {
    String resourcePath = resource.getPath();
    LOGGER.debug("SmartCropInfo :: Resource path -> " + resourcePath);
    List<SmartCropRenditions> smartCropRenditionsList =
        CommonUtils.getSmartCropInfoFromContentPolicy(resolver, resourcePath, "crops");
    try {
      if (!smartCropRenditionsList.isEmpty()) {
        for (SmartCropRenditions model : smartCropRenditionsList) {
          if (StringUtils.isNotBlank(model.getBreakpoint())
              && Integer.parseInt(model.getBreakpoint()) <= MOBILE_BREAKPOINT) {
            if (StringUtils.isNotBlank(mobileImageSrc)
                && !DynamicMediaUtils.isDamImage(mobileImageSrc)) {
              model.setSrcsetList(getSourceUrlList(model, mobileImageSrc, listModel, isTransparent));
            }
          } else {
            if (StringUtils.isNotBlank(deskTopImageSrc)
                && !DynamicMediaUtils.isDamImage(deskTopImageSrc)) {
              model.setSrcsetList(getSourceUrlList(model, deskTopImageSrc, listModel, isTransparent));
            }
          }
        }
      }
    } catch (Exception exp) {
      LOGGER.error("Exception Occurred at SmartCropInfo " + exp.getMessage());
    }
    return smartCropRenditionsList;
  }

  /**
   * Prepare source url list.
   *
   * @param model         model
   * @param imageSrc      image source
   * @param listModel     image src list
   * @param isTransparent is transparent
   *
   * @return srcSetList
   */
  private static List<String> getSourceUrlList(final SmartCropRenditions model,
                                               final String imageSrc,
                                               final ImageSrcSetListModel listModel,
                                               final Boolean isTransparent) {
    List<String> srcList = new ArrayList<>();
    for (Map<String, String> map : model.getPropsList()) {
      String url = DynamicMediaUtils.getScene7ImageWithDefaultImage(imageSrc, model.getRendition(),
          false, false, isTransparent);
      String srcUrl = getFormedSrcUrl(url, map.get(PN_WID), map.get(PN_HEI));
      srcList.add(srcUrl + SPACE + map.get(PN_WID) + "w");
      if (StringUtils.isBlank(listModel.getDefaultImage())) {
        listModel.setDefaultImage(srcUrl);
      }
    }
    return srcList;
  }

  /**
   * Prepare source url list.
   *
   * @param model model
   * @param imageSrc image source
   * @return srcSetList
   */
  private static List<String> getSourceUrlList(final SmartCropRenditions model,
                                               final String imageSrc) {
    List<String> srcList = new ArrayList<>();
    for (Map<String, String> map : model.getPropsList()) {
      String url =
          DynamicMediaUtils.getScene7ImageWithDefaultImage(imageSrc, model.getRendition(), false);
      String srcUrl = getFormedSrcUrl(url, map.get(PN_WID), map.get(PN_HEI));
      srcList.add(srcUrl + SPACE + map.get(PN_WID) + "w");
    }
    return srcList;
  }

  private static String getFormedSrcUrl(final String url, final String wid, final String hei) {
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

  public static String getFormedSrcUrlWithProfileAndWidHei(
      String s7featureEventImage, boolean isCnServer, String profile, String width, String heigh) {
    String scene7ImageWithDefaultImage = DynamicMediaUtils
        .getScene7ImageWithDefaultImage(s7featureEventImage, profile, false, isCnServer);
    return getFormedSrcUrl(scene7ImageWithDefaultImage, width, heigh);
  }
}
