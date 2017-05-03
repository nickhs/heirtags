import TagBag from '../src/index';

const e = 'test';

test('tagbag inserts', () => {
  const bag = new TagBag();
  bag.insert('/group/key', e);

  expect(bag.keys.group).toBeDefined();
  expect(bag.keys.key).toBeDefined();
});

test('inserts complex', () => {
  const bag = new TagBag();
  bag.insert('/group/key', e);
  bag.insert('/group/something else', e);
  bag.insert('/group/more/specific', e);
  bag.insert('/group/xxx', e);
  bag.insert('/something/group/blah', e);

  expect(Object.keys(bag.keys).length).toEqual(8);
  const groups = bag.keys.group;
  expect(groups.length).toEqual(2);

  // FIXME finish off tests here
});

test('find_matches', () => {
  const bag = new TagBag();
  bag.insert('/group/key', e);
  bag.insert('/group/something else', e);
  bag.insert('/group/more/specific', e);
  bag.insert('/group/xxx', e);
  bag.insert('/something/group/blah', e);

  let matches = bag.findMatches('/group/xxx');
  expect(matches.length).toEqual(1);
  expect(matches[0].dumpPath()).toEqual('/group/xxx');

  matches = bag.findMatches('group/xxx');
  expect(matches.length).toEqual(1);
  expect(matches[0].dumpPath()).toEqual('/group/xxx');

  matches = bag.findMatches('xxx/');
  expect(matches.length).toEqual(0);

  matches = bag.findMatches('xxx');
  expect(matches.length).toEqual(1);
  expect(matches[0].dumpPath()).toEqual('/group/xxx');

  // FIXME finish off these tests
});

test('adds children correctly', () => {
  const bag = new TagBag();
  bag.insert('/group/level2/level1', e);
  let matches = bag.findMatches('/group');
  expect(matches.length).toEqual(1);
  expect(matches[0].dumpPath()).toEqual('/group');
  expect(matches[0].children.length).toEqual(1);
  expect(matches[0].children[0].dumpPath()).toEqual('/group/level2');

  // cool now add something else
  bag.insert('/group/level2/level1other', e);
  matches = bag.findMatches('/group');
  expect(matches.length).toEqual(1);
  expect(matches[0].dumpPath()).toEqual('/group');
  expect(matches[0].children.length).toEqual(1);
  expect(matches[0].children[0].dumpPath()).toEqual('/group/level2');
});
