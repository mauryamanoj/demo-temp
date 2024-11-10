//import { CarouselEventData } from './../util/Carousel/SendCarouselsEventData';

export interface PackagesSignupFormData {
  event: string;
  form: {
    type: string;
    error: string;
  };
}

export interface TabsEventData {
  event: 'Page Tab Switched';
  pageTab: {
    name: string;
    title: string;
  };
}

export interface SearchFilterEventData {
  event: 'Calendar Filter Applied';
  calendar: {
    searchResults: number;
    filter: {
      keyword: string;
      areaToExplore: string;
      startDate: string;
      endDate: string;
      category: string;
      audience: string;
      freepaid: string;
      season: string;
    };
  };
}

// Data type needs to be any because backend might change it in the future.
export interface CarouselTrackingEventData {
  event: 'Carousel Scrolled';
  carousel: any;
}

export interface VideoEventData {
  event: 'Video Played';
  video: {
    name: string;
    title: string;
    filename: string;
  };
}

export interface PackageTourEventData {
  event: string; // This info can be changed from BE side so it must be string
  tour: {
    packageName: string;
    vendorName: string;
    linkTitle: string;
    linkURL: string;
  };
}

export interface FormsFilters {
  event: 'Search Filter Applied';
  searchFilter: {
    name: string;
    searchResults: number;
    data: Array<FieldInfo>;
  };
}

export interface FieldInfo {
  fieldName: string;
  fieldValue: string;
}

type UserData = {
  id: string;
  interest: string;
  age: string;
  gender: string;
  travelling: string;
};

export interface UserUpdateProfile {
  event: 'User Profile Saved';
  user: UserData;
}

export interface UserLogout {
  event: 'User Logged Out';
}

export interface UserLogin {
  event: 'User Logged In';
  user: UserData;
}

export interface UserRegistered {
  event: 'User Registered';
  user: { id: string };
}

export interface SearchForm {
  event: 'Search Results Loaded';
  search: {
    term: string;
    results: number;
  };
}

export interface TripPlanEventData {
  event: 'Trip Itinerary Created' | 'Trip Itinerary Changed' | 'Trip Itinerary Deleted';
  trip: {
    id: string;
    dateFrom?: string;
    dateTo?: string;
    days: number;
    activities: number;
  };
}

export type TripActivityEventData = {
  tripId: string;
  type: string;
  city: string;
  id: string;
};

export interface TripActivitiesEventData {
  event: 'Trip Activity Added' | 'Trip Activity Moved' | 'Trip Activity Deleted';
  tripActivity?: Array<TripActivityEventData>;
}

export type StepNames = 'name' | 'where' | 'when';

export interface TripCreationFlowEventData {
  event: 'Trip Planner Creation Flow';
  tripPlanner: {
    stepName: StepNames;
  };
}

export interface EventsCtaEventData {
  event: 'Get Ticket Clicked';
  tickets: {
    ticketName: string;
    linkTitle: string;
    linkURL: string;
  };
}
