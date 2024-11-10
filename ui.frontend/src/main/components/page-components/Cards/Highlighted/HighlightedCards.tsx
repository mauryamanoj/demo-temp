import React from 'react';
import HighlightedCardsComp from './HighlightedCardsComp';

const Faq: React.FC<any> = (props) => {
  return (
    <div className='min-h-[400px] relative mb-16 md:mb-20'>
      <HighlightedCardsComp {...props}/>
    </div>
  );
};
export default Faq;
