package com.sta.core.vendors.servlets;

import com.google.gson.Gson;
import com.sta.core.vendors.VendorsService;
import com.sta.core.vendors.models.PackageListEntry;
import com.sta.core.scene7.DMAssetUploadService;

import com.sta.core.vendors.models.PackageEntry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.LinkedHashMap;


import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.sta.core.vendors.ScriptConstants;
import com.sta.core.vendors.service.PackageEntryService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.sta.core.vendors.PackageEntryUtils;
import com.saudi.tourism.core.utils.StatusEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.utils.Constants.PN_BANNER_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_ITINERARY_DETAILS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Package entry processing servlet.
 * This servlet should only be active on author side.
 */
@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + PackageEntryServlet.DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_PUT,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_DELETE,
               SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/author/api/v1/entry/package"})
@Slf4j
public class PackageEntryServlet extends BaseAllMethodsServlet {

  /**
   * Service description.
   */
  static final String DESCRIPTION = "Package entry processing servlet";

  private Map<String, String> tagMapping;


  /**
   * Package entry service.
   */
  @Reference
  private transient PackageEntryService packageEntryService;
  /**
   * Event entry service.
   */
  @Reference
  private transient VendorsService vendorsService;
  /**
   * Event entry service.
   */
  @Reference
  private transient DMAssetUploadService dmAssetUploadService;

  /**
   * Save package entry.
   *
   * @param request  request
   * @param response response
   * @throws IOException io exception
   */
  @Override protected void doPost(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws IOException {
    try {
      Map<String, Object> map = CommonUtils.getBody(request);
      String vendor = request.getParameter(ScriptConstants.VENDOR_KEY);
      String name = request.getParameter(ScriptConstants.NAME);
      map.put(ScriptConstants.VENDOR_KEY, vendor);
      if (Objects.nonNull(name) && !"null".equals(name)) {
        map.put(ScriptConstants.NAME, name);
      }
      tagMapping = new HashMap<>();
      Map<String, Object> nodeProperties = extractNodeProperties(map, vendor);
      vendorsService.createVendorNodes(nodeProperties, request.getResourceResolver(), "package");
      List<PackageListEntry> entries = packageEntryService.getEntries(vendor);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK, entries);
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Delete package entry.
   *
   * @param request  request
   * @param response response
   * @throws IOException io exception
   */
  @Override protected void doDelete(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws IOException {
    try {
      String dmc = PackageEntryUtils.getMandatoryParameter(request, ScriptConstants.VENDOR_KEY);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      packageEntryService.deleteEntry(dmc, name);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK,
          new ResponseMessage(HttpStatus.SC_OK, "Deleted", "Package info deleted"));
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Get package entry.
   *
   * @param request  request
   * @param response response
   * @throws ServletException servlet exception
   * @throws IOException      io exception
   */
  @Override protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      String dmc = PackageEntryUtils.getMandatoryParameter(request, ScriptConstants.VENDOR_KEY);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      PackageEntry entry = packageEntryService.getEntry(dmc, name);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK, entry);
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Update package entry.
   *
   * @param request  request
   * @param response response
   * @throws IOException io exception
   */
  @Override protected void doPut(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws IOException {
    doPost(request, response);
  }

  private Map<String, Object> extractNodeProperties(
      final @NonNull Map<String, Object> requestParameterMap, final String vendor) {

    Map<String, Object> nodeProperties = new HashMap<>();
    // modifiedDate used for showing last modified item at top
    requestParameterMap
        .put("modifiedDate", CommonUtils.dateToString(Calendar.getInstance().getTime()));
    requestParameterMap.forEach((k, v) -> mapExcelToNodeProps(nodeProperties, k, v));
    try {

      if (requestParameterMap.containsKey("googleMapLink")) {
        PackageEntryUtils.buildLatLong(requestParameterMap.get("googleMapLink"), nodeProperties);
      }
    } catch (Exception e) {
      LOGGER.error("Error in extractNodeProperties {}", e);
    }
    handleImageUpload(nodeProperties, vendor);
    handleItineraryDetails(nodeProperties, vendor);
    return nodeProperties;

  }

  private void handleImageUpload(final Map<String, Object> nodeProperties, final String vendor) {
    if (nodeProperties.containsKey(PN_BANNER_IMAGE) && !(nodeProperties
        .get(PN_BANNER_IMAGE) instanceof String)) {

      ResponseMessage resp = dmAssetUploadService
          .uploadAsset(nodeProperties.get(PN_BANNER_IMAGE).toString(), "package/" + vendor, vendor);

      if (Objects.nonNull(resp.getMessage())) {
        HashMap<String, String> map = new HashMap<>();
        map.put("label", "Banner Image");
        map.put("value", resp.getMessage().replace("http:", "https:"));
        nodeProperties.put(PN_BANNER_IMAGE, new Gson().toJson(map));
      } else {
        nodeProperties.remove(PN_BANNER_IMAGE);
      }
    }
  }

  private void handleItineraryDetails(final Map<String, Object> nodeProperties,
      final String vendor) {
    if (nodeProperties.containsKey(PN_ITINERARY_DETAILS)) {
      ArrayList<LinkedHashMap> itineraryDetails =
          (ArrayList<LinkedHashMap>) nodeProperties.get(PN_ITINERARY_DETAILS);
      for (LinkedHashMap item : itineraryDetails) {
        boolean label = item.get(PN_IMAGE).toString().split(",")[0].contains("base64");
        if (label) {
          ResponseMessage resp =
              dmAssetUploadService.uploadAsset(item.get(PN_IMAGE).toString(), "package/" + vendor, vendor);

          if (Objects.nonNull(resp.getMessage())) {
            HashMap<String, String> map = new HashMap<>();
            map.put("label", "Itinerary Image");
            map.put("value", resp.getMessage().replace("http:", "https:"));
            item.put(PN_IMAGE, map);
          } else {
            item.remove(PN_IMAGE);
          }
        }
      }
      nodeProperties.put(PN_ITINERARY_DETAILS, new Gson().toJson(itineraryDetails));
    }
  }

  private void mapExcelToNodeProps(final Map<String, Object> nodeProperties, final String key,
      final Object value) {
    try {
      if (StringUtils.isNotBlank(key) && Objects.nonNull(value)) {
        if (key.contains("calendar") || key.contains("modifiedDate")) {
          nodeProperties.put(key,
              CommonUtils.getAEMDateFormat(value.toString(), ScriptConstants.FORMAT_ORIGINAL_DATE));
        } else if (key.contains("Time")) {
          nodeProperties.put(key,
              CommonUtils.getAEMDateFormat(value.toString(), ScriptConstants.FORM_FORMAT_12_TIME));
        } else if (key.contains("Tags")) {
          nodeProperties.put(key, getStringMappingTags((List<String>) value));
        } else {
          nodeProperties.put(key, value);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in mapExcelToNodeProps {}", e);
    }
  }

  private List<String> getStringMappingTags(List<String> items) {
    List<String> tags = new ArrayList<>();
    if (Objects.nonNull(items)) {
      for (String item : items) {
        tags.add(tagMapping.getOrDefault(item, item));
      }
    }
    return tags;
  }

}
