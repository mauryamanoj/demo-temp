package com.saudi.tourism.core.models.components.evisa.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.List;

/**
 * E Visa Download Apps Model.
 */

@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class DownloadAppsModel {

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * E Visa Applications.
   */
  @Expose
  @Inject
  private List<EVisaApplications> eVisaApplications;

  /**
   * E Visa Applications Model.
   */
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  @NoArgsConstructor
  public static class EVisaApplications {

    /**
     * App Title.
     */
    @ValueMapValue
    @Expose
    private String title;

    /**
     * App Description.
     */
    @ValueMapValue
    @Expose
    private String description;

    /**
     * Brand Icon.
     */
    @ValueMapValue
    @Expose
    private String brandIcon;

    /**
     * Brand Icon.
     */
    @Inject
    @Expose
    @Setter
    private List<AppsList> appsList;

  }

  /**
   * Apps List Model.
   */
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class AppsList {

    /**
     * Desktop Image.
     */
    @ValueMapValue
    @Expose
    private String appStoreIcon;
    /**
     * App Url.
     */
    @ValueMapValue
    @Expose
    private String appUrl;
    /**
     * App Platform.
     */
    @ValueMapValue
    @Expose
    private String appPlatform;
  }
  /**
   *
   * @return json data.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
