package com.saudi.tourism.core.models.components.evisa.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.models.mobile.components.EvisaMobileConfigModel;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.GeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.TextTemplate;
import com.saudi.tourism.core.models.mobile.components.atoms.RowsGeneralInformationWidget;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * C-29 eVisa config model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class EvisaConfigModel {

  /**
   * Mobile image width.
   */
  public static final String MOBILE_IMAGE_WIDTH = "700";

  /**
   * Desktop image width.
   */
  public static final String DESKTOP_IMAGE_WIDTH = "1500";

  /**
   * Mobile image relative width.
   */
  public static final String MOBILE_IMG_RELATIVE_WIDTH_90VW = "90vw";

  /**
   * Desktop image relative width.
   */
  public static final String DESKTOP_IMG_RELATIVE_WIDTH_40VW = "40vw";
  /**
   * Current Resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * Image.
   */
  @ChildResource
  private Image image;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Select Country Label.
   */
  @ValueMapValue
  private String selectCountryLabel;

  /**
   * Select country placeholder.
   */
  @ValueMapValue
  private String selectCountryPlaceholder;

  /**
   * Search country placeholder.
   */
  @ValueMapValue
  private String searchCountryPlaceholder;


  /**
   * Search question label.
   */
  @ValueMapValue
  private String selectQuestionLabel;

  /**
   * Search question placeholder.
   */
  @ValueMapValue
  private String selectQuestionPlaceholder;

  /**
   * Informations Labels.
   */
  @ValueMapValue
  private String informationsLabel;

  /**
   * Requirements Labels.
   */
  @ValueMapValue
  private String requirementsLabel;

  /**
   * Eligible For Label.
   */
  @ValueMapValue
  private String eligibleForLabel;

  /**
   * Questions to show to user depending on its country.
   */
  @Inject
  private List<QuestionsByVisaGroup> questionsByVisaGroup;

  /**
   * Visa Types.
   */
  @Inject
  private List<VisaType> visaTypes;


  /**
   * Download App.
   */
  @ChildResource
  private List<MobileDownloadAppsModel> appAdDownload;


  public static EvisaMobileConfigModel fromEvisaConfigModel(final EvisaConfigModel evisaConfigModel,
                                                            final ResourceResolver resourceResolver,
                                                            final SlingSettingsService settingsService) {

    if (evisaConfigModel == null || evisaConfigModel.getVisaTypes() == null) {
      return null;
    }

    String requirementsLabel = evisaConfigModel.getRequirementsLabel();

    List<EvisaMobileConfigModel.EvisaMobileConfigOption> visaOptions = evisaConfigModel.getVisaTypes().stream()
        .findFirst()
        .map(visaType -> visaType.getSections().stream()
            .map(section -> mapSectionToVisaOption(section, requirementsLabel, resourceResolver, settingsService))
            .collect(Collectors.toList()))
        .orElse(Collections.emptyList());

    List<EvisaMobileConfigModel.EvisaMobileConfigAppDownload> appAdDownload =
        Optional.ofNullable(evisaConfigModel.getAppAdDownload())
            .orElseGet(Collections::emptyList) // Provides an empty list if getAppAdDownload() is null
            .stream()
            .map(app -> mapAppDownload(app, resourceResolver, settingsService))
            .collect(Collectors.toList());

    return EvisaMobileConfigModel.builder().visaOptions(visaOptions).appAdDownload(appAdDownload).build();
  }

  private static CustomAction createCustomAction(Section section) {
    CustomAction action = new CustomAction();
    action.setShow(true); // always true
    action.setEnable(true); // always true
    String title = Optional.ofNullable(section.getCard())
        .map(card -> card.getLink())
        .filter(Objects::nonNull)
        .map(link -> link.getText())
        .orElse(null);

    String url =
        Optional.ofNullable(section.getCard())
            .map(card -> card.getLink())
            .filter(Objects::nonNull)
            .map(link -> link.getUrl())
            .orElse(null);

    if (StringUtils.isBlank(title) && StringUtils.isBlank(url)) {
      return null;
    }

    action.setTitle(title);
    Cta cta = new Cta();
    cta.setType("WEB"); // always WEB
    cta.setUrl(url);
    action.setCta(cta);
    ButtonComponentStyle style = new ButtonComponentStyle();
    style.setComponentUIType("PRIMARY"); // always PRIMARY
    action.setButtonComponentStyle(style);
    return action;
  }

  private static EvisaMobileConfigModel.EvisaMobileConfigOption mapSectionToVisaOption(
      Section section,
      String requirementsLabel,
      ResourceResolver resourceResolver,
      SlingSettingsService settingsService) {

    List<RowsGeneralInformationWidget> rows = processInformations(section);

    GeneralInformationWidget generalInformationWidget = createGeneralInformationWidget(rows);
    TextTemplate textTemplate = createTextTemplate(section, requirementsLabel);
    CustomAction customAction = createCustomAction(section);

    String absoluteIconUrl = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, section.getMobileIcon(),
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    return EvisaMobileConfigModel.EvisaMobileConfigOption.builder()
        .details(EvisaMobileConfigModel.EvisaMobileConfigDetail.builder()
            .generalInformationWidget(generalInformationWidget)
            .textTemplate(textTemplate)
            .customAction(customAction)
            .build())
        .buttonTitle(Optional.ofNullable(section.getCard()).map(CtaCard::getViewMoreLabel).orElse(null))
        .title(section.getTitle())
        .imageURL(absoluteIconUrl)
        .bannerImageURL(absoluteIconUrl)
        .build();
  }


  private static GeneralInformationWidget createGeneralInformationWidget(
      List<RowsGeneralInformationWidget> rows) {
    GeneralInformationWidget widget = new GeneralInformationWidget();
    widget.setRows(rows);
    widget.setExpandable(false); // always false
    return widget;
  }

  private static TextTemplate createTextTemplate(Section section, String requirementsLabel) {
    TextTemplate template = new TextTemplate();

    if (StringUtils.isBlank(section.getRequirementsAndDocumentation())) {
      template.setHtml(section.getDescription());
    } else {
      template.setHtml(section.getRequirementsAndDocumentation());
    }
    template.setShowPattern(false); // always false
    template.setTitle(requirementsLabel);
    return template;
  }

  private static RowsGeneralInformationWidget mapInformationToRow(Information information) {
    RowsGeneralInformationWidget row =
        new RowsGeneralInformationWidget();


    row.setTitle(information.getKey());
    row.setSideTitle(information.getValue());
    row.setSideTitleType("text");


    return row;
  }

  private static EvisaMobileConfigModel.EvisaMobileConfigAppDownload mapAppDownload(
      MobileDownloadAppsModel appDownload, ResourceResolver resourceResolver, SlingSettingsService settingsService) {

    String absoluteImageURL = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, appDownload.getImageURL(),
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    return EvisaMobileConfigModel.EvisaMobileConfigAppDownload.builder().html(appDownload.getHtml())
        .imageURL(absoluteImageURL).build();


  }


  private static List<RowsGeneralInformationWidget> processInformations(Section section) {
    List<Information> informations = Optional.ofNullable(section.getInformations()).orElseGet(Collections::emptyList);

    // Ensure we have a positive size before proceeding
    int size = informations.size();
    if (size <= 0) {
      return Collections.emptyList(); // Return an empty list if there are no informations
    }

    // Process all elements except the last
    List<RowsGeneralInformationWidget> rows =
        IntStream.range(0, size - 1) // Exclude the last element
            .mapToObj(informations::get)
            .map(EvisaConfigModel::mapInformationToRow)
            .collect(Collectors.toList());

    // Now handle the last element separately, if it exists
    if (size > 0) {
      Information lastInformation = informations.get(size - 1);
      RowsGeneralInformationWidget lastRow = mapInformationToRowForValidity(lastInformation);
      rows.add(lastRow); // Add this last processed item to the list
    }

    return rows;
  }

  private static RowsGeneralInformationWidget mapInformationToRowForValidity(
      Information information) {
    // A modified version of mapInformationToRow to apply the special treatment for the last information
    RowsGeneralInformationWidget row =
        new RowsGeneralInformationWidget();
    row.setDescription(information.getValue()); // Assume we're setting the description for the last item differently
    // Set any other properties of row as needed
    return row;
  }


  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    if (image != null) {
      final var mobileScRendition = new SmartCropRenditions();
      mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
      mobileScRendition.setRendition(CropEnum.CROP_375x280.getValue());
      mobileScRendition.setWidths(MOBILE_IMAGE_WIDTH);
      mobileScRendition.setImgRelativeWidth(MOBILE_IMG_RELATIVE_WIDTH_90VW);
      mobileScRendition.init();

      final var desktopScRendition = new SmartCropRenditions();

      desktopScRendition.setRendition(CropEnum.CROP_375x280.getValue());
      desktopScRendition.setWidths(DESKTOP_IMAGE_WIDTH);
      desktopScRendition.setImgRelativeWidth(DESKTOP_IMG_RELATIVE_WIDTH_40VW);
      desktopScRendition.init();

      final var scRenditions = Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});
      image.setS7mobileImageReference(image.getS7fileReference());
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, scRenditions, CropEnum.CROP_375x280.getValue(),
          CropEnum.CROP_375x280.getValue(), BreakPointEnum.DESKTOP.getValue(), BreakPointEnum.MOBILE.getValue());
    }
  }

  /**
   * Questions By ContryType model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class QuestionsByVisaGroup {
    /**
     * Visa Group.
     */
    @ValueMapValue
    private String visaGroup;

    /**
     * Questions.
     */
    @Inject
    private List<QuestionLabel> questions;
  }

  /**
   * Question Label Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class QuestionLabel {
    /**
     * Code of the question.
     */
    @ValueMapValue
    private String code;

    /**
     * Label of the question.
     */
    @ValueMapValue
    private String label;
  }

  /**
   * Question Response Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class QuestionResponse {
    /**
     * Code of the question.
     */
    @ValueMapValue
    private String code;

    /**
     * Label of the question.
     */
    @ValueMapValue
    private Boolean response;
  }

  /**
   * VisaType Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class VisaType {
    /**
     * Visa Group.
     */
    @ValueMapValue
    private String visaGroup;

    /**
     * Visa Group.
     */
    @ValueMapValue
    private String visaTypesTitle;

    /**
     * Questions.
     */
    @Inject
    private List<QuestionResponse> questions;

    /**
     * Sections.
     */
    @Inject
    private List<Section> sections;

  }

  /**
   * CTA Card Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class CtaCard {

    /**
     * CTA card title.
     */
    @ValueMapValue
    private String title;

    /**
     * CTA Card Lik.
     */
    @ChildResource
    private Link link;

    /**
     * CTA card icon.
     */
    @ValueMapValue
    private String icon;

    /**
     * CTA card viewMoreLabel.
     */
    @ValueMapValue
    private String viewMoreLabel;

    /**
     * CTA card ViewMoreMobileLabel.
     */
    @ValueMapValue
    private String viewMoreMobileLabel;

    /**
     * init method.
     */
    @PostConstruct
    private void init() {
      if (Objects.nonNull(link) && StringUtils.isEmpty(link.getUrl())) {
        link = null;
      }
    }
  }

  /**
   * Information Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class Information {

    /**
     * Information key.
     */
    @ValueMapValue
    private String key;

    /**
     * Information value.
     */
    @ValueMapValue
    private String value;
  }

  /**
   * Section Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class Section {

    /**
     * Resource Resolver.
     */
    @Inject
    private transient ResourceResolver resourceResolver;

    /**
     * RunMode Service.
     *
     * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
     */
    @OSGiService
    @Getter(AccessLevel.NONE)
    private transient RunModeService runModeService;

    /**
     * Title of the section.
     */
    @ValueMapValue
    private String title;

    /**
     * Description of section.
     */
    @ValueMapValue
    private String description;

    /**
     * Icon of the section.
     */
    @ValueMapValue
    @Getter
    private String icon;

    /**
     * Mobile icon of the section.
     */
    @ValueMapValue
    @Getter
    private String mobileIcon;

    /**
     * Requirements & documentation.
     */
    @ValueMapValue
    private String requirementsAndDocumentation;

    /**
     * CTA card.
     */
    @ChildResource
    private CtaCard card;


    /**
     * Informations.
     */
    @ChildResource
    private List<Information> informations;


    /**
     * init method.
     */
    @PostConstruct
    private void init() {
      if (StringUtils.isNotEmpty(requirementsAndDocumentation)) {
        requirementsAndDocumentation = LinkUtils.rewriteLinksInHtml(requirementsAndDocumentation, resourceResolver,
            runModeService.isPublishRunMode());
      }
      if (StringUtils.isNotEmpty(description)) {
        description = LinkUtils.rewriteLinksInHtml(description, resourceResolver,
          runModeService.isPublishRunMode());
      }
    }
  }
}
