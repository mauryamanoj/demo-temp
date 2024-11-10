package com.saudi.tourism.core.models.components.menu;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.menu.MenuCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is the Sling Model class for the Menu Content Fragment component.
 * The {@link PostConstruct} annotation allows us to perform some logic after the object has been instantiated.
 * The {@link Model} annotation allows us to register the class as a Sling Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class MenuModel {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * The content fragment path.
   */
  @ValueMapValue(name = "fragmentPath",
      injectionStrategy = InjectionStrategy.OPTIONAL)
  @Nullable
  private String fragmentPath;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * The slingHttpServletRequest .
   */
  @JsonIgnore
  @Self
  private SlingHttpServletRequest slingHttpServletRequest;

  /** Resource Resolver. */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Fragment Menu Wrapper.
   */
  private MenuCFModel menuCFModel = new MenuCFModel();

  @PostConstruct
  public void init() {
    if (StringUtils.isNotEmpty(this.fragmentPath)) {
      String path = slingHttpServletRequest.getPathInfo();
      String language = CommonUtils.getLanguageForPath(path);
      // Replace {language} with the value of the language.
      String replacedFragmentPath = fragmentPath.replace("{language}", language);
      // check if exist resource for replacedFragmentPath
      // case language is wrong, ex. https://www.visitsaudi.com/nopage
      if (resourceResolver.getResource(replacedFragmentPath) == null) {
        // if not exist, replace {language} with en (default language)
        replacedFragmentPath = fragmentPath.replace("{language}", "en");
      }
      Resource menuCF = resourceResolver.getResource(replacedFragmentPath + Constants.JCR_CONTENT_DATA_MASTER);
      if (menuCF != null) {
        menuCFModel = menuCF.adaptTo(MenuCFModel.class);
        processMenuDataForDynamicMedia(menuCFModel);
        processLanguages(menuCFModel, language);
        buildLogo(language);
      }
    }
  }

  /**
   * Process menu data for dynamic media.
   *
   * @param menuCFModel the menu cf model
   */
  public void processMenuDataForDynamicMedia(MenuCFModel menuCFModel) {
    if (menuCFModel != null && menuCFModel.getData() != null) {
      menuCFModel.getData().stream().filter(Objects::nonNull).flatMap(menuItem -> {
        if (menuItem.getData() != null) {
          if (menuItem.getImage() != null) {
            DynamicMediaUtils.setAllImgBreakPointsInfo(menuItem.getImage(),
                CropEnum.CROP_1920X1080.getValue(), CropEnum.CROP_1920X1080.getValue(),
                "1280", "420",
                slingHttpServletRequest.getResourceResolver(),
                slingHttpServletRequest.getResource());
          }
          return menuItem.getData().stream().filter(Objects::nonNull);
        }
        return Stream.empty();
      }).forEach(menuItemData -> {
        if (menuItemData.getImage() != null) {
          DynamicMediaUtils.setAllImgBreakPointsInfo(menuItemData.getImage(),
              CropEnum.CROP_1920X1080.getValue(), CropEnum.CROP_1920X1080.getValue(),
              "1280", "420",
              slingHttpServletRequest.getResourceResolver(),
              slingHttpServletRequest.getResource());
        }
      });
    }
  }

  /**
   * Process languages.
   *
   * @param menuCFModel the menu cf model
   * @param currentLanguage the current language
   */
  private void processLanguages(MenuCFModel menuCFModel, String currentLanguage) {
    if (Objects.isNull(menuCFModel) || Objects.isNull(menuCFModel.getLanguages())
        || Objects.isNull(menuCFModel.getLanguages().getData())) {
      return;
    }

    menuCFModel.getLanguages().getData().stream()
        .filter(Objects::nonNull)
        .forEach(languageItem -> {
          String path = StringUtils.removeEnd(languageItem.getPath(), "/");
          path = path.replaceFirst("\\.html$", "");  // Remove .html extension if present
          String language = CommonUtils.getLanguageForPath(path);
          languageItem.setSelected(currentLanguage.equals(language));
        });
  }


  /**
   * Build logo.
   *
   * @param language the language
   */
  private void buildLogo(String language) {
    String logoBasePath = menuCFModel.getLogoBasePath();
    String saudiLogoUrlPath = menuCFModel.getSaudiLogoUrlPath();
    Image imageLogo = new Image();
    if (StringUtils.isNotBlank(logoBasePath)) {
      // Remove trailing slash if present
      String cleanedLogoBasePath = StringUtils.removeEnd(logoBasePath, "/");

      String fileName = StringUtils.substringAfterLast(cleanedLogoBasePath, "/");
      String logoPath = StringUtils.join(cleanedLogoBasePath, "/", fileName, "-", language, ".svg");
      String logoMobilePath = StringUtils.join(cleanedLogoBasePath, "/", fileName, "-mobile.svg");
      imageLogo.setDesktopImage(logoPath);
      imageLogo.setMobileImage(logoMobilePath);
      menuCFModel.setLogo(imageLogo);
    }
    if (StringUtils.isNotBlank(saudiLogoUrlPath)) {
      saudiLogoUrlPath = LinkUtils.getAuthorPublishUrl(resourceResolver, saudiLogoUrlPath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
      menuCFModel.setSaudiLogoUrlPath(saudiLogoUrlPath);

    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(menuCFModel);
  }
}
