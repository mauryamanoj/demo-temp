package com.saudi.tourism.core.models.components.alerts;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

/**
 * AlertTourCFAdapter.
 */
@Component(service = AlertCFAdapter.class, immediate = true)
public class AlertTourCFAdapter implements AlertCFAdapter, ContentFragmentAwareModel {

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
  public AlertCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var alert = getElementValue(cf, "alert", String.class);
    var alertColor = getElementValue(cf, "alertColor", String.class);

    return AlertCFModel.builder()
      .alert(alert)
      .alertColor(alertColor)
      .build();
  }
}
