import React from "react";
import DestinationCard from "src/main/components/page-components/Cards/Destination/DestinationCard";
import Text from 'src/main/components/common/atoms/Text/Text';
import { DestinationCardProps } from 'src/main/components/page-components/Cards/Destination/IDestinationCard';

interface AllDestinationsProps {
  title: string;
  destinations: DestinationCardProps[];
}

const AllDestinations = ({ destinations, title }: AllDestinationsProps) => {

  return (
    <section className="px-5 lg:px-100 mb-16 md:mb-20">
      {title && <Text text={title} isTitle />}

      {destinations && destinations.length > 0 ?
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {destinations.map((item: any, index: any) => (
            <div key={index}>
              <DestinationCard {...item} />
            </div>
          )
          )}
        </div>
        : <></>}
    </section>
  );
};

export default AllDestinations;
