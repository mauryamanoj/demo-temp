package com.saudi.tourism.core.models.components.tripplan.v1;

import com.saudi.tourism.core.models.components.tripplan.PathLocaleFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Class stores all parameters for searching / filtering activities.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchActivityFilter implements PathLocaleFilter {

  /**
   * Requesting an activity or activities list by path.
   */
  private String path;

  /**
   * Language parameter.
   */
  private String locale;

  /**
   * Filter activities by city.
   */
  private List<String> city;

  /**
   * Filter activities by interest.
   */
  private List<String> interest;

  /**
   * Filter activities by travel partner.
   */
  private List<String> partner;

  @Override
  public String toString() {
    return "[" + "path='" + path + '\'' + ", locale='" + locale + '\'' + ", city=" + city + ']';
  }
}
