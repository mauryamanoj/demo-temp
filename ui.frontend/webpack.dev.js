/* eslint-disable @typescript-eslint/no-var-requires */
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const TerserPlugin = require('terser-webpack-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const htmlPlugins = require('./devPlugins.js');

module.exports = env => {
    return merge(common,htmlPlugins(env), {
        mode: 'development',
        devtool: "source-map",
        performance: {
            hints: false,
            maxAssetSize: 1048576,
            maxEntrypointSize: 1048576
        },
        output: {
            publicPath: '/'
        },
        optimization: {
            moduleIds: 'deterministic',
            minimize: true,
            minimizer: [
                new TerserPlugin({
                    parallel: true,
                    minify: TerserPlugin.uglifyJsMinify,
                    // `terserOptions` options will be passed to `uglify-js`
                    // Link to options - https://github.com/mishoo/UglifyJS#minify-options
                    terserOptions: { compress: true, },
                }),
                new CssMinimizerPlugin()
            ],
            // runtimeChunk: 'single',
            runtimeChunk: {
                name: entrypoint => `runtimechunk~${entrypoint.name}`
            },
            splitChunks: {
                chunks: 'all',
                cacheGroups: {
                    main: {
                        chunks: 'all',
                        name: 'site',
                        test: 'main',
                        enforce: true
                    },
                    vendor: {
                        name: "node_vendors",
                        test: /[\\/]node_modules[\\/]/,
                        chunks: "all",
                    },
                    components: {
                        name: "components",
                        test: /[\\/]src_main_components[\\/]/,
                        chunks: "all",
                    }
                }
            },
        }
    });
};