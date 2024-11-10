package com.saudi.tourism.core.models.components.header;

import java.util.Locale;
import java.util.Objects;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.settings.SlingSettingsService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class HeaderSectionModel {
  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;
  /**
   * Resource Resolver.
   */
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * Enable manual authoring.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Expose
  private Boolean enableManualAuthoring;


  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Subtitle.
   */
  @ValueMapValue
  @Expose
  private String subtitle;


  /**
   * Author.
   */
  @ChildResource
  @Setter
  @Expose
  private Author author;

  /**
   * cfModel.
   */
  @Getter
  private HeaderSectionCFModel cfModel;

  /**
   *  By Label.
   */
  @Expose
  private String byLabel;

  @PostConstruct
  void init() {

    if (Boolean.FALSE.equals(enableManualAuthoring)) {
      final var pageManager = resourceResolver.adaptTo(PageManager.class);
      final var currentPage = pageManager.getContainingPage(currentResource);
      if (currentPage == null) {
        return;
      }

      final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
      if (StringUtils.isEmpty(cfPath)) {
        return;
      }

      final var cfResource = resourceResolver.getResource(cfPath);
      if (cfResource == null) {
        return;
      }

      cfModel = cfResource.adaptTo(HeaderSectionCFModel.class);
      if (cfModel == null) {
        return;
      }

      title = cfModel.getTitle();
      subtitle = cfModel.getSubtitle();
      Image image = new Image();
      if (author == null) {
        author = new Author();
      }
      author.setAuthorText(cfModel.getAuthorText());
      String authorCtaLink = cfModel.getAuthorCtaLink();
      authorCtaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, authorCtaLink,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
      author.setAuthorCtaLink(authorCtaLink);
      if (cfModel.getS7image() != null) {
        image.setS7fileReference(cfModel.getS7image());
      }
      if (cfModel.getImage() != null) {
        image.setDesktopImage(cfModel.getImage());
      }
      if (cfModel.getAlt() != null) {
        image.setAlt(cfModel.getAlt());
      }
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-660x337", "crop-80x40",
          "1280", "420", currentResource.getResourceResolver(), currentResource);

      author.setImage(image);
    }

    if (Objects.nonNull(i18nProvider)) {
      final var language = CommonUtils.getLanguageForPath(currentResource.getPath());
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));
      byLabel = CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_BY);
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
