import React, { PropTypes } from 'react';
import TreeView from 'react-treeview';
import styles from './styles.css';
import Autocomplete from '../Autocomplete';

export default class HeirtagsView extends React.Component {
  constructor(props) {
    super(props);

    this.handleSelect = this.handleSelect.bind(this);

    this.state = {
      selected: new Set(),
      collapsed: new Set(),
    };
  }

  handleSelect(tag, evt) {
    const { selected } = this.state;
    let newSelected;
    if (selected.has(tag)) {
      selected.delete(tag);
      newSelected = selected;
    } else {
      newSelected = selected.add(tag);
    }

    this.setState({
      selected: newSelected,
    }, () => {
      if (this.props.onChange) {
        this.props.onChange(this.state.selected, tag, evt);
      }
    });
  }

  generateTree(tag) {
    return this._generateTree('', tag);
  }

  handleCollapse(key) {
    const { collapsed } = this.state;
    let c;
    if (collapsed.has(key)) {
      collapsed.delete(key);
      c = collapsed;
    } else {
      c = collapsed.add(key);
    }

    this.setState({
      collapsed: c,
    });
  }

  _generateTree(prev, tag) {
    const { collapsed, selected } = this.state;
    const key = `${prev}/${tag.key}`;
    const func = this._generateTree.bind(this, key);
    const isSelected = selected.has(tag) ? styles.isSelected : '';

    if (tag.children.length > 0) {
      const children = tag.children.map(func);
      const label = (
        <div
          className={styles.arrowLabel}
          onClick={this.handleCollapse.bind(this, key)}
        >
          {tag.key}
        </div>);

      const collapsedClass = collapsed.has(key) ? styles.arrowActive : '';
      const arrowCName = `${styles.arrow} ${collapsedClass}`;

      return (
        <TreeView
          key={key}
          nodeLabel={label}
          className={arrowCName}
          itemClassName={styles.treeItem}
          collapsed={!collapsed.has(key)}
          onClick={this.handleCollapse.bind(this, key)}
        >
          {children}
        </TreeView>
      );
    }

    const cname = `${isSelected} ${styles.arrowLabel}`;

    return (
      <div
        key={key}
        onClick={this.handleSelect.bind(this, tag)}
        className={cname}
      >
        {tag.key}
      </div>
    );
  }

  render() {
    const { tagbag } = this.props;
    const { selected } = this.state;

    const roots = tagbag.getRoots();
    const trees = roots.map(this.generateTree, this);

    // what do I want?
    /*
     * <selected tags>
     * <search box>
     * <tree>
    */

    /*
    const stack = matches;
    const divs = [];
    while (stack.length > 0) {
      const item = stack.pop();
      item.children.forEach(i => stack.push(i));
      divs.push(<div key={item.key}>{item.key}</div>);
    }
    */

    const selectedDivs = Array.from(selected).map(x => {
      const path = x.dumpPath();
      return (
        <div
          className={styles.selectedTag}
          key={path}
          onClick={this.handleSelect.bind(this, x)}
        >
          {path}
        </div>);
    });

    return (
      <div>
        <div className={styles.autocomplete}>
          <Autocomplete
            tagbag={tagbag}
            className={styles.search}
            onSelect={this.handleSelect}
          />
        </div>

        <div className={styles.selected}>
          { selectedDivs }
        </div>

        <div>
          { trees }
        </div>
      </div>
    );
  }
}

HeirtagsView.propTypes = {
  tagbag: PropTypes.object.isRequired,
  onChange: PropTypes.func,
};
