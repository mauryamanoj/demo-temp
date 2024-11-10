import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";
import { SpecialShowWidgetProps } from "./ISpecialShowWidget";
const SpecialShowWidgetComp = React.lazy(() => import("./SpecialShowWidgetComp"));

const SpecialShowWidget = (props: SpecialShowWidgetProps) => {
  const visible = props.data?.length >0;
  return (
    <Wrapper componentId={props.componentId} className={visible?"min-h-[200px] !mb-4":"!mb-0"}>
     {visible? <SpecialShowWidgetComp {...props} /> : <></>}
    </Wrapper>
  );
};

export default SpecialShowWidget;
