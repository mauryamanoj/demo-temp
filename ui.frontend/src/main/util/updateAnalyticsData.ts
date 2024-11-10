/* eslint-disable max-len */
import { omitBy } from 'lodash';
import { gtmHandleCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
declare global {
  interface Window {
    digitalData: any;
    _satellite: {
      track(eventName: string): void;
    };
  }
}

export const updateAnalyticsData = (
  event: string,
  payload: any,
): void => {
  try {
    Object.keys(payload.link).length > 0
      ? (window.digitalData.page.link = payload.link)
      : delete window.digitalData.page.link;
    Object.keys(payload.event).length > 0
      ? (window.digitalData.page.event = payload.event)
      : delete window.digitalData.page.event;
    console.log('Adobe >>> Event Name >>>', event);
    console.log('Adobe >>> Event tracking data', payload);
    console.log('Adobe >>> Initial payload with tracking data', window.digitalData);
    window._satellite?.track(event);
  }
  catch (err) {
    console.log(err);
  }
};
const createPayload = (dataSet: any) => {
  try {
    const {
      trackEventname,
      trackSection,
      platformName,
      trackName,
      eventProperties
    } = dataSet;
    const eventProps: any = {
      link: {},
      event: {}
    };
    eventProps.link.name = trackName ? trackName.toLowerCase() : null;
    eventProps.link.section = trackSection ? trackSection.toLowerCase() : null;
    eventProps.link.platform_name = platformName ? platformName : null;
    eventProps.eventProperties = eventProperties ? eventProperties : null;
    eventProps.event = omitBy(eventProps.event, (value) => value === null) || trackName ? trackName.toLowerCase() : null;
    eventProps.event_name = omitBy(eventProps.event, (value) => value === null) || trackEventname ? trackEventname.toLowerCase() : null;

    updateAnalyticsData(trackEventname, eventProps);
  }
  catch (err) {
    console.log(err);
  }
};
export const trackEvents = (dataSet: any) => {
  try {
    const { trackEventname } = dataSet;
    trackEventname && createPayload(dataSet);
  }
  catch (err) {
    console.log(err);
  }
};

interface PageInfo {
  city: string;
  pageCategory: string;
  pageSubcategory: string;
}

export function extractPageInfoFromEventProperties() {
  const eventProperties = (window as any).eventProperties;
  if (eventProperties) {
    return {
      city: eventProperties.city,
      pageCategory: eventProperties.page_category,
      pageSubcategory: eventProperties.page_subcategory,
    };
  }
}

export function trackingEvent({
  event_name,
  title,
  url,
  page_category,
  page_subcategory,
  city = '',
  event_category = '',
  currency = '',
  visaPrice = '',
  countrySelected = '',
  sectionName = '',
  platform_name = '',
  navigation_name = '',
  language = getLanguage()
}: {
  event_name: string;
  title: string;
  url?: string;
  page_category?: string;
  page_subcategory?: string;
  city?: string;
  event_category?: string;
  currency?: string;
  visaPrice?: string;
  countrySelected?: string;
  sectionName?: string;
  platform_name?: string;
  navigation_name?: string;
  language?: string;
}
) {
  gtmHandleCustomEventClick({
    'event': 'dl_push',
    'event_name': event_name,
    'event_properties': {
      ...(title && { 'title': title }),
      ...(page_category && { 'page_category': page_category }),
      ...(page_subcategory && { 'page_subcategory': page_subcategory }),
      ...(event_category && { 'event_category': event_category }),
      ...(url && { 'link_click_url': url }),
      'language': language,
      ...(navigation_name && { 'navigation_name': navigation_name }),
      ...(platform_name && { 'platform_name': platform_name }),
      ...(city && { 'city': city }),
      ...(currency && { 'currency': currency }),
      ...(visaPrice && { 'visa_price': visaPrice }),
      ...(countrySelected && { 'country_selected': countrySelected }),
      ...(sectionName && { 'section_name': sectionName }),
    }
  });
}

export function trackAnalytics({
  trackEventname,
  trackName,
  url,
  title,
  event_category,
  page_category,
  page_subcategory,
  city,
  platform_name,
  navigation_name,
  language = getLanguage(),
  visaPrice,
  currency,
  sectionName,
  countrySelected
}: {
  trackEventname: string;
  trackName: string;
  title?: string;
  url?: string;
  page_category?: string;
  page_subcategory?: string;
  city?: string;
  platform_name?: string;
  navigation_name?: string;
  language?: string;
  event_category?: string;
  visaPrice?: string;
  currency?: string;
  sectionName?: string;
  countrySelected?: string;
}) {
  const dataSet: any = {};
  dataSet.trackEventname = trackEventname;
  dataSet.trackName = trackName;

  if (platform_name) {
    dataSet.platform_name = platform_name;
  }
  if (visaPrice) dataSet.visaPrice = visaPrice;
  if (sectionName) dataSet.sectionName = sectionName;
  if (countrySelected) dataSet.countrySelected = countrySelected;

  dataSet.eventProperties = {
    ...(page_category && { 'page_category': page_category }),
    ...(platform_name && { 'platform_name': platform_name }),
    ...(page_subcategory && { 'page_subcategory': page_subcategory }),
    ...(navigation_name && { 'navigation_name': navigation_name }),
    ...(event_category && { 'event_category': event_category }),
    ...(visaPrice && { 'visa_price': visaPrice }),
    ...(url && { 'link_click_url': url }),
    ...(city && { 'city': city }),
    ...(currency && { 'currency': currency }),
    ...(countrySelected && { 'country_selected': countrySelected }),
    ...(sectionName && { 'section_name': sectionName }),
    language: language,
  };
  dataSet.trackEventname && trackEvents(dataSet);
}


// export function extractPageInfo(url: string): PageInfo {
//   const urlParts = new URL(url);
//   const pathSegments = urlParts.pathname.split('/').filter(segment => segment !== '');

//   let city = '';
//   let pageCategory = '';
//   let pageSubcategory = '';

//   if (pathSegments.length >= 4) {
//     city = pathSegments[2];
//     pageCategory = pathSegments[3];

//     if (pathSegments.length >= 5) {
//       pageSubcategory = pathSegments[4];
//     }
//   }

//   return {
//     city,
//     pageCategory,
//     pageSubcategory,
//   };
// }

//Example usage
//const url = 'https://qa-revamp.visitsaudi.com/en/destinations/riyadh/things-to-do/attractions/salam-park';
//const pageInfo: PageInfo = extractPageInfo(url);


// console.log(pageInfo.city);            // Output: riyadh
// console.log(pageInfo.pageCategory);    // Output: things-to-do
// console.log(pageInfo.pageSubcategory); // Output: attractions