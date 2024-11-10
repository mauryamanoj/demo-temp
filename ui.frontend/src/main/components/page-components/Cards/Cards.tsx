import React from 'react';
import Wrapper from '../../common/HOC/Wrapper/Wrapper';
const CategoryCards =  React.lazy(()=> import('./Category/CategoryCards')) ;
const DealsCards =  React.lazy(()=> import('./Deals/DealsCards'));
const OffersCards =  React.lazy(()=> import('./Offers/OffersCards'));
const OthersCards =  React.lazy(()=> import('./Others/OthersCards'));
const GuideCards =  React.lazy(()=> import('./Guide/GuideCards'));
const MainCardsSlider =  React.lazy(()=> import('./Main/MainCardsSlider'));
const DestinationCard =  React.lazy(()=> import('./Destination/DestinationCard'));
const FeaturedDestinationCard =  React.lazy(()=> import('./Destination/FeaturedDestinationCard'));
const BuyCards =  React.lazy(()=> import('./Buy/BuyCards'));
const ExploringCards =  React.lazy(()=> import('./Exploring/ExploringCards'));
const AttractionCard =  React.lazy(()=> import('./Attraction/AttractionCard'));
const HighlightedCards =  React.lazy(()=> import('./Highlighted/HighlightedCards'));
const AppListCards =  React.lazy(()=> import('./AppList/AppListCards'));
const DestinationGuideCards =  React.lazy(()=> import('./DestinationGuide/DestinationGuideCards'));


const Cards = (props: any) => {
    const cardType = props.type;

    return (
        <Wrapper componentId={props.componentId|| 'CardsWrapperId'} className="min-h-[200px] !mb-0 md:!mb-0">
          {/* will change the classname later just needed every component to be lazy loaded */}
            {cardType == 'CategoryCards' && <CategoryCards {...props} />}
            {cardType == 'Map' && <OthersCards {...props} />}
            {cardType == 'Other' && <OthersCards {...props} />}
            {cardType == 'AttractionCard' && <AttractionCard {...props} />}
            {cardType == 'Story' && <OthersCards {...props} />}
            {cardType == 'DealsCards' && <DealsCards {...props} />}
            {cardType == 'OffersCards' && <OffersCards {...props} />}
            {(cardType == 'GuideCardsItem' ||cardType == 'GuideCardsSlider') && <GuideCards {...props}/>}
            {cardType == 'BuyCards' && <BuyCards {...props} />}
            {cardType == 'ExploringCards' && <ExploringCards {...props} />}
            {cardType == 'MainCardsSlider' && <MainCardsSlider {...props} />}
            {cardType == 'DestinationCard' && <DestinationCard {...props} />}
            {cardType == 'FeaturedDestinationCard' && <FeaturedDestinationCard {...props} />}
            {cardType == 'HighlightedCards' && <HighlightedCards {...props} />}
            {cardType == 'AppListCards' && <AppListCards {...props} />}
            {cardType == 'DestinationGuideCards' && <DestinationGuideCards {...props} />}


        </Wrapper>
    );


};

export default Cards;
