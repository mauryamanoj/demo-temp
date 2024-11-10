import React from 'react';
import Wrapper from 'src/main/components/common/HOC/Wrapper/Wrapper';

const VisaRegulationComp = React.lazy(()=> import("./VisaRegulationComp"));

const VisaRegulation: React.FC<any> = (props) => {
  return (
    <Wrapper componentId={props.componentId} className="min-h-[30vh]">
      <VisaRegulationComp {...props}/>
    </Wrapper>
  );
};
export default VisaRegulation;
