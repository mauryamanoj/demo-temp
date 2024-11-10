/* eslint-disable max-len */
import React from "react";
import { DestinationsAndMapProps } from "./IDestinationsAndMap";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
const DestinationsAndMapComp = React.lazy(() => import("./DestinationsAndMapComp"));

const DestinationsAndMap = (props: DestinationsAndMapProps) => {

  return (
    //props.mapApiKey &&
    <Wrapper componentId={props.componentId || 'DestinationsAndMapWrapperId'} className="min-h-[730px] md:min-h-[760px]">
      <DestinationsAndMapComp {...props} />
    </Wrapper>
  );
};

export default DestinationsAndMap;
