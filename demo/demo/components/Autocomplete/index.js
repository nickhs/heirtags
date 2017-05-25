import React, { PropTypes } from 'react';
import AutoComplete from 'react-autocomplete';
import classnames from 'classnames';
import styles from './styles.css';

export default class Autocomplete extends React.Component {
  constructor(props) {
    super(props);

    this.handleSearchChange = this.handleSearchChange.bind(this);
    this.handleSearchSelect = this.handleSearchSelect.bind(this);
    this.handleKeyPress = this.handleKeyPress.bind(this);

    this.state = {
      search: '',
    };
  }

  handleSearchChange(event, value) {
    this.setState({
      search: value,
    });
  }

  handleSearchSelect(value) {
    this.setState({
      search: value,
    });

    const tag = this.props.tagbag.findMatches(value)[0];

    // only do leaf tags
    if (tag.children.length > 0) {
      return;
    }

    this.props.onSelect(tag);
    this.setState({
      search: '',
    });
  }

  handleKeyPress(firstTag, evt) {
    if (evt.key === 'Tab') {
      evt.preventDefault();
      const tag = this.props.tagbag.findMatches(firstTag)[0];
      let tagString = firstTag;
      if (tag.children.length > 0) {
        tagString = `${tagString}/`;
      }
      this.setState({
        search: tagString,
      });
    }
  }

  renderItem(item, isHighlighted) {
    const classes = classnames({
      [styles.autocompleteItem]: true,
      [styles.highlighted]: isHighlighted,
    });

    return (
      <div className={classes}>{item}</div>
    );
  }

  render() {
    const { tagbag, className, wrapperStyle } = this.props;
    const { search } = this.state;

    const wrapperStyleCustom = Object.assign({}, wrapperStyle, {
      display: 'block',
    });

    let matches = [];
    if (search) {
      matches = tagbag.findMatches(search);
    }

    const autocomplete = matches.map(x => x.dumpPath());
    const autocompleteDiv = (
      <AutoComplete
        value={search}
        onChange={this.handleSearchChange}
        onSelect={this.handleSearchSelect}
        items={autocomplete}
        renderItem={this.renderItem}
        getItemValue={item => item}
        inputProps={{
          className,
          placeholder: 'Search Tags',
          onKeyDown: this.handleKeyPress.bind(this, autocomplete[0]),
        }}
        wrapperStyle={wrapperStyleCustom}
      />);

    return (
      <div onKeyPress={this.handleKeyPress}>
        {autocompleteDiv}
      </div>
    );
  }
}

Autocomplete.propTypes = {
  tagbag: PropTypes.object.isRequired,
  className: PropTypes.string,
  wrapperStyle: PropTypes.object,
  onSelect: PropTypes.func.isRequired,
  // onChange: PropTypes.func,
};

Autocomplete.defaultProps = {
  className: '',
};
