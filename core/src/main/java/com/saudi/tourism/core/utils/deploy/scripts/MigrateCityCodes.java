package com.saudi.tourism.core.utils.deploy.scripts;

import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * Migration city codes script.
 */
public class MigrateCityCodes extends OnDeployScriptBase {

  /**
   * changedMap.
   */
  private static Map<String, String> changedMap = new HashMap<>();

  /**
   * EASTERN REGION code.
   */
  private static final String EASTERN_REGION = "eastern-region";

  /**
   * MAKKAH REGION code.
   */
  private static final String MAKKAH_REGION = "makkah-region";

  /**
   * RIYADH REGION code.
   */
  private static final String RIYADH_REGION = "riyadh-region";

  /**
   * MADINAH REGION code.
   */
  private static final String MADINAH_REGION = "madinah-region";

  static {
    changedMap.put("aljouf", "al-jouf-region");
    changedMap.put("eastern-province", EASTERN_REGION);
    changedMap.put("albaha", "al-baha");
    changedMap.put("aseer-region", "asir-region");
    changedMap.put("hofuf", "hofuf-and-almabarraz");
    changedMap.put("ithra", "dhahran");
    changedMap.put("citytest", "riyadh");

    changedMap.put("jizan", "jazan");
    changedMap.put("mecca", "makkah");
    changedMap.put("medina", "madinah");
    changedMap.put("al-khobr", "al-khobar");
    changedMap.put("al-ula", "alula");
    changedMap.put("kaec", "king-abdullah-economic-city");
    changedMap.put("aseer", "asir");
    changedMap.put("amlaj", "umluj");
    changedMap.put("deebaa", "duba");
  }

  /**
   * changedMap.
   */
  private static Map<String, String> regionMap = new HashMap<>();

  static {
    regionMap.put("La Meca", MAKKAH_REGION);
    regionMap.put("La Mecque", MAKKAH_REGION);
    regionMap.put("Province de La Mecque", MAKKAH_REGION);
    regionMap.put("Provinz Mekka", MAKKAH_REGION);
    regionMap.put("Mekka", MAKKAH_REGION);
    regionMap.put("Провинция Мекка", MAKKAH_REGION);
    regionMap.put("Meca", MAKKAH_REGION);
    regionMap.put("Джидда", MAKKAH_REGION);
    regionMap.put("Provincia de La Meca", MAKKAH_REGION);
    regionMap.put("مكة المكرمة", MAKKAH_REGION);
    regionMap.put("Мекка", MAKKAH_REGION);
    regionMap.put("Восточная провинция", EASTERN_REGION);
    regionMap.put("المنطقة الشرقيّة", EASTERN_REGION);
    regionMap.put("Riad", RIYADH_REGION);
    regionMap.put("Riyad", RIYADH_REGION);
    regionMap.put("Riyadh Boulevard", RIYADH_REGION);
    regionMap.put("منطقة الرياض", RIYADH_REGION);
    regionMap.put("Эр-Рияд", RIYADH_REGION);
    regionMap.put("Провинция Эр-Рияд", RIYADH_REGION);
    regionMap.put("Провинция Эш-Шаркийя", EASTERN_REGION);
    regionMap.put("Provincia Oriental", EASTERN_REGION);
    regionMap.put("Province d'Ach-Charqiya", EASTERN_REGION);
    regionMap.put("المنطقة الشرقية", EASTERN_REGION);
    regionMap.put("Ach-Charqiya", EASTERN_REGION);
    regionMap.put("Östliche Provinz", EASTERN_REGION);
    regionMap.put("Dschidda", MAKKAH_REGION);
    regionMap.put("Yida", MAKKAH_REGION);
    regionMap.put("Centro histórico de Yida", MAKKAH_REGION);
    regionMap.put("Medina Region", MADINAH_REGION);
    regionMap.put("المدينة المنورة", MADINAH_REGION);
    regionMap.put("منطقة المدينة المنورة", MADINAH_REGION);

    regionMap.put("Jizan Region", "jazan-region");
  }

  /**
   * resourceTypeMap.
   */
  private static List<MigrationObj> migrationObjlist = new ArrayList<>();

  static {
    migrationObjlist.add(
        new MigrationObj(NT_UNSTRUCTURED, "sauditourism/components/structure/app-city-page",
            "city", true));
    migrationObjlist.add(
        new MigrationObj(NT_UNSTRUCTURED, "sauditourism/components/content/cards-grid",
            "gridCards/*/city", false));
    migrationObjlist.add(new MigrationObj(NT_UNSTRUCTURED,
        "sauditourism/components/content/trip-planner/v1/city-itinerary", "city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/app-location-page",
            "city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/app-package-page",
            "city", true));
    migrationObjlist.add(new MigrationObj(TYPE_PAGE_CONTENT,
        "sauditourism/components/structure/arabian-experience-page", "city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/activity-page",
            "city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/attraction-page",
            "attraction/city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/page",
            "city", true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/page", "region",
            true));
    migrationObjlist.add(
        new MigrationObj(TYPE_PAGE_CONTENT, "sauditourism/components/structure/event-detail-page",
            "city", true));
  }

  @Override protected void execute() {
    logger.info("OnDeploy Script - execute started");
    Set<String> cityCodeSet = loadCitiesFixedCodeSet();
    for (MigrationObj entry : migrationObjlist) {
      migrateResourceType(entry, cityCodeSet);
    }
    logger.info("Properties transfer finished successfully. Processed {} rules",
        migrationObjlist.size());
  }

  /**
   * migrateResourceType.
   *
   * @param migrationObj migrationObj
   * @param cityCodeSet cityCodeSet
   */
  private void migrateResourceType(MigrationObj migrationObj, final Set<String> cityCodeSet) {
    try {
      final QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
      if (queryBuilder == null) {
        logger.error("Couldn't get QueryBuilder. Processing failed");
        return;
      }

      final Map<String, String> queryMap = prepareQueryMap(migrationObj);
      final Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), getSession());
      final SearchResult result = query.getResult();

      final Iterator<Resource> it = result.getResources();
      while (it.hasNext()) {
        Resource root = it.next();
        final String fieldPath = migrationObj.getFieldPath();
        for (Resource res : getResourcesByPath(root, fieldPath)) {
          processResource(res, migrationObj, cityCodeSet, fieldPath);
        }
      }
      getResourceResolver().commit();
    } catch (Exception e) {
      throw new IllegalStateException("Properties transfer failed", e);
    }
  }

  /**
   * Process resource.
   * @param res resource
   * @param migrationObj migrationObj
   * @param cityCodeSet cityCodeSet
   * @param fieldPath fieldPath
   */
  private void processResource(Resource res, MigrationObj migrationObj, Set<String> cityCodeSet,
      String fieldPath) {
    final String resourcePath = res.getPath();
    logger.debug("Processing resource: {}, field(s): {}", resourcePath, fieldPath);

    ModifiableValueMap map = res.adaptTo(ModifiableValueMap.class);
    String[] split = fieldPath.split("/");
    String fieldName = split[split.length - 1];
    //noinspection ConstantConditions
    String orig = map.get(fieldName, String.class);
    if (StringUtils.isNotBlank(orig)) {
      String val = AppUtils.stringToID(orig.trim());
      String val2 = changedMap.get(val);
      if (StringUtils.isNotBlank(val2)) {
        val = val2;
      }
      if (!orig.equals(val)) {
        if (cityCodeSet.contains(val)) {
          // Workaround for arrays - just remove old field
          map.remove(fieldName);
          map.put(fieldName, val);
        } else {
          if (fieldPath.endsWith("region") && Objects.nonNull(regionMap.get(orig.trim()))) {
            map.put(fieldName, regionMap.get(orig.trim()));
          } else {
            logger.warn("Incorrect value filed[{}] value[{}] path[{}] type[{}] ", fieldPath,
                orig, resourcePath, migrationObj.getResourceType());
          }
        }
      }
    }
  }

  /**
   * getResourcesByPath.
   *
   * @param root      root
   * @param fieldPath fieldPath
   * @return getResourcesByPath
   */
  private List<Resource> getResourcesByPath(Resource root, String fieldPath) {
    int indexOf = fieldPath.indexOf('/');
    if (indexOf < 0) {
      return ImmutableList.of(root);
    }
    List<Resource> currentList = new ArrayList<>();
    List<Resource> resultList = new ArrayList<>();
    resultList.add(root);
    String[] path = fieldPath.split("/");
    for (int i = 0; i < path.length - 1; i++) {
      currentList.clear();
      currentList.addAll(resultList);
      resultList.clear();
      if (path[i].equals("*")) {
        for (Resource res : currentList) {
          for (Resource rr : res.getChildren()) {
            resultList.add(rr);
          }
        }
      } else {
        for (Resource res : currentList) {
          resultList.add(res.getChild(path[i]));
        }
      }
    }
    return resultList;
  }

  /**
   * prepareQueryMap.
   *
   * @param migrationObj migrationObj
   * @return map
   */
  private Map<String, String> prepareQueryMap(MigrationObj migrationObj) {
    ImmutableMap.Builder<String, String> mapBuilder =
        ImmutableMap.<String, String>builder().put("p.limit", "-1")
            .put("path", Constants.ROOT_CONTENT_PATH).put("type", migrationObj.getType())
            .put("1_property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)
            .put("1_property.value", migrationObj.getResourceType());
    if (migrationObj.isCheckOnExist()) {
      mapBuilder.put("2_property", migrationObj.getFieldPath());
      mapBuilder.put("2_property.operation", "exists");
    }
    return mapBuilder.build();
  }

  /**
   * MigrationObj.
   */
  @Data
  private static class MigrationObj {
    /**
     * type.
     */
    private String type;
    /**
     * resourceType.
     */
    private String resourceType;
    /**
     * fieldName.
     */
    private String fieldPath;
    /**
     * checkOnExist.
     */
    private boolean checkOnExist;

    /**
     * Constructor.
     *
     * @param type         type
     * @param resourceType resourceType
     * @param fieldPath    fieldPath
     * @param checkOnExist checkOnExist
     */
    MigrationObj(final String type, final String resourceType, final String fieldPath,
        final boolean checkOnExist) {
      this.type = type;
      this.resourceType = resourceType;
      this.fieldPath = fieldPath;
      this.checkOnExist = checkOnExist;
    }
  }

  /**
   * loadCitiesFixedCodeSet.
   * @return set
   */
  private Set<String> loadCitiesFixedCodeSet() {
    Resource root = getResourceResolver()
        .getResource("sauditourism/components/content/utils/city-fixed");
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    final Set<String> set = new HashSet<>();
    for (@NotNull Iterator<Resource> it = root.listChildren(); it.hasNext();) {
      ValueMap valueMap = it.next().getValueMap();
      set.add(valueMap.get(Constants.VALUE, String.class));
    }
    return set;
  }
}
