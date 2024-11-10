import React from 'react';
import Text from '../../common/atoms/Text/Text';

interface ParagraphData {
    paragraph_title: string;
    paragraph_description: string;
    header_title: string;
    header_description: string;
}

const ParagraphPage = (props: ParagraphData) => {

    const { paragraph_title = "", paragraph_description = "", header_title="", header_description="" } = props;

    return (
        <div className="container mx-auto text-dark px-4 py-4">
            <div className="mb-16">
                <div className="lg:flex justify-between items-center">
                    {/* start text */}
                    <Text styles="text-xl lg:text-3.5xl lg:mb-16" text={paragraph_title} />
                    {/* end text */}
                    <span className="text-theme text-sm">Button Label</span>
                </div>
                <div className="lg:flex justify-between items-center">
                    <div>
                        {/* start text */}
                        <Text styles="text-xl lg:text-3.5xl lg:mb-4" text={paragraph_title} />
                        {/* end text */}

                        {/* start text */}
                        <Text styles="text-lg lg:text-1.5xl" text={paragraph_description} />
                        {/* end text */}
                    </div>
                    <span className="text-theme text-sm">Button Label</span>
                </div>
            </div>
            <div>
                <div className="lg:flex justify-between items-center">
                    {/* start text */}
                    <Text styles="text-3xl lg:text-5xl lg:mb-16" text={header_title} />
                    {/* end text */}
                    <span className="text-theme text-sm">Button Label</span>
                </div>
                <div className="lg:flex justify-between items-center">
                    <div>
                        {/* start text */}
                        <Text styles="text-3xl lg:text-5xl lg:mb-4" text={header_title} />
                        {/* end text */}

                        {/* start text */}
                        <Text styles="text-lg lg:text-1.5xl" text={header_description} />
                        {/* end text */}
                    </div>
                    <span className="text-theme text-sm">Button Label</span>
                </div>
            </div>
        </div>
    );
};

export default ParagraphPage;
