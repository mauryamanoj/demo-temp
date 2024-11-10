import React from "react";

import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { NeighborhoodsProps } from "./INeighborhoodsProps";

const NeighborhoodsSectionComp = React.lazy(() => import("./NeighborhoodsSectionComp"));

const NeighborhoodsSection: React.FC<NeighborhoodsProps> = (props) => {


    return (
        <Wrapper componentId={props.componentId} className={"min-h-[200px]"}>
            {(
                <NeighborhoodsSectionComp {...props} />
            )}
        </Wrapper>
    );
};

export default NeighborhoodsSection;

