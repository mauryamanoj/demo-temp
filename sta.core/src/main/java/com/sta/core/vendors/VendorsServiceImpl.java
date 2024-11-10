package com.sta.core.vendors;

import com.sta.core.vendors.models.ImageEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.adobe.internal.util.UUID;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.PageManager;
import com.google.api.client.util.Base64;
import com.google.gson.Gson;
import com.sta.core.vendors.models.PackageEntryItinerary;
import com.sta.core.vendors.models.WorkFlowInfoModel;
import com.sta.core.vendors.service.WorkflowService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.day.cq.replication.ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_MOD;
import static com.sta.core.vendors.ScriptConstants.FORM_FORMAT_12_TIME;
import static com.saudi.tourism.core.utils.Constants.PN_BANNER_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_ITINERARY_DETAILS;
import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_FOLDER;

/**
 * The service responsible for generating sitemaps. The services uses
 * to generate actual sitemap files.
 */
@Component(service = VendorsService.class,
           immediate = true)
@Slf4j
public class VendorsServiceImpl implements VendorsService {

  /**
   * The constant VAR_COMMERCE_EVENTS.
   */
  public static final String VAR_COMMERCE_EVENTS = "/var/commerce/events";

  /**
   * The constant VAR_COMMERCE_PACKAGES.
   */
  public static final String VAR_COMMERCE_PACKAGES = "/var/commerce/packages";

  /**
   * The constant CONTENT_PATH_EVENTS.
   */
  public static final String CONTENT_PATH_EVENTS = "/content/sauditourism/{language}/events";

  /**
   * The constant CONTENT_PATH_PACKAGES.
   */
  public static final String CONTENT_PATH_PACKAGES = "/content/sauditourism/{language}/packages";
  /**
   * The constant PACKAGE_INFO_ITEM_NAME.
   */
  public static final String PACKAGE_INFO_ITEM_NAME = "text:";

  /**
   * Function to convert time string to date.
   */
  private static final BiFunction<String, Object, Object> STRING_TIME_CONVERTER =
      (key, value) -> Optional.ofNullable((Object) CommonUtils.getAEMDateFormat(value.toString(),
          FORM_FORMAT_12_TIME)).orElse(StringUtils.EMPTY);
  /**
   * Error message format.
   */
  private static final String ERROR_IN_UPDATE_MULTIFIELD_PROPS_MIGRATE =
      "error in updateMultifieldProps Migrate {}";
  /**
   * Property 'statusType'.
   */
  private static final String PN_STATUS_TYPE = "statusType";

  /**
   * ResourceBundleProvider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * Workflow instance.
   */
  @Reference
  private WorkflowService workflowService;

  @Override public void createVendorNodes(Map<String, Object> requestParameterMap,
      final ResourceResolver resourceResolver, String type) {
    long startTime = System.currentTimeMillis();
    String commercePath = null;
    try {
      String nodeName = getIdFromString(
          StringUtils.defaultIfEmpty(requestParameterMap.get(PN_TITLE).toString(), ""));
      if (requestParameterMap.containsKey(ScriptConstants.NAME)) {
        nodeName = requestParameterMap.get(ScriptConstants.NAME).toString();
      }
      if (type.equals("event")) {
        commercePath = VAR_COMMERCE_EVENTS;
      } else {
        commercePath = VAR_COMMERCE_PACKAGES;
      }
      String folderPath = PathUtils
          .concat(commercePath, requestParameterMap.get(ScriptConstants.VENDOR_KEY).toString());
      Resource folderResource = ResourceUtil
          .getOrCreateResource(resourceResolver, folderPath, new HashMap<>(), NT_SLING_FOLDER,
              false);
      @Nullable Node folderNode = folderResource.adaptTo(Node.class);
      Node eventNode;
      if (folderNode.hasNode(nodeName)) {
        eventNode = folderNode.getNode(nodeName);
      } else {
        eventNode = folderNode.addNode(nodeName, NT_UNSTRUCTURED);

      }
      WorkFlowInfoModel workFlowInfoModel = new WorkFlowInfoModel();
      updateNodeProperties(eventNode, requestParameterMap, workFlowInfoModel, type,
              Constants.DEFAULT_LOCALE);

      if (workFlowInfoModel.isUpdated()) {
        eventNode.setProperty(PN_STATUS_TYPE, "pending");
        resourceResolver.commit();
      }

    } catch (Exception e) {
      LOGGER.error("Error in createVendorNodes: {}", e);
    }
    LOGGER.info("Sitemap generation for all locales took {} ms", startTime);

  }

  @Override public void createVendorPages() {
    ResourceResolver resolver = null;
    try {
      resolver = userService.getWritableResourceResolver();
      Resource eventRootVarNode = resolver.getResource(VAR_COMMERCE_EVENTS);
      createPages(resolver, eventRootVarNode, ScriptConstants.EVENT);

      Resource packageRootVarNode = resolver.getResource(VAR_COMMERCE_PACKAGES);
      createPages(resolver, packageRootVarNode, ScriptConstants.PACKAGE);
    } catch (Exception ex) {
      LOGGER.error("Exception while creating vendor pages", ex);
    } finally {
      if (resolver.isLive()) {
        resolver.close();
      }
    }
  }

  /**
   *
   * @param resolver resolver.
   * @param rootVarNode root Node.
   * @param type type.
   */
  private void createPages(ResourceResolver resolver, Resource rootVarNode, String type) {
    try {
      @NotNull Iterator<Resource> dmciter = rootVarNode.listChildren();
      while (dmciter.hasNext()) {
        Resource dmcRes = dmciter.next();
        processDmciter(resolver, type, dmcRes);
      }
    } catch (Exception e) {
      LOGGER.error("Error in createVendorPages", e);
    }
  }

  /**
   * Process dmciter resource.
   * @param resolver resolver
   * @param type type
   * @param dmcRes dmcRes resource
   */
  private void processDmciter(final ResourceResolver resolver, final String type,
      final Resource dmcRes) {
    try {
      Iterator<Resource> iter = dmcRes.listChildren();
      while (iter.hasNext()) {
        Resource source = iter.next();
        processDmciterChild(resolver, type, source);
      }
    } catch (Exception e) {
      LOGGER.error("error in updating property {}", e.getMessage());
    }
  }

  /**
   * Process dmciter child resource.
   * @param resolver resolver
   * @param type type
   * @param source child resource
   */
  private void processDmciterChild(final ResourceResolver resolver, final String type,
      final Resource source) {
    try {
      if (source.getValueMap().containsKey(PN_TITLE)) {
        handlePageCreation(resolver, source, Constants.DEFAULT_LOCALE, type);
        handlePageCreation(resolver, source, Constants.ARABIC_LOCALE, type);
      }
    } catch (Exception e) {
      LOGGER.error("error in page Creation {}", e.getMessage());
    }
  }

  /**
   *
   * @param resolver Resolver.
   * @param source source.
   * @param lang language.
   * @param type type.
   */
  private void handlePageCreation(final ResourceResolver resolver, final Resource source,
      final String lang, String type) {
    String contentPath = null;
    if (type.equals(ScriptConstants.EVENT)) {
      contentPath = CONTENT_PATH_EVENTS;
    } else if (type.equals(ScriptConstants.PACKAGE)) {
      contentPath = CONTENT_PATH_PACKAGES;
    }
    WorkFlowInfoModel workFlowInfoModel =
        createPackagePage(CommonUtils.getResourcePath(lang, contentPath), source.getName(),
            getHasMapValueMap(source.getValueMap(), lang, type), resolver, type, lang);

    startWorkflow(workFlowInfoModel, type, source, resolver);
  }

  /**
   *
   * @param workFlowInfoModel workflowModel.
   * @param type type.
   * @param source source.
   * @param resolver resolver.
   */
  private void startWorkflow(final WorkFlowInfoModel workFlowInfoModel, final String type,
      final Resource source, final ResourceResolver resolver) {
    ResourceResolver workflowResourceResolver = null;

    Node varNode = source.adaptTo(Node.class);
    Node pageNode =
        resolver.getResource(workFlowInfoModel.getPath() + "/jcr:content").adaptTo(Node.class);
    try {
      workflowResourceResolver = userService.getWorkflowResourceResolver();
      LOGGER.debug("Starting the event Workflow: {}", workFlowInfoModel);
      if (workFlowInfoModel.isUpdated()) {

        pageNode.setProperty(PN_PAGE_LAST_MOD, Calendar.getInstance());
        resolver.commit();

        WorkflowSession wfsession = workflowResourceResolver.adaptTo(WorkflowSession.class);
        UserManager userManager = resolver.adaptTo(UserManager.class);
        WorkflowData wfData = wfsession.newWorkflowData("JCR_PATH", workFlowInfoModel.getPath());
        WorkflowModel workflowModel = chooseWorkflowModel(wfsession, type);

        Map<String, Object> metaData = new HashMap<>();
        if (workFlowInfoModel.getMessage().length() > 100) {
          workFlowInfoModel.setMessage(workFlowInfoModel.getMessage().substring(0, 100));
        }
        metaData.put("workflowTitle", workFlowInfoModel.getMessage());

        Optional<Workflow> workflow = Optional.ofNullable(workflowModel)
            .flatMap(wm -> workflowService.getWorkflowByPayload(wfsession, wm.getId(),
                workFlowInfoModel.getPath()));
        if (workflow.isPresent()) {
          workflowService.moveToBeginning(wfsession, workflow.get(), userManager);
        } else {
          wfsession.startWorkflow(workflowModel, wfData, metaData);
        }
      } else {
        Calendar lastModified = pageNode.getProperty(PN_PAGE_LAST_MOD).getDate();
        Calendar lastReplicated = null;

        if (pageNode.hasProperty(NODE_PROPERTY_LAST_REPLICATED)) {
          lastReplicated = pageNode.getProperty(NODE_PROPERTY_LAST_REPLICATED).getDate();

        }
        if (Objects.nonNull(lastReplicated) && lastReplicated.after(lastModified)) {
          varNode.setProperty(PN_STATUS_TYPE, "published");
          varNode.getSession().save();
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in startWorkflow: {}", e);
    } finally {
      workflowResourceResolver.close();
    }
  }

  /**
   *
   * @param wfsession Workflow session.
   * @param type type.
   * @return workflowmodel.
   * @throws WorkflowException workflow exception.
   */
  private WorkflowModel chooseWorkflowModel(WorkflowSession wfsession, String type)
      throws WorkflowException {
    if (type.equals(ScriptConstants.EVENT)) {
      return wfsession.getModel("/var/workflow/models/event_approval");
    } else if (type.equals(ScriptConstants.PACKAGE)) {
      return wfsession.getModel("/var/workflow/models/trip_package_approval");
    }
    return null;
  }

  /**
   *
   * @param valueMap valueMap.
   * @param lang language.
   * @param type type.
   * @return valuemap.
   */
  private Map<String, Object> getHasMapValueMap(final ValueMap valueMap, final String lang,
      final String type) {

    Map<String, Object> nodeMap = new HashMap<>();
    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
      if (entry.getValue() instanceof String) {
        nodeMap.put(entry.getKey(), entry.getValue().toString());
      } else {
        nodeMap.put(entry.getKey(), entry.getValue());
      }
    }

    nodeMap.computeIfPresent("season", (key, value) -> {
      if ("-".equals(value)) {
        return "";
      } else {
        return value;
      }
    });

    nodeMap.computeIfPresent("startTime", STRING_TIME_CONVERTER);
    nodeMap.computeIfPresent("endTime", STRING_TIME_CONVERTER);

    handleBannerImage(nodeMap);
    if (Constants.ARABIC_LOCALE.equals(lang)) {
      extractArabicProps(nodeMap, PN_TITLE, "title_ar");
      extractArabicProps(nodeMap, "copy", "copy_ar");
      if (ScriptConstants.PACKAGE.equals(type)) {
        extractArabicProps(nodeMap, "exclude", "exclude_ar");
        extractArabicProps(nodeMap, "importantInfo", "importantInfo_ar");
        extractArabicProps(nodeMap, "include", "include_ar");
        extractArabicProps(nodeMap, "bookNow", "bookNow_ar");
      }
    }

    nodeMap.remove("copy_ar");
    nodeMap.remove("title_ar");
    nodeMap.remove(JCR_PRIMARYTYPE);
    nodeMap.remove(PN_STATUS_TYPE);
    if (ScriptConstants.PACKAGE.equals(type)) {
      extractArabicProps(nodeMap, JCR_TITLE, PN_TITLE);
      nodeMap.remove("exclude_ar");
      nodeMap.remove("importantInfo_ar");
      nodeMap.remove("include_ar");
      nodeMap.remove("bookNow_ar");
    }

    return nodeMap;
  }

  /**
   *
   * @param nodeMap node map.
   * @param engProp english proerty.
   * @param arProp arabic property.
   */
  private void extractArabicProps(final Map<String, Object> nodeMap, final String engProp,
      final String arProp) {
    if (nodeMap.containsKey(arProp)) {
      nodeMap.put(engProp, nodeMap.get(arProp));
    }

  }

  /**
   *
   * @param nodeMap nodeMap.
   */
  private void handleBannerImage(final Map<String, Object> nodeMap) {
    if (nodeMap.containsKey(PN_BANNER_IMAGE)) {
      Gson gson = new Gson();
      nodeMap.put(PN_BANNER_IMAGE,
          gson.fromJson(nodeMap.get(PN_BANNER_IMAGE).toString(), ImageEntry.class).getValue());

    }
    if (nodeMap.containsKey(PN_FEATURE_EVENT_IMAGE)) {
      Gson gson = new Gson();
      nodeMap.put(PN_FEATURE_EVENT_IMAGE,
          gson.fromJson(nodeMap.get(PN_FEATURE_EVENT_IMAGE).toString(), ImageEntry.class).getValue());

    }
  }

  /**
   *
   * @param path path.
   * @param pageTitle page title.
   * @param packageItems package items.
   * @param resolver resolver.
   * @param type type.
   * @param lang language.
   * @return workflowModel.
   */
  private WorkFlowInfoModel createPackagePage(String path, String pageTitle,
      final Map<String, Object> packageItems, ResourceResolver resolver, String type, String lang) {
    WorkFlowInfoModel workFlowInfoModel = new WorkFlowInfoModel();
    String contentTemplate = null;
    try {
      workFlowInfoModel.setMessage("");
      String vendorName = packageItems.get(ScriptConstants.VENDOR_KEY).toString();
      String pageName = getIdFromString(pageTitle);
      String dmcPath = path + Constants.FORWARD_SLASH_CHARACTER + vendorName;
      boolean created = false;
      @Nullable Session session = resolver.adaptTo(Session.class);
      if (Objects.isNull(pageName)) {
        return workFlowInfoModel;
      }
      @Nullable PageManager pageManager = resolver.adaptTo(PageManager.class);

      if (!session.nodeExists(dmcPath)) {
        pageManager.create(path, vendorName, ScriptConstants.CONTENT_TEMPLATE, pageTitle);

      }

      if (type.equals(ScriptConstants.EVENT)) {
        contentTemplate = ScriptConstants.EVENT_TEMPLATE;
      } else if (type.equals(ScriptConstants.PACKAGE)) {
        contentTemplate = ScriptConstants.PACKAGE_TEMPLATE;
      }

      if (!session.nodeExists(dmcPath + Constants.FORWARD_SLASH_CHARACTER + pageName)) {
        pageManager.create(dmcPath, pageName, contentTemplate, pageTitle);
        session.save();
        created = true;
      }

      if (session.nodeExists(dmcPath)) {
        workFlowInfoModel.setPath(dmcPath + Constants.FORWARD_SLASH_CHARACTER + pageName);
        updateNodeProperties(session
                .getNode(dmcPath + Constants.FORWARD_SLASH_CHARACTER + pageName + "/jcr:content"),
            packageItems, workFlowInfoModel, type, lang);
        if (workFlowInfoModel.isUpdated()) {
          session.save();
          workFlowInfoModel.setMessage(pageTitle + Constants.LEFT_BRACKET + lang.toUpperCase()
                  + Constants.RIGHT_BRACKET + Constants.SPACE_MINUS_DASH + " " + type + "Updated"
                  + Constants.SPACE_MINUS_DASH + vendorName + Constants.SPACE_MINUS_DASH
                  + workFlowInfoModel.getMessage());
          if (created) {
            workFlowInfoModel.setMessage(pageTitle + Constants.LEFT_BRACKET
                    + lang.toUpperCase() + Constants.RIGHT_BRACKET + Constants.SPACE_MINUS_DASH
                + "New " + type + " Created " + Constants.SPACE  + Constants.SPACE_MINUS_DASH + vendorName
            );
          }
          return workFlowInfoModel;
        }
      }
    } catch (Exception e) {
      LOGGER.error("error in running Migrate {}", e.getMessage());
    }
    return workFlowInfoModel;
  }

  /**
   *
   * @param jcrContent jcr content.
   * @param packageItems package items.
   * @param workFlowInfoModel workflow model.
   * @param type type.
   * @param lang language.
   */
  private void updateNodeProperties(final Node jcrContent, final Map<String, Object> packageItems,
      WorkFlowInfoModel workFlowInfoModel, final String type, String lang) {
    try {
      migrateNodeProperties(jcrContent, packageItems, workFlowInfoModel);
      if (type.equals(ScriptConstants.PACKAGE)) {

        updateChildNodes(packageItems.get("importantInfo"),
            jcrContent.getNode("important-info/details"), PACKAGE_INFO_ITEM_NAME, lang);
        updateChildNodes(packageItems.get("include"), jcrContent.getNode("package-include/details"),
            PACKAGE_INFO_ITEM_NAME, lang);
        updateChildNodes(packageItems.get("exclude"), jcrContent.getNode("package-exclude/details"),
            PACKAGE_INFO_ITEM_NAME, lang);
        updateChildNodes(packageItems.get(PN_ITINERARY_DETAILS), jcrContent.getNode("days-info/days"),
            "text---", lang);
      }

    } catch (RepositoryException e) {
      LOGGER.error("error in getNodes {}", e.getMessage());
    }
  }

  /**
   *
   * @param value value.
   * @param source source.
   * @param fieldName fieldname.
   * @param lang language.
   */
  private void updateChildNodes(final Object value, final Node source, String fieldName, String lang) {

    if (!JCR_TITLE.equals(fieldName) && fieldName.contains(":")) {
      fieldName = fieldName.split(":")[0];
      updateMultifieldDetails(source, fieldName, value.toString());

    } else if (fieldName.contains("---")) {
      Gson gson = new Gson();
      handleDaysContent(source, fieldName,
          gson.fromJson(value.toString(), PackageEntryItinerary[].class), lang);
    }
  }

  /**
   *
   * @param source source.
   * @param fieldName fieldName.
   * @param migrateValue migrate value.
   */
  private void updateMultifieldDetails(Node source, final String fieldName,
      final String migrateValue) {

    String[] values = migrateValue.split("\n");
    String[] trimValues =
        Arrays.stream(values).filter(x -> !x.trim().isEmpty()).toArray(String[]::new);

    try {
      removeChildren(source, values.length);
    } catch (RepositoryException e) {
      LOGGER.error(ERROR_IN_UPDATE_MULTIFIELD_PROPS_MIGRATE, e.getMessage());
    }

    for (int i = 0; i < trimValues.length; i++) {
      String value = trimValues[i];
      Node itemNode = null;

      try {
        String nodeName = "item" + i;
        if (source.hasNode(nodeName)) {
          itemNode = source.getNode(nodeName);
        } else {
          itemNode = source.addNode(nodeName);
        }
        updateProperties(itemNode, fieldName, value);

      } catch (RepositoryException e) {
        LOGGER.error(ERROR_IN_UPDATE_MULTIFIELD_PROPS_MIGRATE, e.getMessage());
      }

    }


  }

  /**
   *
   * @param source source.
   * @param fieldName fieldName.
   * @param packageEntryItinerary package itenary.
   * @param lang language.
   */
  private void handleDaysContent(final Node source, String fieldName,
      final PackageEntryItinerary[] packageEntryItinerary, String lang) {
    fieldName = fieldName.split("---")[0];
    try {

      int countRealUpdate = 0;
      boolean oneDay = packageEntryItinerary.length == 1;

      removeChildren(source, packageEntryItinerary.length);

      for (int i = 0; i < packageEntryItinerary.length; i++) {
        PackageEntryItinerary itinerary = packageEntryItinerary[i];
        String description = null;
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));

        daysRootNodeUpdates(source, countRealUpdate, itinerary, oneDay, i18nBundle, lang);
        String nodeDetailsName = "item" + countRealUpdate + "/details";
        Node itemNodeDetails;
        if (source.hasNode(nodeDetailsName)) {
          itemNodeDetails = source.getNode(nodeDetailsName);
        } else {
          itemNodeDetails = source.addNode(nodeDetailsName);
        }
        if (lang.equals(Constants.DEFAULT_LOCALE)) {
          description = itinerary.getDescription();
        } else if (lang.equals(Constants.ARABIC_LOCALE)) {
          description = itinerary.getDescription_ar();
        }
        updateMultifieldDetails(itemNodeDetails, fieldName, description);
        countRealUpdate++;



      }
    } catch (RepositoryException e) {
      LOGGER.error("error in handleDaysContent Migrate {}", e.getMessage());
    }
  }

  /**
   *
   * @param source source.
   * @param newLength new length.
   * @throws RepositoryException repository exception.
   */
  private static void removeChildren(final Node source, int newLength)
      throws RepositoryException {
    NodeIterator nodeIterator = source.getNodes();
    if (nodeIterator.getSize() > newLength) {
      while (nodeIterator.hasNext()) {
        nodeIterator.nextNode().remove();
      }
    }
  }

  /**
   *
   * @param source source.
   * @param countRealUpdate count update.
   * @param itinerary itinerary.
   * @param oneDay oneDay.
   * @param i18nBundle i18n.
   * @param lang language.
   * @throws RepositoryException exception.
   */
  private void daysRootNodeUpdates(final Node source, final int countRealUpdate,
      final PackageEntryItinerary itinerary, boolean oneDay, ResourceBundle i18nBundle, String lang)
          throws RepositoryException {

    String nodeName = "item" + countRealUpdate;
    Node itemNode;
    String dayTitle = null;
    if (source.hasNode(nodeName)) {
      itemNode = source.getNode(nodeName);
    } else {
      source.addNode("item" + countRealUpdate);
      itemNode = source.getNode(nodeName);
    }

    String i18value = null;
    if (lang.equals(Constants.DEFAULT_LOCALE)) {
      if (oneDay) {
        dayTitle = "dayItinerarySingle";
      } else {
        dayTitle = "dayItinerarySeveral";
      }
      i18value = StringUtils.replaceEach(CommonUtils.getI18nString(i18nBundle, dayTitle),
              new String[] {"{0}"}, new String[] {String.valueOf(countRealUpdate + 1)});
    } else if (lang.equals(Constants.ARABIC_LOCALE)) {
      if (oneDay) {
        i18value = CommonUtils.getI18nString(i18nBundle, "dayItinerarySingle");
      } else {
        i18value = CommonUtils.getI18nString(i18nBundle, "dayItinerarySeveral "
                + (countRealUpdate + 1));
      }
    }

    updateProperties(itemNode, PN_TITLE, i18value);
    updateProperties(itemNode, PN_IMAGE, itinerary.getImage().getValue());
  }

  /**
   *
   * @param source source.
   * @param packageItems package items.
   * @param workFlowInfoModel workflow model.
   */
  private void migrateNodeProperties(Node source, final Map<String, Object> packageItems,
      final WorkFlowInfoModel workFlowInfoModel) {
    boolean updated = false;
    for (Map.Entry<String, Object> entry : packageItems.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      try {
        if (value instanceof String) {
          updated = updateProperties(source, key, value.toString());
        } else if (value instanceof ArrayList) {
          updated = updateMultifieldProps(source, key, (List<String>) value);
        } else if (value instanceof String[]) {
          updated = updateMultifieldProps(source, key, Arrays.asList((String[]) value));
        } else {
          updated = updateDateProperties(source, key, (Calendar) value);
        }

        if (updated) {
          workFlowInfoModel.setUpdated(true);
          workFlowInfoModel.setMessage(workFlowInfoModel.getMessage() + key + ", ");
        }
      } catch (Exception e) {
        LOGGER.error("error in updating property {}", e.getMessage());
      }
    }

  }

  /**
   *
   * @param source source.
   * @param fieldName field name.
   * @param migrateValue migrate value.
   * @return if property is updated.
   */
  private boolean updateProperties(Node source, final String fieldName, final String migrateValue) {
    boolean updated = false;
    try {
      if (source.hasProperty(fieldName)) {
        javax.jcr.Property prop = source.getProperty(fieldName);
        if (!prop.isMultiple()) {
          if (!prop.getString().equals(migrateValue)) {
            source.setProperty(fieldName, migrateValue);
            updated = true;
          }
        } else {
          source.setProperty(fieldName, (Value) null);
          source.setProperty(fieldName, migrateValue);
        }
      } else {
        source.setProperty(fieldName, migrateValue);
        updated = true;
      }
    } catch (RepositoryException e) {
      LOGGER.error("error in updateProperties Migrate {}", e.getMessage());
    }
    return updated;
  }

  /**
   *
   * @param source source.
   * @param fieldName fieldName.
   * @param migrateValue migrate value.
   * @return if date property is updated.
   */
  private boolean updateDateProperties(Node source, final String fieldName,
      final Calendar migrateValue) {
    boolean updated = false;
    try {
      if (source.hasProperty(fieldName)) {
        Calendar currentValue = source.getProperty(fieldName).getDate();
        if (currentValue.getTimeInMillis() != migrateValue.getTimeInMillis()) {
          source.setProperty(fieldName, migrateValue);
          updated = true;
        }
      } else {
        source.setProperty(fieldName, migrateValue);
        updated = true;
      }
    } catch (RepositoryException e) {
      LOGGER.error("error in updateProperties Migrate {}", e.getMessage());
    }
    return updated;
  }

  /**
   * updateMultifieldProps.
   *
   * @param source    Node
   * @param fieldName fieldName
   * @param values    List
   * @return boolean
   */
  private boolean updateMultifieldProps(Node source, final String fieldName,
      final List<String> values) {
    boolean updated = false;

    try {
      if (source.hasProperty(fieldName)) {
        Value[] currentValues = source.getProperty(fieldName).getValues();
        List<String> oldValues = Arrays.stream(currentValues).map(value -> {
          try {
            return value.getString();
          } catch (RepositoryException e) {
            //in case we can't retrieve String value we should return some unique value, so that
            // equals() will return false
            return UUID.createUUID();
          }
        }).collect(Collectors.toList());

        if (!oldValues.equals(values)) {
          source.setProperty(fieldName, values.toArray(new String[values.size()]));
          updated = true;
        }

      } else {
        source.setProperty(fieldName, values.toArray(new String[values.size()]));
        updated = true;
      }
    } catch (RepositoryException e) {
      LOGGER.error(ERROR_IN_UPDATE_MULTIFIELD_PROPS_MIGRATE, e.getMessage());
    }

    return updated;
  }

  /**
   * getIdFromString.
   *
   * @param pageTitle pageTitle
   * @return id from String
   */
  public static String getIdFromString(final String pageTitle) {
    if (pageTitle == null) {
      return null;
    }
    return pageTitle.toLowerCase().trim().replace(" & ", "").replace("&", "").replace(" ", "-")
        .replace(",", "");
  }

  /**
   * write do DAM.
   *
   * @param imageAsset imageAsset
   * @param dmcName    dmcName
   * @return file path
   */
  public String writeToDam(String imageAsset, String dmcName) {
    ResourceResolver resolver = null;
    try {
      resolver = userService.getWritableResourceResolver();
      byte[] imageBytes = Base64.decodeBase64(imageAsset);
      String fileName = dmcName.replace(Constants.SPACE, "-").toLowerCase();
      AssetManager assetMgr = resolver.adaptTo(AssetManager.class);
      String imagePath = PathUtils
          .concat(Constants.DAM_SAUDITOURISM_PATH, ScriptConstants.VENDOR_EVENT_DAM_PATH, dmcName,
              fileName);
      assetMgr.createAsset(imagePath, new ByteArrayInputStream(imageBytes), "image/jpeg", true);
      return imagePath;
    } catch (Exception e) {
      LOGGER.error("Unable to retrieve InputStream", e);
      return null;
    } finally {
      if (resolver.isLive()) {
        resolver.close();
      }
    }
  }
}
