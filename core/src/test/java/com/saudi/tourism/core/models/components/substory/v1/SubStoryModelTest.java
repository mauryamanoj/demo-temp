package com.saudi.tourism.core.models.components.substory.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.models.components.about.v1.AboutActivityCFAdapter;
import com.saudi.tourism.core.models.components.about.v1.AboutCFAdapter;
import com.saudi.tourism.core.models.components.about.v1.AboutCFModelAdapterFactory;
import com.saudi.tourism.core.models.components.contentfragment.story.SubStoryCFModel;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SubStoryModelTest {
  @Mock
  private ResourceBundleProvider i18nProvider;
  @Mock
  private SlingSettingsService settingsService;
  @Mock
  private AdapterFactory adapterFactory;
  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  private final ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case I18nConstants.I18_GET_DIRECTIONS:
          return "Get Directions";
        case I18nConstants.I18_KEY_MORE:
          return "more";
        default:
          return "fake_i18n_value";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  public void setUp(final AemContext context) {
    context.registerService(SlingSettingsService.class, settingsService);
    context.addModelsForClasses(SubStoryModel.class, SubStoryCFModel.class);
  }
  @Test
  public void testStoryComponent_fromStoryCF(final AemContext context) {
    context
      .load()
      .json(
        "/components/sub-story/sub-story.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/sub_story");

    context
      .load()
      .json("/components/sub-story/story-cf.json",
        "/content/dam/sauditourism/cf/en/stories/story-2");

    ContentFragment contentFragment =
      context
        .resourceResolver()
        .getResource("/content/dam/sauditourism/cf/en/stories/story-2")
        .adaptTo(ContentFragment.class);
    ContentFragment spyFragment = Mockito.spy(contentFragment);

    // Mocking all elements and their values based on your model:
    Utils.mockContentFragmentElement(spyFragment, "title", "My Story Title");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "About the story");
    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[]{
        "{\"type\":\"video\",\"image\":\"\",\"s7image\":\"\",\"video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"s7video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"thumbnail\":\"/content/dam/sauditourism/saudi-winter.jpg\",\"featureInSubStory\":\"true\"}"
      }
    );

    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var model = context
      .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/sub_story")
      .adaptTo(SubStoryModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, SubStoryModel.class);

    final var cfModel = context
      .currentResource("/content/dam/sauditourism/cf/en/stories/story-2/jcr:content/data/master")
        .adaptTo(SubStoryCFModel.class);

    data.setTitle(cfModel.getTitle());
    data.setDescription(cfModel.getAboutDescription());
    data.setGallery(cfModel.getImageBanners());

    assertNotNull(data);
    assertEquals("My Story Title", data.getTitle());
    assertEquals("About the story", data.getDescription());
    assertEquals("/content/sauditourism/en/about-e-visa", data.getLink().getUrl());
    assertEquals("See All", data.getLink().getText());
    assertTrue(data.getLink().isTargetInNewWindow());

    assertEquals("/content/sauditourism/en/abha-festival", data.getDirectionsLink().getUrl());
    assertTrue(data.getDirectionsLink().isTargetInNewWindow());

    assertEquals("video", data.getGallery().get(0).getType());
    assertEquals("/content/dam/sauditourism/saudi-winter.jpg", data.getGallery().get(0).getThumbnail());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getVideoFileReference());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getS7videoFileReference());
    assertFalse(data.getGallery().get(0).getVideo().isAutoplay());
    assertFalse(data.getGallery().get(0).getVideo().isAutorerun());
  }
}
