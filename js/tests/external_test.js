import fsp from 'fs-promise';
import fs from 'fs';
import path from 'path';

import TagBag from '../src/index';

const PATHS = [
  path.resolve('../tests/core/'),
];

describe('external tests', () => {
  PATHS.forEach(dirname => {
    const filenames = fs.readdirSync(dirname);
    filenames
      .filter(x => {
        return x.match(/\.json$/) !== null;
      })
      .forEach(filename => {
        const filepath = path.resolve(dirname, filename);
        const testCommands = JSON.parse(fs.readFileSync(filepath));
        describe(`${filepath}`, () => {
          runTest(testCommands);
        });
      });
  });
});

function runTest(commands) {
  const bag = new TagBag();

  commands.forEach(item => {
    it(`${JSON.stringify(item)}`, () => {
      if (item.command === 'insert') {
        bag.insert(item.key, item.value);
      } else if (item.command === 'find_matching') {
        const matches = bag.findMatches(item.key)
        const entities = matches.reduce(
          (acc, x) => acc.concat(x.entities), []);
        expect(entities)
          .toEqual(expect.arrayContaining(item.results));
        expect(entities.length).toEqual(item.results.length);
      } else {
        throw new Error(`unknown command: ${item}`);
      }
    });
  });
}
