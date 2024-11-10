package com.sta.core.vendors.servlets;

import com.sta.core.scene7.DMAssetUploadService;
import com.sta.core.vendors.PackageEntryUtils;
import com.sta.core.vendors.ScriptConstants;
import com.sta.core.vendors.VendorsService;
import com.sta.core.vendors.models.EventEntry;
import com.sta.core.vendors.models.EventListEntry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.google.gson.Gson;
import com.sta.core.vendors.service.EventEntryService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Event entry processing servlet.
 * This servlet should only be active on author side.
 */
@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + EventEntryServlet.DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_PUT,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_DELETE,
               SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/author/api/v1/entry/event"})
@Slf4j
public class EventEntryServlet extends BaseAllMethodsServlet {

  /**
   * Service description.
   */
  static final String DESCRIPTION = "Event entry processing servlet";
  private static final String PN_GALLERY_IMAGES = "galleryImages";

  private Map<String, String> tagMapping;


  /**
   * Event entry service.
   */
  @Reference
  private transient EventEntryService eventEntryService;
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
   * Save event entry.
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
      getMappingMap();
      Map<String, Object> nodeProperties = extractNodeProperties(map, vendor);
      vendorsService.createVendorNodes(nodeProperties, request.getResourceResolver(), "event");
      List<EventListEntry> entries = eventEntryService.getEntries(vendor);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK, entries);
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Delete event entry.
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
      eventEntryService.deleteEntry(dmc, name);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK,
          new ResponseMessage(HttpStatus.SC_OK, "Deleted", "Event info deleted"));
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Get event entry.
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
      EventEntry entry = eventEntryService.getEntry(dmc, name);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK, entry);
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Update event entry.
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
    requestParameterMap.put("modifiedDate",
        CommonUtils.dateToString(Calendar.getInstance().getTime()));
    requestParameterMap.forEach((k, v) -> mapExcelToNodeProps(nodeProperties, k, v));
    try {

      if (requestParameterMap.containsKey("googleMapLink")) {
        PackageEntryUtils
                .buildLatLong(requestParameterMap.get("googleMapLink"), nodeProperties);
      }
    } catch (Exception e) {
      LOGGER.error("Error in extractNodeProperties {}", e);
    }
    handleImageUpload(nodeProperties, vendor);
    return nodeProperties;

  }

  private void handleImageUpload(final Map<String, Object> nodeProperties, final String vendor) {
    if (nodeProperties.containsKey(PN_FEATURE_EVENT_IMAGE) &&
        !(nodeProperties.get(PN_FEATURE_EVENT_IMAGE) instanceof String)) {


      ResponseMessage resp = dmAssetUploadService
          .uploadAsset(nodeProperties.get(PN_FEATURE_EVENT_IMAGE).toString(), "events/" + vendor, vendor);

      if (Objects.nonNull(resp.getMessage())) {
        HashMap<String, String> map = new HashMap<>();
        map.put("label", "Feature Event Image");
        map.put("value", resp.getMessage().replace("http:", "https:"));
        nodeProperties.put(PN_FEATURE_EVENT_IMAGE, new Gson().toJson(map));
      } else {
        nodeProperties.remove(PN_FEATURE_EVENT_IMAGE);
      }
    }
    if (nodeProperties.containsKey(PN_GALLERY_IMAGES) &&
        !(nodeProperties.get(PN_GALLERY_IMAGES) instanceof String)) {

      ArrayList<LinkedHashMap> galleryImages = createGalleryImages(nodeProperties, vendor);
      nodeProperties.put(PN_GALLERY_IMAGES, new Gson().toJson(galleryImages));
    }
  }

  /**
   * Create Gallery Images.
   * @param nodeProperties nodeProperties
   * @param vendor vendor
   * @return List of Gallery Images
   */
  private ArrayList<LinkedHashMap> createGalleryImages(final Map<String, Object> nodeProperties,
      final String vendor) {
    ArrayList<LinkedHashMap> galleryImages = (ArrayList<LinkedHashMap>)
        nodeProperties.get(PN_GALLERY_IMAGES);
    for (LinkedHashMap item : galleryImages) {
      boolean label = item.get(PN_IMAGE).toString().split(",")[0].contains("base64");
      if(label) {
        ResponseMessage resp = dmAssetUploadService.uploadAsset(item.get(PN_IMAGE).toString(),
            "events/" + vendor, vendor);

        if (Objects.nonNull(resp.getMessage())) {
          HashMap<String, String> map = new HashMap<>();
          map.put("label", "Gallery Image");
          map.put("value", resp.getMessage().replace("http:", "https:"));
          item.put(PN_IMAGE, map);
        } else {
          item.remove(PN_IMAGE);
        }
      }
    }
    return galleryImages;
  }

  private void mapExcelToNodeProps(final Map<String, Object> nodeProperties, final String key,
       Object value) {
    try {
      if (StringUtils.isNotBlank(key) && Objects.nonNull(value)) {
        if (key.contains("calendar") || key.contains("modifiedDate")) {
          nodeProperties.put(key,
              CommonUtils.getAEMDateFormat(value.toString(), ScriptConstants.FORM_FORMAT_DATE));
        } else if (key.contains("Tags")) {
          //categoryTags and subCategoryTags are send as Strings from FE
          //but must be added to page properties as arrays
          if (key.equals("categoryTags") || key.equals("subCategoryTags")) {
            value = Arrays.asList(value);
          }
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

  private void getMappingMap() {


    tagMapping = new HashMap<>();
    tagMapping.put("Culture", "sauditourism:events/culture");
    tagMapping.put("Shows & Performing Arts", "sauditourism:events/Performing-Arts");
    tagMapping.put("Western", "sauditourism:events/western");
    tagMapping.put("Sightseeing", "sauditourism:events/sightseeing");
    tagMapping.put("Eastern", "sauditourism:events/eastern");
    tagMapping.put("Shopping & Retail", "sauditourism:events/shopping-retail");
    tagMapping.put("Sport", "sauditourism:events/sport");
    tagMapping.put("Adventure", "sauditourism:events/adventure");
    tagMapping.put("Nature", "sauditourism:events/nature");
    tagMapping.put("Visa", "sauditourism:events/visa");
    tagMapping.put("Themed Attractions", "sauditourism:events/themed-attractions");
    tagMapping.put("Dining", "sauditourism:events/dining");
    tagMapping.put("Conferences", "sauditourism:events/Conferences");
    tagMapping.put("Digital", "sauditourism:events/digital");
    tagMapping.put("Central", "sauditourism:events/central");
    tagMapping.put("All", "sauditourism:audience/all");
    tagMapping.put("16+", "sauditourism:audience/16+");
    tagMapping.put("12+", "sauditourism:audience/12+");
    tagMapping.put("Public", "sauditourism:audience/public");
    tagMapping.put("18 +", "sauditourism:audience/18-plus");
    tagMapping.put("Children", "sauditourism:audience/children");
    tagMapping.put("Families", "sauditourism:audience/families");
  }

}
