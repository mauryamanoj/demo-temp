package com.saudi.tourism.core.models.components.contactus;


import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_PHONE_STATUS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import com.saudi.tourism.core.utils.CommonUtils;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.inject.Inject;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Call US Model.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class CallUsModel {
  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String headlineCallUs;

  /**
   * No results text.
   */
  @ValueMapValue
  @Expose
  private String noResultsText;
  /**
   * All Countries Label.
   */
  @ValueMapValue
  @Expose
  private String allCountriesLabel;

  /**
   * Search input placeholder Label.
   */
  @ValueMapValue
  @Expose
  private String inputPlaceholder;
  /**
   * component id.
   */
  @ValueMapValue
  @Expose
  private String componentHtmlId;
  /**
   * List of regions and data.
   */
  @ChildResource
  @Getter
  @Expose
  private List<Regions> regions;
  /**
   * Call Us bean.
   */
  @Expose
  private CallUsBean countryData;
  /**
   * countries list.
   */
  private List<String> countriesStringList = new ArrayList<>();
  /**
   * final data.
   */
  @Getter
  private String finalData;

  /**
   * countriesList.
   */
  @Getter
  @Expose
  private String countriesList;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * post construct.
   */
  @PostConstruct
  protected void init() {
    final String language = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    if (null != regions) {
      for (Regions region : regions) {
        Iterator<Countries> items = region.getCountries().iterator();
        while (items.hasNext()) {
          Countries country = items.next();
          countryData = new CallUsBean();
          countryData.setRegion(region.getRegionName());
          countryData.setFlag(country.getNationFlag());
          countryData.setName(country.getNationName());
          countryData.setPhone(country.getPhoneNumber());
          countryData.setStatus(i18nBundle.getString(I18_KEY_PHONE_STATUS));
          countriesStringList.add(new Gson().toJson(countryData));
        }
      }
    }
    countriesList = countriesStringList.toString();
  }

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
