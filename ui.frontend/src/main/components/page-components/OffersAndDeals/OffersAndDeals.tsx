import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";
import { Type } from "./IOffersAndDeals";

const DealsSliderComp = React.lazy(() => import("./components/DealsSliderComp"));
const OffersSliderComp = React.lazy(() => import("./components/OffersSliderComp"));

const OffersAndDeals = (props: any) => {

    return (
        <Wrapper componentId={props.componentId || 'OffersAndDealsWrapperId'} className={"min-h-[300px]"}>

            {props?.type === Type.Deal ?
                <DealsSliderComp {...props} /> : <OffersSliderComp {...props} />
            }
        </Wrapper>
    );
};

export default OffersAndDeals;

