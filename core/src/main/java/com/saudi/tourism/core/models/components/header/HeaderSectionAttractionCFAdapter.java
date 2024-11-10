package com.saudi.tourism.core.models.components.header;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;


@Component(service = HeaderSectionCFAdapter.class, immediate = true)
public class HeaderSectionAttractionCFAdapter implements HeaderSectionCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equals("attraction", cf.getTemplate().getTitle().toLowerCase());
  }

  @Override
  public HeaderSectionCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "title", String.class);
    var subtitle = getElementValue(cf, "subtitle", String.class);
    var authorText = getElementValue(cf, "authorText", String.class);
    var authorCtaLink = getElementValue(cf, "authorCtaLink", String.class);
    var s7image = getElementValue(cf, "s7image", String.class);
    var alt = getElementValue(cf, "alt", String.class);

    return HeaderSectionCFModel.builder()
      .title(title)
      .subtitle(subtitle)
      .authorText(authorText)
      .authorCtaLink(authorCtaLink)
      .s7image(s7image)
      .alt(alt)
      .build();
  }
}
