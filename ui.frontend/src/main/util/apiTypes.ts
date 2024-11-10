export interface Pagination {
  total: number;
  offset: number;
  limit: number;
}

export interface Locale {
  locale: string;
}

// TODO: this looks very strange, probably needs to be gone (check if date is used anywhere)
export interface PaginationWithDate {
  date?: string;
  offset?: number; // default 0
  limit?: number; // default 50
  total?:number;
}

// TODO: this is already defined in api.ts, but should be moved here
export interface Response<T> {
  data: T;
}

export interface PaginatedResponse<T> extends Response<T> {
  pagination: Pagination;
}

export interface PaginatedResponseWithFilters<T, F> extends PaginatedResponse<T> {
  filters: F;
}

export interface Resource {
  id: string;
}

export interface ResourceWithValue extends Resource {
  value: string;
}

export interface SpecialOffers {
  details: ReadonlyArray<string>;
  title: string;
  subTitle: string;
  ctaText: string;
}
export type FavoriteResponse = { details: Array<FavoritesDetail>; items: Array<FavoriteItem> };

export type FavoritesDetail = {
  altImage: string;
  cardImage: string;
  favId: string;
  link: string;
  path: string;
  title: string;
  type: string;
  apiBasePath?: string;
  calendarEndDate?: string;
  calendarStartDate?: string;
  endDate?: string;
  startDate?: string;
  tripPlan?: boolean;
  city?: City;
};

export type FavoriteItem = string;

export type City = {
  id: string;
  name: string;
  image: Image;
  preferredTripDaysCount: number;
};

type Image = {
  alt?: string;
  desktopImage: string;
  mobileImage: string;
};

export interface SearchEventsResponse {
  data: Array<Event>;
  pagination: {
    total: number;
    offset: number;
    limit: number;
  };
}
