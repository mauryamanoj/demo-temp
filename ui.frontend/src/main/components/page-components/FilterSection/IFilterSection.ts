import { IFilterDropdownButton } from "./DesktopFilter/FilterDropdownButton";

export interface Cta {
    label: string;
    link: string;
    targetInNewWindow: boolean;
}

export interface IFilterSection {
    filters: Array<IFilterDropdownButton>;
    cta: Cta;
}

