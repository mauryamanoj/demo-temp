package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

@Component(service = InformationListWidgetCFAdapter.class, immediate = true)
public class InformationListWidgetAttractionCFAdapter implements InformationListWidgetCFAdapter,
    ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("attraction", cf.getTemplate().getTitle());
  }

  @Override
  public InformationListWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }

    var idealForLabel = getElementValue(cf, "idealForLabel", String.class);
    var agesLabel = getElementValue(cf, "agesLabel", String.class);
    var accessibilityLabel = getElementValue(cf, "accessibilityLabel", String.class);
    var accessibilityValue = getElementValue(cf, "accessibilityValue", String[].class);
    List<String> accessibilityValuelist = new ArrayList<>();
    if (accessibilityValue != null) {
      accessibilityValuelist = List.of(accessibilityValue);
    }
    var openingHoursLabel = getElementValue(cf, "openingHoursLabel", String.class);
    var agesValue = getElementValue(cf, "agesValue", String.class);
    var informationLabel = getElementValue(cf, "informationLabel", String.class);
    var durationLabel = getElementValue(cf, "durationLabel", String.class);
    var durationValue = getElementValue(cf, "durationValue", String.class);
    var languageLabel = getElementValue(cf, "languageLabel", String.class);
    var languageValue = getElementValue(cf, "languageValue", String[].class);
    var startDate = getElementValue(cf, "startDate", String.class);
    var endDate = getElementValue(cf, "endDate", String.class);

    List<String> languageValuelist = new ArrayList<>();
    if (languageValue != null) {
      languageValuelist = List.of(languageValue);
    }
    var typeLabel = getElementValue(cf, "typeLabel", String.class);
    var typeValue = getElementValue(cf, "typeValue", String.class);

    var sameTimeAcrossWeek = Optional.ofNullable(getElementValue(cf, "sameTimeAcrossWeek", Boolean.class))
        .orElse(false);

    var timings = getElementValue(cf, "timings", String[].class);
    List<String> timinglist = new ArrayList<>();
    if (timings != null) {
      timinglist = List.of(timings);
    }
    List<OpeningHoursValue> openingHoursValue = new ArrayList<>();
    if (sameTimeAcrossWeek) {
      var dailyLabel = getElementValue(cf, "dailyLabel", String.class);
      var dailyStartTime = getElementValue(cf, "dailyStartTime", String.class);
      var dailyEndTime = getElementValue(cf, "dailyEndTime", String.class);
      OpeningHoursValue op = new OpeningHoursValue(dailyLabel, dailyStartTime, dailyEndTime);
      openingHoursValue.add(op);
    } else {
      ObjectMapper objectMapper = new ObjectMapper();
      for (String item : timinglist) {
        try {
          OpeningHoursValue op = objectMapper.readValue(item, OpeningHoursValue.class);
          openingHoursValue.add(op);
        } catch (Exception e) {
          LOGGER.error("Error while parsing Opening Hours", e);
        }
      }
    }
    var locationLabel = getElementValue(cf, "locationLabel", String.class);
    var locationValue = getElementValue(cf, "locationValue", String.class);
    var openNowLabel = getElementValue(cf, "openNowLabel", String.class);
    var closeNowLabel = getElementValue(cf, "closedNowLabel", String.class);
    var toLabel = getElementValue(cf, "toLabel", String.class);
    var categoriesIcon = getElementValue(cf, "categories", String[].class);
    List<String> categoriesList = new ArrayList<>();
    if (categoriesIcon != null) {
      categoriesList = List.of(categoriesIcon);
    }

    return InformationListWidgetCFModel.builder()
      .idealForLabel(idealForLabel)
      .accessibilityLabel(accessibilityLabel)
      .accessibilityValue(accessibilityValuelist)
      .agesLabel(agesLabel)
      .agesValue(agesValue)
      .locationLabel(locationLabel)
      .locationValue(locationValue)
      .closedNowLabel(closeNowLabel)
      .durationLabel(durationLabel)
      .informationLabel(informationLabel)
      .endDate(endDate)
      .startDate(startDate)
      .durationValue(durationValue)
      .languageLabel(languageLabel)
      .languageValue(languageValuelist)
      .openingHoursLabel(openingHoursLabel)
      .sameTimeAcrossWeek(sameTimeAcrossWeek)
      .openingHoursValue(openingHoursValue)
      .openNowLabel(openNowLabel)
      .toLabel(toLabel)
      .typeLabel(typeLabel)
      .typeValue(typeValue)
      .categoriesIcon(categoriesList)
      .build();
  }
}
