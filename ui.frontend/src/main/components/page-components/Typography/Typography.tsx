/* eslint-disable max-len */
import React from 'react';
import Text from '../../common/atoms/Text/Text';

const Typography: React.FC<any> = () => {

    return (
        <div className="lg:container mx-auto pt-20 p-10">
            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div>
                    <Text styles="text-3.5xl lg:mb-16 font-secondary" text="Displace 2.0" type="h2" />
                    <Text styles="text-xl lg:text-3.5xl lg:mb-16 font-tertiary-regular mb-20" text="Regular" />
                    <Text styles="text-1.5xl lg:mb-16 font-secondary-regular mb-20 lg:max-w-lg mr-4" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />
                </div>
                <div>
                    <Text styles="text-3.5xl font-secondary-regular mb-20" text="Regular" />
                    <Text styles="text-5.5xl lg:text-8xl font-secondary-regular" text="Displace 2.0 / 80 H1 / Regular" />
                    <Text styles="text-4.5xl font-secondary-regular mb-20" text="Displace 2.0 / 46 H1 / Regular" />
                </div>
            </div>
            {/* end section */}

            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div>
                    <Text styles="text-3.5xl font-primary-semibold mb-20" text="Gilroy" type="h2" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-regular mb-20" text="Regular" type="h2" />
                    <Text styles="text-1.5xl font-primary-regular mb-20 lg:max-w-lg mr-4" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />
                </div>
                <div>
                    <Text styles="text-3.5xl font-tertiary-regular mb-5" text="Book" />
                    <Text styles="text-3xl lg:text-5xl font-primary-regular mb-5" text="Gilroy / 48 H2 / Regular" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-regular mb-5" text="Gilroy / 32 H3 / Regular" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-regular mb-5" text="Gilroy / 22 H4 / Regular" />
                    <Text styles="text-base lg:text-lg font-tertiary-regular mb-5" text="Gilroy / 18 H5 / Regular" />
                    <Text styles="text-base font-tertiary-regular mb-5" text="Gilroy / 16 Body / Regular" />
                    <Text styles="text-xs lg:text-sm font-tertiary-regular mb-5" text="Gilroy / 14 Tags / Regular" />
                    <Text styles="text-xs font-tertiary-regular mb-5" text="Gilroy / 12 Small Caption / Regular" />
                    <Text styles="text-xs font-tertiary-regular mb-5" text="Gilroy / 12 Small Caption / SemiBold" />

                </div>
            </div>
            {/* end section */}


            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div>
                    <Text styles="font-secondary-bold text-3.5xl mb-20" text="Displace 2.0" type="h2" />
                    <Text styles="font-primary-regular mb-20 text-3.5xl" text="Medium" type="h2" />
                    <Text styles="mb-50 text-1.5xl  font-secondary-regular lg:max-w-lg mr-4 mb-16 pb-2" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" type="p" />

                    <Text styles="font-primary-semibold text-xl lg:text-3.5xl mb-20" text="Gilroy" type="h2" />
                    <Text styles="font-primary-semibold mb-20 text-xl lg:text-3.5xl" text="SemiBold" type="p" />
                    <Text styles="mb-20 text-1.5xl font-primary-semibold lg:max-w-lg mr-4" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" type="p" />
                </div>
                <div>
                    <Text styles="text-xl lg:text-3.5xl font-secondary-regular lg:font-tertiary" text="Medium" type="p" />
                    <Text styles="text-5.5xl lg:text-8xl font-secondary" text="Displace 2.0 / 80 H1 / Medium" type="p" />
                    <Text styles="text-4.5xl font-secondary-regular mb-20" text="Displace 2.0 / 46 H1 / Medium" type="p" />


                    <Text styles="text-3.5xl font-tertiary  mb-5" text="SemiBold" type="p" />
                    <Text styles="text-3xl lg:text-5xl font-primary-semibold mb-5" text="Gilroy / 48 H2 / SemiBold" type="p" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-semibold mb-5" text="Gilroy / 22 H4 / SemiBold" type="p" />
                    <Text styles="text-base lg:text-lg font-primary-semibold mb-5" text="Gilroy / 18 H5 / SemiBold" type="p" />
                    <Text styles="text-sm lg:text-base font-primary-semibold mb-5" text="Gilroy / 16 Body / SemiBold" type="p" />
                    <Text styles="text-xs lg:text-sm font-primary-semibold mb-5" text="Gilroy / 14 Tags / SemiBold" type="p" />
                    <Text styles="text-xxs lg:text-xs font-primary-semibold mb-5" text="Gilroy / 12 Small Caption / SemiBold" type="p" />

                </div>
            </div>
            {/* end section */}



            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div>
                    <Text styles="font-secondary-bold text-3.5xl mb-20" text="Displace 2.0" type="h2" />
                    <Text styles="font-primary-bold mb-20 text-3.5xl" text="Bold" type="h2" />
                    <Text styles="mb-50 text-1.5xl font-secondary-regular lg:max-w-lg mr-4" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />

                    <Text styles="font-primary-bold text-3.5xl mb-20 mt-20" text="Gilroy" type="h2" />
                    <Text styles="font-primary-bold mb-20 text-3.5xl" text="Bold" />
                    <Text styles="mb-20 text-1.5xl font-primary-bold lg:max-w-lg mr-4" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />
                </div>
                <div>
                    <Text styles="text-3.5xl font-tertiary-bold text-bold" text="Bold" />
                    <Text styles="text-5.5xl lg:text-8xl font-secondary-bold mb-20" text="Displace 2.0 / 80 H1 / Bold" />
                    <Text styles="text-3.5xl font-tertiary-bold mb-20 text-bold" text="Bold" />
                    <Text styles="text-3xl lg:text-5xl font-primary-bold mb-5" text="Gilroy / 48 H2 / Bold" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-bold mb-5" text="Gilroy / 32 H2 / Bold" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-bold mb-5" text="Gilroy / 22 H4 / Bold" />
                    <Text styles="text-base lg:text-lg font-primary-bold mb-5" text="Gilroy / 18 H5 / Bold" />
                    <Text styles="text-sm lg:text-base font-primary-bold mb-5" text="Gilroy / 16 Body / Bold" />
                    <Text styles="text-xs lg:text-sm font-primary-bold mb-5" text="Gilroy / 14 Tags / Bold" />
                    <Text styles="text-xxs lg:text-xs font-primary-bold mb-5" text="Gilroy / 12 Small Caption / Bold" />

                </div>
            </div>
            {/* end section */}




            <div className="font-blk">
                <Text styles="font-primary-regular text-4xl" type="h1" text="Fonts types:" />
                <Text styles="font-secondary-regular text-2xl mt-10" type="h2" text="displace20" />
                <Text styles="font-secondary-regular text-2xl" type="h3" text="Regular" />
                <Text styles="font-secondary-semibold text-2xl" type="h3" text="SemiBold" />
                <Text styles="font-secondary-bold text-2xl" type="h3" text="Bold" />


                <Text styles="font-primary-regular text-2xl mt-10" type="h3" text="Gilroy" />
                <Text styles="font-primary-regular text-2xl" type="h3" text="Regular" />
                <Text styles="font-primary-semibold text-2xl" type="h3" text="SemiBold" />
                <Text styles="font-primary-bold text-2xl" type="h3" text="Bold" />


                <Text styles="font-tertiary text-2xl mt-10" type="h3" text="Gotham" />
                <Text styles="font-tertiary-regular text-2xl" type="h3" text="Regular" />
                <Text styles="font-tertiary-semibold text-2xl" type="h3" text="SemiBold" />
                <Text styles="font-tertiary-bold text-2xl" type="h3" text="Bold" />

            </div>
        </div>


    );
};

export default Typography;
