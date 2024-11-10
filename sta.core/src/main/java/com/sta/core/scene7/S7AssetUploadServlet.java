package com.sta.core.scene7;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * S7 Asset Upload Servlet.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "S7 Asset Upload Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/s7-asset-upload"})
@Slf4j
public class S7AssetUploadServlet extends SlingAllMethodsServlet {

  /**
   * DMAsset upload service.
   */
  @Reference
  private transient DMAssetUploadService dmAssetUploadService;

  @Override protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      String base64Data = request.getParameter("data");

      ResponseMessage responseMessage = dmAssetUploadService.uploadAsset(base64Data, "events1",
          "test");
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), responseMessage);
    } catch (Exception e) {
      LOGGER.error("Exception at: S7AssetUploadServlet post " + e.getMessage());
    }
  }
}
