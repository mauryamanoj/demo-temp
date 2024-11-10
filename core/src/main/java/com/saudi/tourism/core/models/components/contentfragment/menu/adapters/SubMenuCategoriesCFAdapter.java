package com.saudi.tourism.core.models.components.contentfragment.menu.adapters;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.menu.SubMenuCF;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

/** Adapts Categories CF to SubMenu CF Model. */
@Component(service = SubMenuCFAdapter.class, immediate = true)
public class SubMenuCategoriesCFAdapter implements SubMenuCFAdapter, ContentFragmentAwareModel {
  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("Category", cf.getTemplate().getTitle());
  }

  @Override
  public SubMenuCF adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return null;
    }
    var imagePath = getElementValue(cf, "image", String.class);
    var s7imagePath = getElementValue(cf, "s7image", String.class);
    var title = getElementValue(cf, "title", String.class);

    final var image = new Image();
    image.setFileReference(imagePath);
    image.setMobileImageReference(imagePath);
    image.setS7fileReference(s7imagePath);
    image.setS7mobileImageReference(s7imagePath);
    image.setTransparent(true);

    return SubMenuCF.builder()
      .ctaLabel(title)
      .image(image)
      .build();
  }
}
