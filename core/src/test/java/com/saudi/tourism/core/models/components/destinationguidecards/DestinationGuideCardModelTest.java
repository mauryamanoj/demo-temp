package com.saudi.tourism.core.models.components.destinationguidecards;

import com.saudi.tourism.core.models.components.destinationguide.DestinationGuideCards;
import com.saudi.tourism.core.models.components.destinationguide.Card;
import com.saudi.tourism.core.models.components.destinationguide.Item;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class DestinationGuideCardModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";
  @Mock
  private WeatherService weatherService;


    @BeforeEach
    public void setUp(final AemContext context) {
      context.addModelsForClasses(DestinationGuideCards.class,Card.class, Item.class);
      context.registerService(WeatherService.class, weatherService);
      context.load().json("/components/destination-guide-cards/content.json", CONTENT_PATH);
    }

    @Test
    public void testGuideCardsModel(AemContext context) {
      var weatherResponse = new ArrayList<SimpleWeatherModel>();
      var weather = new SimpleWeatherModel();
      weather.setWeather("Clear");
      weather.setCity("Riyadh");
      weather.setCityId("riyadh");
      weather.setIcon("Clear");
      weather.setTemp(28.11f);
      weatherResponse.add(weather);
      weather = new SimpleWeatherModel();
      weather.setWeather("Clear");
      weather.setCity("Jeddah");
      weather.setCityId("jeddah");
      weather.setIcon("Clear");
      weather.setTemp(31.92f);
      weatherResponse.add(weather);
      when(weatherService.getSimpleWeatherGroupOfCities(any())).thenReturn(weatherResponse);
      String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/destinations_guide";
        DestinationGuideCards guideCards =
        context.currentResource( RESOURCE_PATH).adaptTo(DestinationGuideCards.class);
        assertEquals(3, guideCards.getCards().size());
        assertEquals("DEMO", guideCards.getTitle());
        assertEquals(guideCards.getCards().get(2).getIcon(),"Clear");
        assertEquals(guideCards.getCards().get(2).getWeather(),"Clear");
      assertEquals("DestinationGuideCards", guideCards.getType());
      assertEquals("Sub DEMO", guideCards.getSubTitle());
      assertEquals("/content/dam/static-images/resources/flags/png/171-galapagos-islands.png", guideCards.getBackground().getFileReference());
      assertEquals(true, guideCards.isHaveBackgroundImage());
      assertEquals("/content/dam/static-images/resources/flags/png/065-eritrea.png", guideCards.getCards().get(1).getLogo().getFileReference());

      testCardSlidesData(guideCards);
    }


    /**
     * Test card slide specific data.
     * @param guideCards DestinationGuideCards
     */
    void testCardSlidesData(DestinationGuideCards guideCards){
        for (Card card : guideCards.getCards()) {
          Assertions.assertNotNull(card.getTitle());
          Assertions.assertNotNull(card.getType());
        }
    }
}
