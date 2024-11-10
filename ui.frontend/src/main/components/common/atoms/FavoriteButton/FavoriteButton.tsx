/* eslint-disable max-len */
import React, { useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

import {
    isFavorite,
    deleteFavorite,
    updateFavoriteItems,
    isUserLogged,
} from "src/main/util/ssid-provider/ssidAction";

interface FavoriteButtonProps {
    id: string;
    boldIcon?: boolean;
    updateFavoriteUrl?: string;
    deleteFavoriteUrl?: string;
    wrapperClass?: string;
}

const FavoriteButton: React.FC<FavoriteButtonProps> = ({
    id,
    wrapperClass,
    boldIcon,
}) => {
    const [isLogged] = useState<boolean>(isUserLogged());
    const [favoriteItem, setFavoriteItem] = useState<boolean>(isFavorite(id));

    const handleClickFavIcon = (e: any) => {
        e.stopPropagation()
        if (!isLogged) {
            const loginModal = document.getElementById("login-modal-component");
            loginModal?.classList.remove("invisible");
            loginModal?.classList.remove("hidden");
            loginModal?.classList.add("visible");
            document.body.style.overflow = "hidden";
        } else {
            if (favoriteItem) {
                deleteFavorite(id);
                setFavoriteItem(false);
            } else {
                updateFavoriteItems(id);
                setFavoriteItem(true);
            }
        }
    };

    return (
        <>
            <div
                className={`cursor-pointer items-center justify-center flex w-8 h-8
                           bg-black/50 rounded-full hover:bg-black/70 ${wrapperClass}`}
                onClick={handleClickFavIcon}
            >
                <Icon
                    name={
                        !favoriteItem && boldIcon
                            ? "heart-bold"
                            : !favoriteItem
                            ? "heart"
                            : "heart-active"
                    }
                />
            </div>
        </>
    );
};

export default FavoriteButton;

