package com.saudi.tourism.core.services.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ExportException;
import org.apache.sling.models.factory.MissingExporterException;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Optional;

/**
 * Component to get resource's corresponding model and exported json value.
 */
@Component(service = ResourceExporterService.class)
@Slf4j
public class ResourceExporterServiceImpl implements ResourceExporterService {

  /**
   * Sling model factory implementation.
   */
  @Reference
  private ModelFactory modelFactory;

  /**
   * Get resource's exported value in string format.
   * @param resourceResolver resource resolver
   * @param path path
   * @return exported string
   */
  public String exportResource(@NonNull ResourceResolver resourceResolver, @NonNull String path) {
    Object model = getModel(resourceResolver, path);
    if (model != null) {
      try {
        return modelFactory.exportModel(model, ExporterConstants.SLING_MODEL_EXPORTER_NAME,
            String.class, new HashMap<>());
      } catch (ExportException | MissingExporterException e) {
        LOGGER.error("Couldn't export resource {}", path, e);
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * Find corresponding model for resource. Pages' models are searched by their inner content
   * resource nodes.
   * @param resourceResolver resource resolver
   * @param path path
   * @return model
   */
  public Object getModel(@NonNull ResourceResolver resourceResolver, @NonNull String path) {
    String absolutePath = CommonUtils.toAbsolutePath(path);
    Resource resource = Optional.ofNullable(resourceResolver.adaptTo(PageManager.class))
        .map(pageManager -> pageManager.getPage(absolutePath))
        .map(Page::getContentResource)
        .orElse(resourceResolver.getResource(absolutePath));

    return Optional.ofNullable(resource)
        .map(modelFactory::getModelFromResource)
        .orElse(null);
  }


}
