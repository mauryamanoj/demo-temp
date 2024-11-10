package com.saudi.tourism.core.models.mobile.components.autosections.utils;

import com.saudi.tourism.core.beans.bestexperience.ExperienceData;
import com.saudi.tourism.core.models.mobile.components.atoms.About;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.RowsGeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.GeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.PriceWidget;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.HeaderResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.FooterResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsDetailsResponseModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;

import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Mobile Experience Data Util.
 */
public final class ExperienceUtils {

  /**
   * Experience Utils.
   */
  private ExperienceUtils() {

  }

  /**
   * This method converts HY experience data to item details response model.
   *
   * @param experience experience data from halaY API.
   * @param i18nProvider i18n provider.
   * @param language language.
   * @param items items
   * @return item details.
   */
  public static ItemsDetailsResponseModel convertHalaYExperienceDataToItemDetails(
      @NonNull ExperienceData experience,
      ResourceBundleProvider i18nProvider,
      @NonNull String language,
      List<ItemResponseModel> items) {
    ItemsDetailsResponseModel item = new ItemsDetailsResponseModel();

    item.setId(experience.getId());
    item.setIsDmc(experience.getIsDmc());
    item.setPackageUrl(experience.getPackageUrl());
    item.setType("experience");
    item.setHeader(createHeaderFromExperience(experience));
    item.setFooter(createFooterFromExperience(experience, i18nProvider, language));

    List<SectionResponseModel> sections = new ArrayList<>();
    sections.add(createGallerySectionFromExperience(experience));
    sections.add(createAboutSectionFromExperience(experience, i18nProvider, language));
    sections.add(createGeneralInfoIncludedFromExperience(experience, i18nProvider, language));
    sections.add(createGeneralInfoExcludedFromExperience(experience, i18nProvider, language));
    sections.add(createGeneralInfoSuitableForFromExperience(experience, i18nProvider, language));
    sections.add(createGeneralInfoRefundFromExperience(experience, i18nProvider, language));
    sections.add(createAutoMapSectionFromExperience(experience, i18nProvider, language));
    sections.add(createMayAlsoLikeSectionFromExperience(experience, i18nProvider, language, items));

    item.setSections(sections.stream().filter(Objects::nonNull).collect(Collectors.toList()));

    return item;
  }

  private static SectionResponseModel createMayAlsoLikeSectionFromExperience(
      ExperienceData experience,
      ResourceBundleProvider i18nProvider,
      String language,
      List<ItemResponseModel> items) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/you-may-also-like");
    section.setFilterType(Constants.EXPERIENCE);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("MAIN")
            .componentScrollDirection("HORIZONTAL")
            .build());
    section.setSectionHeader(
        SectionResponseModel.SectionHeader.builder()
            .title(CommonUtils.getI18nString(i18n, "You may also like"))
            .showViewAll(false)
            .build());

    if (CollectionUtils.isNotEmpty(items)) {
      section.setItems(items);
      section.setTotalCount(section.getItems().size());
    } else {
      return null;
    }

    return section;
  }

  private static SectionResponseModel createAutoMapSectionFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();
    String lat = null;
    String lng = null;

    section.setId("auto/map");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("MAP")
            .componentScrollDirection("HORIZONTAL")
            .build());
    if (StringUtils.isNotBlank(experience.getLatlng())) {
      var latlng = experience.getLatlng().split(Constants.COMMA);
      lat = latlng[0].trim();
      lng = latlng[1].trim();
    }
    section.setItems(
        Collections.singletonList(
            ItemResponseModel.builder()
                .customAction(
                    CustomAction.builder()
                        .title(CommonUtils.getI18nString(i18n, "getDirections"))
                        .show(true)
                        .enable(true)
                        .buttonComponentStyle(
                            ButtonComponentStyle.builder().componentUIType("PRIMARY").build())
                        .build())
                .location(Location.builder().lat(lat).lng(lng).build())
                .build()));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createGeneralInfoRefundFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/general-info/refund");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("GENERAL_INFORMATION")
            .componentScrollDirection("VERTICAL")
            .build());
    if (StringUtils.isNotBlank(experience.getRefundPolicy())) {
      ItemResponseModel item = new ItemResponseModel();
      RowsGeneralInformationWidget row = new RowsGeneralInformationWidget();
      row.setTitle(experience.getRefundPolicy());
      if (Objects.nonNull(experience.getRefundPolicyDetails())) {
        if (language.equals(Constants.ARABIC_LOCALE)) {
          row.setDescription(experience.getRefundPolicyDetails().get(Constants.ARABIC_LOCALE));
        } else {
          row.setDescription(experience.getRefundPolicyDetails().get(Constants.DEFAULT_LOCALE));
        }
      }
      item.setGeneralInformationWidget(
          GeneralInformationWidget.builder()
              .rows(Collections.singletonList(row))
              .title(CommonUtils.getI18nString(i18n, "Refund Policy"))
              .expandable(false)
              .build());
      section.setItems(Collections.singletonList(item));
    } else {
      return null;
    }
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createGeneralInfoSuitableForFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/general-info/suitableFor");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("GENERAL_INFORMATION")
            .componentScrollDirection("VERTICAL")
            .build());
    if (StringUtils.isNotBlank(experience.getAgeFrom())
        && StringUtils.isNotBlank(experience.getAgeTo())) {
      ItemResponseModel item = new ItemResponseModel();
      item.setGeneralInformationWidget(
          GeneralInformationWidget.builder()
              .rows(
                  Collections.singletonList(
                      RowsGeneralInformationWidget.builder()
                          .title(
                              experience.getAgeFrom()
                                  + " - "
                                  + experience.getAgeTo()
                                  + " "
                                  + CommonUtils.getI18nString(i18n, "years old"))
                          .build()))
              .title(CommonUtils.getI18nString(i18n, "Suitable For"))
              .expandable(false)
              .build());
      section.setItems(Collections.singletonList(item));
    } else {
      return null;
    }
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createGeneralInfoExcludedFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/general-info/excluded");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("GENERAL_INFORMATION")
            .componentScrollDirection("VERTICAL")
            .build());
    if (CollectionUtils.isNotEmpty(experience.getExcluded())) {
      ItemResponseModel item = new ItemResponseModel();
      item.setGeneralInformationWidget(
          GeneralInformationWidget.builder()
              .rows(
                  experience.getExcluded().stream()
                      .filter(Objects::nonNull)
                      .map(
                          i -> {
                            RowsGeneralInformationWidget row = new RowsGeneralInformationWidget();
                            if (language.equals(Constants.ARABIC_LOCALE)) {
                              row.setTitle(i.get(Constants.ARABIC_LOCALE));
                            } else {
                              row.setTitle(i.get(Constants.DEFAULT_LOCALE));
                            }
                            return row;
                          })
                      .collect(Collectors.toList()))
              .title(CommonUtils.getI18nString(i18n, "What's not Included"))
              .expandable(false)
              .build());
      section.setItems(Collections.singletonList(item));
    } else {
      return null;
    }
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createGeneralInfoIncludedFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/general-info/included");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("GENERAL_INFORMATION")
            .componentScrollDirection("VERTICAL")
            .build());
    if (CollectionUtils.isNotEmpty(experience.getIncluded())) {
      ItemResponseModel item = new ItemResponseModel();
      item.setGeneralInformationWidget(
          GeneralInformationWidget.builder()
              .rows(
                  experience.getIncluded().stream()
                      .filter(Objects::nonNull)
                      .map(
                          i -> {
                            RowsGeneralInformationWidget row = new RowsGeneralInformationWidget();
                            if (language.equals(Constants.ARABIC_LOCALE)) {
                              row.setTitle(i.get(Constants.ARABIC_LOCALE));
                            } else {
                              row.setTitle(i.get(Constants.DEFAULT_LOCALE));
                            }
                            return row;
                          })
                      .collect(Collectors.toList()))
              .title(CommonUtils.getI18nString(i18n, "included"))
              .expandable(false)
              .build());
      section.setItems(Collections.singletonList(item));
    } else {
      return null;
    }
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createAboutSectionFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, @NonNull String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/about-section");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("ABOUT")
            .componentScrollDirection("VERTICAL")
            .build());
    section.setSectionHeader(
        SectionResponseModel.SectionHeader.builder()
            .title(CommonUtils.getI18nString(i18n, "about"))
            .build());

    section.setItems(
        Collections.singletonList(
            ItemResponseModel.builder()
                .about(
                    About.builder()
                        .description(experience.getDescription())
                        .categoriesAbout(Collections.emptyList())
                        .build())
                .build()));
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static SectionResponseModel createGallerySectionFromExperience(ExperienceData experience) {
    SectionResponseModel section = new SectionResponseModel();

    section.setId("auto/gallery-section");
    section.setFilterType(StringUtils.EMPTY);
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(
        SectionResponseModel.ItemComponentStyle.builder()
            .componentUIType("BANNER_DETAILS_PAGE")
            .componentScrollDirection("HORIZONTAL")
            .build());
    if (CollectionUtils.isNotEmpty(experience.getImages())) {
      experience.getImages().stream()
          .filter(StringUtils::isNotBlank)
          .findFirst()
          .ifPresent(
              image -> {
                section.setMediaGallery(
                    Collections.singletonList(
                        MediaGallery.builder().url(image).type("IMAGE").build()));
              });
    }
    section.setItems(Collections.emptyList());
    section.setSectionHeader(
        SectionResponseModel.SectionHeader.builder().title(experience.getName()).build());
    section.setTotalCount(section.getItems().size());

    return section;
  }

  private static FooterResponseModel createFooterFromExperience(
      ExperienceData experience, ResourceBundleProvider i18nProvider, @NonNull String language) {
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    FooterResponseModel footer = new FooterResponseModel();
    String price = null;
    if (language.equals(Constants.ARABIC_LOCALE)) {
      // The \u200F is used to start writing the price with currency from RTL to handle arabic case.
      price = "\u200F" + experience.getFinalPrice() + " " + CommonUtils.getI18nString(i18n, Constants.SAR);
    } else {
      price = experience.getFinalPrice() + " " + CommonUtils.getI18nString(i18n, Constants.SAR);
    }
    footer.setPriceWidget(
        PriceWidget.builder()
            .type("PRICE_TICKETS")
            .price(
                PriceWidget.Price.builder()
                    .price(price)
                    .tier(experience.getPriceTier())
                    .currency(StringUtils.EMPTY)
                    .build())
            .customAction(
                CustomAction.builder()
                    .title(CommonUtils.getI18nString(i18n, "Book Now"))
                    .show(true)
                    .enable(true)
                    .buttonComponentStyle(
                        ButtonComponentStyle.builder().componentUIType("PRIMARY").build())
                    .cta(Cta.builder().type("DEEPLINK_SCREEN").url("EXPERIENCE").build())
                    .build())
            .build());
    footer.setFooterComponentStyle(
        FooterResponseModel.FooterComponentStyle.builder().componentUIType("PRICE").build());

    return footer;
  }

  private static HeaderResponseModel createHeaderFromExperience(ExperienceData experience) {
    HeaderResponseModel header = new HeaderResponseModel();
    header.setTitles(HeaderResponseModel.Titles.builder().title(experience.getName()).build());
    header.setShowMapButton(false);
    header.setShowSearchBar(false);
    header.setHeaderComponentStyle(
        HeaderResponseModel.HeaderComponentStyle.builder()
            .componentUIType("HEADER_BANNER")
            .build());
    header.setSteps(HeaderResponseModel.Steps.builder().show(false).build());

    HeaderResponseModel.SideActions sideActions =
        HeaderResponseModel.SideActions.builder()
            .showFilter(false)
            .showShare(true)
            .shareUrl(experience.getPackageUrl())
            .showMap(false)
            .showFavorite(false)
            .customAction(
                HeaderResponseModel.CustomAction.builder()
                    .show(false)
                    .enable(false)
                    .buttonComponentStyle(
                        HeaderResponseModel.CustomAction.ButtonComponentStyle.builder()
                            .componentUIType("PRIMARY")
                            .build())
                    .cta(HeaderResponseModel.CustomAction.CTA.builder().type("WEB").build())
                    .build())
            .build();
    header.setSideActions(sideActions);
    return header;
  }
}
