package com.saudi.tourism.core.services.impl;

import com.google.common.collect.ImmutableSet;
import com.saudi.tourism.core.beans.PackageFormParams;
import com.saudi.tourism.core.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * Unit test for MailchimpService.
 */
class MailchimpServiceTest {

  private static final String TEST_EMAIL = "test@test.test";

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private MailchimpServiceImpl testService;

  public MailchimpServiceTest() {
    MockitoAnnotations.initMocks(this);

    ResourceBundle i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    ResourceBundleProvider i18nProvider = mock(ResourceBundleProvider.class);
    Mockito.when(i18nProvider.getResourceBundle(Mockito.any())).thenReturn(i18nBundle);

    Utils.setInternalState(testService, "i18nProvider", i18nProvider);
  }

  @Test
  void getMailchimpMembersUrl() {
    final String listId = "listId";
    final String result = "https://us10.api.mailchimp.com/3.0/lists/" + listId + "/members/";
    Set<String> check = ImmutableSet
        .of("https://us10.api.mailchimp.com/3.0/lists/9c0a110ff6/members/62eeb292278",
            "https://us10.api.mailchimp.com/3.0/lists/{listId}/members/",
            "https://us10.api.mailchimp.com/3.0/lists/{listId}",
            "https://us10.api.mailchimp.com/3.0/lists/", "https://us10.api.mailchimp.com/3.0/",
            "https://us10.api.mailchimp.com/3.0");

    for (String configUrl : check) {
      assertEquals(result, testService.getMailchimpMembersUrl(configUrl, listId));
    }
  }

  @Test
  void addSubscription_not_configured() {
    final PackageFormParams formParams = new PackageFormParams();
    formParams.setEmail(TEST_EMAIL);
    // Clean the url field
    Utils.setInternalState(testService, "url", StringUtils.EMPTY);
    assertThrows(IllegalStateException.class, () -> testService.addSubscription(formParams, "dmc"));
  }
}
