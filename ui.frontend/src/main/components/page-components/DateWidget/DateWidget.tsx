import React from "react";

import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { DateWidgetProps } from "./IDateWidget";

const DateWidgetComp = React.lazy(() => import("./DateWidgetComp"));

const DateWidget: React.FC<DateWidgetProps> = (props) => {
    return (
        <Wrapper
            componentId={props.componentId}
            className={
                (props?.comingSoonLabel ||
                (props?.startDate &&
                    props?.endDate ))
                    ? "min-h-[70px] !mb-4"
                    : "min-h-0 !mb-0"
            }
        >
            {
                (props?.comingSoonLabel ||
                    (props?.startDate &&
                        props?.endDate)) && (
                        <DateWidgetComp {...props} />
                    )}
        </Wrapper>
    );
};

export default DateWidget;

