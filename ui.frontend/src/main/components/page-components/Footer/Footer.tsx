/* eslint-disable max-len */
import React from "react";
import { isAuthorView } from "src/main/util/type/helpers";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { FooterInterface } from "./IFooter";

const FooterComp = React.lazy(() => import("./FooterComp"));

const Footer: React.FC<FooterInterface> = (props) => {
    return (
        <Wrapper  componentId={props.componentId|| 'FooterWrapperId'} className={`${!!isAuthorView() ? 'min-h-[400px]':'min-h-[40vh]'} !mb-0`}>
            <footer id="footer">
                <FooterComp {...props} />
            </footer>
        </Wrapper>
    );
};

export default Footer;

