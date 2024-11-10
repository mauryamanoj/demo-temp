package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.ArticleDetail;
import com.saudi.tourism.core.models.components.cards.CardDetail;
import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.CardsService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CardsServiceImplTest {

  private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

  @Mock
  private Cache memCache;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private SearchResult searchResult;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private CardsService cardsService;

  @BeforeEach
  void setUp() throws Exception {
    // AEM
    aemContext.registerService(Cache.class, memCache);
    aemContext.registerService(QueryBuilder.class, queryBuilder);

    final Dictionary i18nBundleProps = new Hashtable();
    i18nBundleProps.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, i18nBundleProps);

    final Map<String, Object> cardsServiceProps = new HashMap<>();
    cardsServiceProps.put("service", CardsService.class);
    cardsServiceProps.put("immediate", true);
    cardsService = new CardsServiceImpl();
    aemContext.registerInjectActivateService(cardsService, cardsServiceProps);

    aemContext.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);

    aemContext.addModelsForClasses(CardDetail.class, ArticleDetail.class);

    aemContext
        .load()
        .json(
            "/services/cards-service/top-attractions.json",
            "/content/sauditourism/en/Configs/articles/top-attractions");

    // Mock
    when(memCache.get(any())).thenReturn(null);
    when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class)))
        .thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getNodes()).thenReturn(getNodeIterator());

    AdminUtil.setAdminSettingsService(adminSettingsService);
  }

  @Test
  public void shouldReturnAllCardsWhenAllIsTrueAndNotCityFilter() throws RepositoryException {
    // Arrange
    CardsRequestParams cardsRequestParams = new CardsRequestParams();
    cardsRequestParams.setType("top-attractions");
    cardsRequestParams.setAll(true);
    cardsRequestParams.setLocale("en");

    // Act
    final CardsListModel cardsListModel =
        cardsService.getFilteredCards(aemContext.request(), cardsRequestParams, "v1");

    // Assert
    assertNotNull(cardsListModel);
    assertEquals(24, cardsListModel.getData().size());
    assertEquals(0, cardsListModel.getPagination().getLimit());
    assertEquals(0, cardsListModel.getPagination().getOffset());
    assertEquals(24, cardsListModel.getPagination().getTotal());
    assertFalse(cardsListModel.isHideFilters());
  }

  @Test
  public void shouldReturnFiltredCardsWhenAllIsTrueAndCityFilter() throws RepositoryException {
    // Arrange
    CardsRequestParams cardsRequestParams = new CardsRequestParams();
    cardsRequestParams.setCity(Arrays.asList("aseer"));
    cardsRequestParams.setAll(true);
    cardsRequestParams.setLocale("en");

    // Act
    final CardsListModel cardsListModel =
        cardsService.getFilteredCards(aemContext.request(), cardsRequestParams, "v1");

    // Assert
    assertNotNull(cardsListModel);
    assertEquals(1, cardsListModel.getData().size());
    assertEquals(0, cardsListModel.getPagination().getLimit());
    assertEquals(0, cardsListModel.getPagination().getOffset());
    assertEquals(1, cardsListModel.getPagination().getTotal());
    assertFalse(cardsListModel.isHideFilters());

    assertEquals("aseer", cardsListModel.getData().get(0).getCity().get(0).getId());
    assertEquals("aseer", cardsListModel.getData().get(0).getCardCities().get(0));
    assertEquals("/content/sauditourism/en/Configs/articles/top-attractions/al-shallal-park-aseer",
        cardsListModel.getData().get(0).getPath());
  }

  private Iterator<Node> getNodeIterator() throws RepositoryException {
    final List<Node> nodes = new ArrayList<>();
    final ResourceResolver resourceResolver = aemContext.resourceResolver();
    final Session session = resourceResolver.adaptTo(Session.class);
    final Node root = session.getNode("/content/sauditourism/en/Configs/articles/top-attractions");
    final NodeIterator rootIterator = root.getNodes();
    while (rootIterator.hasNext()) {
      final Node current = rootIterator.nextNode();
      if (current.hasNode("jcr:content")) {
        nodes.add(current.getNode("jcr:content"));
      }
    }
    return nodes.iterator();
  }
}
