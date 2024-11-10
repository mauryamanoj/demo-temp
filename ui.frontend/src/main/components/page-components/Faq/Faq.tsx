import React from 'react';
import Wrapper from '../../common/HOC/Wrapper/Wrapper';
import { FaqProps } from './IFaq';

const FaqComp = React.lazy(()=> import("./FaqComp"));

const Faq: React.FC<FaqProps> = (props) => {
  return (
    <Wrapper  componentId={props.componentId} className="min-h-[30vh] mx-5 md:mx-100">
      <FaqComp {...props}/>
    </Wrapper>
  );
};
export default Faq;
