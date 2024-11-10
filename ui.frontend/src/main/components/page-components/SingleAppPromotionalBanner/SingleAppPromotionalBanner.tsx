import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";

import { SingleAppPromotionalBannerProps } from "./ISingleAppPromotionalBanner";

const SingleAppPromotionalBannerComp = React.lazy(
    () => import("./SingleAppPromotionalBannerComp")
);

const SingleAppPromotionalBanner = (props: SingleAppPromotionalBannerProps) => {

    return (
        <Wrapper componentId={props.componentId|| 'SingleAppPromotionalBannerWrapperId'}
            className={"min-h-[200px] !pt-[100px] md:!pt-[90px]"}
        >
            {(
                <SingleAppPromotionalBannerComp
                    { ...props}
                />
            )}
        </Wrapper>
    );
};

export default SingleAppPromotionalBanner;

