const path = require('path');

module.exports = {
  entry: path.resolve(__dirname, '../index'),
  output: {
    filename: 'test.js',
    path: path.resolve(__dirname, '../build'),
  },

  module: {
    rules: [
      {
        test: /\.js$/,
        use: [
          loader: 'babel-loader',
        ],
      },
    ],
  },
};
