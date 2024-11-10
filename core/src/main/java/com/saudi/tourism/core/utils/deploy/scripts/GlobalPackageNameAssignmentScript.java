package com.saudi.tourism.core.utils.deploy.scripts;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * .
 */
@Generated
public class GlobalPackageNameAssignmentScript extends PagePropertyMigrationScript {

  /**
   * Default locale root.
   */
  private static final String DEFAULT_LOCALE_ROOT = PathUtils.concat(Constants.ROOT_CONTENT_PATH,
      Constants.DEFAULT_LOCALE);

  /**
   * Where to search: /content/sauditourism/app.
   */
  private static final String PATH_TO_SEARCH = PathUtils.concat(DEFAULT_LOCALE_ROOT,
      Constants.PACKAGES);

  /**
   * Property 1: jcr:content/sling:resourceType.
   */
  private static final String RESOURCE_TYPE = PathUtils.concat(JcrConstants.JCR_CONTENT,
      JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);

  /**
   * Site locales list.
   */
  private List<String> locales;

  @Override protected Map<String, String> prepareQueryMap() {
    final Map<String, String> queryMap = new HashMap<>();

    queryMap.put("p.limit", "-1");

    queryMap.put("path", PATH_TO_SEARCH);
    queryMap.put("type", NameConstants.NT_PAGE);

    queryMap.put("1_group.1_property", RESOURCE_TYPE);
    queryMap.put("1_group.1_property.value", Constants.PACKAGE_DETAIL_RES_TYPE);

    return queryMap;
  }

  @Override protected void transferProperties(final Node node, final int pageNumber)
      throws RepositoryException {
    Page enPage = getPageManager().getPage(node.getPath());
    final String globalPackageTitle = enPage.getProperties().get(Constants.PN_TITLE, String.class);
    final String relativePath = PathUtils.relativize(DEFAULT_LOCALE_ROOT, node.getPath());
    getLocales().stream().forEach(locale -> {
      String localePagePath = PathUtils.concat(Constants.ROOT_CONTENT_PATH, locale, relativePath);

      Page localePage =
          getPageManager().getPage(localePagePath);
      if (localePage == null) {
        logger.warn("{} page wasn't found.", localePagePath);
        return;
      }

      Resource contentResource = localePage.getContentResource();
      if (contentResource == null) {
        logger.warn("{} node misses content resource node.", localePage.getPath());
        return;
      }

      ValueMap valueMap = contentResource.adaptTo(ModifiableValueMap.class);
      valueMap.put(Constants.PN_GLOBAL_PACKAGE_TITLE, globalPackageTitle);
    });
  }

  /**
   * Locales lazy loading.
   * @return list of locales
   */
  private List<String> getLocales() {
    if (this.locales == null) {
      this.locales =
          CommonUtils.getLocales(getResourceResolver()).stream()
              .filter(locale -> !Constants.DEFAULT_LOCALE.equals(locale))
              .filter(locale -> LocaleUtils.isAvailableLocale(new Locale(locale)))
              .collect(Collectors.toList());
    }
    return this.locales;
  }
}
