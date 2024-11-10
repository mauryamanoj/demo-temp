package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import com.saudi.tourism.core.services.CardsService;
import com.saudi.tourism.core.services.HotelService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AreaDetailsServiceImplTest {

  private static final String JEDDAH_CITY_PAGE_PATH = "/content/sauditourism/app1/en/cities/jeddah";
  private static final String JEDDAH_CITY_RESOURCE_PATH = "/content/sauditourism/app1/en/cities/jeddah/jcr:content";

  @Mock
  private CardsService cardsService;

  @Mock
  private HotelService hotelService;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private MockSlingHttpServletRequest request;

  @Mock
  private ResourceResolver resourceResolver;

  @Mock
  private Session session;

  @Mock
  private SearchResult searchResult;

  @Mock
  private Iterator<Node> iteratorNode;

  @Mock
  private Node node;

  @Mock
  private CardsListModel cardsListModel;

  @Captor
  ArgumentCaptor<CardsRequestParams> cardsRequestParamsCaptor;

  private AreaDetailsServiceImpl areaDetailsService;

  @BeforeEach
  void setUp(final AemContext aemContext) throws RepositoryException {
    aemContext.registerService(CardsService.class, cardsService);
    aemContext.registerService(HotelService.class, hotelService);
    aemContext.registerService(QueryBuilder.class, queryBuilder);
    areaDetailsService = new AreaDetailsServiceImpl();
    aemContext.registerInjectActivateService(areaDetailsService);

    aemContext.load().json("/services/area-details-service/cities/jeddah.json", JEDDAH_CITY_PAGE_PATH);

    request = new MockSlingHttpServletRequest(resourceResolver);

    when(resourceResolver.adaptTo(eq(Session.class))).thenReturn(session);
    when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getNodes()).thenReturn(iteratorNode);
    when(cardsService.getFilteredCards(any(SlingHttpServletRequest.class), any(CardsRequestParams.class), any(String.class))).thenReturn(cardsListModel);
  }


  @Test
  void getAreaDetailsShouldHandleShoppingArticleDetails(final AemContext aemContext) throws RepositoryException {
    //Arrange
    request.setParameterMap(ImmutableMap.<String,Object>builder()
      .put("area", "jeddah")
      .build());

    final Resource resource = aemContext.currentResource(JEDDAH_CITY_RESOURCE_PATH);

    when(iteratorNode.hasNext()).thenReturn(true, false);
    when(iteratorNode.next()).thenReturn(node);
    when(node.getPath()).thenReturn(JEDDAH_CITY_RESOURCE_PATH);
    when(resourceResolver.getResource(eq(JEDDAH_CITY_RESOURCE_PATH))).thenReturn(resource);

    //Act
    areaDetailsService.getAreaDetails(request);

    //Assert
    verify(cardsService, times(5)).getFilteredCards(any(SlingHttpServletRequest.class), cardsRequestParamsCaptor.capture(), any(String.class));
    final Optional<CardsRequestParams> optionalCardsRequestParams = cardsRequestParamsCaptor.getAllValues().stream()
      .filter(r -> StringUtils.equals("articles", r.getType()))
      .filter(r -> CollectionUtils.isNotEmpty(r.getCategory()))
      .filter(r -> r.getCategory().contains("attractions"))
      .findFirst();

    assertTrue(optionalCardsRequestParams.isPresent());
  }


  @Test
  void getAreaDetailsShouldHandleShoppingShoppingDetails(final AemContext aemContext) throws RepositoryException {
    //Arrange
    request.setParameterMap(ImmutableMap.<String,Object>builder()
      .put("area", "jeddah")
      .build());

    final Resource resource = aemContext.currentResource(JEDDAH_CITY_RESOURCE_PATH);

    when(iteratorNode.hasNext()).thenReturn(true, false);
    when(iteratorNode.next()).thenReturn(node);
    when(node.getPath()).thenReturn(JEDDAH_CITY_RESOURCE_PATH);
    when(resourceResolver.getResource(eq(JEDDAH_CITY_RESOURCE_PATH))).thenReturn(resource);

    //Act
    areaDetailsService.getAreaDetails(request);

    //Assert
    verify(cardsService, times(5)).getFilteredCards(any(SlingHttpServletRequest.class), cardsRequestParamsCaptor.capture(), any(String.class));
    final Optional<CardsRequestParams> optionalCardsRequestParams = cardsRequestParamsCaptor.getAllValues().stream()
      .filter(r -> StringUtils.equals("articles", r.getType()))
      .filter(r -> CollectionUtils.isNotEmpty(r.getCategory()))
      .filter(r -> r.getCategory().contains("shopping"))
      .findFirst();

    assertTrue(optionalCardsRequestParams.isPresent());
  }
}