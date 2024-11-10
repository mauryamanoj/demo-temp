package com.saudi.tourism.core.models.components.contentfragment.menu.adapters;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.menu.SubMenuCF;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/** Adapts Activities CF to SubMenu CF Model. */
@Component(service = SubMenuCFAdapter.class, immediate = true)
public class SubMenuActivitiesCFAdapter implements SubMenuCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("activity", cf.getTemplate().getTitle());
  }

  @Override
  public SubMenuCF adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return null;
    }

    String[] imagesArray = getElementValue(cf, "images", String[].class);
    final var images =
        Optional.ofNullable(imagesArray).map(Arrays::asList).orElse(Collections.emptyList());

    var firstImage =
        images.stream()
            .filter(
                jsonString -> {
                  JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                  return jsonObject != null && "image".equals(jsonObject.get("type").getAsString());
                })
            .findFirst()
            .orElse(null);

    var imagePath = StringUtils.EMPTY;
    var s7imagePath = StringUtils.EMPTY;
    final var image = new Image();

    if (StringUtils.isNotEmpty(firstImage)) {
      final var imageJson = JsonParser.parseString(firstImage).getAsJsonObject();
      if (imageJson.has("image")) {
        imagePath = imageJson.get("image").getAsString();
      }
      if (imageJson.has("s7image")) {
        s7imagePath = imageJson.get("s7image").getAsString();
      }
      image.setFileReference(imagePath);
      image.setMobileImageReference(imagePath);
      image.setS7fileReference(s7imagePath);
      image.setS7mobileImageReference(s7imagePath);
      image.setTransparent(true);
    }

    var title = getElementValue(cf, "title", String.class);

    return SubMenuCF.builder().ctaLabel(title).image(image).build();
  }
}
