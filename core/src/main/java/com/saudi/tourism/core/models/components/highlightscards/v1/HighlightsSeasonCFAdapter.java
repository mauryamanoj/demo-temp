package com.saudi.tourism.core.models.components.highlightscards.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.Calendar;
import java.util.Optional;

@Slf4j
@Component(service = HighlightsCFAdapter.class, immediate = true)
public class HighlightsSeasonCFAdapter implements HighlightsCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("season", cf.getTemplate().getTitle());
  }

  public HighlightsCard adaptTo(Adaptable adaptable) {

    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "title", String.class);
    var subTitle = getElementValue(cf, "subtitle", String.class);
    var startDate = getElementValue(cf, "startDate", Calendar.class);
    var endDate = getElementValue(cf, "endDate", Calendar.class);
    var fileReference = getElementValue(cf, "image", String.class);
    var s7image = getElementValue(cf, "s7image", String.class);
    var pagePath = getElementValue(cf, "pagePath", String.class);
    var targetInNewWindow = Optional.ofNullable(getElementValue(cf, "targetInNewWindow", Boolean.class)).orElse(false);
    var comingSoonLabel = getElementValue(cf, "comingSoonLabel", String.class);
    var expiredLabel = getElementValue(cf, "expiredLabel", String.class);

    final var image = new Image();

    image.setFileReference(fileReference);
    image.setS7fileReference(s7image);

    return HighlightsCard.builder()
      .title(title)
      .subTitle(subTitle)
      .startDate(startDate)
      .endDate(endDate)
      .image(image)
      .cardCtaLink(pagePath)
      .targetInNewWindow(targetInNewWindow)
      .comingSoonLabel(comingSoonLabel)
      .expiredLabel(expiredLabel)
      .build();
  }
}

