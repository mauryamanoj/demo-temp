package com.saudi.tourism.core.models.mobile.components;

import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.GeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.TextTemplate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.io.Serializable;
import java.util.List;

/**
 * EvisaMobileConfigModel model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Builder
public class EvisaMobileConfigModel implements Serializable {

  /**
   * visaOptions list.
   */
  private List<EvisaMobileConfigOption> visaOptions;


  /**
   * appAdDownload list.
   */
  private List<EvisaMobileConfigAppDownload> appAdDownload;

  /**
   * EvisaMobileConfigAppDownload Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  @Builder
  public static class EvisaMobileConfigAppDownload {

    /**
     * imageURL.
     */
    private String imageURL;

    /**
     * html.
     */
    private String html;



  }

  /**
   * EvisaMobileConfigOptions Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  @Builder
  public static class EvisaMobileConfigOption {

    /**
     * Title.
     */
    private String title;
    /**
     * buttonTitle.
     */
    private String buttonTitle;
    /**
     * imageURL.
     */
    private String imageURL;

    /**
     * bannerImageURL.
     */
    private String bannerImageURL;

    /**
     * visa Details .
     */
    private EvisaMobileConfigDetail details;
  }
  /**
   * EvisaMobileConfigDetails Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  @Builder
  public static class EvisaMobileConfigDetail {
    /**
     * The customAction.
     */
    private CustomAction customAction;

    /**
     * The generalInformationWidget.
     */
    private GeneralInformationWidget generalInformationWidget;

    /**
     * The textTemplate.
     */
    private TextTemplate textTemplate;

  }
}
