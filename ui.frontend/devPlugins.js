/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/no-var-requires */
const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const apiMocker = require('connect-api-mocker');
const { file } = require("connect-api-mocker/helpers");
const SOURCE_ROOT = __dirname + "/src/main";
module.exports = (env) => {
    const writeToDisk = env && Boolean(env.writeToDisk);
    return {
        plugins: [
            new HtmlWebpackPlugin({
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/index.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "home-page.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/pages/home-page.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "AtomsShowcase.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/pages/AtomsShowcase.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "breadcrumbs.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/breadcrumbs.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "anchors.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/anchors.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "footer.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/footer.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "texture-overlay.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/texture-overlay.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "titles.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/titles.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "typography.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/typography.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "typographyArabic.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/typographyArabic.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "tabs.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/tabs.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "search-result.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/search-result.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "cards.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/cards.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "bannerSlider.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/bannerSlider.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "latestStories.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/latestStories.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "helpWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/helpWidget.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "promotionalBanner.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/promotionalBanner.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "promotionalSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/promotionalSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "destinations.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/pages/destinations.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "attractions.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/pages/attractions.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "AboutSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/AboutSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "mainMenu.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/mainMenu.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "informationSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/informationSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "headerSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/headerSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "popup.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/popup.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "alerts.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/alerts.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "errors.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/errors.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "contactUs.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/contactUs.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "mapWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/mapWidget.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "reachUs.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/reachUs.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "dateWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/dateWidget.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "specialShowWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/specialShowWidget.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "textImageSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/textImageSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "gallery.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/gallery.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "singleAppPromotionalBanner.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT +
                        "/static/components/singleAppPromotionalBanner.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "priceWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/priceWidget.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "informationListWidget.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT +
                        "/static/components/informationListWidget.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "things-to-do-cards.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/things-to-do-cards.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "things-to-do-explorer.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/things-to-do-explorer.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "stories-explorer.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/stories-explorer.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "faq.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/faq.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "neighborhoodsSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/neighborhoodsSection.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "offersAndDeals.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/offersAndDeals.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "visa-regulation.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/visa-regulation.html"
                ),
            }),
            new HtmlWebpackPlugin({
                filename: "filterSection.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/filterSection.html"
                ),
            }),

            new HtmlWebpackPlugin({
                filename: "LoginModal.html",
                template: path.resolve(
                    __dirname,
                    SOURCE_ROOT + "/static/components/LoginModal.html"
                ),
            }),

            new HtmlWebpackPlugin({
              filename: "loyalty-enrollement.html",
              template: path.resolve(__dirname, SOURCE_ROOT + '/static/components/loyalty-enrollement.html')
          }),
        ],
        devServer: {
            // commented as FE server is run locally without AEM
            // proxy: [{
            //     context: ['/', HOME_DIR],
            //     target: 'http://localhost:9000',
            // }],
            static: {
                directory: path.join(__dirname, "public"),
            },
            compress: true,
            port: 9000,
            historyApiFallback: true,
            client: {
                overlay: {
                    errors: true,
                    warnings: false,
                },
            },
            onBeforeSetupMiddleware(devServer) {
                // Mock API configuration for development
                devServer.app.use('/bin/api/v3', apiMocker('./mocks/api'));
                devServer.app.use('/bin/api/v1', apiMocker('./mocks/api'));




                devServer.app.use(
                    "/content/dam/sauditourism/arts3.png",
                    file("./mocks/images/arts3.png")
                );

                devServer.app.use(
                    "/content/dam/sauditourism/app_apple.png",
                    file("./mocks/images/app_apple.png")
                );

                devServer.app.use(
                    "/content/dam/sauditourism/app_googleplay.png",
                    file("./mocks/images/app_googleplay.png")
                );

                devServer.app.use(
                    "/content/dam/sauditourism/app_gallery.png",
                    file("./mocks/images/app_gallery.png")
                );

                devServer.app.use(
                    "/content/dam/sauditourism/visit_saudi_logo.png",
                    file("./mocks/images/visit_saudi_logo.png")
                );

                devServer.app.use(
                    "/content/dam/sauditourism/sta_logo.png",
                    file("./mocks/images/sta_logo.png")
                );
            },
            watchFiles: ["src/**/*"],
            hot: false,
            devMiddleware: {
                writeToDisk: writeToDisk,
            },
        },
    };
};

