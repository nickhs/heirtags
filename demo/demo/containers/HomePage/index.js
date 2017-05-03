import React from 'react';
import styles from './styles.css';
import HeirtagsView from '../../components/HeirtagsView';
import TagBag from '../../../lib/index';
import Autocomplete from '../../components/Autocomplete';
import bookData from '../../bookData';

export default class HomePage extends React.Component {
  constructor(props) {
    super(props);

    const bookBag = new TagBag();
    bookData.forEach(book => {
      book.tags.forEach(tag => {
        bookBag.insert(tag, book);
      });
    });

    this.state = {
      bookBag,
      selected: new Set(bookData),
      active: null,
    };

    this.handleTagChange = this.handleTagChange.bind(this);
    this.handleAddTag = this.handleAddTag.bind(this);
  }

  handleTagChange(selectedTags) {
    let books = new Set();
    if (selectedTags.size === 0) {
      return this.setState({
        selected: new Set(bookData),
      });
    }

    selectedTags.forEach(x => {
      books = new Set(
        [...books, ...x.entities]);
    });

    return this.setState({
      selected: books,
    });
  }

  handleAddTag() {
    const text = this.autocomplete.state.search;
    if (!text || text[0] !== '/') return;
    this.state.active.tags.push(text);
    this.state.bookBag.insert(text, this.state.active);
    this.autocomplete.setState({
      search: '',
    });
    this.forceUpdate();
  }

  handleBookDetails(book) {
    this.setState({
      active: book,
    });
  }

  render() {
    const { bookBag, selected, active } = this.state;
    const selectedBooks = Array.from(selected).map(x => {
      return (
        <a
          key={x.id}
          className={styles.book}
          onClick={this.handleBookDetails.bind(this, x)}
        >
          {x.name}
        </a>
      );
    });

    let details = null;
    if (active) {
      const tags = active.tags.map(t => {
        return (
          <div
            className={styles}
            key={t}
          >
            { t }
          </div>
        );
      });

      details = (
        <div className={styles.detailsPane}>
          <div className={styles.content}>
            <div>
              <h3 className={styles.deets}>Details</h3>
            </div>
            <div className={styles.info}>
              <span className={styles.id}>{ active.id }</span>
              <span className={styles.name}>{ active.name }</span>
            </div>

            <h4 className={styles.deets}>Tags</h4>
            <div className={styles.tagBox}>
              <div className={styles.tags}>
                { tags }
              </div>
              <Autocomplete
                tagbag={bookBag}
                className={styles.autocomplete}
                wrapperStyle={{
                  display: 'block',
                }}
                // eslint-disable-next-line no-return-assign
                ref={r => this.autocomplete = r}
              />
              <a className={styles.addTag} onClick={this.handleAddTag}>Add Tag</a>
            </div>
          </div>

          <div className={styles.closeButton} onClick={this.handleBookDetails.bind(this, null)}>
            Close
          </div>
        </div>
      );
    }

    return (
      <div>
        <div className={styles.header}>
          <h1>
            Heirtags
          </h1>
          <h3>
            Better Tagging for Classification and Categorization
          </h3>

          <p>
            Heirtags are a better way to tag, classify and categorize entities in your system.
            Contemporary tagging makes use of a many-to-many relationship to allow developers
            to express multiple attributes on an entity. However these tags are often simple
            strings that can quicky become unmanageable.
          </p>

          <p>
            Heirtags make use of tags that are heirarchical in nature. Each tag looks something like:
          </p>

          <pre className={styles.demo}>
            /core/authors/British/Winston Churchill
          </pre>

          <p>
            Below is an example of a library of books that have been tagged accordingly by author, type and genre.
            You can drill down on the tags, search through them and add new tags to the existing books.
          </p>
        </div>

        <div className={styles.panes}>
          <div className={styles.leftPane}>
            <HeirtagsView
              tagbag={bookBag}
              onChange={this.handleTagChange}
            />
          </div>

          <div className={styles.rightPane}>
            <div className={styles.books}>
              { selectedBooks }
            </div>

            { details }
          </div>
        </div>
      </div>
    );
  }
}
