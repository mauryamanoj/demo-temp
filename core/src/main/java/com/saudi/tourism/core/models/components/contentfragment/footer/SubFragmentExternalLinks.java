package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SubFragmentExternalLinks {

  /**
   * titleLinkCol3 of the fragment.
   */
  @ValueMapValue(name = "titleLinkCol3")
  @Expose
  private String titleLink;
  /**
   * ctaBusinessList of the fragment.
   */
  @ValueMapValue
  private List<String> ctaBusinessList;

  /**
   * ctaList of the fragment.
   */
  @Expose
  private List<CtaLabel> ctaList = new ArrayList<>();

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * setting service.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  @PostConstruct
  public void init() throws RepositoryException, JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    for (String cta : ctaBusinessList) {
      CtaLabel ctaLabel = mapper.readValue(cta, CtaLabel.class);
      ctaLabel.setCtaLink(LinkUtils.getAuthorPublishUrl(resourceResolver, ctaLabel.getCtaLink(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH)));
      ctaList.add(ctaLabel);
    }
  }
}
