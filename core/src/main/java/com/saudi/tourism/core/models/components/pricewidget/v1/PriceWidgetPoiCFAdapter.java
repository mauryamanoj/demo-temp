package com.saudi.tourism.core.models.components.pricewidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Component(service = PriceWidgetCFAdapter.class, immediate = true)
public class PriceWidgetPoiCFAdapter implements PriceWidgetCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("poi", cf.getTemplate().getTitle());
  }

  @Override
  public PriceWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }

    var ticketType = getElementValue(cf, "ticketType", String.class);
    var ticketPrice = getElementValue(cf, "ticketPrice", String.class);
    var ticketPriceSuffix = getElementValue(cf, "ticketPriceSuffix", String.class);
    var ticketDetails = getElementValue(cf, "ticketDetails", String.class);
    var ticketCTALabel = getElementValue(cf, "ticketCTALabel", String.class);
    var ticketCTALink = getElementValue(cf, "ticketCTALink", String.class);


    return PriceWidgetCFModel.builder()
      .ticketType(ticketType)
      .ticketPrice(ticketPrice)
      .ticketPriceSuffix(ticketPriceSuffix)
      .ticketDetails(ticketDetails)
      .ticketCTALabel(ticketCTALabel)
      .ticketCTALink(ticketCTALink)
      .build();
  }
}
