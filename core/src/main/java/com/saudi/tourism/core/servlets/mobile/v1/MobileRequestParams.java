package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/** The Class ViewAllRequestParams. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileRequestParams {
  /** Default limit. */
  public static final Integer DEFAULT_LIMIT = 10;

  /** Default offset. */
  public static final Integer DEFAULT_OFFSET = 0;

  /** Expected parts length. */
  private static final int EXPECTED_PARTS_LENGTH = 3;

  /** Contains Operator. */
  private static final String CONTAINS = "contains";

  /** Equals Operator. */
  private static final String EQ = "eq";

  /** The limit. */
  private Integer limit = DEFAULT_LIMIT;

  /** The offset. */
  private Integer offset = DEFAULT_OFFSET;

  /** Locale. */
  private String locale;

  /** Type. */
  private String type;

  /** Id. */
  private String id;

  /** Section ID Request Param. */
  private String sectionId;

  /** Tab ID Request Param. */
  private String tabId;

  /** Sub tab ID Request Param. */
  private String subTabId;

  /** Item ID Request Param. */
  private String itemId;

  /** excludeSectionsId Request Param. */
  private List<String> excludeSectionsId;

  /** Filter Request Param. */
  private String filters;

  /** Request latitude filter. */
  private String lat;

  /** Request longitude filter. */
  private String lng;

  /** Request search filter. */
  private String search;

  /** Request startDate filter. */
  private String startDate;

  /** Request endDate filter. */
  private String endDate;

  /** Request sortBy. */
  private List<String> sortBy;

  /** The category tags. */
  private List<String> categories;

  /** The poitype tags. */
  private List<String> poiTypes;

  /** The destinations list. */
  private List<String> destinations;

  /** The seasons list. */
  private List<String> seasons;

  /** The types list. */
  private List<String> types;

  /** discounted items. */
  private Boolean discounted;

  /** minimum price. */
  private Integer minPrice;

  /** maximum price. */
  private Integer maxPrice;

  /**
   * Things To Do Request Params.
   */
  private FetchThingsToDoRequest thingsToDoRequest;

  public void setFilters(String filters) {
    this.filters = filters;
    extractFiltersFromRequestParam();
  }


  public void setExcludeSectionsId(String excludeSectionsId) {
    this.excludeSectionsId = createListFromFilterValue(excludeSectionsId);
  }

  void extractFiltersFromRequestParam() {
    List<String> filtersParam = Arrays.asList(filters.split(";"));
    Map<String, String> filterValues = new HashMap<>();

    filtersParam.stream()
        .map(filter -> filter.split(":"))
        .filter(parts -> parts.length == EXPECTED_PARTS_LENGTH)
        .forEach(
            parts -> {
              var key = parts[0].trim();
              var operator = parts[1].trim();
              var value = parts[2].trim();

              if (EQ.equals(operator) || CONTAINS.equals(operator)) {
                filterValues.put(key, value);
              }
            });

    lat = filterValues.get(Constants.LAT);
    lng = filterValues.get(Constants.LNG);
    search = filterValues.get(Constants.SEARCH);
    startDate = filterValues.get(Constants.START_DATE);
    endDate = filterValues.get(Constants.END_DATE);
    categories = createListFromFilterValue(filterValues.get(Constants.CATEGORIES));
    destinations = createListFromFilterValue(filterValues.get(Constants.DESTINATIONS));
    seasons = createListFromFilterValue(filterValues.get(Constants.SEASONS));
    types = createListFromFilterValue(filterValues.get(Constants.TYPES));
    poiTypes = createListFromFilterValue(filterValues.get(Constants.POI_TYPES));
    discounted = Boolean.parseBoolean(filterValues.get("discounted"));
    if (StringUtils.isNotBlank(filterValues.get("minPrice"))) {
      minPrice = Integer.parseInt(filterValues.get("minPrice"));
    }
    if (StringUtils.isNotBlank(filterValues.get("maxPrice"))) {
      minPrice = Integer.parseInt(filterValues.get("maxPrice"));
    }
  }

  /**
   * Create list of filters from filter value.
   * @param filterValue
   * @return list
   */
  private List<String> createListFromFilterValue(String filterValue) {
    if (StringUtils.isNotBlank(filterValue)) {
      return Arrays.asList(filterValue.split(Constants.COMMA));
    } else {
      return Collections.emptyList();
    }
  }
}
