import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { AboutSectionInterface } from "./IAboutSection";
const AboutSectionComp = React.lazy(() => import("./AboutSectionComp"));

const AboutSection: React.FC<AboutSectionInterface> = (props) => {

    return (
        <Wrapper componentId={props.componentId || 'AboutSectionWrapperId'}>
            <AboutSectionComp {...props} />
        </Wrapper>
    );
};

export default AboutSection;

