package com.saudi.tourism.core.services.mobile.v1.filters;

import com.saudi.tourism.core.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Fetch Mobile Filters API request params. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchMobileFiltersRequest {

  /** The language. */
  private String locale;

  /** Filter Type String: filtering header items. */
  private String filterType;

  /** Filter Type List: filtering header items. */
  private List<String> filterTypes;

  /** Filter Type String: filtering categories items. */
  private String type;

  /** Filter Type List: filtering categories items. */
  private List<String> types;

  public void setFilterType(String filterType) {
    this.filterType = filterType;
    createFilterTypesList();

    if (StringUtils.isBlank(type) || StringUtils.isBlank(type.replaceAll(Constants.COMMA, StringUtils.EMPTY))) {
      types = filterTypes;
    }
  }

  public void setType(String type) {
    this.type = type;
    createTypesList();
  }

  private void createFilterTypesList() {
    if (StringUtils.isNotBlank(filterType)) {
      this.filterTypes = List.of(filterType.split(Constants.COMMA));
    } else {
      this.filterTypes = Collections.emptyList();
    }
  }

  private void createTypesList() {
    if (StringUtils.isNotBlank(type)) {
      types = Stream.of(type.split(Constants.COMMA))
              .map(String::trim)
              .filter(StringUtils::isNotBlank)
              .collect(Collectors.toList());
    } else {
      setFilterType(filterType);
    }
  }
}
