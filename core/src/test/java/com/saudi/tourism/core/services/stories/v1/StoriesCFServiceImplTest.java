package com.saudi.tourism.core.services.stories.v1;

import java.util.Arrays;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.filters.v1.FiltersService;
import com.saudi.tourism.core.services.stories.v1.filters.CategoriesFilter;
import com.saudi.tourism.core.services.stories.v1.filters.DestinationsFilter;
import com.saudi.tourism.core.services.stories.v1.filters.StoriesFiltersChain;
import com.saudi.tourism.core.services.stories.v1.filters.StoriesFiltersChainImpl;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class StoriesCFServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private FragmentTemplate ft;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private SearchResult searchResult;

  @Mock
  private StoriesFiltersChainImpl storiesFiltersChain;

  @Mock
  private RunModeService runModeService;

  @Mock
  private FiltersService filtersService;

  private StoriesCFService service = new StoriesCFServiceImpl();

  @BeforeEach
  public void setUp(final AemContext context) throws InvalidTagFormatException {

    context.load().json("/services/stories-service/story1-root.json", "/content/dam/sauditourism/cf/en/stories/story1");

    context.addModelsForClasses(StoryCFModel.class);

    storiesFiltersChain = new StoriesFiltersChainImpl();
    storiesFiltersChain.setFilters(Arrays.asList(new DestinationsFilter(), new CategoriesFilter()));

    context.registerService(UserService.class, userService);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(AdminSettingsService.class, adminSettingsService);
    context.registerService(QueryBuilder.class, queryBuilder);
    context.registerService(StoriesFiltersChain.class, storiesFiltersChain);
    context.registerService(RunModeService.class, runModeService);
    context.registerService(FiltersService.class, filtersService);
    context.registerInjectActivateService(service);
  }

  @Test
  public void getFilteredStoriesShouldRaiseIfRequestIsNull(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
      service.getFilteredStories(null);
    });


    //Assert
    assertEquals("request is marked non-null but is null", exception.getMessage());
  }

  @Test
  public void getFilteredStoriesShouldFilterAuto(final AemContext aemContext){
    //Arrange
    final var storyResource = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/stories/story1");

    when(saudiTourismConfigs.getStoriesCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/stories");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {storyResource}).iterator());
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/stories/story1").adaptTo(
      ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    Utils.mockContentFragmentElement(spyFragment, "title", "Story 1");
    Utils.mockContentFragmentElement(spyFragment, "destination", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/content/sauditourism/en/stories/story1");
    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/banner.png\",\"s7image\":\"https://scth.scene7.com/is/image/scth/banner.png\",\"alt\":\"Banner Alt\"}",
        "{\"type\":\"video\",\"video\":\"/content/dam/sauditourism/banner.mp4\",\"s7video\":\"/content/dam/sauditourism/banner.mp4\",\"thumbnail\":\"/content/dam/sauditourism/banner.png\",\"location\":\"Riyadh\"}"
      });
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:categories/kayaking"});

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var request = FetchStoriesRequest.builder()
      .locale("en")
      .limit(10)
      .offset(0)
      .build();

    //Act
    final var response = service.getFilteredStories(request);


    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());

    final var story = response.getData().get(0);

    assertEquals("Story 1", story.getTitle());
    assertEquals("story1", story.getId());
    assertEquals("sauditourism:categories/kayaking", story.getCategoriesTags().get(0));
    assertTrue(story.getHideFavorite());
    assertNotNull(response.getPagination());
    assertEquals(1, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }
}