import { Minimatch } from 'minimatch';

// FIXME what does this thing do?
export class Entity {
  constructor(value, tags = []) {
    this.value = value;
    this.tags = tags;
  }
}

export class TagNode {
  constructor(key, {
      entities = [],
      parent = null,
      children = [],
  } = {}) {
    // workaround as destructuring from
    // babel seems a bit broken, and I don't
    // want to chase a babel bug right now
    if (key === undefined || key === null) {
      throw new Error('You must define a key');
    }

    this.key = key;
    this.entities = entities || [];
    this.parent = parent || null;
    this.children = children || [];
  }

  isRoot() {
    return this.key.startsWith('/');
  }

  dumpPath() {
    let parent = this.parent;
    const parents = [this];
    while (parent) {
      parents.push(parent);
      parent = parent.parent;
    }

    return parents
      .reverse()
      .map(p => p.key)
      .join('/');
  }
}

export default class TagBag {
  constructor() {
    this.keys = {};
  }

  insert(key, value) {
    // FIXME assert key is an absolute key

    // ignore the first absolute empty string
    let keys = key.split('/').slice(1);
    const head = keys[0];
    keys = keys.slice(1);

    // find the root node
    let matches = this.keys[head] || [];
    matches = matches.filter(x => x.parent === null);

    // used to hold the start for the array below
    let prev = null;

    if (matches.length === 1) {
      // we found a root node
      prev = matches[0];
    } else if (matches.length === 0) {
      // no root node, need to make one
      const newNode = new TagNode(`/${head}`);
      this.keys[head] = [newNode];
      prev = newNode;
    } else {
      throw new Error('Got non-sensical matches', matches);
    }

    const filterFunc = x => x.parent === prev;

    // eslint-disable-next-line no-restricted-syntax
    for (const keyVal of keys) {
      let partialMatches = this.keys[keyVal] || [];
      partialMatches = partialMatches.filter(filterFunc);

      // hold the match we find in here
      let match;

      if (partialMatches.length === 0) {
        // no matches - add a new node
        match = new TagNode(keyVal, { parent: prev });
        const arr = this.keys[keyVal] || [];
        arr.push(match);
        this.keys[keyVal] = arr;
      } else if (partialMatches.length === 1) {
        match = partialMatches[0];
      } else {
        // should never happen?
      }

      if (!prev.children.some(x => x === match)) {
        prev.children.push(match);
      }

      prev = match;
    }

    prev.entities.push(value);
  }

  findMatches(key) {
    let keys = key.split('/').filter(x => x.length > 0);
    const head = keys[0];
    keys = keys.slice(1);

    if (head == null || head === '') {
      return [];
    }

    const matchedKeys = Object.keys(this.keys).filter(x => x.toLowerCase().indexOf(head.toLowerCase()) !== -1);
    let potential = matchedKeys.reduce((prev, next) => {
      return prev.concat(this.keys[next]);
    }, []);

    if (key[0] === '/') {
      potential = potential.filter(x => x.isRoot());
    }

    // eslint-disable-next-line no-restricted-syntax
    for (const keyItem of keys) {
      let newPotential = [];

      // turn the keyItem into a regex?
      const matcher = new Minimatch(keyItem.toLowerCase());

      // eslint-disable-next-line no-restricted-syntax
      for (const pot of potential) {
        newPotential = newPotential.concat(
          pot.children.filter(p => {
            return matcher.match(p.key.toLowerCase());
          })
        );
      }

      potential = newPotential;
    }

    if (key[key.length - 1] === '/') {
      potential = potential.reduce((prev, cur) => {
        return prev.concat(cur.children);
      }, []);
    }

    return potential;
  }

  getRoots() {
    return Object.values(this.keys)
      .reduce((prev, next) => prev.concat(next))
      .filter(x => x.isRoot());
  }
}
