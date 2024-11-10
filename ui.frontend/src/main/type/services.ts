export type Tag = {
  id: string;
  copy: string;
};

export interface SearchParamsResponseKey {
  id: string;
  value: string;
}

export interface Event {
  id: string;
  path: string;
  link: string;
  title: string;
  subtitle: string;
  shortDescription: string;
  category?: Array<Tag>;
  image?: string;
  cardImage?: string;
  cardImageMobile?: string;
  altImage?: string;
  startDate: string;
  endDate: string;
  displayedStartDate?: string;
  displayedEndDate?: string;
  city: string;
  region: string;
  favoritePath: string;
  urlSlingExporter: string;
  priority: 'low' | 'medium' | 'high';
  featured: boolean;
  featureEventImage: string;
}

export interface SearchParams {
  city: Array<SearchParamsResponseKey>;
  region?: Array<SearchParamsResponseKey>;
  category: Array<SearchParamsResponseKey>;
  season?: Array<SearchParamsResponseKey>;
  target?: Array<SearchParamsResponseKey>;
  freePaid?: Array<SearchParamsResponseKey>;
}
