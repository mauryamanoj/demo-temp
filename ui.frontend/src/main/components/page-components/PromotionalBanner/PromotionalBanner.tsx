import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const PromotionalBannerComp = React.lazy(() => import("./PromotionalBannerComp"));

const PromotionalBanner: React.FC<any> = (props) => {

  return (
    <Wrapper componentId={props.componentId  || 'PromotionalBannerWrapperId'} className={`min-h-[444px]${props.isNotOnTop ? "" : " -mt-16 lg:-mt-20"}`}>
      <PromotionalBannerComp {...props} />
    </Wrapper>
  );
};

export default PromotionalBanner;
