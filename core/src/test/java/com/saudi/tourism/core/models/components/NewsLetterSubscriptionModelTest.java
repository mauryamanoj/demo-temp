package com.saudi.tourism.core.models.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class NewsLetterSubscriptionModelTest {

    private static final String CONTENT_PATH = "/content/sauditourism/languages/english/test-page";
    private static final String RESOURCE_PATH = "/content/sauditourism/languages/english/test-page/jcr:content/root/responsivegrid/news_letter_subscrip";
    @BeforeEach
    void setUp(AemContext context) throws Exception {
        context.load().json("/components/news-letter-subscription/content.json", CONTENT_PATH);
    }

    @Test public void testContent(AemContext context) {
        NewsLetterSubscriptionModel model = context.currentResource(RESOURCE_PATH).adaptTo(NewsLetterSubscriptionModel.class);
        assertEquals("Stay up to date", model.getStayUpToDate());
        assertEquals("Subscribe to our newsletter", model.getSubscribeText());
        assertEquals("Subscribe", model.getSubscribeButtonText());
        assertEquals("Enter email address", model.getEmailInputPlaceholder());
        assertEquals("Invalid Email Address.", model.getInvalidEmailMessage());
        assertEquals("Email is registered", model.getSuccessMessage());
        assertEquals("Error Happened While Registering your email. Please, try again later.", model.getErrorMessage());
        assertEquals("/bin/api/v1/newsletter", model.getApiUrl());
    }
}
