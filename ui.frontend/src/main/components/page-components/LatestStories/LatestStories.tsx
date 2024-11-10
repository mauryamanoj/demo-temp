import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const LatestStoriesComp = React.lazy(() => import("./LatestStoriesComp"));

const Footer: React.FC<any> = (props) => {
    return (
        <Wrapper componentId={props.componentId|| 'LatestStoriesWrapperId'} className="md:mx-100 min-h-[200px] lg:min-h-[500px]">
                <LatestStoriesComp {...props} />
        </Wrapper>
    );
};

export default Footer;

