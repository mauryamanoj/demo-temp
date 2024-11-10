package com.saudi.tourism.core.models.components.mediagallery.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.saudi.tourism.core.utils.MediaGalleryAwareAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

/** Adapts Event CF to Media Gallery CF Model. */
@Component(service = MediaGalleryCFAdapter.class, immediate = true)
public class MediaGalleryEventCFAdapter
    implements MediaGalleryCFAdapter, ContentFragmentAwareModel, MediaGalleryAwareAdapter {
  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("event", cf.getTemplate().getTitle());
  }

  @Override
  public MediaGalleryCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return null;
    }
    String[] imagesArray = getElementValue(cf, "images", String[].class);
    final var images =
        Optional.ofNullable(imagesArray).map(Arrays::asList).orElse(Collections.emptyList());

    final var gallery =
        images.stream()
            .map(this::createImageBannerFromJsonString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return MediaGalleryCFModel.builder().images(gallery).build();
  }
}
