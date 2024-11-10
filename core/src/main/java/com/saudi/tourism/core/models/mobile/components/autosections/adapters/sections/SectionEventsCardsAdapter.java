package com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.events.eventscards.v1.EventsCardsModel;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.atoms.CategoryFilterItem;
import com.saudi.tourism.core.models.mobile.components.atoms.Filter;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import com.saudi.tourism.core.services.mobile.v1.youmayalsolike.YouMayAlsoLikeService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(service = SectionResponseModelAdapter.class, immediate = true)
public class SectionEventsCardsAdapter implements SectionResponseModelAdapter {

  /** Sling settings service. */
  @Reference
  private SlingSettingsService settingsService;

  /**
   * YouMayAlsoLike service.
   */
  @Reference
  private transient YouMayAlsoLikeService mayAlsoLikeService;

  /** The constant EVENTS_CARDS_RES_TYPE. */
  private static final String EVENTS_CARDS_RES_TYPE = "sauditourism/components/content/events-cards";

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase(EVENTS_CARDS_RES_TYPE, ((Resource) adaptable).getResourceType());
  }

  @Override
  public SectionResponseModel adaptTo(Adaptable adaptable) {
    Resource currentResource = ((Resource) adaptable);
    if (currentResource == null) {
      return null;
    }

    var resolver = currentResource.getResourceResolver();
    var pageManager = resolver.adaptTo(PageManager.class);
    if (pageManager == null) {
      return null;
    }

    var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return null;
    }
    var eventsCardsModel = currentResource.adaptTo(EventsCardsModel.class);
    if (eventsCardsModel == null) {
      return null;
    }

    FetchEventsRequest fetchEventsRequest =
        mayAlsoLikeService.fillEventsRequestFromEventsCards(eventsCardsModel);
    FetchEventsResponse events = mayAlsoLikeService.getFilteredEvents(fetchEventsRequest);

    SectionResponseModel section = new SectionResponseModel();
    List<ItemResponseModel> items = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(events.getData())) {
      items =
          events.getData().stream()
              .filter(Objects::nonNull)
              .map(
                  event -> {
                    // Fill titles.
                    Titles titles = new Titles();
                    titles.setTitle(event.getTitle());
                    // Fill media gallery.
                    MediaGallery gallery = new MediaGallery();
                    Optional<Image> firstImage =
                        event.getBannerImages().stream().filter(Objects::nonNull).findFirst();
                    firstImage.ifPresent(
                        image -> {
                          gallery.setType("IMAGE");
                          gallery.setUrl(
                              LinkUtils.getAuthorPublishAssetUrl(
                                  resolver,
                                  image.getFileReference(),
                                  settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                        });
                    // Fill cta.
                    CategoryFilterItem filterItem =
                        CategoryFilterItem.builder()
                            .id("destinations")
                            .items(
                                events.getData().stream()
                                    .map(
                                        e -> {
                                          if (e.getDestination() != null) {
                                            Category category = new Category();
                                            category.setId(e.getDestination().getId());
                                            category.setTitle(e.getDestination().getTitle());
                                            return category;
                                          }
                                          return null;
                                        })
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .collect(Collectors.toList()))
                            .build();
                    Filter filter = new Filter();
                    filter.setCategories(List.of(filterItem));
                    Cta cta = new Cta();
                    // Fill custom action.
                    CustomAction customAction = new CustomAction();
                    if (Objects.nonNull(event.getTicketsCtaLink())) {
                      cta.setType("WEB");
                      cta.setFilter(filter);
                      cta.setUrl(event.getTicketsCtaLink().getUrl());
                      customAction.setTitle(event.getTicketsCtaLink().getText());
                    }
                    customAction.setShow(true);
                    customAction.setEnable(true);
                    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
                    customAction.setCta(cta);
                    // Fill categories.
                    List<Category> categories = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(event.getCategories())) {
                      categories =
                          event.getCategories().stream()
                              .filter(Objects::nonNull)
                              .map(
                                  c -> {
                                    Category category = new Category();
                                    category.setId(c.getId());
                                    category.setTitle(StringUtils.upperCase(c.getTitle()));
                                    return category;
                                  })
                              .collect(Collectors.toList());
                    }
                    // Fill location.
                    Location location = new Location();
                    location.setLat(event.getLat());
                    location.setLng(event.getLng());
                    if (Objects.nonNull(event.getDestination())) {
                      Destination destination =
                          Destination.builder()
                              .id(event.getDestination().getTitle().toLowerCase())
                              .title(StringUtils.upperCase(event.getDestination().getTitle()))
                              .build();
                      location.setDestination(destination);
                    }

                    ItemResponseModel item = new ItemResponseModel();
                    if (StringUtils.isNotBlank(event.getPagePath())) {
                      item.setId(event.getPagePath());
                    }
                    item.setType(Constants.TYPE_AUTO);
                    item.setTitles(titles);
                    item.setMediaGallery(List.of(gallery));
                    item.setCustomAction(customAction);
                    item.setCategories(categories);
                    item.setLocation(location);
                    return item;
                  })
              .filter(i -> Objects.nonNull(i) && !StringUtils.equals(i.getId(), currentPage.getPath()))
              .collect(Collectors.toList());
    }
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(eventsCardsModel.getTitle());
    sectionHeader.setShowViewAll(false);
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("MAIN");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/you-may-also-like");
    section.setFilterType("event");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setSectionHeader(sectionHeader);
    section.setItems(items);
    section.setTotalCount(section.getItems().size());

    return section;
  }
}
