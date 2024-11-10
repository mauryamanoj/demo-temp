package com.saudi.tourism.core.models.mobile.components.autosections.adapters.items;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.stories.v1.Story;
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
 * Adapts story CF to ItemResponseModel.
 */
@Component(service = ItemResponseModelAdapter.class, immediate = true)
public class ItemResponseModelStoryCFAdapter implements ItemResponseModelAdapter, ContentFragmentAwareModel {

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

    return StringUtils.equalsIgnoreCase("story", cf.getTemplate().getTitle());
  }

  @Override
  public ItemResponseModel adaptTo(Adaptable adaptable) {

    Resource currentResource = ((Resource) adaptable);
    if (currentResource == null) {
      return null;
    }

    var resolver = currentResource.getResourceResolver();

    final var storyCFModel = currentResource.adaptTo(StoryCFModel.class);

    if (storyCFModel == null) {
      return null;
    }
    var story = Story.fromCFModel(storyCFModel);
    if (story == null) {
      return null;
    }

    String locale = CommonUtils.getLanguageForPath(currentResource.getPath());

    return MobileUtils.buildItemFromStory(story, resolver, settingsService, locale, i18nProvider);
  }
}
