package com.saudi.tourism.core.models.components.specialshowwidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Special show Widget Model.
 */
@Model(
    adaptables = {Resource.class, ContentFragment.class},
    adapters = SpecialShowWidgetCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class SpecialShowWidgetCFModel implements ContentFragmentAwareModel {

  /**
   * Event ContentFragment Model.
   */
  @Self
  private transient ContentFragment contentFragment;

  /**
   * List of SpecialShowItem.
   */
  @Expose
  private List<SpecialShowItem> data = new ArrayList<>();

  /**
   * specialShowLabel.
   */
  @Expose
  private String title;

  @PostConstruct
  void init() {
    title = getElementValue(contentFragment, "specialShowLabel", String.class);
    String[] specialShowArray = getElementValue(contentFragment, "specialShowDetails", String[].class);
    final var specialShows =
        Optional.ofNullable(specialShowArray).map(Arrays::asList).orElse(Collections.emptyList());

    ObjectMapper mapper = new ObjectMapper();
    data = specialShows.stream()
        .map(specialShowJson -> {
          try {
            SpecialShowItem item = mapper.readValue(specialShowJson, SpecialShowItem.class);

            return item;
          } catch (JsonProcessingException e) {
            LOGGER.warn("Error while parsing specialShow", e);
            return null;
          }
        })
        .filter(Objects::nonNull).collect(Collectors.toList());

  }

}
