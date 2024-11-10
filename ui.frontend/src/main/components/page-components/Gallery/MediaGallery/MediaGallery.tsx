import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";
const GalleryDetailsComp = React.lazy(()=> import("../components/GalleryDetailsComp")) ;

const MediaGallery = (props:any) => {
  return (
    <Wrapper  componentId={props.componentId || 'MediaGalleryWrapperId'} className="min-h-[95px] md:min-h-[464px] md:mx-100 md:!mb-10">
      <GalleryDetailsComp {...props}/>
    </Wrapper>
  );
};

export default MediaGallery;
