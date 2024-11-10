package com.saudi.tourism.core.models.mobile.components.autosections.adapters.items;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Adapts event CF to ItemResponseModel.
 */
@Component(service = ItemResponseModelAdapter.class, immediate = true)
public class ItemResponseModelEventCFAdapter implements ItemResponseModelAdapter, ContentFragmentAwareModel {

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * Sling settings service.
   */
  @Reference
  private SlingSettingsService settingsService;

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
  public ItemResponseModel adaptTo(Adaptable adaptable) {

    Resource currentResource = ((Resource) adaptable);
    if (currentResource == null) {
      return null;
    }

    var resolver = currentResource.getResourceResolver();
    final var event = currentResource.adaptTo(EventCFModel.class);

    if (event == null) {
      return null;
    }

    String locale = CommonUtils.getLanguageForPath(currentResource.getPath());

    return MobileUtils.buildItemFromEventCFModel(event, resolver, settingsService, locale, i18nProvider);
  }
}
