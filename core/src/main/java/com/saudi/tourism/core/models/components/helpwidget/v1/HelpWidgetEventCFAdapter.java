package com.saudi.tourism.core.models.components.helpwidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Component(service = HelpWidgetCFAdapter.class, immediate = true)
public class HelpWidgetEventCFAdapter implements HelpWidgetCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("event", cf.getTemplate().getTitle());
  }

  @Override
  public HelpWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }

    var helpWidgetHeading = getElementValue(cf, "helpWidgetHeading", String.class);
    var helpWidgetDescription = getElementValue(cf, "helpWidgetDescription", String.class);
    var helpWidgetCTALabel = getElementValue(cf, "helpWidgetCTALabel", String.class);
    var helpWidgetCTALink = getElementValue(cf, "helpWidgetCTALink", String.class);

    return HelpWidgetCFModel.builder()
        .helpWidgetHeading(helpWidgetHeading)
        .helpWidgetDescription(helpWidgetDescription)
        .helpWidgetCTALabel(helpWidgetCTALabel)
        .helpWidgetCTALink(helpWidgetCTALink)
        .build();
  }
}
