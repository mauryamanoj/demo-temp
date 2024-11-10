package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.events.v1.EventsCFService;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component(service = AutoItemsAdapter.class, immediate = true, property = {"type=event"})
public class AutoItemsEventAdapter implements AutoItemsAdapter {

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * Events CF service.
   */
  @Reference
  private transient EventsCFService eventsCFService;

  @Override
  public List<ItemResponseModel> processItems(ResourceResolver resourceResolver, SlingSettingsService settingsService,
      @NonNull MobileRequestParams request, @NonNull List<String> types) {
    List<ItemResponseModel> items = new ArrayList<>();
    List<EventCFModel> events = eventsCFService.getAllEvents(resourceResolver, request.getLocale());
    if (CollectionUtils.isNotEmpty(events)) {
      items =
          events.stream()
              .filter(Objects::nonNull)
              .map(
                  event ->
                      MobileUtils.buildItemFromEventCFModel(
                          event, resourceResolver, settingsService, request.getLocale(), i18nProvider))
              .collect(Collectors.toList());
    }

    return items;
  }
}
