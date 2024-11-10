package com.saudi.tourism.core.models.components.fetchExperiences.v1;

import java.util.Calendar;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.gson.IsoCalendarAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Fetch Experiences Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class FetchExperiencesModel {

  /**
   * Current Resource.
   */
  @Self
  private transient Resource currentResource;

  /** Reference of Saudi Tourism Configuration. */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /** i18n provider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * componentId.
   */
  @Expose
  @ValueMapValue
  private String componentId;

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Link.
   */
  @Expose
  @ChildResource
  private Link link;

  /**
   * Display.
   */
  @Expose
  @ChildResource
  private FetchExperiencesDisplayModel display;

  /**
   * Filter.
   */
  @Expose
  @ChildResource
  private FetchExperienceFilterModel filter;

  /**
   * Events api url.
   */
  @Expose
  @ValueMapValue
  private String apiUrl;


  @PostConstruct
  void init() {

    if (Objects.nonNull(display) && StringUtils.equals("list_view", display.getDisplayType())) {
      display = display.withCardSize("small");
    }

    apiUrl = saudiTourismConfigs.getThingsToDoApiEndpoint();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Calendar.class, new IsoCalendarAdapter());
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
