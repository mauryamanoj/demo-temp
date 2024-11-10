// eslint-disable-next-line @typescript-eslint/no-explicit-any
const dataLayer = (window as any).dataLayer || [];

interface GTMPageMetaData {
  event: string;
  page_country: string;
  page_environment: string;
  page_language: string;
  page_path: string;
  page_title: string;
  page_campaign: string;
  page_section: string;
  user_id: string;
  user_visa: string;
}

export const gtmPageMetaData = (data: GTMPageMetaData): void => dataLayer.push(data);

interface GTMVideoData {
  event: string;
  event_category: string;
  event_action: string;
  event_label: string;
  video_id: string;
  video_length: string;
  video_position: string;
}

export const gtmVideoStart = (data: GTMVideoData): void => dataLayer.push(data);
export const gtmVideoStatus = (data: GTMVideoData): void => dataLayer.push(data);
export const gtmVideoComplete = (data: GTMVideoData): void => dataLayer.push(data);

interface GTMHeaderNavClickData {
  event: string;
  navigation_category: string;
  component_id: string;
  navigation_subcategory: string;
  navigation_type: string;
  navigation_url: string;
}

export const gtmHeaderNavClick = (data: GTMHeaderNavClickData): void => dataLayer.push(data);

interface GTMLinkClickData {
  event: string;
  event_category: string;
  event_action: string;
  event_label: string;
  component_id: string;
}

export const gtmLinkClick = (data: GTMLinkClickData): void => dataLayer.push(data);

interface GTMCustomEventClickData {
  event: string;
  event_category: string;
  event_action: string | undefined;
  event_label: string | null;
}
interface GTMHandleCustomEventClickData {
  event: string;
  event_name: string;
  event_properties: any
}
export const gtmHandleCustomEventClick = (data: GTMHandleCustomEventClickData): void => dataLayer.push(data);

export const gtmCustomEventClick = (data: GTMCustomEventClickData): void => dataLayer.push(data);

interface GTMButtonClickData {
  event: string;
  event_category: string;
  component_id: string;
  section_type: string;
  navigation_url: string;
}

export const gtmButtonClick = (data: GTMButtonClickData): void => dataLayer.push(data);

interface GTMCountrySelectionData {
  event: string;
  event_category: string;
  component_id: string;
  country_selection: string;
}

export const gtmCountrySelection = (data: GTMCountrySelectionData): void => dataLayer.push(data);

interface GTMCarouselActionData {
  event: string;
  event_category: string;
  component_id: string;
  navigation_type: string;
}

export const gtmCarouselAction = (data: GTMCarouselActionData): void => dataLayer.push(data);

interface GetTicketsSummerEvents {
  allow_custom_scripts: boolean;
  u1: string;
  u9?: string;
  send_to: string;
}
export const gTagAnalytics = (data: [string, string, GetTicketsSummerEvents]) =>
  dataLayer.push(data);
export const addJs = (data: [string, object]) => dataLayer.push(data);
export const addMeasurementId = (data: [string, string]) => dataLayer.push(data);

interface GTMVisaRegulationData {
  event:string;
  section_name:string;
  click_text:string;
  link:string;
  page_category:string;
  page_subcategory:string;
  language:string;
  vpv:string;
}

export const GTMVisaRegulation = (data:GTMVisaRegulationData) : void => dataLayer.push(data);
