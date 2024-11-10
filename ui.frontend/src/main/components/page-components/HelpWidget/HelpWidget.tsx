import React from "react";

import { IHelpWidgetProps } from "./HelpWidgetCard/IHelpWidgetProps";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";

const HelpWidgetComp = React.lazy(() => import("./HelpWidgetComp"));

const HelpWidget: React.FC<IHelpWidgetProps> = (props) => {
    const hasNonWhiteSpaceContent = (str: string) => str && str.trim() !== "";

    return (
        <Wrapper componentId={props.componentId}
            className={
                (hasNonWhiteSpaceContent(props?.description) ||
                (hasNonWhiteSpaceContent(props?.cta?.text) &&
                    hasNonWhiteSpaceContent(props?.cta?.url)) ||
                hasNonWhiteSpaceContent(props?.title))
                    ? "min-h-[200px] !mb-4"
                    : "min-h-0 !mb-0"
            }
        >
            {(hasNonWhiteSpaceContent(props?.description) ||
                (hasNonWhiteSpaceContent(props?.cta?.text) &&
                    hasNonWhiteSpaceContent(props?.cta?.url)) ||
                hasNonWhiteSpaceContent(props?.title)) && (
                <HelpWidgetComp {...props} />
            )}
        </Wrapper>
    );
};

export default HelpWidget;

