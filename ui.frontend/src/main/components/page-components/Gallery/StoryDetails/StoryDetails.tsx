import React from "react";
import Button from "src/main/components/common/atoms/Button/Button";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Text from "src/main/components/common/atoms/Text/Text";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";
const GalleryDetailsComp = React.lazy(() => import("../components/GalleryDetailsComp"));

const StoryDetails = ({ gallery, moreLabel, title, link, description, directionsLink,componentId }: any) => {
  return (
    <Wrapper componentId={componentId} className="min-h-[95px] md:min-h-[30vh] lg:pb-0 pb-10">
      <div className="lg:flex justify-between items-center pb-6 md:pb-8">
        {title ? <Text text={title} type="h1" styles="text-3xl md:text-5xl font-primary-bold" /> : null}
        {link && link.url && link.text ? (
          <a
            href={link.url}
            title={link.text}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 font-primary-bold"
          >
            {link.text.slice(0,30)}
          </a>
        ) : null}
      </div>
      <GalleryDetailsComp gallery={gallery} moreLabel={moreLabel} story={true}/>
      {description ? (
        <div
          className="text-base font-primary-regular my-6 md:my-4 richTextContainer"
          dangerouslySetInnerHTML={{ __html: description }}
        ></div>
      ) : (
        <></>
      )}
      {directionsLink && directionsLink.url && directionsLink.text ? (
        <div className="flex justify-start float-left">
          <Button
            title={
              <div className="flex gap-2 items-center">
                <div>{directionsLink.text}</div>
                <Icon name="chevron" svgClass={`w-3 h-3  rtl:rotate-180`} />
              </div>
            }
            arrows={false}
            onclick={() => window.open(directionsLink.url, directionsLink?.targetInNewWindow ? "_blank" : "_self")}
          />
        </div>
      ) : null}
    </Wrapper>
  );
};

export default StoryDetails;
