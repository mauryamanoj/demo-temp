package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * E-Visa Banner Config in AppHomepage.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class EVisaBannerConfig implements Serializable {

  /**
   * eVisaLogo.
   */
  @Expose
  @ValueMapValue
  private String eVisaLogo;

  /**
   * poweredByLogo.
   */
  @Expose
  @ValueMapValue
  private String poweredByLogo;

  /**
   * description.
   */
  @Expose
  @ValueMapValue
  private String description;

  /**
   * ctaText.
   */
  @Expose
  @ValueMapValue
  private String ctaText;

  /**
   * ctaUrl.
   */
  @Expose
  @ValueMapValue
  private String ctaUrl;
}
