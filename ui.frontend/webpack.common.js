/* eslint-disable @typescript-eslint/no-var-requires */
'use strict';

const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const TSConfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const ESLintPlugin = require('eslint-webpack-plugin');
const jsonImporter = require('node-sass-json-importer');
const webpack = require('webpack');

const SOURCE_ROOT = __dirname + '/src/main';

const resolve = {
    extensions: ['.js', '.ts', '.tsx', '.json', '.css'],
    plugins: [new TSConfigPathsPlugin({
        configFile: './tsconfig.json'
    })],
    modules: [path.resolve(SOURCE_ROOT, 'src/main'), 'node_modules'],
};

const resolveLoader = {
    modules: [
        'node_modules'
    ]
};

module.exports = {
    resolve: resolve,
    resolveLoader: resolveLoader,
    entry: {
        site: SOURCE_ROOT + '/site/main.ts'
    },
    output: {
        filename: (chunkData) => {
            return chunkData.chunk.name === 'dependencies' ?
                'clientlib-dependencies/[name].js' : 'clientlib-site/[name].js';
        },
        chunkFilename: 'clientlib-dynamic-modules/resources/[name].js',
        path: path.resolve(__dirname, 'dist')
    },
    module: {
        rules: [
            /*
            * ------------------------------------------------
            * JavaScript
            * ------------------------------------------------
            */
            {
                test: /\.js?$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'babel-loader?cacheDirectory',
                    },
                    {
                        loader: 'glob-import-loader',
                        options: {
                            // resolve: resolve
                        }
                    }
                ]
            },
            /*
            * ------------------------------------------------
            * TypeScript
            * ------------------------------------------------
            */
            {
                test: /\.tsx?$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'ts-loader'
                    },
                    {
                        loader: 'glob-import-loader',
                        options: {
                            resolve: resolve
                        }
                    }
                ]
            },
            /*
            * ------------------------------------------------
            * Styling (scss and css)
            * ------------------------------------------------
            */

            {
                test: /\.css$/,
                use: [MiniCssExtractPlugin.loader, 'css-loader', 'postcss-loader'],
            },
            {
                test: /\.(woff|woff2|eot|ttf|otf)$/,
                use: {
                  loader: 'file-loader',
                  options: {
                    name: '[name].[ext]',
                    outputPath: './clientlib-dynamic-modules/resources/fonts/', // Customize the output path as needed
                  },
                },
              },
            /*
           * ------------------------------------------------
           * Images and SVG
           * ------------------------------------------------
           */
            {
                test: /\.(png|jpe?g|gif)(\?.*)?$/,
                exclude: /node_modules/,
                type: 'asset/resource'
            },
            {
                test: /\.svg$/,
                exclude: /node_modules/,
                oneOf: (() => {
                    const svgoLoaderConfig = {
                        loader: 'svgo-loader'
                    };

                    return [
                        {
                            resourceQuery: /inline/,
                            use: [{ loader: 'svg-inline-loader' }, svgoLoaderConfig],
                        },
                        {
                            use: [{ loader: 'url-loader' }, svgoLoaderConfig],
                        },
                        {
                            loader: "react-svg-loader",
                            options: {
                                svgo: {
                                    plugins: [
                                        { removeTitle: false }
                                    ],
                                    floatPrecision: 2
                                }
                            }
                        }
                    ];
                })(),
            }
        ]
    },
    plugins: [
        new CleanWebpackPlugin(),
        new ESLintPlugin({
            extensions: ['js', 'ts', 'tsx']
        }),
        new MiniCssExtractPlugin({
            filename: 'clientlib-[name]/[name].css',
            chunkFilename: 'clientlib-dynamic-modules/resources/[name].css',
            ignoreOrder: true
        }),
        new CopyWebpackPlugin({
            patterns: [
                {
                    from: path.resolve(__dirname, SOURCE_ROOT + '/resources'),
                    to: './clientlib-dynamic-modules/resources'
                }
            ]
        }),
        new webpack.DefinePlugin({
            'process.env': {
                'PUBLIC_RECAPTCHA_SITE_KEY': JSON.stringify('6LdwwdUbAAAAADg54vQQTN9j0EN4X73aEZ1eO0oe'),
                'MAP_TOKEN_GOOGLE': JSON.stringify('AIzaSyAtOW1UiNGs4xMGdImZpgP2Et0Xts3D9iw'),
            }
        }),
    ],
    stats: {
        assetsSort: 'chunks',
        builtAt: true,
        children: false,
        chunkGroups: true,
        chunkOrigins: true,
        colors: false,
        errors: true,
        errorDetails: true,
        env: true,
        modules: false,
        performance: true,
        providedExports: false,
        source: false,
        warnings: false
    }
};
