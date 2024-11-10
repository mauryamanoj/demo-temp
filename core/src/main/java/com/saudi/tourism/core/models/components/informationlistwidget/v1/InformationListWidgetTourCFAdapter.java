package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Component(service = InformationListWidgetCFAdapter.class, immediate = true)
@Slf4j
public class InformationListWidgetTourCFAdapter
    implements InformationListWidgetCFAdapter, ContentFragmentAwareModel {
  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("tour", cf.getTemplate().getTitle());
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
    var agesValue = getElementValue(cf, "agesValue", String.class);
    var informationLabel = getElementValue(cf, "informationLabel", String.class);
    var durationLabel = getElementValue(cf, "durationLabel", String.class);
    var durationValue = getElementValue(cf, "durationValue", String.class);
    var languageLabel = getElementValue(cf, "languageLabel", String.class);
    var languageValue = getElementValue(cf, "languageValue", String[].class);
    List<String> languageValuelist = new ArrayList<>();
    if (languageValue != null) {
      languageValuelist = List.of(languageValue);
    }
    var typeLabel = getElementValue(cf, "typeLabel", String.class);
    var typeValue = getElementValue(cf, "type", String.class);

    var locationLabel = getElementValue(cf, "locationLabel", String.class);
    var locationValue = getElementValue(cf, "destination", String.class);
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
        .durationLabel(durationLabel)
        .informationLabel(informationLabel)
        .durationValue(durationValue)
        .languageLabel(languageLabel)
        .languageValue(languageValuelist)
        .typeLabel(typeLabel)
        .typeValue(typeValue)
        .categoriesIcon(categoriesList)
        .build();
  }
}
