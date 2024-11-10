package com.saudi.tourism.core.models.components.informationsection.v1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Component(service = InformationSectionCFAdapter.class, immediate = true)
public class InformationSectionAttractionCFAdapter implements InformationSectionCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("attraction", cf.getTemplate().getTitle());
  }

  @Override
  public InformationSectionCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var qaTitle = getElementValue(cf, "qaTitle", String.class);
    var qaHideReadMore = Optional.ofNullable(getElementValue(cf, "qaHideReadMore", Boolean.class)).orElse(false);
    var qaReadMoreLabel = getElementValue(cf, "qaReadMoreLabel", String.class);
    var qaReadLessLabel = getElementValue(cf, "qaReadLessLabel", String.class);
    String[] questionAnswerArray = getElementValue(cf, "questionAnswer", String[].class);
    var questionsAnswers = Optional.ofNullable(questionAnswerArray).map(Arrays::asList).orElse(Collections.emptyList());

    return InformationSectionCFModel.builder()
      .qaTitle(qaTitle)
      .qaHideReadMore(qaHideReadMore)
      .qaReadMoreLabel(qaReadMoreLabel)
      .qaReadLessLabel(qaReadLessLabel)
      .questionsAnswers(questionsAnswers)
      .build();
  }
}
