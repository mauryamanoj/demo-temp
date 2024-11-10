import React from 'react';

import ButtonAtom from 'src/main/components/common/atoms/Button/Button';
import Overlay from '../../common/atoms/Overlay/Overlay';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import OrnamentsBars from '../../common/atoms/Ornaments/OrnamentsBrushes';
import OrnamentsBrushes from '../../common/atoms/Ornaments/OrnamentsBrushes';
import Picture from '../../common/atoms/Picture/Picture';
import Collapsible from '../../common/atoms/Collapsible/Collapsible';
import Stars from '../../common/atoms/Stars/Stars';
import DateCard from '../../common/atoms/DateCard/DateCard';
import Pill from '../../common/atoms/Pill/Pill';
import SliderArrow from '../../common/atoms/SliderArrow/SliderArrow';

const AtomsShowcase = (props: any) => {
    const { atom } = props;

    return (
        <>
            {atom == 'Button' && <ButtonAtom {...props} />}
            {atom == 'Overlay' && <Overlay {...props} />}
            {atom == 'Icon' && <Icon {...props} />}
            {atom == 'OrnamentsBars' && <OrnamentsBars {...props} />}
            {atom == 'OrnamentsBrushes' && <OrnamentsBrushes {...props} />}
            {atom == 'Picture' && <Picture {...props} />}
            {atom == 'Collapsible' && <Collapsible {...props} />}
            {atom == 'Stars' && <Stars {...props} />}
            {atom == 'DateCard' && <DateCard {...props} />}
            {atom == 'Pill' && <Pill {...props} />}
            {atom == 'SliderArrow' && <SliderArrow {...props} />}
        </>
    );
};

export default AtomsShowcase;
