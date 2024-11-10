package com.saudi.tourism.core.models.components.contentfragment.footer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import javax.annotation.PostConstruct;
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SubFragmentNewsletter {
  /**
   * Reference of Saudi Tourism Configuration.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;
  /**
   * news letter Title.
   */
  @ValueMapValue(name = "newsletterTitle")
  @Setter
  @Expose
  private String title;

  /**
   * placeholder.
   */
  @ValueMapValue(name = "newsletterPlaceholder")
  @Expose
  private String placeholder;

  /**
   * ctaLabel.
   */
  @ValueMapValue
  @Expose
  private String ctaLabel;

  /**
   * invalid Email Message.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String invalidEmailMessage;

  /**
   * success Message.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String successMessage;

  /**
   * failure Message.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String failureMessage;

  /**
   * apiUrl of the fragment.
   */
  @ValueMapValue
  @Expose
  private String apiUrl;

  @PostConstruct
  public void init() {
    apiUrl = new StringBuilder(saudiTourismConfigs.getMiddlewareDNS())
      .append(saudiTourismConfigs.getMiddlewareNewsLetterEndpoint())
      .toString();
  }
}
