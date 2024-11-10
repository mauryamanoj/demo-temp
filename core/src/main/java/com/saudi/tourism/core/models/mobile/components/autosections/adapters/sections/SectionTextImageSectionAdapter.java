package com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.textimage.v1.TextImageModel;
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
import java.util.Objects;

@Component(service = SectionResponseModelAdapter.class, immediate = true)
public class SectionTextImageSectionAdapter implements SectionResponseModelAdapter {


  /**
   * The constant TEXT_IMAGE_RES_TYPE.
   */
  private static final String TEXT_IMAGE_RES_TYPE = "sauditourism/components/content/text-image";

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

    return StringUtils.equalsIgnoreCase(TEXT_IMAGE_RES_TYPE, ((Resource) adaptable).getResourceType());
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

    var textImageModel = currentResource.adaptTo(TextImageModel.class);

    if (textImageModel == null) {
      return null;
    }


    SectionResponseModel sectionResponseModel = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    TextTemplate template = new TextTemplate();
    if (Objects.nonNull(textImageModel.getImage())) {
      template.setImage(
          LinkUtils.getAuthorPublishAssetUrl(
              resolver,
              textImageModel.getImage().getFileReference(),
              settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    }
    template.setHtml(textImageModel.getDescription());
    template.setTitle(textImageModel.getTitle());

    item.setTextTemplate(template);
    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("TEXT_TEMPLATE");
    itemComponentStyle.setComponentScrollDirection("VERTICAL");
    sectionResponseModel.setId("auto/text-template-" + MobileUtils.generateUniqueIdentifier());
    sectionResponseModel.setFilterType("");
    sectionResponseModel.setFavoriteContentType("STORIES");
    sectionResponseModel.setItemComponentStyle(itemComponentStyle);
    sectionResponseModel.setItems(List.of(item));
    sectionResponseModel.setTotalCount(sectionResponseModel.getItems().size());

    return sectionResponseModel;
  }


}
