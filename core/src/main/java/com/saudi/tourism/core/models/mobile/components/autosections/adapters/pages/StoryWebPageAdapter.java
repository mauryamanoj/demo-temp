package com.saudi.tourism.core.models.mobile.components.autosections.adapters.pages;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.CommonCFModel;
import com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections.SectionResponseModelAdapterFactory;
import com.saudi.tourism.core.models.mobile.components.autosections.utils.SectionsBuilder;
import com.saudi.tourism.core.models.mobile.components.autosections.utils.SectionsMapper;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.HeaderResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.FooterResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsDetailsResponseModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(service = ItemDetailsAdapter.class, immediate = true)
public class StoryWebPageAdapter implements ItemDetailsAdapter {



  /**
   * Sling settings service.
   */
  @Reference
  private SlingSettingsService settingsService;


  /**
   * sectionResponseModelAdapterFactory.
   */
  @Reference
  private transient SectionResponseModelAdapterFactory sectionResponseModelAdapterFactory;


  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    return Optional.ofNullable(adaptable)
        .map(adpt -> (Resource) adpt)
        .map(Resource::getResourceType)
        .map(type -> StringUtils.equalsIgnoreCase(Constants.STORIES_RES_TYPE, type))
        .orElse(false);
  }

  @Override
  public ItemsDetailsResponseModel adaptTo(Adaptable adaptable) {
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

    ItemsDetailsResponseModel item = new ItemsDetailsResponseModel();
    item.setType(Constants.TYPE_AUTO);
    CommonCFModel cfModel = SectionsMapper.loadCommonCF(currentPage, resolver, settingsService);

    if (cfModel != null) {
      HeaderResponseModel header = SectionsMapper.mapHeaderFromContentFragment(cfModel, resolver, settingsService);
      FooterResponseModel footer = SectionsMapper.mapFooterFromContentFragment(cfModel);
      item.setHeader(header);
      item.setFooter(footer);
    }

    List<SectionResponseModel> sections = buildSections(currentPage, resolver);


    item.setSections(sections);

    return item;
  }


  private List<SectionResponseModel> buildSections(Page currentPage, ResourceResolver resolver) {
    List<SectionResponseModel> sections = new ArrayList<>();
    sections.add(SectionsBuilder.buildAutoPageBanner(currentPage, resolver, settingsService));
    sections.add(SectionsBuilder.buildAutoAboutSection(currentPage, resolver, settingsService, "stories"));
    sections.add(SectionsBuilder.buildAutoGeneralInformation(currentPage, resolver, settingsService));
    sections.addAll(findResponsiveGridSections(currentPage, resolver));

    return sections.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }
  private List<SectionResponseModel> findResponsiveGridSections(Page currentPage, ResourceResolver resolver) {
    Resource responsiveGrid = SectionsBuilder.findComponentResource(currentPage, resolver, "root/responsivegrid");
    return CommonUtils.iteratorToStream(responsiveGrid.listChildren())
        .map(res -> sectionResponseModelAdapterFactory.getAdapter(res, SectionResponseModel.class))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


}
