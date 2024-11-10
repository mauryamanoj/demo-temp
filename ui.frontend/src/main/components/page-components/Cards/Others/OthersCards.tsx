/* eslint-disable max-len */
import React, { useEffect } from 'react';
import './Card/Card.css';
import Text from '../../../common/atoms/Text/Text';
import StoryCard from '../Story/Story';
import OtherOrMap from './Card/Card';

export interface SimpleListProps {
    alt?: string;
    description?: string;
    title: string;
    type: any,
    cards: any
}


const OthersCards: React.FC<SimpleListProps> = (props) => {

    const { cards, type } = props;

    function handleResize() {
        innerWidth = window.innerWidth;
    }


    useEffect(() => {
        window.addEventListener('resize', handleResize);
    }, []);


    return (
        <div className="container m-auto p-4">
            {type == 'Other' || type == 'Map' ? <>
                <Text text={type + ' Card'} styles="title font-tertiary-medium text-lg lg:text-1.5xl text-theme-100" type="h6" />
                {cards?.map((card: any) => <div className="lg:inline-block mx-auto lg:mx-2">
                    <OtherOrMap card={card} type={type} />
                </div>)}
            </> :
                <>
                    {type == 'Story' ? <>
                        <Text text={type + ' Card'} styles="title gotham-medium text-lg lg:text-1.5xl text-theme-100" type="h6" />
                        <StoryCard props={props} /></> : ''}
                </>
            }
        </div>
    );
};

export default OthersCards;
