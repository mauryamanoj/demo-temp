package com.saudi.tourism.core.models.components.travellerquotes.v1;

import com.saudi.tourism.core.models.common.Image;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class TravellerQuotesModelTest {
    private static final String CONTENT_PATH = "/content/sauditourism/en/see";


    @BeforeEach
    public void setUp(AemContext context) {
        context.load().json("/components/traveller-quotes/content.json", CONTENT_PATH);
    }

    @Test
    public void testTravellerQuotesModel(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/see/jcr:content/root/responsivegrid/traveller_quotes";
        TravellerQuotesModel travellerQuotesModel =
            context.currentResource( RESOURCE_PATH).adaptTo(TravellerQuotesModel.class);
        assertEquals(2, travellerQuotesModel.getQuotesSlides().size());
        testQuotesSlidesData(travellerQuotesModel);
    }


    /**
     * Test quotes slide specific data.
     * @param travellerQuotesModel TravellerQuotesModel
     */
    void testQuotesSlidesData(TravellerQuotesModel travellerQuotesModel){
        for (QuotesSlide quotesSlide : travellerQuotesModel.getQuotesSlides()) {
            Assertions.assertNotNull(quotesSlide.getImage());
            Image image = quotesSlide.getImage();
            Assertions.assertNotNull(image.getAlt());
            Assertions.assertNotNull(image.getFileReference());
            Assertions.assertNotNull(quotesSlide.getQuote());
            Assertions.assertNotNull(quotesSlide.getQuoterName());
            Assertions.assertNotNull(quotesSlide.getQuoterNameWeight());
            Assertions.assertNotNull(quotesSlide.getQuoterCity());
            Assertions.assertNotNull(quotesSlide.getQuoterCityWeight());
            Assertions.assertNotNull(quotesSlide.getAudioFile());
        }
    }
}
