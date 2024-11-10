
import { DestinationCardProps } from "src/main/components/page-components/Cards/Destination/IDestinationCard";
export interface DestinationsAndMapProps {
    mapApiKey: string;
    showMapBorder: boolean;
    title: string;
    link:{
        url: string;
        targetInNewWindow: boolean;
        text: string;
    }

    destinations: DestinationCardProps[];
    componentId: string;
}


