package com.saudi.tourism.core.models.components.contentfragment.menu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;


@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class MenuCFModel {

  /**
   * The resource resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * The Resource.
   */
  @Inject
  private Resource currentResource;

  /**
   * The logo base path.
   */
  @ValueMapValue
  private String logoBasePath;

  /**
   * The saudi Logo Url Path.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String saudiLogoUrlPath;

  /**
   * The menu items.
   */
  @ValueMapValue
  private String[] menuItems;

  /**
   * The languages.
   */
  @ValueMapValue
  private List<String>  languageItems;

  /**
   * The language label.
   */
  @ValueMapValue
  private String languageLabel;

  /**
   * The logo.
   */
  @Expose
  @Setter
  private Image logo;

  /**
   * The items.
   */
  @Expose
  private List<MenuItemCFModel> data = new LinkedList<>();

  /**
   * The languages.
   */
  @Expose
  private Languages languages;

  /**
   * The evisa.
   */
  @Expose
  @Self
  private EvisaCFModel evisa;

  /**
   * The user menu.
   */
  @Expose
  @Self
  private UserMenuCFModel userMenu;

  /**
   * The search box.
   */
  @Expose
  @Self
  private SearchBoxCFModel searchBox;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  @PostConstruct
  public void init() {
    saudiLogoUrlPath =
      LinkUtils.getAuthorPublishUrl(
        resourceResolver,
        saudiLogoUrlPath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    buildMenuItems();
    buildLanguages();
  }

  /**
   * Build menu items.
   *
   */
  private void buildMenuItems() {
    if (CollectionUtils.isNotEmpty(Arrays.asList(menuItems))) {
      data = Arrays.stream(menuItems).filter(Objects::nonNull).map(resourceResolver::getResource)
        .filter(Objects::nonNull).map(fragmentResource -> {
          Resource subMenuCFModel = resourceResolver.getResource(
              fragmentResource.getPath() + Constants.JCR_CONTENT_DATA_MASTER);
          if (subMenuCFModel == null) {
            return null;
          }
          return subMenuCFModel.adaptTo(MenuItemCFModel.class);
        }).filter(Objects::nonNull)
        .collect(Collectors.toList());
    }
  }

  /**
   * Build languages.
   *
   * @throws JsonProcessingException the json processing exception
   */
  private void buildLanguages() {
    List<Language>  languagesData = new LinkedList<>();
    for (String language : languageItems) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        Language languageItem = mapper.readValue(language, Language.class);
        String path = languageItem.getCtaLink();
        String link = StringUtils.join("/" + CommonUtils.getLanguageForPath(languageItem.getCtaLink()));
        String unAvailableLanLink = LinkUtils.getAuthorPublishUrl(
            resourceResolver,
            languageItem.unAvailableLanLink,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
        languagesData.add(new Language(languageItem.getCtaLabel(), link,
            languageItem.unAvailableLanMessage, unAvailableLanLink, languageItem.unAvailableLanLinkLabel, path));
      } catch (JsonProcessingException e) {
        LOGGER.error("Error while parsing language item", e);
      }
    }
    languages = Languages.builder().
      label(languageLabel).
      data(languagesData).
      build();
  }

  @Builder
  @Getter
  public static class Languages {

    /**
     * Label.
     */
    @Expose
    private String label;

    /**
     * Language data.
     */
    @Expose
    private List<Language> data;

  }

  @Getter
  @Setter
  public static class Language {

    /**
     * CTA Label.
     */
    @Expose
    private String ctaLabel;

    /**
     * CTA Link.
     */
    @Expose
    private String ctaLink;

    /**
     * unAvailable Lan Message.
     */
    @Expose
    private String unAvailableLanMessage;

    /**
     * unAvailable Lan Link.
     */
    @Expose
    private String unAvailableLanLink;

    /**
     * unAvailable Lan Link Lable.
     */
    @Expose
    private String unAvailableLanLinkLabel;
    /**
     * Path.
     */
    private String path;

    /**
     * Is selected.
     */
    @Expose
    private boolean isSelected;

    public Language() {
    }

    public Language(String ctaLabel, String ctaLink,
                    String unAvailableLanMessage, String unAvailableLanLink,
                    String unAvailableLanLinkLabel, String path) {
      this.ctaLabel = ctaLabel;
      this.ctaLink = ctaLink;
      this.unAvailableLanMessage = unAvailableLanMessage;
      this.unAvailableLanLink = unAvailableLanLink;
      this.unAvailableLanLinkLabel = unAvailableLanLinkLabel;
      this.path = path;
    }
  }
}
