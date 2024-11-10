package com.saudi.tourism.core.utils;

import com.adobe.cq.social.group.api.GroupConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.DEFAULT_RESULTS_SIZE;
import static com.saudi.tourism.core.utils.Constants.GROUP_PREDICATE;
import static com.saudi.tourism.core.utils.Constants.PREDICATE_FULLTEXT;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_HUNDRED;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_ZERO;

/**
 * This class is used to construct JCR queries via QueryBuilder.
 */
@Slf4j
public final class JcrQueryUtils {

  /**
   * private constructor.
   */
  private JcrQueryUtils() {
    //private constructor.
  }

  /**
   * execute query and and build send search result.
   *
   * @param resourceResolver resourceResolver
   * @param queryParams      queryParams
   * @param resultsOffset    resultsOffset
   * @param limit            limit
   * @param queryBuilder     QueryBuilder
   * @return SearchResultModel
   */
  public static SearchResult getSearchResult(final ResourceResolver resourceResolver,
      final Map<String, String> queryParams, final long resultsOffset, final int limit,
      final QueryBuilder queryBuilder) {
    PredicateGroup predicates = PredicateConverter.createPredicates(queryParams);

    if (Objects.nonNull(queryBuilder)) {
      Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));

      if (limit <= CONST_ZERO || limit > CONST_HUNDRED) {
        query.setHitsPerPage(DEFAULT_RESULTS_SIZE);
      } else {
        query.setHitsPerPage(limit);
      }
      if (resultsOffset != CONST_ZERO) {
        query.setStart(resultsOffset);
      }
      return query.getResult();
    }
    return null;
  }

  /**
   * This method Adds the search paths to map, that will be used by Query builder.
   *
   * @param groupCount  int groupCount value
   * @param queryParams Map<String, String>
   * @param searchPaths String[]
   * @param orCondition boolean
   */
  public static void addSearchPaths(final int groupCount,
      final Map<String, String> queryParams, final String[] searchPaths,
      final boolean orCondition) {
    boolean condition = BooleanUtils.toBooleanDefaultIfNull(orCondition, false);
    if (Objects.nonNull(queryParams) && ArrayUtils.isNotEmpty(searchPaths)) {
      for (int index = 0; index < searchPaths.length; index++) {
        queryParams.put(groupCount + GROUP_PREDICATE + index + "_path", searchPaths[index]);
      }
      queryParams.put(groupCount + GROUP_PREDICATE + Constants.P_OR, String.valueOf(condition));
      queryParams.put(groupCount + GROUP_PREDICATE + Constants.PATH_SELF, "true");
    }
  }

  /**
   * This method Adds the exclude paths to map, that will be used by Query builder.
   *
   * @param groupCount  int groupCount value
   * @param queryParams Map<String, String>
   * @param excludePath String
   */
  public static void addExcludePath(final int groupCount,
      final Map<String, String> queryParams, final String excludePath) {
    if (Objects.nonNull(queryParams) && StringUtils.isNotBlank(excludePath)) {
      queryParams.put(groupCount + GROUP_PREDICATE + "1_path", excludePath);
      queryParams.put(groupCount + GROUP_PREDICATE + Constants.P_NOT, "true");
      queryParams.put(groupCount + GROUP_PREDICATE + "1_" + Constants.PATH_SELF, "true");
    }
  }

  /**
   * This method Adds the search node types to map, that will be used by Query builder.
   *
   * @param groupCount  int groupCount value
   * @param queryParams Map<String, String>
   * @param types       String[]
   * @param orCondition boolean
   */
  public static void addSearchNodeTypes(final int groupCount,
      final Map<String, String> queryParams, final String[] types, final boolean orCondition) {
    boolean condition = BooleanUtils.toBooleanDefaultIfNull(orCondition, false);
    if (Objects.nonNull(queryParams) && ArrayUtils.isNotEmpty(types)) {
      for (int index = 0; index < types.length; index++) {
        queryParams.put(groupCount + GROUP_PREDICATE + index + "_type", types[index]);
      }
      queryParams.put(groupCount + GROUP_PREDICATE + Constants.P_OR, String.valueOf(condition));
    }
  }



  /**
   * This method Adds the search node types to map, that will be used by Query builder.
   *
   * @param groupCount       int groupCount value
   * @param queryParams      Map<String, String>
   * @param fulltext         String queryParam fulltext .
   */
  public static void addSearchMultipleFullTextParam(final int groupCount,
      final Map<String, String> queryParams, final String fulltext) {
    String[] arrSearchTexts = StringUtils.split(fulltext, Constants.SPACE);

    if (Objects.nonNull(queryParams) && StringUtils
        .isNotBlank(fulltext)) {
      queryParams.put(groupCount
          + GROUP_PREDICATE
          + Constants.P_OR, Constants.STR_TRUE);
      // Sort the String array - Descending manner based on String length
      Arrays.sort(arrSearchTexts, (a, b) -> Integer.compare(b.length(), a.length()));
      for (int index = 0; index < arrSearchTexts.length; index++) {
        if (index <= NumberConstants.CONST_4) {
          queryParams.put(groupCount + GROUP_PREDICATE + index
                  + Constants.UNDERSCORE
                  + PREDICATE_FULLTEXT,
              StringUtils
                  .join(arrSearchTexts[index] + Constants.QUERY_PARTIAL_TEXT_CHARACTER));
        }
      }
    }
  }

  /**
   * This method Adds the search node types to map, that will be used by Query builder.
   *
   * @param groupCount       int groupCount value
   * @param queryParams      Map<String, String>
   * @param fulltext         String queryParam fulltext .
   */
  public static void addSearchFullTextParam(final int groupCount,
      final Map<String, String> queryParams, final String fulltext) {
    if (Objects.nonNull(queryParams) && StringUtils
        .isNotBlank(fulltext)) {
      queryParams.put(groupCount + GROUP_PREDICATE + Constants.P_OR, Constants.STR_TRUE);
      queryParams.put(groupCount + GROUP_PREDICATE + PREDICATE_FULLTEXT, fulltext);
    }
  }

  /**
   * This method Adds the query Param to exclude hideInNav.
   *
   * @param queryParams Map<String, String>
   */
  public static void addExcludeHideInNavProperty(final Map<String, String> queryParams) {
    if (Objects.nonNull(queryParams)) {
      queryParams.put(Constants.BOOL_PROPERTY, Constants.JCR_CONTENT_HIDE_IN_NAV);
      queryParams.put(Constants.BOOLPROPERTY_VALUE, Constants.STR_FALSE);
    }
  }

  /**
   * This method validates if events is expired.
   *
   * @param queryParams Map<String, String>
   */
  public static void addEventsDataFilter(final Map<String, String> queryParams) {
    if (Objects.nonNull(queryParams)) {
      queryParams.put(Constants.NOTEXPIRED, Constants.STR_TRUE);
      queryParams.put(Constants.NOTEXPIRED_PROPERTY, Constants.JCR_CONTENT_CALENDAR_END_DATE);
    }
  }

  /**
   * Query builder predicates map for getting all resources of the specified type.
   *
   * @param path             events path
   * @param pageResourceType resource type to search
   * @return map predicate query map
   */
  public static Map<String, String> getQueryMapToSearchPages(final String path,
      final String pageResourceType) {
    Map<String, String> map = new HashMap<>();

    map.put(Constants.PATH_PROPERTY, path);
    map.put(GroupConstants.PROPERTY_TYPE, Constants.TYPE_PAGE_CONTENT);

    final String propName = "1" + Constants.UNDERSCORE + JcrPropertyPredicateEvaluator.PROPERTY;
    map.put(propName, JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put(propName + Constants.DOT + JcrPropertyPredicateEvaluator.VALUE, pageResourceType);

    map.put(Constants.QP_LIMIT, Constants.QPV_NO_LIMIT);
    return map;
  }

  /**
   * Adds properties for search facets, using param name as `&lt;index&gt;_property` starting from
   * the provided index.
   *
   * @param queryMap   map to update
   * @param index      starting index to add params
   * @param properties list of property names to be added to facets
   * @return map jcr property to query property
   */
  public static Map<String, String> addFacetProperties(final Map<String, String> queryMap,
      Integer index, final String... properties) {
    final Map<String, String> facetPropsMap = new HashMap<>();

    queryMap.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT + "facets",
        Boolean.TRUE.toString());

    if (ArrayUtils.isEmpty(properties)) {
      return facetPropsMap;
    }

    if (index == null) {
      index = 1;
    }

    for (String jcrPropertyName : properties) {
      final String queryPropertyName =
          index + Constants.UNDERSCORE + JcrPropertyPredicateEvaluator.PROPERTY;
      facetPropsMap.put(jcrPropertyName, queryPropertyName);

      queryMap.put(queryPropertyName, jcrPropertyName);

      index++;
    }

    return facetPropsMap;
  }

  /**
   * Add only nodes with /jcr:content/sling:resourceType property.
   * @param queryParams query params
   */
  public static void addResourceTypeExistsProperty(final Map<String, String> queryParams) {
    if (Objects.nonNull(queryParams)) {
      queryParams.put(JcrPropertyPredicateEvaluator.PROPERTY,
          PathUtils.concat(JcrConstants.JCR_CONTENT,
              JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY));
      queryParams.put(JcrPropertyPredicateEvaluator.PROPERTY + "."
              + JcrPropertyPredicateEvaluator.OPERATION,
          JcrPropertyPredicateEvaluator.OP_EXISTS);
      queryParams.put(JcrPropertyPredicateEvaluator.PROPERTY + "."
              + JcrPropertyPredicateEvaluator.VALUE,
          Boolean.TRUE.toString());
    }
  }
}
