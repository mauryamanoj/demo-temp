import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const ThingsToDoCardsComp = React.lazy(() => import("./ThingsToDoCardsComp"));
const ThingsToDoCards: React.FC<any> = (props) => {
    return (
        <Wrapper componentId={props.componentId || 'thingsToDoWrapperId'}>
            <ThingsToDoCardsComp {...props} />
        </Wrapper>
    );
};

export default ThingsToDoCards;

