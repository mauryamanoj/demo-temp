package com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.substory.v1.SubStoryModel;
import com.saudi.tourism.core.models.mobile.components.atoms.TextTemplate;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.MobileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(service = SectionResponseModelAdapter.class, immediate = true)
public class SectionSubStoryAdapter implements SectionResponseModelAdapter {


  /**
   * The constant SUB_STORY_RES_TYPE.
   */
  private static final String SUB_STORY_RES_TYPE = "sauditourism/components/content/sub-story";

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

    return StringUtils.equalsIgnoreCase(SUB_STORY_RES_TYPE, ((Resource) adaptable).getResourceType());
  }

  @Override
  public SectionResponseModel adaptTo(Adaptable adaptable) {
    Resource currentResource = ((Resource) adaptable);
    if (currentResource == null) {
      return null;
    }

    var resolver = currentResource.getResourceResolver();
    var pageManager = resolver.adaptTo(PageManager.class);
    if (pageManager == null) {
      return null;
    }

    var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return null;
    }

    var subStoryModel = currentResource.adaptTo(SubStoryModel.class);

    if (subStoryModel == null) {
      return null;
    }

    SectionResponseModel sectionResponseModel = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    TextTemplate template = new TextTemplate();

    var firstImage =
        subStoryModel.getGallery().stream()
            .filter(
                asset -> {
                  return "image".equals(asset.getType());
                })
            .findFirst()
            .orElse(null);
    if (firstImage != null) {
      template.setImage(LinkUtils.getAuthorPublishAssetUrl(
          resolver,
          firstImage.getImage().getFileReference(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH)));

    }
    template.setHtml(subStoryModel.getDescription());
    template.setTitle(subStoryModel.getTitle());

    item.setTextTemplate(template);
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("TEXT_TEMPLATE");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    sectionResponseModel.setId("auto/text-template-" + MobileUtils.generateUniqueIdentifier());
    sectionResponseModel.setFilterType("");
    sectionResponseModel.setFavoriteContentType("STORIES");
    sectionResponseModel.setItemComponentStyle(itemComponentStyle);
    sectionResponseModel.setItems(List.of(item));
    sectionResponseModel.setTotalCount(sectionResponseModel.getItems().size());

    return sectionResponseModel;
  }


}
