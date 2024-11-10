
package com.saudi.tourism.core.models.components.contentfragment.story;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class StoryCFModelTest {

  @Mock
  private RunModeService runModeService;

  @Mock
  private SlingSettingsService settingsService;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.registerService(SlingSettingsService.class, settingsService);
    context.registerService(RunModeService.class, runModeService);
    context.addModelsForClasses(StoryCFModel.class);
  }

  @Test
  void getStoryCFModel(final AemContext aemContext) {
    //Arrange
    aemContext
      .load()
      .json(
        "/components/cf/story/story1.json",
        "/content/dam/sauditourism/cf/en/story");

    ContentFragment contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/story").adaptTo(ContentFragment.class);
    ContentFragment spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "title", "My Story Title");
    Utils.mockContentFragmentElement(spyFragment, "subtitle", "My Story Subtitle");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "About the story");
    Utils.mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", false);
    Utils.mockContentFragmentElement(spyFragment, "lat", 34.0522);
    Utils.mockContentFragmentElement(spyFragment, "lng", -118.2437);
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/content/my-story-page-path");
    Utils.mockContentFragmentElement(spyFragment, "images", new String[]{"{\"type\":\"image\",\"image\":\"/content/dam/my-story-image-path.jpg\",\"s7image\":\"/content/dam/s7-my-story-image-path.jpg\"}"});
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:story/natural-site"});

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(runModeService.isPublishRunMode()).thenReturn(true);

    //Act
    final var model =
        aemContext
            .currentResource("/content/dam/sauditourism/cf/en/story")
            .adaptTo(StoryCFModel.class);

    //Assert
    assertNotNull(model);
    // Validate each field in the model
    assertEquals("My Story Title", model.getTitle());
    assertEquals(Arrays.asList("Natural Site"), model.getTags());
    assertEquals("My Story Subtitle", model.getSubtitle());
    assertEquals("About the story", model.getAboutDescription());
    assertEquals("About Title", model.getAboutTitle());
    assertFalse(model.isHideFavorite());
    assertEquals(34.0522, model.getLatitude());
    assertEquals(-118.2437, model.getLongitude());

    // Validate Images model object.
    assertNotNull(model.getImageBanners());
    assertEquals("/content/dam/my-story-image-path.jpg", model.getImageBanners().get(0).getImageBanner().getFileReference());
    assertEquals("/content/dam/my-story-image-path.jpg", model.getImageBanners().get(0).getImageBanner().getMobileImageReference());
    assertEquals("/content/dam/s7-my-story-image-path.jpg", model.getImageBanners().get(0).getImageBanner().getS7fileReference());
    assertEquals("/content/dam/s7-my-story-image-path.jpg", model.getImageBanners().get(0).getImageBanner().getS7mobileImageReference());
    assertEquals("image", model.getImageBanners().get(0).getType());
    Calendar publishedDate = model.getPublishedDate();
    publishedDate.setTimeZone(TimeZone.getTimeZone("CET"));
    // Format the calendar's time to a String
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getTimeZone("CET"));
    String formattedPublishedDate = sdf.format(publishedDate.getTime());
    assertEquals("Tue Oct 03 16:59:12 CEST 2023", formattedPublishedDate);

    // Validate Link object.
    assertNotNull(model.getLink());
    assertEquals("/content/my-story-page-path", model.getLink().getUrl());
  }

  // Mock a ContentFragment element with the given name and value.
  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);
    Mockito.when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
  }

}
