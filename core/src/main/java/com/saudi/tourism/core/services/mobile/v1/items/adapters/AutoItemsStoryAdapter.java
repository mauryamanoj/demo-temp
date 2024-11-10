package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesResponse;
import com.saudi.tourism.core.services.stories.v1.StoriesCFService;
import com.saudi.tourism.core.services.stories.v1.Story;
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

import static com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest.DEFAULT_OFFSET;

@Component(service = AutoItemsAdapter.class, immediate = true, property = {"type=story"})
public class AutoItemsStoryAdapter implements AutoItemsAdapter {

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;


  /**
   * Events CF service.
   */
  @Reference
  private transient StoriesCFService storiesCFService;

  @Override
  public List<ItemResponseModel> processItems(ResourceResolver resourceResolver, SlingSettingsService settingsService,
      @NonNull MobileRequestParams request, @NonNull List<String> types) {
    List<ItemResponseModel> items = new ArrayList<>();

    FetchStoriesRequest fetchStoriesRequest =
        FetchStoriesRequest.builder()
            .locale(request.getLocale())
            .offset(DEFAULT_OFFSET)
            .limit(-1)
            .build();

    FetchStoriesResponse storyResponse =
        storiesCFService.getFilteredStories(fetchStoriesRequest);

    if (Objects.nonNull(storyResponse)) {
      List<Story> stories = storyResponse.getData();
      if (CollectionUtils.isNotEmpty(stories)) {
        items =
            stories.stream()
                .filter(Objects::nonNull)
                .map(
                    story ->
                        MobileUtils.buildItemFromStory(
                            story, resourceResolver, settingsService, request.getLocale(), i18nProvider))
                .collect(Collectors.toList());
      }
    }


    return items;
  }
}
