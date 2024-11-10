package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component(service = AutoItemsAdapter.class, immediate = true, property = {"type=thingsToDo"})
public class AutoItemsThingsToDoAdapter implements AutoItemsAdapter {
  /**
   * Constant OFFSET.
   */
  private static final Integer OFFSET = 0;
  /**
   * Constant LIMIT.
   */
  private static final Integer LIMIT = -1;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /** ThingsToDo CF Service. */
  @Reference
  private transient ThingsToDoCFService thingsToDoCFService;

  @Override
  public List<ItemResponseModel> processItems(ResourceResolver resourceResolver, SlingSettingsService settingsService,
      @NonNull MobileRequestParams request, @NonNull List<String> types) {

    List<ItemResponseModel> items = new ArrayList<>();

    FetchThingsToDoRequest fetchThingsToDoRequest =
        FetchThingsToDoRequest.builder()
            .locale(request.getLocale())
            .type(types.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))
            .offset(OFFSET)
            .limit(LIMIT)
            .build();

    FetchThingsToDoResponse fetchThingsToDoResponse =
        thingsToDoCFService.getFilteredThingsToDo(fetchThingsToDoRequest);

    if (Objects.nonNull(fetchThingsToDoResponse)) {
      List<ThingToDoModel> thingsToDo = fetchThingsToDoResponse.getData();
      if (CollectionUtils.isNotEmpty(thingsToDo)) {
        items =
            thingsToDo.stream()
                .filter(Objects::nonNull)
                .map(
                    thingToDo ->
                        MobileUtils.buildItemFromThingsToDo(
                            thingToDo, resourceResolver, settingsService, request.getLocale(), i18nProvider))
                .collect(Collectors.toList());
      }
    }

    return items;
  }
}
