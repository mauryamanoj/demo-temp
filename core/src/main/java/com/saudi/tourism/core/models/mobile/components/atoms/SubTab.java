package com.saudi.tourism.core.models.mobile.components.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sub-Tab Model.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubTab {
  /**
   * Id.
   */
  @ValueMapValue
  private Integer id;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Sections Paths.
   */
  @ValueMapValue
  @JsonIgnore
  private List<String> sectionPaths;



  /**
   * Init method.
   */
  @PostConstruct
  protected void init() {
    if (CollectionUtils.isNotEmpty(sectionPaths)) {
      sectionPaths = sectionPaths.stream()
        .map(MobileUtils::extractSectionId)
        .collect(Collectors.toList());
    }
  }
}
