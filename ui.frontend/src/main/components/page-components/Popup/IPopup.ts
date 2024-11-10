export enum PopupType {
    Default = "default",
    Info = "info",
    Warning = "warning",
    Error = "error",
    Success = "success",
}
export interface Cta {
    label: string;
}

export interface IPopup {
    type: PopupType;
    icon?: string;
    title: string;
    subTitle?: string;
    text?: string;
    cat1?: Cta;
    cat2?: Cta;
    handleClose?: () => void;
}

