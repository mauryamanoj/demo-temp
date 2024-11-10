package com.saudi.tourism.core.models.app.contact;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Telephone Provider model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Telephone {

  /**
   * header.
   */
  @ValueMapValue
  @Getter
  private String header;

  /**
   * Telephone provider List.
   */
  @Getter
  @ChildResource
  private List<TelephoneProviderDetail> telephoneProvider;
}
