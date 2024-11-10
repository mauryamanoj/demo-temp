package com.saudi.tourism.core.models.mobile.components.autosections.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.common.BannerCard;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.CommonCFModel;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.about.v1.AboutSectionModel;
import com.saudi.tourism.core.models.components.alerts.AlertsModel;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetModel;
import com.saudi.tourism.core.models.components.events.eventscards.v1.EventsCardsModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import com.saudi.tourism.core.models.components.informationsection.v1.InformationSectionModel;
import com.saudi.tourism.core.models.components.mapwidget.v1.MapWidgetModel;
import com.saudi.tourism.core.models.components.mediagallery.v1.MediaGalleryModel;
import com.saudi.tourism.core.models.components.pagebanner.v1.PageBannerModel;
import com.saudi.tourism.core.models.components.promotional.PromotionalBannerModel;
import com.saudi.tourism.core.models.components.promotional.PromotionalSectionBannerModel;
import com.saudi.tourism.core.models.components.specialshowwidget.v1.SpecialShowWidgetModel;
import com.saudi.tourism.core.models.components.thingstodo.v1.ThingsToDoCardsModel;
import com.saudi.tourism.core.models.mobile.components.atoms.About;
import com.saudi.tourism.core.models.mobile.components.atoms.ToastWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.atoms.Filter;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.Expandable;
import com.saudi.tourism.core.models.mobile.components.atoms.EventWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.models.mobile.components.atoms.Rows;
import com.saudi.tourism.core.models.mobile.components.atoms.SideTitle;
import com.saudi.tourism.core.models.mobile.components.atoms.RowsGeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.GeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.atoms.CategoryFilterItem;
import com.saudi.tourism.core.models.mobile.components.atoms.PriceWidget;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.HeaderResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.FooterResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.GeneralInfosIcons;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.TagUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Optional;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Mobile Automatic Sections Mapper.
 */
public final class SectionsMapper {

  /**
   * Constant MIDNIGHT_TIME.
   */
  public static final String MIDNIGHT_TIME = "00:00:00";

  /**
   * Constant DEFAULT.
   */
  public static final String DEFAULT = "default";

  /**
   * Constant GREEN.
   */
  public static final String GREEN = "Green";

  /**
   * Constant SUCCESS.
   */
  public static final String SUCCESS = "success";

  /**
   * Constant RED.
   */
  public static final String RED = "Red";

  /**
   * Constant ERROR.
   */
  public static final String ERROR = "error";

  /**
   * Constant WARNING.
   */
  public static final String WARNING = "warning";

  /**
   * Constant YELLOW.
   */
  public static final String YELLOW = "Yellow";

  /**
   * CF master node.
   */
  private static final String CF_MASTER_NODE = "/jcr:content/data/master";


  /**
   * Sections Mapper.
   */
  private SectionsMapper() {

  }

  /**
   * This method map web about section into automatic about section for mobile.
   *
   * @param model
   * @param resolver
   * @param favoriteContentType
   * @param settingsService
   * @return automatic about section
   */
  public static SectionResponseModel mapAboutSectionToAutoSection(
      AboutSectionModel model,
      ResourceResolver resolver,
      SlingSettingsService settingsService,
      String favoriteContentType) {
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    About about = new About();
    about.setDescription(CommonUtils.stripHtml(model.getAboutDescription()));
    List<About.CategoriesAbout> categories = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(model.getCategoriesTags())) {
      categories =
          model.getCategoriesTags().stream()
              .filter(Objects::nonNull)
              .map(
                  c -> {
                    About.CategoriesAbout tag = new About.CategoriesAbout();
                    TagUtils.processTagWithoutNamespace(c);
                    tag.setId(c.getId());
                    tag.setTitle(c.getTitle());
                    tag.setIcon(
                        LinkUtils.getAuthorPublishAssetUrl(
                            resolver,
                            c.getIcon(),
                            settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                    return tag;
                  })
              .collect(Collectors.toList());
    }
    about.setCategoriesAbout(categories);
    item.setAbout(about);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(model.getAboutTitle());
    itemComponentStyle.setComponentUIType("ABOUT");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    section.setSectionHeader(sectionHeader);
    section.setItemComponentStyle(itemComponentStyle);
    section.setId("auto/about-section");
    section.setFilterType("");
    section.setFavoriteContentType(favoriteContentType);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method aims to map web alert widget into auto alert section for mobile.
   *
   * @param alerts alerts model
   * @param resolver resource resolver
   * @param settingsService sling settings service
   * @return auto/alert
   */
  public static SectionResponseModel mapAlertWidgetToAutoAlertSection(
      AlertsModel alerts, ResourceResolver resolver, SlingSettingsService settingsService) {
    if (StringUtils.isBlank(alerts.getAlert())) {
      return null;
    }

    var isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);

    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    String type;
    String icon;
    if (StringUtils.equalsIgnoreCase(alerts.getAlertColor(), YELLOW)) {
      type = WARNING;
      icon = MobileUtils.generateAlertIconUrl(resolver, "Information", isPublish);
    } else if (StringUtils.equalsIgnoreCase(alerts.getAlertColor(), RED)) {
      type = ERROR;
      icon = MobileUtils.generateAlertIconUrl(resolver, "Error", isPublish);
    } else if (StringUtils.equalsIgnoreCase(alerts.getAlertColor(), GREEN)) {
      type = SUCCESS;
      icon = MobileUtils.generateAlertIconUrl(resolver, "Check", isPublish);
    } else {
      type = DEFAULT;
      icon = MobileUtils.generateAlertIconUrl(resolver, "WarningOctagon", isPublish);
    }
    ToastWidget toastWidget =
        ToastWidget.builder()
            .type(type)
            .title(CommonUtils.stripHtml(alerts.getAlert()))
            .icon(icon)
            .build();
    item.setToastWidget(toastWidget);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("TOAST");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    section.setItemComponentStyle(itemComponentStyle);
    section.setId("auto/alert");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web event date widget into event widget for mobile.
   *
   * @param model event widget model
   * @return automatic event widget section
   */
  public static SectionResponseModel mapEventWidgetToAutoSection(EventDateWidgetModel model) {
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    EventWidget eventWidget = new EventWidget();
    List<Calendar> eventDates = new ArrayList<>();
    eventDates.add(model.getStartDate());
    eventDates.add(model.getEndDate());
    eventWidget.setDates(eventDates);
    eventWidget.setExpired(false);
    item.setEventWidget(eventWidget);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("EVENT");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    section.setId("auto/event-widget");
    section.setFilterType("");
    section.setItemComponentStyle(itemComponentStyle);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web media gallery into gallery section for mobile.
   *
   * @param model           AboutSectionModel
   * @param resolver        resource resolver
   * @param settingsService settings service
   * @param title           page title
   * @return automatic gallery section
   */
  public static SectionResponseModel mapMediaGalleryToAutoSection(
      MediaGalleryModel model,
      ResourceResolver resolver,
      SlingSettingsService settingsService,
      String title) {
    SectionResponseModel section = new SectionResponseModel();
    List<ItemResponseModel> items = new ArrayList<>();
    List<MediaGallery> gallerySection = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(model.getGallery())) {
      var firstImage =
          model.getGallery().stream()
              .filter(i -> Constants.PN_IMAGE.equalsIgnoreCase(i.getType()))
              .findFirst();

      firstImage.ifPresent(
          i -> {
            MediaGallery galleryItem = new MediaGallery();
            galleryItem.setType(i.getType().toUpperCase());
            galleryItem.setUrl(
                LinkUtils.getAuthorPublishAssetUrl(
                    resolver,
                    i.getImage().getFileReference(),
                    settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            gallerySection.add(galleryItem);
          });

      items =
          model.getGallery().stream()
              .filter(Objects::nonNull)
              .filter(i -> !firstImage.isPresent() || !firstImage.get().equals(i))
              .map(
                  i -> {
                    MediaGallery galleryItem = new MediaGallery();
                    galleryItem.setType(i.getType().toUpperCase());
                    if (Constants.PN_IMAGE.equalsIgnoreCase(i.getType())) {
                      galleryItem.setUrl(
                          LinkUtils.getAuthorPublishAssetUrl(
                              resolver,
                              i.getImage().getFileReference(),
                              settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                    } else {
                      galleryItem.setUrl(
                          LinkUtils.getAuthorPublishAssetUrl(
                              resolver,
                              i.getVideo().getVideoFileReference(),
                              settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                    }
                    Location location = new Location();
                    Destination destination = Destination.builder().title(i.getLocation()).build();
                    location.setDestination(destination);

                    ItemResponseModel item = new ItemResponseModel();
                    item.setMediaGallery(List.of(galleryItem));
                    item.setLocation(location);

                    return item;
                  })
              .collect(Collectors.toList());
    }

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(title);
    itemComponentStyle.setComponentUIType("BANNER_DETAILS_PAGE");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/gallery-section");
    section.setFilterType("");
    section.setItemComponentStyle(itemComponentStyle);
    section.setSectionHeader(sectionHeader);
    section.setMediaGallery(gallerySection);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItems(items);
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web special show widget into auto special show for mobile.
   *
   * @param model special show model
   * @return automatic special show
   */
  public static SectionResponseModel mapSpecialShowWidgetToAutoSection(
      SpecialShowWidgetModel model) {
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    Expandable expandable = new Expandable();
    if (Objects.nonNull(model.getCfModel())) {
      expandable.setTitle(model.getCfModel().getTitle());
      List<Rows> rows = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(model.getCfModel().getData())) {
        rows =
            model.getCfModel().getData().stream()
                .filter(Objects::nonNull)
                .map(
                    r -> {
                      Rows row = new Rows();
                      if (StringUtils.isNotBlank(r.getKey())) {
                        row.setKey(r.getKey());
                      }
                      if (StringUtils.isNotBlank(r.getValue())) {
                        row.setValue(r.getValue());
                      }
                      return row;
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
      }
      if (CollectionUtils.isEmpty(rows)) {
        return null;
      }
      expandable.setRows(rows);
    }
    item.setExpandable(expandable);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("EXPANDABLE_DATE");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    section.setId("auto/special-show");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web information list widget into mobile auto opening hours.
   *
   * @param model
   * @return auto opening hours
   */
  public static SectionResponseModel mapInformationListToOpeningHours(
      InformationListWidgetModel model) {

    if (Objects.isNull(model.getCfModel())) {
      return null;
    }
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    // Fill side title information.
    SideTitle sideTitle = new SideTitle();
    List<OpeningHoursValue> openingHoursValues = model.getCfModel().getOpeningHoursValue();
    if (showOpeningHoursStatus(model.getCfModel())) {
      if (isOpenNow(
          model.getCfModel().isSameTimeAcrossWeek(), openingHoursValues, model.getLanguage(),
          model.getCfModel().getStartDate(), model.getCfModel().getEndDate())) {
        sideTitle.setTitle(model.getCfModel().getOpenNowLabel());
        sideTitle.setColor("Green");
      } else {
        sideTitle.setTitle(model.getCfModel().getClosedNowLabel());
        sideTitle.setColor("Red");
      }
    }

    // Fill expandable with title, side title and rows, then add it to item.
    Expandable expandable = new Expandable();
    expandable.setTitle(model.getCfModel().getOpeningHoursLabel());
    expandable.setSideTitle(sideTitle);
    // Fill rows information.
    List<Rows> rows = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(openingHoursValues)) {
      rows =
          openingHoursValues.stream()
              .filter(Objects::nonNull)
              .map(
                  r -> {
                    Rows row = new Rows();
                    if (StringUtils.isNotBlank(r.getDayLabel())) {
                      row.setKey(MobileUtils.getFullDayName(r.getDayLabel(), model.getLanguage()));
                    }
                    if (StringUtils.isNotBlank(r.getStartTimeLabel())
                        && StringUtils.isNotBlank(r.getEndTimeLabel())) {
                      final var openingTime =
                          MobileUtils.formatTimeToHHMM(r.getStartTimeLabel(), model.getLanguage());
                      final var closingTime =
                          MobileUtils.formatTimeToHHMM(r.getEndTimeLabel(), model.getLanguage());
                      String oc;
                      if (model.getLanguage().equals(Constants.ARABIC_LOCALE)) {
                        oc = "\u200F" + openingTime + " - " + closingTime;
                      } else {
                        oc = openingTime + " - " + closingTime;
                      }
                      row.setValue(oc);
                    }
                    return row;
                  })
              .collect(Collectors.toList());
    }
    if (CollectionUtils.isEmpty(rows)) {
      return null;
    }
    expandable.setRows(rows);
    item.setExpandable(expandable);

    // Fill item component style.
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("EXPANDABLE_DATE");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    // Prepare section.
    section.setId("auto/opening-hours");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web promotional banner into mobile auto/banner.
   *
   * @param model
   * @param resolver
   * @param settingsService
   * @return auto/pagebanner
   */
  public static SectionResponseModel mapPageBannerToAutoSection(
      PageBannerModel model, ResourceResolver resolver, SlingSettingsService settingsService) {
    SectionResponseModel section = new SectionResponseModel();
    MediaGallery assetGallery = new MediaGallery();
    if (CollectionUtils.isNotEmpty(model.getCards())) {
      Optional<BannerCard> firstCard =
          model.getCards().stream().filter(Objects::nonNull).findFirst();
      firstCard.ifPresent(
          card -> {
            var sectionHeader = new SectionResponseModel.SectionHeader();
            sectionHeader.setTitle(card.getTitle());
            sectionHeader.setSubtitle(card.getDescription());
            section.setSectionHeader(sectionHeader);
            // if video is set, its override the image
            if (Objects.nonNull(card.getVideo())) {
              assetGallery.setType("VIDEO");
              assetGallery.setUrl(
                  LinkUtils.getAuthorPublishAssetUrl(
                      resolver,
                      card.getVideo().getVideoFileReference(),
                      settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            } else if (Objects.nonNull(card.getImage())) {
              assetGallery.setType("IMAGE");
              assetGallery.setUrl(
                  LinkUtils.getAuthorPublishAssetUrl(
                      resolver,
                      card.getImage().getFileReference(),
                      settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            }
            section.setMediaGallery(List.of(assetGallery));

          });
    }


    var mediaGalleryThumbsItem = Optional.ofNullable(model.getThumbs())
        .map(thumbs -> Optional.ofNullable(thumbs.getGallery())
            .orElse(Collections.emptyList()))
        .stream()
        .flatMap(List::stream)
        .map(asset -> {
          MediaGallery newasset = new MediaGallery();

          if ("image".equalsIgnoreCase(asset.getType())) {
            newasset.setType("IMAGE");

            newasset.setUrl(
                LinkUtils.getAuthorPublishAssetUrl(
                    resolver,
                    asset.getImage().getFileReference(),
                    settingsService.getRunModes().contains(Externalizer.PUBLISH)));
          } else if ("video".equalsIgnoreCase(asset.getType())) {
            newasset.setType("video");
            newasset.setUrl(
                LinkUtils.getAuthorPublishAssetUrl(
                    resolver,
                    asset.getVideo().getVideoFileReference(),
                    settingsService.getRunModes().contains(Externalizer.PUBLISH)));
          } else {
            return null;
          }


          var itemGallery = new ItemResponseModel();
          itemGallery.setMediaGallery(List.of(newasset));
          if (StringUtils.isNotBlank(asset.getLocation())) {
            Location location = new Location();
            location.setTitle(asset.getLocation());
            itemGallery.setLocation(location);
          }
          return itemGallery;
        }).filter(Objects::nonNull).collect(Collectors.toList());


    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("BANNER_DETAILS_PAGE");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/pagebanner");
    section.setFilterType("");
    section.setFavoriteContentType("stories");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(mediaGalleryThumbsItem);
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map information list widget into mobile auto general information section.
   *
   * @param model           model
   * @param resolver        resolver
   * @param settingsService settingsService
   * @return automatic general information
   */
  public static SectionResponseModel mapInformationListToGeneralInfo(
      InformationListWidgetModel model, ResourceResolver resolver,
      SlingSettingsService settingsService) {

    if (Objects.isNull(model.getCfModel())) {
      return null;
    }
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    List<RowsGeneralInformationWidget> rows = new ArrayList<>();
    var cfModel = model.getCfModel();

    var isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);

    if (StringUtils.isNotBlank(cfModel.getAgesLabel()) && StringUtils.isNotBlank(cfModel.getAgesValue())) {
      RowsGeneralInformationWidget age =
          new RowsGeneralInformationWidget(cfModel.getAgesLabel(), cfModel.getAgesValue(), "text",
              MobileUtils.generateIconUrlGeneralInformation(resolver, GeneralInfosIcons.AGE.getCode(), isPublish));
      rows.add(age);
    }

    if (StringUtils.isNotBlank(cfModel.getAccessibilityLabel())
        && CollectionUtils.isNotEmpty(cfModel.getAccessibilityValue())) {
      RowsGeneralInformationWidget accessibility =
          new RowsGeneralInformationWidget(
              cfModel.getAccessibilityLabel(), String.join(", ", cfModel.getAccessibilityValue()), "text",
              MobileUtils.generateIconUrlGeneralInformation(resolver, GeneralInfosIcons.ACCESSIBILITY.getCode(),
                  isPublish));
      rows.add(accessibility);
    }

    if (StringUtils.isNotBlank(cfModel.getLocationLabel()) && StringUtils.isNotBlank(cfModel.getLocationValue())) {
      RowsGeneralInformationWidget location =
          new RowsGeneralInformationWidget(
              cfModel.getLocationLabel(), cfModel.getLocationValue(), "text",
              MobileUtils.generateIconUrlGeneralInformation(resolver, GeneralInfosIcons.LOCATION.getCode(), isPublish));
      rows.add(location);
    }

    if (StringUtils.isNotBlank(cfModel.getTypeLabel()) && StringUtils.isNotBlank(cfModel.getTypeValue())) {
      RowsGeneralInformationWidget type =
          new RowsGeneralInformationWidget(cfModel.getTypeLabel(), cfModel.getTypeValue(), "text",
              MobileUtils.generateIconUrlGeneralInformation(resolver, "group", isPublish));
      rows.add(type);
    }

    if (StringUtils.isNotBlank(cfModel.getDurationLabel()) && StringUtils.isNotBlank(cfModel.getDurationValue())) {
      RowsGeneralInformationWidget duration =
          new RowsGeneralInformationWidget(cfModel.getDurationLabel(), cfModel.getDurationValue(), "text",
              MobileUtils.generateIconUrlGeneralInformation(resolver, GeneralInfosIcons.DURATION.getCode(), isPublish));
      rows.add(duration);
    }

    if (CollectionUtils.isEmpty(rows)) {
      return null;
    }

    GeneralInformationWidget generalInfo = new GeneralInformationWidget();
    generalInfo.setTitle(cfModel.getInformationLabel());
    generalInfo.setExpandable(false);
    generalInfo.setRows(rows);
    item.setGeneralInformationWidget(generalInfo);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("GENERAL_INFORMATION");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");

    section.setId("auto/general-info");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web map widget into mobile automatic section map.
   *
   * @param model model
   * @return automatic map
   */
  public static SectionResponseModel mapMapWidgetToAutoSection(MapWidgetModel model) {
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    Location location = new Location();
    location.setTitle(model.getLocationValue());
    location.setLat(model.getLatitude());
    location.setLng(model.getLongitude());
    item.setLocation(location);

    CustomAction customAction = new CustomAction();
    customAction.setTitle(model.getCtaLabel());
    customAction.setShow(true);
    customAction.setEnable(true);
    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
    item.setCustomAction(customAction);

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("MAP");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/map");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web information section into auto/before-you-go.
   *
   * @param model
   * @return auto/before-you-go
   */
  public static SectionResponseModel mapInformationSectionToAutoSection(
      InformationSectionModel model) {
    SectionResponseModel section = new SectionResponseModel();
    List<ItemResponseModel> items = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(model.getData())) {
      items =
          model.getData().stream()
              .filter(Objects::nonNull)
              .map(
                  faq -> {
                    Expandable expandable = new Expandable();
                    expandable.setTitle(faq.getQuestion());
                    expandable.setText(CommonUtils.stripHtml(faq.getAnswer()));

                    ItemResponseModel item = new ItemResponseModel();
                    item.setExpandable(expandable);
                    return item;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(model.getTitle());
    itemComponentStyle.setComponentUIType("EXPANDABLE_TEXT");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");
    section.setId("auto/before-you-go");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setSectionHeader(sectionHeader);
    section.setItems(items);
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method map web promotional banner into mobile auto/banner.
   *
   * @param model
   * @param resolver
   * @param settingsService
   * @return auto/banner
   */
  public static SectionResponseModel mapPromotionalBannerToAutoSection(
      PromotionalBannerModel model,
      ResourceResolver resolver,
      SlingSettingsService settingsService) {
    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    Titles titles = new Titles();
    MediaGallery assetGallery = new MediaGallery();
    CustomAction customAction = new CustomAction();
    if (CollectionUtils.isNotEmpty(model.getCards())) {
      Optional<PromotionalSectionBannerModel> firstCard =
          model.getCards().stream().filter(Objects::nonNull).findFirst();
      firstCard.ifPresent(
          card -> {
            titles.setTitle(card.getTitle());
            // if video is set, its override the image
            if (Objects.nonNull(card.getVideo())) {
              assetGallery.setType("VIDEO");
              assetGallery.setUrl(
                  LinkUtils.getAuthorPublishAssetUrl(
                      resolver,
                      card.getVideo().getVideoFileReference(),
                      settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            } else if (Objects.nonNull(card.getImage())) {
              assetGallery.setType("IMAGE");
              assetGallery.setUrl(
                  LinkUtils.getAuthorPublishAssetUrl(
                      resolver,
                      card.getImage().getFileReference(),
                      settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            }

            // Fill cta.
            CategoryFilterItem filterItem = CategoryFilterItem.builder().id("destinations").build();
            Filter filter = new Filter();
            filter.setCategories(List.of(filterItem));
            Cta cta = new Cta("DEEPLINK_SCREEN", "discover", filter);
            // Fill custom action.
            customAction.setTitle(card.getLink().getCopy());
            customAction.setShow(true);
            customAction.setEnable(true);
            customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
            customAction.setCta(cta);
            item.setMediaGallery(List.of(assetGallery));
            item.setTitles(titles);
            item.setCustomAction(customAction);
          });
    }

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("OFFER");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/banner");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method compare current day time with OpeningHoursValue day and timing to check if opened
   * or closed now.
   *
   * @param openingHoursValues   openingHoursValues
   * @param language             language
   * @param isSameTimeAcrossWeek isSameTimeAcrossWeek
   * @param startDate startDate
   * @param endDate endDate
   * @return isOpeningHours.
   */
  public static Boolean isOpenNow(
      Boolean isSameTimeAcrossWeek, List<OpeningHoursValue> openingHoursValues, String language, String startDate,
      String endDate) {
    if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
      String currentSaudiDate = CommonUtils.dateToString(LocalDate.now(), Constants.FORMAT_DATE);
      if (currentSaudiDate.compareTo(endDate) > 0 ||  currentSaudiDate.compareTo(startDate) < 0) {
        return false;
      }

    }

    if (CollectionUtils.isNotEmpty(openingHoursValues)) {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.FORMAT_HOUR_MINUTE);
      DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();




      String currentDay = todayDayOfWeek.getDisplayName(TextStyle.SHORT, new Locale(language));

      DayOfWeek yesterdayDayOfWeek = todayDayOfWeek.minus(1);
      String yesterdayDay = yesterdayDayOfWeek.getDisplayName(TextStyle.SHORT, new Locale(language));

      String currentTime = ZonedDateTime.now(ZoneId.of("Asia/Riyadh")).format(dateTimeFormatter);

      if (isSameTimeAcrossWeek) {
        if (checkOpeningHoursForDay(openingHoursValues.get(0), currentTime)) {
          return true;
        }
      }

      for (OpeningHoursValue openingValue : openingHoursValues) {
        if (currentDay.equalsIgnoreCase(openingValue.getDayLabel())) {
          if (checkOpeningHoursForDay(openingValue, currentTime)) {
            return true;
          }
        }
      }

      // Check yesterday's closing time if it spans after midnight
      for (OpeningHoursValue openingValue : openingHoursValues) {
        if (yesterdayDay.equalsIgnoreCase(openingValue.getDayLabel())) {
          if (checkYesterdayOpeningHours(openingValue, currentTime)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static Boolean checkOpeningHoursForDay(OpeningHoursValue openingValue, String currentTime) {
    String startTime = openingValue.getStartTimeLabel();
    String endTime = openingValue.getEndTimeLabel();
    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
      if (startTime.compareTo(endTime) >= 0) {
        return currentTime.compareTo(startTime) >= 0;
      } else {
        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0;
      }
    }
    return false;
  }

  private static Boolean checkYesterdayOpeningHours(OpeningHoursValue openingValue, String currentTime) {
    String startTime = openingValue.getStartTimeLabel();
    String endTime = openingValue.getEndTimeLabel();
    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.compareTo(endTime) > 0) {
      return currentTime.compareTo(MIDNIGHT_TIME) >= 0 && currentTime.compareTo(endTime) < 0;
    }
    return false;
  }


  /**
   * This method check if the status (Open Now, Closed Now) should be displayed or not.
   *
   * @param cfModel Information List Widget CF Model
   * @return isOpeningHours
   */
  public static Boolean showOpeningHoursStatus(InformationListWidgetCFModel cfModel) {
    if (CollectionUtils.isEmpty(cfModel.getOpeningHoursValue())) {
      return false;
    }

    String currentSaudiDate = CommonUtils.dateToString(LocalDate.now(), Constants.FORMAT_DATE);
    if (StringUtils.isNotBlank(cfModel.getEndDate())
        && currentSaudiDate.compareTo(cfModel.getEndDate()) > 0) {
      return false;
    }

    if (StringUtils.isNotBlank(cfModel.getStartDate())
        && currentSaudiDate.compareTo(cfModel.getStartDate()) < 0) {
      return false;
    }

    return true;
  }

  /**
   * This method map web events cards into automatic you may also like section.
   *
   * @param model           cards model
   * @param events          events
   * @param resolver        resolver
   * @param settingsService settingsService
   * @return auto/you-may-also-like
   */
  public static SectionResponseModel mapEventsCardsToAutoMayAlsoLike(
      EventsCardsModel model,
      FetchEventsResponse events,
      ResourceResolver resolver,
      SlingSettingsService settingsService) {
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
                                    .collect(Collectors.toList()))
                            .build();
                    Filter filter = new Filter();
                    filter.setCategories(List.of(filterItem));
                    Cta cta = new Cta();
                    // Fill custom action.
                    CustomAction customAction = new CustomAction();
                    if (Objects.nonNull(event.getTicketsCtaLink())) {
                      cta.setType("WEB");
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
              .collect(Collectors.toList());
    }
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(model.getTitle());
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("MAIN");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/you-may-also-like");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setSectionHeader(sectionHeader);
    section.setItems(items);
    section.setTotalCount(section.getItems().size());

    return section;
  }

  /**
   * This method builds the item header.
   *
   * @param cfModel         eventCFModel
   * @param resolver        resource resolver
   * @param settingsService settings service
   * @return header
   */
  public static HeaderResponseModel mapHeaderFromContentFragment(
      CommonCFModel cfModel, ResourceResolver resolver, SlingSettingsService settingsService) {
    HeaderResponseModel.Titles titles = new HeaderResponseModel.Titles();
    HeaderResponseModel.SideActions sideActions = new HeaderResponseModel.SideActions();
    if (Objects.nonNull(cfModel)) {
      titles.setTitle(cfModel.getTitle());
      titles.setSubTitle(cfModel.getSubtitle());
      sideActions = getSideActions(cfModel.getDetailsPage(), resolver, settingsService);
    }

    HeaderResponseModel.HeaderComponentStyle headerComponentStyle =
        new HeaderResponseModel.HeaderComponentStyle();
    headerComponentStyle.setComponentUIType("HEADER_BANNER");

    HeaderResponseModel.Steps steps = new HeaderResponseModel.Steps();
    steps.setShow(false);

    HeaderResponseModel header = new HeaderResponseModel();
    header.setSteps(steps);
    header.setTitles(titles);
    header.setShowMapButton(false);
    header.setShowSearchBar(false);
    header.setSideActions(sideActions);
    header.setHeaderComponentStyle(headerComponentStyle);

    return header;
  }

  /**
   * This method builds sideActions for item header.
   *
   * @param detailsPagePath web page path
   * @param resolver        resource resolver
   * @param settingsService settings service
   * @return sideActions
   */
  private static HeaderResponseModel.SideActions getSideActions(
      String detailsPagePath, ResourceResolver resolver, SlingSettingsService settingsService) {
    HeaderResponseModel.CustomAction.CTA cta = new HeaderResponseModel.CustomAction.CTA();
    cta.setType("WEB");
    var buttonStyle = new HeaderResponseModel.CustomAction.ButtonComponentStyle();
    buttonStyle.setComponentUIType("PRIMARY");
    HeaderResponseModel.CustomAction customAction = new HeaderResponseModel.CustomAction();
    customAction.setShow(false);
    customAction.setEnable(false);
    customAction.setButtonComponentStyle(buttonStyle);
    customAction.setCta(cta);

    HeaderResponseModel.SideActions sideActions = new HeaderResponseModel.SideActions();
    sideActions.setShowFilter(false);
    sideActions.setShowMap(false);
    sideActions.setShowShare(true);
    sideActions.setShareUrl(
        LinkUtils.getAuthorPublishUrl(
            resolver,
            detailsPagePath,
            settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    sideActions.setShowFavorite(false);
    sideActions.setCustomAction(customAction);

    return sideActions;
  }

  /**
   * This method builds the item footer.
   *
   * @param cfModel eventCFModel
   * @return footer
   */
  public static FooterResponseModel mapFooterFromContentFragment(CommonCFModel cfModel) {
    var footerComponentStyle = new FooterResponseModel.FooterComponentStyle();
    footerComponentStyle.setComponentUIType("PRICE");

    FooterResponseModel footer = new FooterResponseModel();
    footer.setFooterComponentStyle(footerComponentStyle);

    if (Objects.isNull(cfModel)) {
      return footer;
    }

    if (getPriceWidget(cfModel) != null) {
      footer.setPriceWidget(getPriceWidget(cfModel));
    }

    return footer;
  }

  /**
   * This method fills the price widget objects from cfModel.
   *
   * @param cfModel cfModel
   * @return priceWidget
   */
  private static PriceWidget getPriceWidget(CommonCFModel cfModel) {
    String ticketCTALabel = cfModel.getTicketCTALabel();
    String ticketType = "FREE_TICKETS";
    if (StringUtils.isBlank(cfModel.getTicketType())) {
      return null;
    }
    if (StringUtils.equals(cfModel.getTicketType(), "bookTicket")) {
      ticketType = "PRICE_TICKET";
    }
    if (StringUtils.equals(cfModel.getTicketType(), "noTicket")) {
      ticketType = "NO_TICKETS_NEEDED";
    }

    Cta cta = new Cta();
    cta.setType("WEB");
    cta.setUrl(cfModel.getTicketCTALink());

    CustomAction customAction = new CustomAction();
    customAction.setTitle(ticketCTALabel);
    customAction.setShow(true);
    customAction.setEnable(true);
    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
    customAction.setCta(cta);

    PriceWidget.Price price = new PriceWidget.Price();

    price.setPrice(cfModel.getTicketPrice());

    price.setTier(cfModel.getTicketPriceSuffix());

    PriceWidget priceWidget = new PriceWidget();
    priceWidget.setType(ticketType);
    priceWidget.setDescription(CommonUtils.stripHtml(cfModel.getTicketDetails()));
    priceWidget.setPrice(price);
    priceWidget.setCustomAction(customAction);

    return priceWidget;
  }

  /**
   * This method fill a common cf model from tour, event, activities and attraction CFs.
   *
   * @param currentPage     currentPage
   * @param resolver        resolver
   * @param settingsService settings service
   * @return eventCF
   */
  public static CommonCFModel loadCommonCF(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    String cfPath =
        currentPage.getProperties().get(Constants.REFERENCED_FRAGMENT_REFERENCE, String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return null;
    }

    var cfResource = resolver.getResource(cfPath + CF_MASTER_NODE);
    if (cfResource == null) {
      return null;
    }

    var title = cfResource.getValueMap().get("title", String.class);
    var subtitle = cfResource.getValueMap().get("subtitle", String.class);
    var ticketType = cfResource.getValueMap().get("ticketType", String.class);
    var ticketPrice = cfResource.getValueMap().get("ticketPrice", String.class);
    var ticketPriceSuffix = cfResource.getValueMap().get("ticketPriceSuffix", String.class);
    var ticketDetails = cfResource.getValueMap().get("ticketDetails", String.class);
    var ticketCTALink = cfResource.getValueMap().get("ticketCTALink", String.class);
    var detailsPage = cfResource.getValueMap().get("pagePath", String.class);
    var ticketCTALabel = cfResource.getValueMap().get("ticketCTALabel", String.class);

    ticketCTALink =
        LinkUtils.getAuthorPublishUrl(
            resolver, ticketCTALink, settingsService.getRunModes().contains(Externalizer.PUBLISH));

    return CommonCFModel.builder()
        .title(title)
        .subtitle(subtitle)
        .ticketType(ticketType)
        .ticketPrice(ticketPrice)
        .ticketPriceSuffix(ticketPriceSuffix)
        .ticketDetails(ticketDetails)
        .ticketCTALink(ticketCTALink)
        .ticketCTALabel(ticketCTALabel)
        .detailsPage(detailsPage)
        .build();
  }

  public static SectionResponseModel mapThingsToDoCardsToAutoMayAlsoLike(
      ThingsToDoCardsModel cardsModel,
      FetchThingsToDoResponse thingsToDo,
      ResourceResolver resolver,
      SlingSettingsService settingsService) {
    SectionResponseModel section = new SectionResponseModel();
    List<ItemResponseModel> items = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(thingsToDo.getData())) {
      items =
          thingsToDo.getData().stream()
              .filter(Objects::nonNull)
              .map(
                  thingToDo -> {
                    // Fill titles.
                    Titles titles = new Titles();
                    titles.setTitle(thingToDo.getTitle());
                    // Fill media gallery.
                    MediaGallery gallery = new MediaGallery();
                    Optional<Image> firstImage =
                        thingToDo.getBannerImages().stream().filter(Objects::nonNull).findFirst();
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
                                thingsToDo.getData().stream()
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
                                    .collect(Collectors.toList()))
                            .build();
                    Filter filter = new Filter();
                    filter.setCategories(List.of(filterItem));
                    Cta cta = new Cta("DEEPLINK_SCREEN", "discover", filter);
                    // Fill custom action.
                    CustomAction customAction = new CustomAction();
                    if (Objects.nonNull(thingToDo.getTicketsCtaLink())) {
                      customAction.setTitle(thingToDo.getTicketsCtaLink().getText());
                    }
                    customAction.setShow(true);
                    customAction.setEnable(true);
                    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
                    customAction.setCta(cta);
                    // Fill categories.
                    List<Category> categories = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(thingToDo.getCategories())) {
                      categories =
                          thingToDo.getCategories().stream()
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
                    location.setLat(thingToDo.getLat());
                    location.setLng(thingToDo.getLng());
                    if (Objects.nonNull(thingToDo.getDestination())) {
                      Destination destination =
                          Destination.builder()
                              .id(thingToDo.getDestination().getTitle().toLowerCase())
                              .title(StringUtils.upperCase(thingToDo.getDestination().getTitle()))
                              .build();
                      location.setDestination(destination);
                    }

                    ItemResponseModel item = new ItemResponseModel();
                    if (StringUtils.isNotBlank(thingToDo.getPagePath())) {
                      item.setId(thingToDo.getPagePath());
                    }
                    item.setType(Constants.TYPE_AUTO);
                    item.setTitles(titles);
                    item.setMediaGallery(List.of(gallery));
                    item.setCustomAction(customAction);
                    item.setCategories(categories);
                    item.setLocation(location);
                    return item;
                  })
              .collect(Collectors.toList());
    }
    var sectionHeader = new SectionResponseModel.SectionHeader();
    sectionHeader.setTitle(cardsModel.getTitle());
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("MAIN");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/you-may-also-like");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setSectionHeader(sectionHeader);
    section.setItems(items);
    section.setTotalCount(section.getItems().size());

    return section;
  }
}
