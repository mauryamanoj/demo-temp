package com.saudi.tourism.core;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.saudi.tourism.core.utils.Constants.FEATURE_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;

/**
 * Component that push selected image to the page featureImage.
 */
@Component(service = SlingPostProcessor.class, immediate = true)
public class HeroFeatureImagePostProcessor implements SlingPostProcessor {

  /**
   * Set of hero resource types.
   */
  private static final Set<String> HERO_RESOURCE_TYPE_SET = new HashSet<>();
  static {
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h01-brand-page-hero/v1/h01-brand-page-hero");
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h01-brand-page-hero/v2/h01-brand-page-hero");
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h02-secondary-hero/v1/h02-secondary-hero");
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h03-detail-hero/v1/h03-detail-hero");
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h03-detail-hero/v2/h03-detail-hero");
    HERO_RESOURCE_TYPE_SET
        .add("sauditourism/components/content/h04-event-page-hero/v1/h04-event-page-hero");
  }

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  @Override public void process(final SlingHttpServletRequest request,
      final List<Modification> list) throws Exception {
    Resource resource = request.getResource();
    String resourceType =
        Optional.ofNullable(request.getRequestParameter("./sling:resourceType"))
        .map(RequestParameter::getString).orElse(null);
    if (!HERO_RESOURCE_TYPE_SET.contains(resourceType)) {
      return;
    }

    String fileReference =
        Optional.ofNullable(request.getRequestParameter("./image/fileReference"))
        .map(RequestParameter::getString).orElse(null);
    if (Objects.nonNull(fileReference)) {
      ResourceResolver resolver = request.getResourceResolver();
      Optional.ofNullable(resolver.adaptTo(PageManager.class))
          .map(pm -> pm.getContainingPage(resource)).map(Page::getContentResource)
          .ifPresent(cr -> {
            String pageResourceType = cr.getResourceType();
            String destinationField = FEATURE_IMAGE;
            if ("sauditourism/components/structure/event-detail-page".equals(pageResourceType)) {
              destinationField = PN_FEATURE_EVENT_IMAGE;
            }
            ModifiableValueMap mvm = cr.adaptTo(ModifiableValueMap.class);
            if (Objects.isNull(mvm)) {
              return;
            }
            mvm.put(destinationField, fileReference);
            Resource imageResource = resolver.getResource(fileReference);
            if (DamUtil.isAsset(imageResource)) {
              Asset asset = DamUtil.getAssets(imageResource).next();
              String s7featureImage =  DynamicMediaUtils
                  .getScene7Path(asset, saudiTourismConfig.getScene7Domain());
              if (Objects.nonNull(s7featureImage)) {
                mvm.put("s7" + destinationField, s7featureImage);
              }
            }
          });
    }
  }
}
