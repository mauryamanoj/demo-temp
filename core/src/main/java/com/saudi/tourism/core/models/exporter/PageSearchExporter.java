package com.saudi.tourism.core.models.exporter;


import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

/**
 * The Saudi Content Page Sling Model Exporter.
 */
@Model(adaptables = Resource.class,
       resourceType = {Constants.PAGE_RESOURCE_TYPE},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson",
          selector = "search",
          extensions = "json",
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@Slf4j
public class PageSearchExporter extends PageFavoritesExporter {


}
