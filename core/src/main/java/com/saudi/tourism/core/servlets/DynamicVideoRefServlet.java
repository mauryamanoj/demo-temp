package com.saudi.tourism.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Dynamic Image Reference Servlet.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Dynamic Video Reference Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
            + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/api/v1/dynamic-video-reference"})
@Slf4j
public class DynamicVideoRefServlet extends SlingAllMethodsServlet {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private transient SaudiTourismConfigs saudiTourismConfig;

  /**
   * Image reference property.
   */
  private static final String S7_VIDEO_REFERENCE = "s7VideoReference";

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String videoPath = request.getRequestPathInfo().getSuffix();
    Map<String, String> dataMap = new HashMap<>();
    dataMap.put(S7_VIDEO_REFERENCE, videoPath);
    try {
      if (StringUtils.isNotBlank(videoPath)) {
        Resource videoResource = request.getResourceResolver().getResource(videoPath);
        if (Objects.nonNull(videoResource) && DamUtil.isAsset(videoResource)) {
          Asset asset = DamUtil.getAssets(videoResource).next();

          String s7Path =
              DynamicMediaUtils.getScene7VideoPath(asset, saudiTourismConfig.getScene7Domain());

          if (StringUtils.isNotBlank(s7Path)) {
            LOGGER.debug("Scene7Path : " + s7Path);
            dataMap.put(S7_VIDEO_REFERENCE, s7Path);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Exception Occurred at {} ", e.getLocalizedMessage());
    } finally {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), dataMap);
    }
  }
}
