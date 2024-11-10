package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Rows General Information Widget.
 */
@Data
@AllArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RowsGeneralInformationWidget {

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * icon Url.
   */
  @ValueMapValue
  private String iconUrl;

  /**
   * sideTitle.
   */
  @ValueMapValue(name = "sidetitle")
  private String sideTitle;

  /**
   * side Title Type.
   */
  @ValueMapValue
  private String sideTitleType;

  /**
   * side Value.
   */
  @ValueMapValue
  private String sideValue;

  /**
   * description.
   */
  @ValueMapValue
  private String description;

  /**
   * lat.
   */
  @ValueMapValue
  private String lat;

  /**
   * lng.
   */
  @ValueMapValue
  private String lng;

  public RowsGeneralInformationWidget() {

  }

  /**
   * Constructor with all params.
   * @param title
   * @param sideTitle
   * @param sideTitleType
   */
  public RowsGeneralInformationWidget(
      String title,
      String sideTitle,
      String sideTitleType) {
    this.title = title;
    this.sideTitle = sideTitle;
    this.sideTitleType = sideTitleType;
  }

  /**
   * Constructor with all params.
   * @param title
   * @param sideTitle
   * @param iconUrl
   * @param sideTitleType
   */
  public RowsGeneralInformationWidget(
      String title,
      String sideTitle,
      String sideTitleType, String iconUrl) {
    this.title = title;
    this.sideTitle = sideTitle;
    this.sideTitleType = sideTitleType;
    this.iconUrl =  iconUrl;
  }
}
