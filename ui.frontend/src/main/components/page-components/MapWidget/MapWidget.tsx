import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const MapWidgetComp = React.lazy(() => import("./MapWidgetComp"));

const MapWidget = (props: any) => {
    const { latitude, longitude, mapApiKey } = props
    return (
        mapApiKey &&
        <Wrapper componentId={props.componentId}
            className={(latitude && longitude && mapApiKey) ? "min-h-[160px] !mb-4" : "min-h-0 !mb-0"}>
            {latitude && longitude && mapApiKey && <MapWidgetComp {...props} />}
        </Wrapper>
    );
};

export default MapWidget;

