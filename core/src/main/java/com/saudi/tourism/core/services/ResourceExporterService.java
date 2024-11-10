package com.saudi.tourism.core.services;

import lombok.NonNull;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service to export resource via ModelExporter.
 */
public interface ResourceExporterService {

  /**
   * Get resource's json representation.
   * @param resourceResolver resource resolver
   * @param path path
   * @return exported string
   */
  String exportResource(@NonNull ResourceResolver resourceResolver, @NonNull String path);

  /**
   * Get model for resource. Page nodes are adapted through inner contentResource nodes.
   * @param resourceResolver resource resolver
   * @param path path
   * @return model
   */
  Object getModel(@NonNull ResourceResolver resourceResolver, @NonNull String path);

}
