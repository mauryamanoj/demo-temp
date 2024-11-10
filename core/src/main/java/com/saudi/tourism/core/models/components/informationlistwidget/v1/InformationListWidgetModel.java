package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/** InformationListWidgetModel. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class InformationListWidgetModel {

  /** tagManager. */
  @Inject
  private transient TagManager tagManager;

  /** Resource Resolver. */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /** Current resource. */
  @Self
  private transient Resource currentResource;
  /** i18nProvider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /** language. */
  private String language = StringUtils.EMPTY;

  /** CFModel. */
  @Expose
  private InformationListWidgetCFModel cfModel;

  /** categories Icon. */
  private List<String> categoriesIcon;

  /** categories Tags. */
  @Expose private List<Category> categoriesTags;


  @PostConstruct
  void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    if (Objects.isNull(pageManager)) {
      return;
    }
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (Objects.isNull(currentPage)) {
      return;
    }

    final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfPath);
    if (Objects.isNull(cfResource)) {
      return;
    }

    cfModel = cfResource.adaptTo(InformationListWidgetCFModel.class);
    if (Objects.isNull(cfModel)) {
      return;
    }
    var locationValue = getDestinationLocationValue(cfModel.getLocationValue(), resourceResolver);
    cfModel.setLocationValue(locationValue);

    language = CommonUtils.getLanguageForPath(currentPage.getPath());
    if (Objects.isNull(tagManager)) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
    }
    Locale locale = new Locale(language);
    if (tagManager != null && cfModel.getAccessibilityValue() != null) {
      cfModel.setAccessibilityValue(
          cfModel.getAccessibilityValue().stream()
              .map(tag -> CommonUtils.getTagName(tag, tagManager, locale))
              .collect(Collectors.toList()));
    }
    if (tagManager != null && cfModel.getLanguageValue() != null) {
      cfModel.setLanguageValue(
          cfModel.getLanguageValue().stream()
              .map(tag -> CommonUtils.getTagName(tag, tagManager, locale))
              .collect(Collectors.toList()));
    }
    if (tagManager != null && cfModel.getTypeValue() != null) {
      cfModel.setTypeValue(CommonUtils.getTagName(cfModel.getTypeValue(), tagManager, locale));
    }

    if (tagManager != null && StringUtils.isNotBlank(cfModel.getAgesValue())) {
      cfModel.setAgesValue(CommonUtils.getTagName(cfModel.getAgesValue(), tagManager, locale));
    }

    categoriesIcon = cfModel.getCategoriesIcon();
    processCategoriesTags();
  }

  private void processCategoriesTags() {
    if (CollectionUtils.isNotEmpty(categoriesIcon)) {
      cfModel.setCategoriesTags(categoriesIcon.stream()
          .map(p -> TagUtils.getTagOrChildren(p, tagManager, resourceResolver, language))
          .flatMap(List::stream)
          .collect(Collectors.toList()));
    }
  }

  public static String getDestinationLocationValue(String destinationPath, ResourceResolver resourceResolver) {
    final var cfResource = resourceResolver.getResource(destinationPath);
    if (Objects.isNull(cfResource)) {
      return "";
    }
    final var  cfDestinationModel = cfResource.adaptTo(DestinationCFModel.class);
    if (Objects.isNull(cfDestinationModel)) {
      return "";
    }

    return cfDestinationModel.getTitle();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this.cfModel);
  }
}
