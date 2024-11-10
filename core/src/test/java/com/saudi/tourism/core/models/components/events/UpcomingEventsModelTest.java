package com.saudi.tourism.core.models.components.events;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class UpcomingEventsModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/saudi-calendar";

  private static final String RESOURCE_PATH =
    "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/events_cards_1570274_48304227";

  @Mock
  private EventService eventService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private EventListModel eventListModel;

  @Mock
  private Page currentPage;

  @Captor
  private ArgumentCaptor<EventsRequestParams> eventsRequestParamsCaptor;

  private ResourceBundle i18nBundle = new ResourceBundle() {
    @Override
    protected Object handleGetObject(@NotNull String key) {
      return "fake_translation";
    }

    @NotNull
    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  void setUp(final AemContext aemContext) throws RepositoryException {
    aemContext.registerService(EventService.class, eventService);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider,
      ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);

    aemContext.load().json("/pages/saudi-calendar.json", PAGE_PATH);

    final SlingBindings slingBindings = (SlingBindings) aemContext.request().getAttribute(SlingBindings.class.getName());
    slingBindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, currentPage);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);

  }

  @Test
  public void testUpcomingEventsModel(final AemContext aemContext) throws RepositoryException {
    //Arrange
    aemContext.currentResource(RESOURCE_PATH);

    when(eventListModel.getData()).thenReturn(Collections.emptyList());
    when(eventService.getFilteredEvents(any(EventsRequestParams.class))).thenReturn(eventListModel);

    //Act
    final UpcomingEventsModel model = aemContext.request().adaptTo(UpcomingEventsModel.class);

    //Assert
    verify(eventService).getFilteredEvents(eventsRequestParamsCaptor.capture());
    assertNotNull(eventsRequestParamsCaptor.getValue().getSeason());
    assertEquals(1, eventsRequestParamsCaptor.getValue().getSeason().size());
    assertEquals("alula-season", eventsRequestParamsCaptor.getValue().getSeason().get(0));
  }
}