import React, { useEffect, useMemo } from "react";

import { useResize } from "../../../util/hooks/useResize";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { AnchorsProps } from "./IAnchors";

const AnchorsComp = React.lazy(() => import("./AnchorsComp"));

const Anchors: React.FC<AnchorsProps> = (props) => {
  const { isMobile } = useResize();

  // // set margin bottom if there is a component under the anchors
  // useEffect(() => {
  //   setTimeout(() => {
  //     const rightColHeight = document.querySelector(".rightCol1")?.clientHeight;
  //     const anchorsHeight = document.querySelector(".anchors")?.clientHeight;
  //     if (rightColHeight && anchorsHeight) {
  //       if (rightColHeight >= anchorsHeight+80) {
  //         document.querySelector(".anchorsSelector")?.classList.remove("md:!mb-0");

  //       }else{
  //         document.querySelector(".anchorsSelector")?.classList.add("md:!mb-0");
  //       }
  //     }
  //   }, 1900);

  // }, [])

  const shouldShowAnchorsComp = useMemo(() => {
    return (
      props.title &&
      props.links?.length > 0 &&
      ((isMobile && !!props.showInResponsive) || !isMobile)
    );
  }, [isMobile, props.showInResponsive, props.title, props.links]);

  return (
    <>
      {shouldShowAnchorsComp && <Wrapper componentId={props.componentId} className={`min-h-20 anchorsSelector md:!mb-0`}>
        {<AnchorsComp {...props} />}
      </Wrapper>}
    </>

  );
};

export default Anchors;