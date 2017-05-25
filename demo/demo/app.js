/**
 * app.js
 *
 * This is the entry file for the application, only setup and boilerplate
 * code.
 */
/* eslint import/first: "off" */
import 'babel-polyfill';

/* eslint-disable import/no-unresolved */
// Load the manifest.json file and the .htaccess file
// eslint-disable-next-line
import '!file?name=[name].[ext]!./manifest.json';
/* eslint-enable import/no-unresolved */

// Import all the third party stuff
import React from 'react';
import ReactDOM from 'react-dom';

// Import the CSS reset, which HtmlWebpackPlugin transfers to the build folder
import 'sanitize.css/sanitize.css';

import App from './containers/App/index';
import HomePage from './containers/HomePage/index';

const render = () => {
  ReactDOM.render(
    (<App>
      <HomePage />
    </App>),
    document.getElementById('app')
  );
};

render();

// Install ServiceWorker and AppCache in the end since
// it's not most important operation and if main code fails,
// we do not want it installed
import { install } from 'offline-plugin/runtime';
install();
