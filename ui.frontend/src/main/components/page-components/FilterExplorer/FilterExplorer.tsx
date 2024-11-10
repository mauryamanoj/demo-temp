import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const FilterExplorerComp = React.lazy(() => import("./FilterExplorerComp"));

const ThingsToDoCards: React.FC<any> = (props) => {
    return (
        <Wrapper componentId={props.componentId} className="things-container mx-5 lg:mx-100
        lg:pt-[0px] lg:!pb-[0] lg:mt-0 mt-0 lg:!mb-0 !mb-4 lg:min-h-[800px] min-h-[500px]">
            <FilterExplorerComp {...props} />
        </Wrapper>
    );
};

export default ThingsToDoCards;
