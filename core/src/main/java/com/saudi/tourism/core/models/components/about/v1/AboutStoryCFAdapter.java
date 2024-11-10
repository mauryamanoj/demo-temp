package com.saudi.tourism.core.models.components.about.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/** Adapts activity CF to AboutCFModel. */
@Component(service = AboutCFAdapter.class, immediate = true)
public class AboutStoryCFAdapter implements AboutCFAdapter, ContentFragmentAwareModel {

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
  public AboutCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var aboutTitle = getElementValue(cf, "aboutTitle", String.class);
    var aboutDescription = getElementValue(cf, "aboutDescription", String.class);
    var hideFavorite = Optional.ofNullable(getElementValue(cf, "hideFavorite", Boolean.class)).orElse(false);
    var readMoreLabel = getElementValue(cf, "readMoreLabel", String.class);
    var readLessLabel = getElementValue(cf, "readLessLabel", String.class);
    var categoriesIcon = Optional.ofNullable(getElementValue(cf, "categories", String[].class)).map(Arrays::asList)
        .orElse(Collections.emptyList());

    return AboutCFModel.builder()
        .aboutTitle(aboutTitle)
        .aboutDescription(aboutDescription)
        .readMoreLabel(readMoreLabel)
        .readLessLabel(readLessLabel)
        .hideFavorite(hideFavorite)
        .categoriesIcon(categoriesIcon)
      .build();
  }
}
