package com.saudi.tourism.core.models.components;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Class DisclaimersModel.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PlaceholderModel {

  /**
   * The Keys.
   */
  @ChildResource
  @Getter
  private List<PlaceholderKey> keys;

  /**
   * Get PlaceholderKey list, ordered by text ascendant.
   *
   * @return the java.util.List<com.saudi.tourism.core.models.components.PlaceholderKey>
   */
  public List<PlaceholderKey> getKeysOrderedByTextAsc() {
    if (Objects.nonNull(keys) && !keys.isEmpty()) {
      return keys.stream().sorted(Comparator.comparing(PlaceholderKey::getText))
          .collect(Collectors.toList());
    }
    return keys;
  }

}
