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
import java.util.stream.Collectors;

@Component(service = ItemDetailsAdapter.class, immediate = true)
public class TourWebPageAdapter implements ItemDetailsAdapter {
  /**
   * sectionResponseModelAdapterFactory.
   */
  @Reference
  private transient SectionResponseModelAdapterFactory sectionResponseModelAdapterFactory;

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

    return StringUtils.equalsIgnoreCase(Constants.TOURS_RES_TYPE, ((Resource) adaptable).getResourceType());
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

    CommonCFModel cfModel = SectionsMapper.loadCommonCF(currentPage, resolver, settingsService);
    HeaderResponseModel header = SectionsMapper.mapHeaderFromContentFragment(cfModel, resolver, settingsService);
    FooterResponseModel footer = SectionsMapper.mapFooterFromContentFragment(cfModel);

    List<SectionResponseModel> sections = buildSections(currentPage, resolver);

    ItemsDetailsResponseModel item = new ItemsDetailsResponseModel();
    item.setType(Constants.TYPE_AUTO);
    item.setHeader(header);
    item.setFooter(footer);
    item.setSections(sections.stream().filter(Objects::nonNull).collect(Collectors.toList()));

    return item;
  }

  private List<SectionResponseModel> buildSections(Page currentPage, ResourceResolver resolver) {
    List<SectionResponseModel> sections = new ArrayList<>();
    sections.add(SectionsBuilder.buildAutoMediaGallery(currentPage, resolver, settingsService));
    sections.add(SectionsBuilder.buildAutoAboutSection(currentPage, resolver, settingsService, "THINGS_TO_DO"));
    sections.add(SectionsBuilder.buildAutoOpeningHours(currentPage, resolver));
    sections.add(SectionsBuilder.buildAutoGeneralInformation(currentPage, resolver, settingsService));
    sections.add(SectionsBuilder.buildAutoMapWidget(currentPage, resolver));
    sections.add(SectionsBuilder.buildAutoBeforeYouGo(currentPage, resolver));
    sections.addAll(
        SectionsBuilder.findResponsiveGridSections(
            currentPage, resolver, sectionResponseModelAdapterFactory));

    return sections.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }
}
