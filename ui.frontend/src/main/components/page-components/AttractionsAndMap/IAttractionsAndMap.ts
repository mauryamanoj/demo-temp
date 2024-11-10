export interface AttractionsAndMapProps {
    noResultsFoundLabel: string;
    enableManualAuthoring: boolean;
    mapApiKey: string;
    showMapBorder: boolean;
    title: string;
    subTitle: string;
    componentId: string;
    link: {
        url: string;
        targetInNewWindow: boolean;
        text: string;
    },
    categoryFilter: {
        label: string;
        apiUrl: string;
    },
    destinationFilter: {
        label: string;
        apiUrl: string;
    },
    allAttractionsFilter: {
        apiUrl: string;
    },
    destinationList?: {
        title: string;
        path: string;
    }[],
    categoryList?: {
        title: string;
        id: string;
        icon: string;
    }[]

}


