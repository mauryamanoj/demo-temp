export interface FilterExplorerInterface {
  filterType: string,
  title: string
  typeLabel: string
  filtersLabel: string
  modalFiltersTitle: string
  clearLabel: string
  clearAllButton: string
  loadingLabel: string
  hideOrnament: boolean
  cancelLabel: string
  applyLabel: string
  apiUrl: string
  resetAllFiltersLabel: string;
  selectLocationLabel: any;
  searchPlaceholderLabel: string
  noResultsFoundLabel: string
  noResultFoundDescription: string
  searchLimit: number
  types: Type[]
}


export interface Type {
  label: string
  checked: boolean
  defaultChecked?: boolean
  id?: string
}
