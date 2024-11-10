package com.saudi.tourism.core.models.components.about.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.models.components.contentfragment.category.CategoryCFModel;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AboutSectionModelTest {
  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;
  @Mock
  private RunModeService runModeService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private FragmentTemplate ft;
  @Mock
  private TagManager tagManager;
  @Mock
  private Tag tag;
  @Mock
  private FavoritesApiEndpoints favoritesApiEndpoints;

  private final Gson gson = new GsonBuilder().create();

  private final ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case I18nConstants.I18_KEY_READ_MORE:
          return "Read More";
        case I18nConstants.I18_KEY_READ_LESS:
          return "Read Less";
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
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(RunModeService.class, runModeService);
    context.addModelsForClasses(AboutSectionModel.class, CategoryCFModel.class, AboutCFModel.class,FavoritesApiEndpointsModel.class);
    Mockito.when(saudiTourismConfigs.getEnableFavorite() ).thenReturn(true);

    var activityAdapter = new AboutActivityCFAdapter();
    var attractionAdapter = new AboutAttractionCFAdapter();
    var eventAdapter = new AboutEventCFAdapter();
    var storyAdapter = new AboutStoryCFAdapter();
    var tourAdapter = new AboutTourCFAdapter();
    var holidayAdapter = new AboutHolidayCFAdapter();
    var destinationAdapter = new AboutDestinationCFAdapter();

    var adapterFactory = new AboutCFModelAdapterFactory();
    List<AboutCFAdapter> adapters = new ArrayList<>();
    adapters.add(activityAdapter);
    adapters.add(attractionAdapter);
    adapters.add(eventAdapter);
    adapters.add(storyAdapter);
    adapters.add(holidayAdapter);
    adapters.add(destinationAdapter);
    adapters.add(tourAdapter);

    adapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.about.v1.AboutCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);

    properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    context.registerService(TagManager.class, tagManager);

    context
      .load()
      .json(
        "/components/about-section/food-cf.json",
        "/content/dam/sauditourism/cf/en/categories/food");
  }

  @Test
  void aboutSectionModelManualAuthoringEnabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-authoring-enabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");
    // Act
    final var model = context
        .currentResource(
            "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section")
        .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertTrue(data.getEnableManualAuthoring());
    assertEquals("About", data.getAboutTitle());
    assertEquals("freestar freestar Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse fermentum consectetur urna, et vulputate odio consectetur eu. Donec vulputate risus id faucibus elementum. Nulla convallis magna vel urna iaculis, consectetur vestibulum lorem volutpat. Praesent mauris nulla, fringilla interdum massa sed, interdum tempus metus. Praesent convallis eu leo in condimentum. Nullam pulvinar et tortor vel gravida. Vivamus sodales mi ut orci tristique maximus. Donec rhoncus libero vitae neque lacinia aliquet.",
        data.getAboutDescription());
    assertEquals("categories_xs_shopping", data.getFavoriteIcon());
    assertEquals("categories_xs_culture", data.getShareIcon());
    assertFalse(data.getHideReadMore());
    assertEquals("Read more", data.getReadMoreLabel());
    assertEquals("Read less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertTrue(data.getHideShare());


    assertNotNull(data.getCategoriesTags());
    assertEquals("Food", data.getCategoriesTags().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", data.getCategoriesTags().get(0).getIcon());
  }

  @Test
  void aboutSectionModelManualAuthoringDisabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "hideFavorite", false);

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
  }


  @Test
  void aboutAttractionSectionModelManualAuthoringEnabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-authoring-enabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/food_and_drink/restaurant/sauditourism:demo");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section")
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertTrue(data.getEnableManualAuthoring());
    assertEquals("About", data.getAboutTitle());
    assertEquals("freestar freestar Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse fermentum consectetur urna, et vulputate odio consectetur eu. Donec vulputate risus id faucibus elementum. Nulla convallis magna vel urna iaculis, consectetur vestibulum lorem volutpat. Praesent mauris nulla, fringilla interdum massa sed, interdum tempus metus. Praesent convallis eu leo in condimentum. Nullam pulvinar et tortor vel gravida. Vivamus sodales mi ut orci tristique maximus. Donec rhoncus libero vitae neque lacinia aliquet.",
      data.getAboutDescription());
    assertEquals("categories_xs_shopping", data.getFavoriteIcon());
    assertEquals("categories_xs_culture", data.getShareIcon());
    assertFalse(data.getHideReadMore());
    assertEquals("Read more", data.getReadMoreLabel());
    assertEquals("Read less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertTrue(data.getHideShare());

    assertNotNull(data.getCategoriesTags());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/food_and_drink/restaurant/sauditourism-demo.svg", data.getCategoriesTags().get(0).getIcon());
  }

  @Test
  void aboutAttractionSectionModelManualAuthoringDisabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "hideFavorite", false);

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertFalse(data.getHideShare());


  }
  @Test
  void aboutSectionModelManualAuthoringDisabledActivityCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-activity-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activity/activity1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activity/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "hideFavorite", false);
    mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
  }
  @Test
  void aboutSectionModelManualAuthoringDisabledEventCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-event-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/event-cf.json",
        "/content/dam/sauditourism/cf/en/event/event1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/event/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "hideFavorite", false);

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
  }
  @Test
  void aboutSectionModelManualAuthoringDisabledStoryCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-story-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/story-cf.json",
        "/content/dam/sauditourism/cf/en/story/story1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");
    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/story/story1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});
    mockContentFragmentElement(spyFragment, "hideFavorite", false);
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");

    when(ft.getTitle()).thenReturn("story");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertFalse(data.getHideShare());


    assertNotNull(data.getCategoriesTags());
    assertEquals("Food", data.getCategoriesTags().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", data.getCategoriesTags().get(0).getIcon());

}
  @Test
  void aboutSectionModelManualAuthoringDisabledHolidayCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-holiday-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/holiday-cf.json",
        "/content/dam/sauditourism/cf/en/holiday/holiday1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");
    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/holiday/holiday1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});
    mockContentFragmentElement(spyFragment, "hideFavorite", false);
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");

    when(ft.getTitle()).thenReturn("holiday");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertFalse(data.getHideShare());


    assertNotNull(data.getCategoriesTags());
    assertEquals("Food", data.getCategoriesTags().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", data.getCategoriesTags().get(0).getIcon());
  }

  @Test
  void aboutSectionModelManualAuthoringDisabledDestinationCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/destination-page-destination-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/about-section/destination-cf.json",
        "/content/dam/sauditourism/cf/en/destination/destination1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/about_section");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");
    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/destination/destination1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});
    mockContentFragmentElement(spyFragment, "hideFavorite", false);
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");

    when(ft.getTitle()).thenReturn("destination");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
    assertFalse(data.getHideShare());


    assertNotNull(data.getCategoriesTags());
    assertEquals("Food", data.getCategoriesTags().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", data.getCategoriesTags().get(0).getIcon());
  }

  @Test
  void aboutSectionModelManualAuthoringDisabledTourCFM(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/about-section/tour-page-authoring-disabled.json",
        "/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context
      .load()
      .json(
        "/components/about-section/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/first-tour");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context.currentResource("/content/sauditourism/en/test/sprint6/menu-demo/first-tour/jcr:content/root");
    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/first-tour").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "aboutDescription", "About the destination");
    mockContentFragmentElement(spyFragment, "aboutTitle", "About Title");
    mockContentFragmentElement(spyFragment, "readMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "readLessLabel", "Read Less");

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(AboutSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AboutSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("About Title", data.getAboutTitle());
    assertEquals("About the destination", data.getAboutDescription());
    assertFalse(data.getHideReadMore());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertFalse(data.getHideFavorite());
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