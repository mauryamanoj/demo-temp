package com.saudi.tourism.core.models.components.mediagallery.v1;

import com.saudi.tourism.core.models.components.contentfragment.CFAdapterFactory;
import java.util.List;
import lombok.Setter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
        service = AdapterFactory.class,
        immediate = true,
        configurationPid = "com.saudi.tourism.core.models.components.mediagallery.v1.MediaGalleryCFAdapterFactory",
        property = {
          "adaptables=org.apache.sling.api.resource.Resource",
          "adapters=com.saudi.tourism.core.models.components.mediagallery.v1.MediaGalleryCFModel"
  })
public class MediaGalleryCFAdapterFactory extends CFAdapterFactory<MediaGalleryCFAdapter> {


  /**
   * MediaGalleryCFModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<MediaGalleryCFAdapter> adapters;

  @Override
  protected List<MediaGalleryCFAdapter> getAdapters() {
    return adapters;
  }
}
