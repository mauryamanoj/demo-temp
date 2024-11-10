package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_HAPPENING_NEARBY;

/**
 * Happening NearBy Events.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Getter
public class HappeningNearByModel {

  /**
   * Card Type 'calendar'.
   */
  private static final String CARD_TYPE_CALENDAR = "calendar";

  /**
   * Card Style 'happening-nearby'.
   */
  private static final String CARD_STYLE_HAPPENING_NEARBY = "happening-nearby";

  /**
   * Card type.
   */
  @Getter
  @Expose
  private String cardType = CARD_TYPE_CALENDAR;

  /**
   * Card Style.
   */
  @Getter
  @Expose
  private String cardStyle = CARD_STYLE_HAPPENING_NEARBY;

  /**
   * Events api url.
   */
  @Expose
  private String apiUrl;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * Resource Bundle Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Current Resource.
   */
  @Inject
  private Resource resource;

  /**
   * City.
   */
  @Expose
  private String city;

  /**
   * Headline.
   */
  @Expose
  private String headline;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    city = resource.getValueMap().get(Constants.CITY, String.class);

    InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentPage.getContentResource());
    String locale = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);

    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    headline = i18n.getString(I18_KEY_HAPPENING_NEARBY);

    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

}
