/* eslint-disable max-len */
import React from 'react';
import Text from '../../common/atoms/Text/Text';

const TypographyArabic: React.FC<any> = () => {

    return (
        <div className="container mx-auto pt-20 p-10">
            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div className="hidden sm:block">
                    <Text styles="text-xl lg:text-3.5xl lg:mb-24 font-primary-bold" text="Readex Pro" type="h2" />
                    <Text styles="text-lg lg:text-1.5xl lg:mb-16 font-primary-light mb-20 lg:max-w-lg mr-4" text="ا ب ت ث ج ح خ د ذ ر ز س ش ص ض ط ظ ع غ ف ق ك ل م ن ه و ي ١٢٣٤٥٦٧٨٩ !@#$%^&*()_-+؟" />
                </div>
                <div dir='rtl' className='text-left'>
                    <Text styles="text-3.5xl font-primary-light mb-20 mb-20" text="Regular" />
                    <Text styles="text-5.5xl lg:text-8xl font-primary-light mb-5" text="ريدكس برو / ٨٠ H1 / ريغلر" />
                    <Text styles="text-3xl lg:text-5xl font-primary-light mb-5" type="h2" text="ريدكس برو / ٤٨ H2 / ريغلر" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-light mb-5" type="h3" text="ريدكس برو / ٣٢ H3 / ريغلر" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-light mb-5" type="h4" text="ريدكس برو / ٢٢ H4 / ريغلر" />
                    <Text styles="text-base lg:text-lg font-primary-light mb-5" type="h5"  text="ريدكس برو / ١٨ H5 / ريغلر" />
                    <Text styles="text-sm lg:text-base font-primary-light mb-5" type="h6" text="ريدكس برو / ١٦ Body / ريغلر" />
                    <Text styles="text-xs lg:text-sm font-primary-light mb-5" text="ريدكس برو / ١٤ Tags / ريغلر" />
                    <Text styles="text-xxs lg:text-xs font-primary-light mb-5" text="ريدكس برو / ١٢ Small Captions / ريغلر" />
                </div>
            </div>
            {/* end section */}

            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div className="hidden sm:block">
                    <Text styles="text-lg lg:text-1.5xl font-primary-regular mb-20 lg:max-w-lg mr-4 invisible" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />
                </div>
                <div dir='rtl' className='text-left'>
                    <Text styles="text-xl lg:text-3.5xl font-tertiary-regular mb-5" text="Medium" />
                    <Text styles="text-5.5xl lg:text-8xl font-primary-regular mb-5" text="ريدكس برو / ٨٠ H1 / ميديوم" />

                    <Text styles="text-3xl lg:text-5xl font-primary-regular mb-5" type="h2" text="ريدكس برو / ٤٨ H2 / ميديوم" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-regular mb-5" type="h3" text="ريدكس برو / ٣٢ H3 / ميديوم" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-regular mb-5" type="h3" text="ريدكس برو / ٣٢ H3 / ميديوم" />
                    <Text styles="text-base lg:text-lg font-primary-regular mb-5" type="h5" text="ريدكس برو / ١٨ H5 / ميديوم" />
                    <Text styles="text-sm lg:text-base font-primary-regular mb-5"  text="ريدكس برو / ١٦ Body / ميديوم" />
                    <Text styles="text-xs lg:text-sm font-primary-regular mb-5" text="ريدكس برو / ١٤ Tags / ميديوم" />
                    <Text styles="text-xxs lg:text-xs font-primary-regular mb-5" text="ريدكس برو / ١٢ Small Captions / ميديوم" />
                </div>
            </div>
            {/* end section */}


            {/* start section  */}
            <div className={"flex align-item-center mb-20"}>
                <div className="hidden sm:block">
                    <Text styles="text-lg lg:text-1.5xl font-primary-regular mb-20 lg:max-w-lg mr-4 invisible" text="abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890!@#$%^&*()_-+?" />
                </div>
                <div dir='rtl' className='text-left'>
                    <Text styles="text-xl lg:text-3.5xl font-primary-light mb-5" text="Bold" />
                    <Text styles="text-5.5xl lg:text-8xl font-primary-bold mb-5" text="ريدكس برو / ٨٠ H1 / بولد" />

                    <Text styles="text-3xl lg:text-5xl font-primary-bold mb-5" type="h2" text="ريدكس برو / ٤٨ H2 / بولد" />
                    <Text styles="text-xl lg:text-3.5xl font-primary-bold mb-5" type="h3" text="ريدكس برو / ٣٢ H3 / بولد" />
                    <Text styles="text-lg lg:text-1.5xl font-primary-bold mb-5" type="h3" text="ريدكس برو / ٣٢ H3 / بولد" />
                    <Text styles="text-base lg:text-lg font-primary-bold mb-5" type="h5" text="ريدكس برو / ١٨ H5 / بولد" />
                    <Text styles="text-sm lg:text-base font-primary-bold mb-5"  text="ريدكس برو / ١٦ Body / بولد" />
                    <Text styles="text-xs lg:text-sm font-primary-bold mb-5" text="ريدكس برو / ١٤ Tags / بولد" />
                    <Text styles="text-xxs lg:text-xs font-primary-bold mb-5" text="ريدكس برو / ١٢ Small Captions / بولد" />
                </div>
            </div>
            {/* end section */}


            <div className="font-blk">
                <Text styles="font-primary-regular text-4xl" type="h1" text="Fonts types:" />
                <Text styles="font-primary-regular text-2xl mt-10" type="h2" text="ريدكس برو ميديوم" />
                <Text styles="font-primary-regular text-2xl" type="h3" text="ريدكس برو ريغلر" />
                <Text styles="font-primary-semibold text-2xl" type="h3" text="ريدكس برو سمي بولد" />
                <Text styles="font-primary-bold text-2xl" type="h3" text="ريدكس برو بولد" />
            </div>
        </div>


    );
};

export default TypographyArabic;
