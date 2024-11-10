package com.saudi.tourism.core.models.components.neighborhoodssection.v1;

import java.util.Arrays;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Slf4j
@Component(service = NeighborhoodsSectionCFAdapter.class, immediate = true)
public class NeighborhoodsSectionAttractionCFAdapter implements NeighborhoodsSectionCFAdapter,
    ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("attraction", cf.getTemplate().getTitle());
  }

  public NeighborhoodsSectionCard adaptTo(Adaptable adaptable) {

    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "title", String.class);
    final var image = new Image();
    String[] imagesArray = getElementValue(cf, "images", String[].class);
    if (CollectionUtils.isNotEmpty(Arrays.asList(imagesArray))) {
      for (String jsonString : imagesArray) {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        if (jsonObject != null && "image".equals(jsonObject.get("type").getAsString())) {
          String imagePath = StringUtils.EMPTY;
          if (jsonObject.has("image")) {
            imagePath = jsonObject.get("image").getAsString();
          }
          String s7imagePath = StringUtils.EMPTY;
          if (jsonObject.has("s7image")) {
            s7imagePath = jsonObject.get("s7image").getAsString();
          }
          image.setFileReference(imagePath);
          image.setMobileImageReference(imagePath);
          image.setS7fileReference(s7imagePath);
          image.setS7mobileImageReference(s7imagePath);
          break;
        }
      }
    }
    var pagePath = getElementValue(cf, "pagePath", String.class);


    return NeighborhoodsSectionCard.builder()
      .title(title)
      .image(image)
      .ctaLink(pagePath)
      .build();
  }
}

