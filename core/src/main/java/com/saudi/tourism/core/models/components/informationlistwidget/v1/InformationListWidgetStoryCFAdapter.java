package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

@Component(service = InformationListWidgetCFAdapter.class, immediate = true)
public class InformationListWidgetStoryCFAdapter
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

    return StringUtils.equalsIgnoreCase("story", cf.getTemplate().getTitle());
  }

  @Override
  public InformationListWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }

    var idealForLabel = getElementValue(cf, "idealForLabel", String.class);
    var informationLabel = getElementValue(cf, "informationLabel", String.class);
    var locationLabel = getElementValue(cf, "locationLabel", String.class);
    var locationValue = getElementValue(cf, "destination", String.class);
    var categoriesIcon = getElementValue(cf, "categories", String[].class);
    List<String> categoriesList = new ArrayList<>();
    if (categoriesIcon != null) {
      categoriesList = List.of(categoriesIcon);
    }

    return InformationListWidgetCFModel.builder()
      .idealForLabel(idealForLabel)
      .locationLabel(locationLabel)
      .locationValue(locationValue)
      .informationLabel(informationLabel)
      .categoriesIcon(categoriesList)
      .build();
  }
}
