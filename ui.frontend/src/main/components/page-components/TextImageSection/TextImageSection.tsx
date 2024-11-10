import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const TextImageSectionComp = React.lazy(() => import("./TextImageSectionComp"));
const TextImageSection: React.FC<any> = (props) => {
    return (
        <Wrapper componentId={props.componentId}>
            <TextImageSectionComp {...props} />
        </Wrapper>
    );
};

export default TextImageSection;

