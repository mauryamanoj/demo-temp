package com.saudi.tourism.core.models.mobile.components.autosections.utils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.saudi.tourism.core.models.components.about.v1.AboutSectionModel;
import com.saudi.tourism.core.models.components.alerts.AlertsModel;
import com.saudi.tourism.core.models.components.events.eventscards.v1.EventsCardsModel;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetModel;
import com.saudi.tourism.core.models.components.informationsection.v1.InformationSectionModel;
import com.saudi.tourism.core.models.components.mapwidget.v1.MapWidgetModel;
import com.saudi.tourism.core.models.components.mediagallery.v1.MediaGalleryModel;
import com.saudi.tourism.core.models.components.pagebanner.v1.PageBannerModel;
import com.saudi.tourism.core.models.components.promotional.PromotionalBannerModel;
import com.saudi.tourism.core.models.components.specialshowwidget.v1.SpecialShowWidgetModel;
import com.saudi.tourism.core.models.components.thingstodo.v1.ThingsToDoCardsModel;
import com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections.SectionResponseModelAdapterFactory;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import com.saudi.tourism.core.services.mobile.v1.youmayalsolike.YouMayAlsoLikeService;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;
import com.saudi.tourism.core.utils.CommonUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.settings.SlingSettingsService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Mobile Automatic Sections Builder. */
public final class SectionsBuilder {

  /** Sections Builder. */
  private SectionsBuilder() {
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param settingsService settings service
   * @return auto/media-gallery
   */
  public static SectionResponseModel buildAutoMediaGallery(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    Resource resourceMediaGallery =
        findComponentResource(currentPage, resolver, "root/gallery_section");
    MediaGalleryModel mediaGallery = resourceMediaGallery.adaptTo(MediaGalleryModel.class);
    return SectionsMapper.mapMediaGalleryToAutoSection(
        mediaGallery, resolver, settingsService, currentPage.getTitle());
  }

  public static SectionResponseModel buildAutoAlert(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    Resource resourceAlertWidget = findComponentResource(currentPage, resolver, "root/alerts");
    AlertsModel alerts = resourceAlertWidget.adaptTo(AlertsModel.class);
    return SectionsMapper.mapAlertWidgetToAutoAlertSection(alerts, resolver, settingsService);
  }

  public static SectionResponseModel buildAutoPageBanner(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    Resource resourcePageBanner =
        findComponentResource(currentPage, resolver, "root/page_banner");
    PageBannerModel pageBannerModel = resourcePageBanner.adaptTo(PageBannerModel.class);
    return SectionsMapper.mapPageBannerToAutoSection(pageBannerModel, resolver, settingsService);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @return auto/event-widget
   */
  public static SectionResponseModel buildAutoEventWidget(
      Page currentPage, ResourceResolver resolver) {
    Resource resourceEventWidget =
        findComponentResource(currentPage, resolver, "root/event_date_widget");
    EventDateWidgetModel eventWidget = resourceEventWidget.adaptTo(EventDateWidgetModel.class);
    return SectionsMapper.mapEventWidgetToAutoSection(eventWidget);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param favoriteContentType favoriteContentType
   * @param settingsService
   * @return auto/about-section
   */
  public static SectionResponseModel buildAutoAboutSection(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService, String favoriteContentType) {
    Resource resourceAboutSection =
        findComponentResource(currentPage, resolver, "root/about_section");
    AboutSectionModel about = resourceAboutSection.adaptTo(AboutSectionModel.class);
    return SectionsMapper.mapAboutSectionToAutoSection(about, resolver, settingsService, favoriteContentType);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @return auto/special-show
   */
  public static SectionResponseModel buildAutoSpecialShow(
      Page currentPage, ResourceResolver resolver) {
    Resource rcSpecialShow =
        findComponentResource(currentPage, resolver, "root/special_show_widget");
    SpecialShowWidgetModel specialShow = rcSpecialShow.adaptTo(SpecialShowWidgetModel.class);
    return SectionsMapper.mapSpecialShowWidgetToAutoSection(specialShow);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @return auto/opening-hours
   */
  public static SectionResponseModel buildAutoOpeningHours(
      Page currentPage, ResourceResolver resolver) {
    Resource rcListWidget =
        findComponentResource(currentPage, resolver, "root/information_list_widget");
    InformationListWidgetModel informationList =
        rcListWidget.adaptTo(InformationListWidgetModel.class);
    return SectionsMapper.mapInformationListToOpeningHours(informationList);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param settingsService settingsService
   * @return auto/general-info
   */
  public static SectionResponseModel buildAutoGeneralInformation(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    Resource rcListWidget =
        findComponentResource(currentPage, resolver, "root/information_list_widget");
    InformationListWidgetModel informationList =
        rcListWidget.adaptTo(InformationListWidgetModel.class);
    return SectionsMapper.mapInformationListToGeneralInfo(informationList, resolver, settingsService);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @return auto/map
   */
  public static SectionResponseModel buildAutoMapWidget(
      Page currentPage, ResourceResolver resolver) {
    Resource resourceMapWidget = findComponentResource(currentPage, resolver, "root/map_widget");
    MapWidgetModel mapWidget = resourceMapWidget.adaptTo(MapWidgetModel.class);
    return SectionsMapper.mapMapWidgetToAutoSection(mapWidget);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @return auto/before-you-go
   */
  public static SectionResponseModel buildAutoBeforeYouGo(
      Page currentPage, ResourceResolver resolver) {
    Resource rcInfoSection =
        findComponentResource(currentPage, resolver, "root/information_section");
    InformationSectionModel informationSection =
        rcInfoSection.adaptTo(InformationSectionModel.class);
    return SectionsMapper.mapInformationSectionToAutoSection(informationSection);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param settingsService settingsService
   * @return auto/banner
   */
  public static SectionResponseModel buildAutoBanner(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    Resource rcPromoBanner =
        findComponentResource(currentPage, resolver, "root/responsivegrid/promotional_banner");
    if (Objects.isNull(rcPromoBanner)) {
      return null;
    }
    PromotionalBannerModel promoBanner = rcPromoBanner.adaptTo(PromotionalBannerModel.class);
    return SectionsMapper.mapPromotionalBannerToAutoSection(promoBanner, resolver, settingsService);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param mayAlsoLikeService eventService
   * @param settingsService settingsService
   * @return auto/you-may-also-like
   */
  public static SectionResponseModel buildAutoYouMayAlsoLike(
      Page currentPage,
      ResourceResolver resolver,
      YouMayAlsoLikeService mayAlsoLikeService,
      SlingSettingsService settingsService) {
    Resource rcEventCards =
        findComponentResource(currentPage, resolver, "root/responsivegrid/events_cards");
    if (Objects.isNull(rcEventCards)) {
      return null;
    }
    EventsCardsModel cardsModel = rcEventCards.adaptTo(EventsCardsModel.class);
    FetchEventsRequest fetchEventsRequest =
        mayAlsoLikeService.fillEventsRequestFromEventsCards(cardsModel);
    FetchEventsResponse events = mayAlsoLikeService.getFilteredEvents(fetchEventsRequest);
    return SectionsMapper.mapEventsCardsToAutoMayAlsoLike(
        cardsModel, events, resolver, settingsService);
  }

  /**
   * @param currentPage currentPage
   * @param resolver resolver
   * @param mayAlsoLikeService eventService
   * @param settingsService settingsService
   * @return auto/you-may-also-like
   */
  public static SectionResponseModel buildAutoYouMayAlsoLikeFromThingsToDo(
      Page currentPage,
      ResourceResolver resolver,
      YouMayAlsoLikeService mayAlsoLikeService,
      SlingSettingsService settingsService) {
    Resource rcThingsToDoCards =
        findComponentResource(currentPage, resolver, "root/responsivegrid/things_todo_cards");
    if (Objects.isNull(rcThingsToDoCards)) {
      return null;
    }
    ThingsToDoCardsModel cardsModel = rcThingsToDoCards.adaptTo(ThingsToDoCardsModel.class);
    FetchThingsToDoRequest fetchThingsToDoRequest =
        mayAlsoLikeService.fillThingsToDoRequestFromEventCards(cardsModel);
    FetchThingsToDoResponse thingsToDo =
        mayAlsoLikeService.getFilteredThingsToDo(fetchThingsToDoRequest);
    return SectionsMapper.mapThingsToDoCardsToAutoMayAlsoLike(
        cardsModel, thingsToDo, resolver, settingsService);
  }

  /**
   * Attempts to find a component by its path. If the component exists in the template's structure
   * but not under the page, it creates a synthetic resource with the path it would have under the
   * page and the same resource type.
   *
   * @param currentPage current AEM page.
   * @param resolver resource resolver for accessing resources.
   * @param componentPath relative path to the component from root of the page
   * @return The Resource of the component, either found or synthetically created to represent its
   *     intended path under the page.
   */
  public static Resource findComponentResource(
      Page currentPage, ResourceResolver resolver, String componentPath) {
    String componentPathInPage = currentPage.getPath() + "/jcr:content/" + componentPath;
    Resource componentResource = resolver.getResource(componentPathInPage);
    if (Objects.isNull(componentResource)) {
      Template template = currentPage.getTemplate();
      if (Objects.nonNull(template)) {
        String structurePath = template.getPath() + "/structure/jcr:content/" + componentPath;
        Resource structureResource = resolver.getResource(structurePath);
        if (Objects.nonNull(structureResource)) {
          componentResource =
              new SyntheticResource(
                  resolver, componentPathInPage, structureResource.getResourceType());
        }
      }
    }
    return componentResource;
  }

  /**
   * This method returns auto sections from web components that are under responsive grid.
   *
   * @param currentPage Current Page
   * @param resolver Resource Resolver
   * @param factory Section Response Model Adapter Factory
   * @return list of sections
   */
  public static List<SectionResponseModel> findResponsiveGridSections(
      Page currentPage, ResourceResolver resolver, SectionResponseModelAdapterFactory factory) {
    Resource responsiveGrid =
        SectionsBuilder.findComponentResource(currentPage, resolver, "root/responsivegrid");
    return CommonUtils.iteratorToStream(responsiveGrid.listChildren())
        .map(res -> factory.getAdapter(res, SectionResponseModel.class))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
