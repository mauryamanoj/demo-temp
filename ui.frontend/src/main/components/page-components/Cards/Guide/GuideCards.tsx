import React from "react";
import GuideCardsComp from "./GuideCardsComp";

const GuideCards: React.FC<any> = (props) => {
  return (
    <div className="min-h-96 relative mb-16 md:mb-20">
      <GuideCardsComp {...props} />
    </div>
  );
};

export default GuideCards;
