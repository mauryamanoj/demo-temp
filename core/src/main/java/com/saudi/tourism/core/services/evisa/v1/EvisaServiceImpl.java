package com.saudi.tourism.core.services.evisa.v1;

import com.google.gson.Gson;
import com.saudi.tourism.core.models.common.CountriesGroupsListConfigModel;
import com.saudi.tourism.core.models.components.evisa.v1.EvisaConfigModel;
import com.saudi.tourism.core.models.mobile.components.EvisaMobileConfigModel;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.services.evisa.v1.EvisaServiceImpl.SERVICE_DESCRIPTION;

/**
 * Service for eVisa.
 */
@Component(
    service = EvisaService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class EvisaServiceImpl implements EvisaService {
  /**
   * This Service description for OSGi.
   */
  static final String SERVICE_DESCRIPTION = "eVisa Service";


  /**
   * Platform All.
   */
  static final String PLATFORM_ALL = "all";
  /**
   * eVisa Config.
   */
  @Reference
  private EvisaConfig evisaConfig;

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /**
   * Settings service.
   */
  @Reference
  private SlingSettingsService settingsService;




  @Override
  public EvisaConfigModel fetchFilteredEvisaConfig(@NonNull final String locale, final String visaGroup,
                                                   @NonNull boolean questionsOnly, boolean visaDetailsOnly,
                                                   final String responseCode, final String platform) {

    EvisaConfigModel model = fetchEvisaConfig(locale);
    if (model == null) {
      return null;
    }
    EvisaConfigModel modelClone = copyEvisasConfigModel(model);

    // Filtering by visaGroup and responseCode
    if (StringUtils.isNotBlank(visaGroup)) {
      List<EvisaConfigModel.QuestionsByVisaGroup> filteredQuestionsList =
          Optional.ofNullable(modelClone.getQuestionsByVisaGroup()).orElse(
                  Collections.emptyList()).stream()
              .filter(question -> question.getVisaGroup() != null && question.getVisaGroup().equals(visaGroup))
              .collect(Collectors.toList());
      modelClone.setQuestionsByVisaGroup(filteredQuestionsList);

      List<EvisaConfigModel.VisaType> filteredVisaTypesList =
          Optional.ofNullable(modelClone.getVisaTypes()).orElse(
                  Collections.emptyList()).stream().filter(visaType -> visaType.getVisaGroup().equals(visaGroup))
              .collect(Collectors.toList());
      if (StringUtils.isNotBlank(responseCode)) {
        filteredVisaTypesList = Optional.ofNullable(filteredVisaTypesList).orElse(
                Collections.emptyList()).stream()
            .filter(visaType -> Objects.nonNull(visaType.getQuestions()) && visaType.getQuestions().stream()
                .anyMatch(question -> responseCode.equals(question.getCode()) && question.getResponse()))
            .collect(Collectors.toList());
      }

      modelClone.setVisaTypes(filteredVisaTypesList);
    }

    // Filtering by questionsOnly and visaDetailsOnly
    EvisaConfigModel filteredmodel = new EvisaConfigModel();

    if (questionsOnly) {
      filteredmodel.setQuestionsByVisaGroup(modelClone.getQuestionsByVisaGroup());
      modelClone = filteredmodel;
    }

    return modelClone;
  }


  @Override
  public EvisaConfigModel fetchEvisaConfig(@NonNull final String locale) {

    final String eVisaConfigPath = MessageFormat.format(evisaConfig.getEVisaConfigPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final Resource eVisaConfig = resourceResolver.getResource(eVisaConfigPath);
      if (eVisaConfig == null) {
        LOGGER.error("eVisa Config not found under %s", eVisaConfigPath);
        return null;
      }

      final var model = eVisaConfig.adaptTo(EvisaConfigModel.class);

      return model;
    }
  }

  @Override
  public EvisaMobileConfigModel fetchMobileFilteredEvisaConfig(@NonNull final String locale, final String visaGroup,
                                                               final String responseCode) {
    EvisaMobileConfigModel mobileModel = null;

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      EvisaConfigModel model =
          fetchFilteredEvisaConfig(locale, visaGroup, false, false, responseCode, null);
      if (Objects.nonNull(model)) {

        mobileModel = EvisaConfigModel.fromEvisaConfigModel(model, resolver, settingsService);
      }
    }
    return mobileModel;
  }

  @Override
  public List<CountriesGroupsListConfigModel.CountryGroupModel> getCountriesGroups(
      @NonNull final String language) {

    final var configPath =
        MessageFormat.format(evisaConfig.getEVisaCountriesGroupsConfigPath(), language);

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      final var resource = resolver.getResource(configPath);
      if (resource != null) {
        final var countriesGroupsListModel = resource.adaptTo(CountriesGroupsListConfigModel.class);
        if (countriesGroupsListModel != null) {
          return countriesGroupsListModel.getListCountriesGroups();
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Error during countries groups", e);
    }

    return null;
  }

  /**
   * Returns copy of eVisaModel.
   *
   * @param model EvisaConfigModel.
   * @return EvisaConfigModel  EvisaConfigModel.
   */
  private EvisaConfigModel copyEvisasConfigModel(EvisaConfigModel model) {
    Gson gson = new Gson();
    EvisaConfigModel deepCopy = gson.fromJson(gson.toJson(model), EvisaConfigModel.class);
    return deepCopy;

  }


}
