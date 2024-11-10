package com.saudi.tourism.core.models.components;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.components.nav.v2.PlanItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Navigation link related data.
 */
@Builder public class NavItem {

  /**
   * Variable to store the title.
   */
  @Getter
  @Setter
  @Expose
  private String title;

  /**
   * Variable to store the description.
   */
  @Getter
  @Setter
  @Expose
  private String description;

  /**
   * Variable to store the featureImage.
   */
  @Getter
  @Setter
  @Expose
  private String featureImage;

  /**
   * Variable to store the url.
   */
  @Getter
  @Setter
  @Expose
  private String url;

  /**
   * Variable to store the urlWithExtension.
   */
  @Getter
  @Setter
  @Expose
  private String urlWithExtension;

  /**
   * Variable to store the list of child pages.
   */
  @Getter
  @Setter
  @Expose
  private List<NavItem> childList;

  /**
   * Variable to store the pageLevel.
   */
  @Getter
  @Setter
  @Expose
  private Integer pageLevel;

  /**
   * Variable to store the siteName.
   */
  @Getter
  @Setter
  @Expose
  @SerializedName("localeCode")
  private String siteName;

  /**
   * Variable to store the navigation type.
   */
  @Getter
  @Setter
  @Expose
  private String navType;

  /**
   * Variable to store properties for Weather navigation type.
   */
  @Getter
  @Setter
  private List<DictItem> cities;

  /**
   * Variable to store properties for Currency navigation type.
   */
  @Getter
  @Setter
  private List<DictItem> currencies;

  /**
   * Variable to store object for Article navigation type.
   */
  @Getter
  @Setter
  private ArticleItem articleItem;

  /**
   * Variable to store list of objects for Plan navigation type.
   */
  @Getter
  @Setter
  private List<PlanItem> plans;

  /**
   * Variable for storing link text.
   */
  @Getter
  @Setter
  @Expose
  private String text;

}


