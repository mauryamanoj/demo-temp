package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Automatic Items Adapter Factory.
 */
@Component(service = AutoItemsAdapterFactory.class, immediate = true)
public class AutoItemsAdapterFactoryImpl implements AutoItemsAdapterFactory {
  /**
   * Constant type event.
   */
  private static final String TYPE_EVENT = "event";

  /**
   * Constant type story.
   */
  private static final String TYPE_STORY = "story";

  /**
   * Constant type experience.
   */
  private static final String TYPE_EXPERIENCE = "experience";

  /**
   * Auto Items Event Adapter.
   */
  @Reference(target = "(type=event)")
  private transient AutoItemsAdapter eventAdapter;

  /**
   * Auto Items ThingsToDo Adapter.
   */
  @Reference(target = "(type=thingsToDo)")
  private transient AutoItemsAdapter thingsToDoAdapter;

  /**
   * Auto Items Story Adapter.
   */
  @Reference(target = "(type=story)")
  private transient AutoItemsAdapter storyAdapter;

  /**
   * Auto Items Experience Adapter.
   */
  @Reference(target = "(type=experience)")
  private transient AutoItemsAdapter experienceAdapter;

  @Override
  public List<ItemResponseModel> getItems(ResourceResolver resourceResolver, SlingSettingsService settingsService,
      @NonNull MobileRequestParams request, List<String> types) {
    List<ItemResponseModel> items = new ArrayList<>();
    if (types.contains(TYPE_EVENT)) {
      items.addAll(
          eventAdapter.processItems(resourceResolver, settingsService, request, List.of("event")));
    }

    List<String> thingsToDoTypes = getFilteredThingsToDoTypes(types);
    if (CollectionUtils.isNotEmpty(thingsToDoTypes)) {
      items.addAll(
          thingsToDoAdapter.processItems(
              resourceResolver, settingsService, request, thingsToDoTypes));
    }

    if (types.contains(TYPE_STORY)) {
      items.addAll(
          storyAdapter.processItems(resourceResolver, settingsService, request, List.of(TYPE_STORY)));
    }

    if (types.contains(TYPE_EXPERIENCE)) {
      items.addAll(
          experienceAdapter.processItems(resourceResolver, settingsService, request, List.of(TYPE_EXPERIENCE)));
    }

    return items;
  }

  private List<String> getFilteredThingsToDoTypes(List<String> types) {
    return types.stream()
        .filter(CommonUtils.THINGS_TO_DO_TYPES::contains)
        .collect(Collectors.toList());
  }
}
