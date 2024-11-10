package com.saudi.tourism.core.models.components.evisa.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
/**
 * E-visa Placeholder component.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EVisaPlaceholderModel {
  /**
   * COUNTRY_LIST_ENDPOINT.
   */
  public static final String COUNTRY_LIST_ENDPOINT = "/bin/api/v1/geo/countries";

  /**
   * E_VISA_CONFIG_ENDPOINT.
   */
  public static final String E_VISA_CONFIG_ENDPOINT = "/bin/api/v1/evisa/config";

  /**
   * country list end point.
   */
  @Expose
  private String countryListEndPoint;

  /**
   * e visa config end point.
   */
  @Expose
  private String eVisaConfigEndPoint;

  /**
   * Error Message.
   */
  @ValueMapValue
  @Expose
  private String errorMessage;

  /**
   * fetching locale.
   */
  @PostConstruct
  protected void init() {
    countryListEndPoint = COUNTRY_LIST_ENDPOINT;
    eVisaConfigEndPoint = E_VISA_CONFIG_ENDPOINT;
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
