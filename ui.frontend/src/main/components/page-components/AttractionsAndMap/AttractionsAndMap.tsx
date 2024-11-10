import React, { useEffect, useRef, useState } from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { AttractionsAndMapProps } from "./IAttractionsAndMap";
const AttractionsAndMapComp = React.lazy(() => import("./AttractionsAndMapComp"));

const AttractionsAndMap: React.FC<AttractionsAndMapProps> = (props) => {

  const [layout2Cols, setIslayout2Cols] = useState(false);
  useEffect(() => {
    const selector = document.querySelector('.AttractionsAndMapComp');
    const layout2Cols = selector?.closest(".layout2Cols");
    if (layout2Cols) setIslayout2Cols(true);
  }, []);

  return (
    //props.mapApiKey ?
      <Wrapper componentId={props.componentId || 'AttractionsAndMapWrapperId'} className={`AttractionsAndMapComp min-h-[441.5px] ${layout2Cols ? 'mx-[-20px] md:mx-0' : 'md:px-5 lg:px-100'}`}>
        <AttractionsAndMapComp {...props} />
      </Wrapper> 
  );
};

export default AttractionsAndMap;
