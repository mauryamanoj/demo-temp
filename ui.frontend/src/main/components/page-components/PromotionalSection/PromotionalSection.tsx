import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";

const PromotionalSectionComp = React.lazy(() => import("./PromotionalSectionComp"));

const SingleAppPromotionalBanner = (props: any) => {
  return (
    <Wrapper componentId={props.componentId}
      className={"min-h-[200px] !mb-20 md:!pt-100"}
      // please dnt remove margin top,otherwise the image at right will overlap on other components
    >
      {<PromotionalSectionComp {...props} />}
    </Wrapper>
  );
};

export default SingleAppPromotionalBanner;
