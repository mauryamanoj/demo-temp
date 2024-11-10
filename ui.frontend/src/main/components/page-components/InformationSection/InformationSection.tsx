import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const InformationSectionComp = React.lazy(
    () => import("./InformationSectionComp")
);

const InformationSection: React.FC<any> = (props) => {


    return (
        props.data?.length ? <Wrapper componentId={props.componentId|| 'InformationSectionWrapperId'}>
            <InformationSectionComp {...props} />
        </Wrapper> : null
    );
};

export default InformationSection;
