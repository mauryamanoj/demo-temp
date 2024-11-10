package com.saudi.tourism.core.models.components.mediagallery.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.*;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MediaGalleryModelTest {
  @Mock private ResourceBundleProvider i18nProvider;

  @Mock private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  private final ResourceBundle i18n =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
          switch (key) {
            case I18nConstants.I18_KEY_VIDEO:
              return "Video";
            case I18nConstants.I18_KEY_IMAGE:
              return "Image";
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
    context.addModelsForClasses(MediaGalleryModel.class, MediaGalleryCFModel.class);

    var eventAdapter = new MediaGalleryEventCFAdapter();
    var activityAdapter =  new MediaGalleryActivityCFAdapter();
    var storyAdapter = new MediaGalleryStoryCFAdapter();
    var attractionAdapter = new MediaGalleryAttractionCFAdapter();
    var tourAdapter = new MediaGalleryTourCFAdapter();
    var adapterFactory = new MediaGalleryCFAdapterFactory();
    adapterFactory.setAdapters(Arrays.asList(eventAdapter, activityAdapter, storyAdapter,attractionAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put(
        "adapters", "com.saudi.tourism.core.models.components.mediagallery.v1.MediaGalleryCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);

    properties = new Hashtable();
    properties.put(
        ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);
  }

  @Test
  public void testMediaGalleryModel_fromEventContentFragment(final AemContext context) {
    // Arrange
    context.addModelsForClasses(
      MediaGalleryModel.class, MediaGalleryCFModel.class, ContentFragment.class);

    context
      .load()
      .json("/components/media-gallery/event-page.json", "/content/sauditourism/en/demo-cf-page");

    context
      .load()
      .json(
        "/components/media-gallery/event-cf.json",
        "/content/dam/sauditourism/cf/en/media-gallery");

    context.currentPage("/content/sauditourism/en/demo-cf-page");

    final var contentFragment =
      context
        .resourceResolver()
        .getResource("/content/dam/sauditourism/cf/en/media-gallery")
        .adaptTo(ContentFragment.class);
    final var spyFragment = spy(contentFragment);

    Utils.mockContentFragmentElement(
        spyFragment,
        "images",
        new String[] {
          "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/favicon.png\",\"s7image\":\"/content/dam/sauditourism/favicon.png\",\"alt\":\"favicon image\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Mecca\"}",
          "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/healthcare ministry.png\",\"s7image\":\"/content/dam/sauditourism/healthcare ministry.png\",\"alt\":\"healthcare\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Al Madina\"}",
          "{\"type\":\"video\",\"image\":\"\",\"s7image\":\"\",\"alt\":\"\",\"video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"s7video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"thumbnail\":\"/content/dam/sauditourism/healthcare ministry.png\",\"location\":\"Jeddah\"}",
          "{\"type\":\"video\",\"image\":\"\",\"s7image\":\"\",\"alt\":\"\",\"video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"s7video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"thumbnail\":\"/content/dam/sauditourism/healthcare ministry.png\",\"location\":\"Al Riad\"}",
        });

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context.currentResource().adaptTo(MediaGalleryModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MediaGalleryModel.class);

    // Assert
    assertNotNull(data);
    assertEquals(4, data.getGallery().size());
    assertEquals("+1 more", data.getMoreLabel());

    assertEquals("video", data.getGallery().get(2).getType());
    assertEquals("Jeddah", data.getGallery().get(2).getLocation());
    assertEquals("Video", data.getGallery().get(2).getTypeLabel());
    assertEquals("/content/dam/sauditourism/healthcare ministry.png", data.getGallery().get(2).getThumbnail());
    assertNull(data.getGallery().get(2).getImage());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(2).getVideo().getVideoFileReference());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(2).getVideo().getS7videoFileReference());

    assertEquals("image", data.getGallery().get(0).getType());
    assertEquals("Mecca", data.getGallery().get(0).getLocation());
    assertEquals("Image", data.getGallery().get(0).getTypeLabel());
    assertEquals(null, data.getGallery().get(0).getThumbnail());
    assertNull(data.getGallery().get(0).getVideo());
    assertEquals("/content/dam/sauditourism/favicon.png", data.getGallery().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/favicon.png", data.getGallery().get(0).getImage().getS7fileReference());
  }

  @Test
  public void testMediaGalleryModel_fromActivityContentFragment(final AemContext context) {
    // Arrange
    context.addModelsForClasses(
      MediaGalleryModel.class, MediaGalleryCFModel.class, ContentFragment.class);

    context
      .load()
      .json("/components/media-gallery/activity-page.json", "/content/sauditourism/en/test/sprint6/menu-demo/ismail-s-activity/december-activity");

    context
      .load()
      .json(
        "/components/media-gallery/activity-cf.json", "/content/dam/sauditourism/cf/en/activities/activity1");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/ismail-s-activity/december-activity");

    final var contentFragment =
      context
        .resourceResolver()
        .getResource("/content/dam/sauditourism/cf/en/activities/activity1")
        .adaptTo(ContentFragment.class);
    final var spyFragment = spy(contentFragment);

    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/horsing-hobby.jpg\",\"s7image\":\"/content/dam/sauditourism/horsing-hobby.jpg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Najrane\",\"featureInSubStory\":\"\"}"      });

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context.currentResource().adaptTo(MediaGalleryModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MediaGalleryModel.class);

    // Assert
    assertNotNull(data);
    assertEquals(1, data.getGallery().size());
    assertEquals("", data.getMoreLabel());

    assertEquals("image", data.getGallery().get(0).getType());
    assertEquals("Najrane", data.getGallery().get(0).getLocation());
    assertEquals("Image", data.getGallery().get(0).getTypeLabel());
    assertNull(data.getGallery().get(0).getThumbnail());
    assertNull(data.getGallery().get(0).getVideo());
    assertEquals("/content/dam/sauditourism/horsing-hobby.jpg", data.getGallery().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/horsing-hobby.jpg", data.getGallery().get(0).getImage().getS7fileReference());
  }

  @Test
  public void testMediaGalleryModel_fromStoryContentFragment(final AemContext context) {
    // Arrange
    context.addModelsForClasses(
      MediaGalleryModel.class, MediaGalleryCFModel.class, ContentFragment.class);

    context
      .load()
      .json("/components/media-gallery/story-page.json", "/content/sauditourism/en/test/sprint6/menu-demo/ismail-s-story/december-story");

    context
      .load()
      .json(
        "/components/media-gallery/story-cf.json", "/content/dam/sauditourism/cf/en/stories/story1");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/ismail-s-story/december-story");

    final var contentFragment =
      context
        .resourceResolver()
        .getResource("/content/dam/sauditourism/cf/en/stories/story1")
        .adaptTo(ContentFragment.class);
    final var spyFragment = spy(contentFragment);

    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/horsing-hobby.jpg\",\"s7image\":\"/content/dam/sauditourism/horsing-hobby.jpg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Najrane\",\"featureInSubStory\":\"\"}"      });

    when(ft.getTitle()).thenReturn("story");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context.currentResource().adaptTo(MediaGalleryModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MediaGalleryModel.class);

    // Assert
    assertNotNull(data);
    assertEquals(1, data.getGallery().size());
    assertEquals("", data.getMoreLabel());

    assertEquals("image", data.getGallery().get(0).getType());
    assertEquals("Najrane", data.getGallery().get(0).getLocation());
    assertEquals("Image", data.getGallery().get(0).getTypeLabel());
    assertNull(data.getGallery().get(0).getThumbnail());
    assertNull(data.getGallery().get(0).getVideo());
    assertEquals("/content/dam/sauditourism/horsing-hobby.jpg", data.getGallery().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/horsing-hobby.jpg", data.getGallery().get(0).getImage().getS7fileReference());
  }
  @Test
  public void testMediaGalleryModel_fromAttractionContentFragment(final AemContext context) {
    // Arrange
    context.addModelsForClasses(
      MediaGalleryModel.class, MediaGalleryCFModel.class, ContentFragment.class);

    context
      .load()
      .json("/components/media-gallery/attraction-page.json", "/content/sauditourism/en/test/sprint6/menu-demo/attractions/jedda-bob-attraction");

    context
      .load()
      .json(
        "/components/media-gallery/attraction-cf.json", "/content/dam/sauditourism/cf/en/attractions/jeddah-bobattraction");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/attractions/jedda-bob-attraction");

    final var contentFragment =
      context
        .resourceResolver()
        .getResource("/content/dam/sauditourism/cf/en/attractions/jeddah-bobattraction")
        .adaptTo(ContentFragment.class);
    final var spyFragment = spy(contentFragment);

    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"video\",\"image\":\"\",\"s7image\":\"\",\"video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"s7video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"thumbnail\":\"/content/dam/we-retail/en/activities/running/running-desert-woman.jpg\",\"location\":\"Riyadh\",\"featureInSubStory\":\"\"}",
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/Jeddah-Season-Festival-1-840x450.jpg\",\"s7image\":\"/content/dam/sauditourism/Jeddah-Season-Festival-1-840x450.jpg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Abha\",\"featureInSubStory\":\"\"}",
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/saudi-culture.jpeg\",\"s7image\":\"/content/dam/sauditourism/saudi-culture.jpeg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Najrane\",\"featureInSubStory\":\"\"}"      });

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context.currentResource().adaptTo(MediaGalleryModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MediaGalleryModel.class);

    // Assert
    assertNotNull(data);
    assertEquals(3, data.getGallery().size());
    assertEquals("", data.getMoreLabel());

    assertEquals("video", data.getGallery().get(0).getType());
    assertEquals("Riyadh", data.getGallery().get(0).getLocation());
    assertEquals("Video", data.getGallery().get(0).getTypeLabel());
    assertEquals("/content/dam/we-retail/en/activities/running/running-desert-woman.jpg", data.getGallery().get(0).getThumbnail());
    assertNotNull(data.getGallery().get(0).getVideo());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getVideoFileReference());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getS7videoFileReference());
  }

  @Test
  public void testMediaGalleryModel_fromTourContentFragment(final AemContext context) {
    // Arrange
    context.addModelsForClasses(
      MediaGalleryModel.class, MediaGalleryCFModel.class, ContentFragment.class);

    context
      .load()
      .json(
        "/components/media-gallery/tour-page.json",
        "/content/sauditourism/en/test/sprint6/menu-demo/first-tour");

    context
      .load()
      .json(
        "/components/media-gallery/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/first-tour");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context.currentResource("/content/sauditourism/en/test/sprint6/menu-demo/first-tour/jcr:content/root");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/first-tour").adaptTo(ContentFragment.class);
    final var spyFragment = spy(contentFragment);

    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"video\",\"image\":\"\",\"s7image\":\"\",\"video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"s7video\":\"/content/dam/sauditourism/Journey through time.mp4\",\"thumbnail\":\"/content/dam/we-retail/en/activities/running/running-desert-woman.jpg\",\"location\":\"Riyadh\",\"featureInSubStory\":\"\"}",
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/Jeddah-Season-Festival-1-840x450.jpg\",\"s7image\":\"/content/dam/sauditourism/Jeddah-Season-Festival-1-840x450.jpg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Abha\",\"featureInSubStory\":\"\"}",
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/saudi-culture.jpeg\",\"s7image\":\"/content/dam/sauditourism/saudi-culture.jpeg\",\"video\":\"\",\"s7video\":\"\",\"thumbnail\":\"\",\"location\":\"Najrane\",\"featureInSubStory\":\"\"}"      });

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context.currentResource().adaptTo(MediaGalleryModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MediaGalleryModel.class);

    // Assert
    assertNotNull(data);
    assertEquals(3, data.getGallery().size());
    assertEquals("", data.getMoreLabel());

    assertEquals("video", data.getGallery().get(0).getType());
    assertEquals("Riyadh", data.getGallery().get(0).getLocation());
    assertEquals("Video", data.getGallery().get(0).getTypeLabel());
    assertEquals("/content/dam/we-retail/en/activities/running/running-desert-woman.jpg", data.getGallery().get(0).getThumbnail());
    assertNotNull(data.getGallery().get(0).getVideo());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getVideoFileReference());
    assertEquals("/content/dam/sauditourism/Journey through time.mp4", data.getGallery().get(0).getVideo().getS7videoFileReference());
  }
}
