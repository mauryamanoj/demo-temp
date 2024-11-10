export interface AboutSectionInterface {
    aboutTitle: string;
    aboutDescription: string;
    hideReadMore: boolean;
    readMoreLabel: string;
    readLessLabel: string;
    hideFavorite: boolean;
    hideShare: boolean;
    categoriesTags: {
        title: string;
        icon: string;
    }[];
    updateFavUrl: string;
    deleteFavUrl: string;
    getFavUrl: string;
    favId: string;
    componentId:string;
}
