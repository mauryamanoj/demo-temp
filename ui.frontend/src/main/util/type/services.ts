
export interface Event {
  id: string;
  path: string;
  link: string;
  title: string;
  subtitle: string;
  shortDescription: string;
  category?: Array<any>;
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
  city: Array<any>;
  region?: Array<any>;
  category: Array<any>;
  season?: Array<any>;
  target?: Array<any>;
  freePaid?: Array<any>;
}
