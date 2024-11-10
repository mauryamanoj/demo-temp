package com.saudi.tourism.core.models.components.tripplan.v1;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The sling model for City Itinerary component.
 * The component extends Core Components' Tabs component, so we implement corresponding interface
 * and also use their model implementation in this class (injected via resource super type).
 * It's used only for a component, for the API json output another class {@link CityItinerary} is
 * used.
 */
@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
          name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Generated
@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {CityItineraryComponent.class, ComponentExporter.class, ContainerExporter.class},
       resourceType = Constants.RT_CITY_ITINERARY)
public class CityItineraryComponent implements Serializable, Tabs {

  /**
   * Contains internationalized title for one day for current locale.
   */
  @Getter
 // @Setter
  @JsonIgnore
  private final transient String dayName;

  /**
   * TabsImpl (from Core Components), injected using super type feature.
   */
  @Getter
  @Setter
  @JsonUnwrapped
  @Self
  @Via(type = ResourceSuperType.class)
  private transient Tabs tabsImpl;

  /**
   * Model initialization constructor.
   *
   * @param currentPage  the current page where this component was placed
   * @param i18nProvider service that provides i18n resource bundles
   */
  @Inject
  public CityItineraryComponent(
      @ScriptVariable(name = WCMBindingsConstants.NAME_CURRENT_PAGE) final Page currentPage,
      @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
      final ResourceBundleProvider i18nProvider) {

    final String pagePath = currentPage.getPath();
    final String language = CommonUtils.getLanguageForPath(pagePath);

    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    dayName = i18nBundle.getString(TripPlanConstants.DAY);
  }

  /**
   * The default active item for Tabs component, is not used for Trip Plan.
   *
   * @return active item name
   */
  @Override
  public String getActiveItem() {
    return getTabsImpl().getActiveItem();
  }

  /**
   * Accessibility label for this component.
   *
   * @return text label for the website's accessibility
   */
  @Override
  public String getAccessibilityLabel() {
    return getTabsImpl().getAccessibilityLabel();
  }

  /**
   * A list of container items (days in case of Trip Plan, each item is a Day Component reference.
   *
   * @return list of items
   */
  @JsonProperty(ExporterConstants.PN_ITEMS)
  @Override
  @NotNull
  public List<ListItem> getItems() {
    final List<ListItem> items = getTabsImpl().getItems();
    final List<ListItem> days = new LinkedList<>();

    if (CollectionUtils.isNotEmpty(items)) {
      for (int i = 0; i < items.size(); i++) {
        days.add(new DayItemImpl(items.get(i), i));
      }
    }

    return days;
  }

  /**
   * :type field for .model.json request, answers current resource type for this component.
   *
   * @return {@link Constants#RT_CITY_ITINERARY}
   * "sauditourism/components/content/trip-planner/v1/city-itinerary"
   */
  @Override
  public @NotNull String getExportedType() {
    return Constants.RT_CITY_ITINERARY;
  }

  /**
   * Inner wrapper class to override title of the tab as a "Day &lt;day number&gt;".
   */
  private class DayItemImpl implements ListItem {
    /**
     * Wrapped original item.
     *
     * @see ListItem
     */
    private final ListItem origItem;

    /**
     * Number of this day in the current trip plan.
     */
    private final int dayNum;

    /**
     * Constructor for this wrapper class.
     *
     * @param origItem list item from Tabs component implementation
     * @param index    tab index
     */
    DayItemImpl(final ListItem origItem, final int index) {
      this.origItem = origItem;
      this.dayNum = index + 1;
    }

    /**
     * Title for this tab (day), is produced automatically as `Day &lt;day number&gt;`.
     *
     * @return a new title for this tab (day)
     */
    @Override
    public @Nullable String getTitle() {
      return dayName + StringUtils.SPACE + dayNum;
    }

    /**
     * Tab description.
     *
     * @return not changed tab description
     */
    @Override
    public @Nullable String getDescription() {
      return origItem.getDescription();
    }

    /**
     * Tab last modified date.
     *
     * @return not changed tab modified date
     */
    @Override
    public @Nullable Calendar getLastModified() {
      return origItem.getLastModified();
    }

    /**
     * Path to this tab item, the behavior hasn't been changed.
     *
     * @return path to tab item
     */
    @Override
    public @Nullable String getPath() {
      return origItem.getPath();
    }

    /**
     * Name of this tab item, the behavior hasn't been changed.
     *
     * @return name of this tab item
     */
    @Override
    public @Nullable String getName() {
      return origItem.getName();
    }
  }
}
