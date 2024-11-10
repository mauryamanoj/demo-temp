package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;


@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class FragmentFooter {
  /**
   * socialMediaIcons of the fragment.
   */
  @ValueMapValue
  private List<String> socialMediaIcons = new ArrayList<>();


  /**
   * The Resource.
   */
  @Inject
  @Setter
  private Resource currentResource;
  @PostConstruct
  public void init() throws RepositoryException, JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    for (String cta : socialMediaIcons) {
      CtaIcon ctaIcon = mapper.readValue(cta, CtaIcon.class);
      ctaListIcon.add(ctaIcon);
    }
  }
  /**
   * SubFragmentNewsletter of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentNewsletter fragmentNewsletter;

  /**
   * SubFragmentDownloads of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentDownloads fragmentDownloads;


  /**
   * SubFragmentGroup of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentGroup fragmentGroup;

  /**
   * SubFragmentBranding of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentBranding fragmentBranding;

  /**
   * SubFragmentInternalLinks of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentInternalLinks fragmentInternalLinks;

  /**
   * SubFragmentExternalLinks of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentExternalLinks fragmentExternalLinks;

  /**
   * SubFragmentContact of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentContact fragmentContact;

  /**
   * ctaListIcon of the fragment.
   */
  @Expose
  private List<CtaIcon> ctaListIcon = new ArrayList<>();

  /**
   * SubFragmentContainer of the fragment.
   */
  @Getter
  @Setter
  @Expose
  private SubFragmentContainer fragmentContainer;
}
