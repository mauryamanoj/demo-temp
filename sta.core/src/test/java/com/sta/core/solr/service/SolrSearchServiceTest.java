package com.sta.core.solr.service;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.search.result.Hit;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.services.UserService;
import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.model.SolrRequest;
import com.sta.core.solr.model.SolrResponse;
import com.sta.core.solr.model.SolrResult;
import com.sta.core.solr.model.SolrSuggestionResponse;
import com.sta.core.solr.services.SolrSearchService;
import com.sta.core.solr.services.SolrServerConfiguration;
import com.sta.core.solr.services.impl.SolrSearchServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SolrSearchServiceTest {

  private static final String CONTENT_PATH_EN = "/content/sauditourism/en/solr";
  private static final String CONTENT_PATH_CF_EVENT = "/content/sauditourism/en/cf/event/solr";
  private static final String CONTENT_PATH_CF = "/content/sauditourism/en/cf/solr";
  private static final String CONTENT_PATH_AR = "/content/sauditourism/ar/solr";
  private static final String CONTENT_PATH_DE = "/content/sauditourism/de/solr";
  private static final String CONTENT_PATH_ES = "/content/sauditourism/es/solr";
  private static final String CONTENT_PATH_FR = "/content/sauditourism/fr/solr";
  private static final String CONTENT_PATH_JA = "/content/sauditourism/ja/solr";
  private static final String CONTENT_PATH_RU = "/content/sauditourism/ru/solr";
  private static final String CONTENT_PATH_ZH = "/content/sauditourism/zh/solr";
  @Mock
  SpellCheckResponse spellCheckResponse;
  private SolrSearchService solrSearchService;
  @Mock
  private SolrServerConfiguration solrConfigurationService;
  @Mock
  private HttpSolrClient httpSolrClient;
  @Mock
  private QueryResponse queryResponse;
  @Mock
  private Hit mockHit;

  private UserService userService;

  @Mock
  private PageManager pageManager;

  @Mock
  private SearchResultsService searchResultsService;

  @Mock
  private SuggesterResponse suggesterResponse;

  @Mock
  private SolrPingResponse solrPingResponse;

  @Mock
  private FragmentTemplate ft;

  @BeforeEach
  private void setUp(final AemContext context)
      throws Exception {

    context.load().json("/solr/content.json", CONTENT_PATH_EN);
    context.load().json("/solr/content-fragment-event-page.json", CONTENT_PATH_CF_EVENT);
    context.load().json("/solr/content-fragment-page.json", CONTENT_PATH_CF);
    context.load().json("/solr/content.json", CONTENT_PATH_AR);
    context.load().json("/solr/content.json", CONTENT_PATH_DE);
    context.load().json("/solr/content.json", CONTENT_PATH_ES);
    context.load().json("/solr/content.json", CONTENT_PATH_FR);
    context.load().json("/solr/content.json", CONTENT_PATH_JA);
    context.load().json("/solr/content.json", CONTENT_PATH_RU);
    context.load().json("/solr/content.json", CONTENT_PATH_ZH);

    context.load().json(
        "/solr/event-cf.json",
        "/content/dam/sauditourism/cf/en/events/event1");
    context.load().json(
        "/solr/cf.json",
        "/content/dam/sauditourism/cf/en/cf");

    ResourceResolver mockResourceResolver = spy(context.resourceResolver());

    TagManager tagManager = mock(TagManager.class);
    Tag tag = mock(Tag.class);
//    when(tag.getTitle(any())).thenReturn("tag title");
//    when(tagManager.resolve(any())).thenReturn(tag);
    lenient().when(mockResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    lenient().when(mockResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    lenient().doNothing().when(mockResourceResolver).close();
    userService = mock(UserService.class);
    solrSearchService = new SolrSearchServiceImpl();

    String[] titles = new String[] {JCR_TITLE, JCR_DESCRIPTION, "cq:tags"};
    String[] subtitles = new String[] {""};
    String[] texts = new String[] {PN_TITLE, "description", "subtitle"};
    String[] cfTitles = new String[] {"title", "subtitle", "authorText", "aboutDescription", "tags"};
    String[] cfTexts = new String[] {"title", "subtitle", "copy", "description", "mainTitle", "text"};

    lenient().when(solrConfigurationService.getTextProperties()).thenReturn(texts);
    lenient().when(solrConfigurationService.getTitleProperties()).thenReturn(titles);
    lenient().when(solrConfigurationService.getSubtitleProperties()).thenReturn(subtitles);
    lenient().when(solrConfigurationService.getCfProperties()).thenReturn(cfTitles);
    lenient().when(solrConfigurationService.getCfTextProperties()).thenReturn(cfTexts);
    lenient().when(solrConfigurationService.getAppSearchPaths()).thenReturn(subtitles);
    lenient().when(solrConfigurationService.getSolrUrl()).thenReturn("");
    lenient().when(solrConfigurationService.getSolrCore()).thenReturn("");
    lenient().when(solrConfigurationService.getMobilePath()).thenReturn("");
    Utils.setInternalState(solrSearchService, "solrConfigurationService", solrConfigurationService);

    lenient().when(userService.getResourceResolver()).thenReturn(mockResourceResolver);
    Utils.setInternalState(solrSearchService, "userService", userService);

    List<SearchResultModel> searchResultModelList = new ArrayList<>();
    SearchResultModel searchResultModel = new SearchResultModel(PN_TITLE, "description", "", "", 20L,
        "", "", new ArrayList<String>());
    searchResultModelList.add(searchResultModel);
    lenient().when(searchResultsService.webSearch(any(), any(), any(), any(), any())).thenReturn(searchResultModelList);
    Utils.setInternalState(solrSearchService, "searchResultsService", searchResultsService);

    lenient().when(httpSolrClient.query(any())).thenReturn(queryResponse);
    lenient().when(httpSolrClient.ping()).thenReturn(solrPingResponse);
    lenient().when(solrPingResponse.getStatus()).thenReturn(0);

    SolrDocumentList solrDocumentList = new SolrDocumentList();
    solrDocumentList.setNumFound(20L);
    solrDocumentList.setStart(0L);

    lenient().when(spellCheckResponse.isCorrectlySpelled()).thenReturn(true);

    lenient().when(queryResponse.getResults()).thenReturn(solrDocumentList);
    lenient().when(queryResponse.getSuggesterResponse()).thenReturn(suggesterResponse);
    lenient().when(queryResponse.getSpellCheckResponse()).thenReturn(spellCheckResponse);
    Map<String, List<String>> suggestedTerms = new HashMap<>();
    suggestedTerms.put("freeTextSuggester", Arrays.asList("suggest1", "suggest2"));
    lenient().when(suggesterResponse.getSuggestedTerms()).thenReturn(suggestedTerms);
    List<SolrArticle> solrArticles = new ArrayList<>();
    SolrArticle solrArticle = new SolrArticle();
    solrArticle.setId(CONTENT_PATH_EN);
    solrArticles.add(solrArticle);
    lenient().when(queryResponse.getBeans(SolrArticle.class)).thenReturn(solrArticles);

    Map<String, HttpSolrClient> httpSolrClientMap = new HashMap<>();
    httpSolrClientMap.put("en", httpSolrClient);
    Utils.setInternalState(solrSearchService, "httpSolrClientMap", httpSolrClientMap);
  }

  @Test
  void solrSearch() {
    when(solrConfigurationService.isUseAEMSearch()).thenReturn(false);
    SolrRequest solrRequest = new SolrRequest();
    solrRequest.setSource("web");
    solrRequest.setLocale("en");
    solrRequest.setQuery("q");
    solrRequest.setLimit(10);
    solrRequest.setOffset(0);
    SolrResponse<SolrResult> solrResponse = solrSearchService.search(solrRequest);
    Assertions.assertEquals(20, solrResponse.getPagination().getTotal());
  }

  @Test
  void oldSearch() {
    when(solrConfigurationService.isUseAEMSearch()).thenReturn(true);
    SolrRequest solrRequest = new SolrRequest();
    solrRequest.setSource("web");
    solrRequest.setLocale("en");
    solrRequest.setQuery("q");
    solrRequest.setLimit(10);
    solrRequest.setOffset(0);
    SolrResponse<SolrResult> solrResponse = solrSearchService.search(solrRequest);
    Assertions.assertEquals(20, solrResponse.getPagination().getTotal());
  }

  @Test
  void suggest() {
    SolrRequest solrRequest = new SolrRequest();
    solrRequest.setSource("web");
    solrRequest.setLocale("en");
    solrRequest.setSuggestion("suggest");
    solrRequest.setLimit(10);
    SolrSuggestionResponse res = solrSearchService.suggest(solrRequest);
    Assertions.assertEquals(2, res.getData().size());
  }


  @Test
  void deletePageFromSolr() throws Exception {
    Assertions.assertTrue(solrSearchService.deletePageFromSolr(CONTENT_PATH_EN));
  }

  @Test
  void solrTest() {
    Assertions.assertTrue(solrSearchService.solrTest("en"));
  }

  @Test
  void getHttpSolrClient() {
    Assertions.assertNotNull(solrSearchService.getHttpSolrClient("ru"));
  }

  @Test
  void indexPageToSolr() throws Exception {
    SolrArticle solrArticle = new SolrArticle();
    solrArticle.setLanguage("en");
    Assertions.assertTrue(solrSearchService.indexPageToSolr(solrArticle));
  }

  @Test
  void createPageMetadataForCFEventPage(final AemContext context) throws Exception {
    context.currentPage(CONTENT_PATH_CF_EVENT);
    context.currentResource(CONTENT_PATH_CF_EVENT);

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "CF title");
    mockContentFragmentElement(spyFragment, "subtitle", "CF subtitle");
    mockContentFragmentElement(spyFragment, "authorText", "CF authorText");
    mockContentFragmentElement(spyFragment, "aboutDescription", "CF aboutDescription");
    mockContentFragmentElement(spyFragment, "tags", "");
    mockContentFragmentElement(spyFragment, "images", "");

    mockContentFragmentElement(spyFragment, "startDate", "2023-11-01T00:00:00.000Z");
    mockContentFragmentElement(spyFragment, "endDate", "2023-11-30T00:00:00.000Z");
    mockContentFragmentElement(spyFragment, "startTime", "09:00");
    mockContentFragmentElement(spyFragment, "endTime", "19:00");
    mockContentFragmentElement(spyFragment, "locationValue", "");

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    SolrArticle solrArticle = solrSearchService.createPageMetadataObject(context.currentResource());

    //Assert
    Assertions.assertNotNull(solrArticle);
    List<String> titles = solrArticle.getTitles();
    Assertions.assertEquals(5, titles.size());
    Assertions.assertEquals("Event 1", titles.get(0));
    Assertions.assertEquals("CF title", titles.get(1));
    Assertions.assertEquals("CF subtitle", titles.get(2));
    Assertions.assertEquals("CF authorText", titles.get(3));
    Assertions.assertEquals("CF aboutDescription", titles.get(4));
    Assertions.assertEquals("2023-11-01T00:00:00.000Z", solrArticle.getStartdate());
    Assertions.assertEquals("2023-11-30T00:00:00.000Z", solrArticle.getEnddate());
    Assertions.assertEquals("09:00", solrArticle.getStarttime());
    Assertions.assertEquals("19:00", solrArticle.getEndtime());

  }

  @Test
  void createPageMetadataForCFPage(final AemContext context) throws Exception {
    context.currentPage(CONTENT_PATH_CF);
    context.currentResource(CONTENT_PATH_CF);

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "CF title");
    mockContentFragmentElement(spyFragment, "subtitle", "CF subtitle");
    mockContentFragmentElement(spyFragment, "authorText", "CF authorText");
    mockContentFragmentElement(spyFragment, "aboutDescription", "CF aboutDescription");
    mockContentFragmentElement(spyFragment, "tags", "");
    mockContentFragmentElement(spyFragment, "image", "/path/to/image");

    when(ft.getTitle()).thenReturn("Destination");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    SolrArticle solrArticle = solrSearchService.createPageMetadataObject(context.currentResource());

    //Assert
    Assertions.assertNotNull(solrArticle);
    List<String> titles = solrArticle.getTitles();
    Assertions.assertEquals(5, titles.size());
    Assertions.assertEquals("Page Title", titles.get(0));
    Assertions.assertEquals("CF title", titles.get(1));
    Assertions.assertEquals("CF subtitle", titles.get(2));
    Assertions.assertEquals("CF authorText", titles.get(3));
    Assertions.assertEquals("CF aboutDescription", titles.get(4));
    Assertions.assertEquals("/path/to/image", solrArticle.getImage());

  }

  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);

    if(value instanceof Calendar) {
      Mockito.lenient().when(elementData.getValue(Calendar.class)).thenReturn((Calendar) value);
    } else {
      Mockito.lenient().when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
    }
  }
}
