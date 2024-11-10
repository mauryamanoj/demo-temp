export interface SearchRes {
  suggestionsEndpoint: string
  searchPagePath: string
  searchPlaceholder: string
  cancelLabel: string
  clearLabel: string
  clearAllButton: string
  searchModalTitle: string
  regions: Region[]
  contentTypeFilter: ContentTypeFilter[]
  searchLimit: number
  resultsCountLabel: string
  clearAllFiltersLabel: string
  clearAllResultsLabel: string
  resetAllFiltersLabel: string
  locationLabel: string
  contentLabel: string
  readMoreLabel: string
  dateSort: string
  searchPlaceholderLabel: string
  noResultsFoundLabel: string
  sortFilterLabel: string
  noResultFoundDescription: string
  filters: string
  recentlySearchedLabel: string
  showAllFilters: string
  modalFiltersTitle: string
  modalSortByFilter: string
  modalViewAsLabel: string
  loadMoreLabel: string
  selectLabel: string
  applyLabel: string
  selectLocationLabel: string
  buttonLabel: string
  viewAllResultsLabel: string
  resultCountLabel: string
  clearInputStyle: any
}


export interface Region {
  code: string | undefined
  value: string
}

export interface ContentTypeFilter {
  code: string
  value: string
}

export interface CardInfo {
  url: string;
  title: string;
  description: string;
  featureImage: string;
  region: string | null;
  startdate: string | null;
  enddate: string | null;
  starttime: string | null;
  endtime: string | null;
  tags: string[] | null; // Explicitly define the type of tags
  categories: string[];
}