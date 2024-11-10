package com.saudi.tourism.core.models.components.filterExplorer.v1;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.gson.IsoCalendarAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Filter Explorer Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class FilterExplorerModel {

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /** Reference of Saudi Tourism Configuration. */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * ApiUrl.
   */
  @Expose
  private String apiUrl;

  /**
   * explorerType.
   */
  @Expose
  @ValueMapValue
  @Default(values = "thingsToDo")
  private String filterType;

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * typeLabel.
   */
  @Expose
  @ValueMapValue
  private String typeLabel;

  /**
   * filtersLabel.
   */
  @ValueMapValue
  @Expose
  private String filtersLabel;

  /**
   * selectLocationLabel.
   */
  @Expose
  @ValueMapValue
  private String selectLocationLabel;

  /**
   * clearLabel.
   */
  @Expose
  private String clearLabel;

  /**
   * search cancel Label.
   */
  @Expose
  private String cancelLabel;

  /**
   * applyLabel.
   */
  @Expose
  private String applyLabel;

  /**
   * searchLimit.
   */
  @Expose
  @ValueMapValue
  private String searchLimit;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @ValueMapValue
  @Expose
  private String noResultsFoundLabel;

  /**
   * variable to hold noResultsFoundDescription.
   */
  @ValueMapValue
  @Expose
  private String noResultFoundDescription;

  /**
   * modalFiltersTitle.
   */
  @ValueMapValue
  @Expose
  private String modalFiltersTitle;

  /**
   * searchPlaceholderLabel.
   */
  @ValueMapValue
  @Expose
  private String searchPlaceholderLabel;

  /**
   * clearAllButton.
   */
  @ValueMapValue
  @Expose
  private String clearAllButton;

  /**
   * loadingLabel.
   */
  @ValueMapValue
  @Expose
  private String loadingLabel;

  /**
   * hideOrnament.
   */
  @ValueMapValue
  @Expose
  private boolean hideOrnament;

  /**
   * types.
   */
  @Expose
  @ChildResource
  private List<FilterExplorerTypeModel> types;

  /**
   * storiesLabel.
   */
  @ValueMapValue
  @JsonIgnore
  private String storiesLabel;

  @PostConstruct
  void init() {
    if (filterType.equals("stories")) {
      apiUrl = saudiTourismConfigs.getStoriesApiEndpoint();
      types = null;
    } else {
      // Default to thingsToDo
      apiUrl = saudiTourismConfigs.getThingsToDoApiEndpoint();
    }
    final var language = CommonUtils.getLanguageForPath(currentResource.getPath());
    if (Objects.nonNull(i18nProvider)) {
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));
      cancelLabel = CommonUtils.getI18nString(i18n, I18nConstants.CANCEL);
      clearLabel = CommonUtils.getI18nString(i18n, I18nConstants.SEARCH_CLEAR);
      applyLabel = CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_APPLY);
    }
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
