import TagBag from 'heirtags';

import React, { PropTypes } from 'react';
import styles from './styles.css';
import HeirtagsView from '../../components/HeirtagsView';
import Autocomplete from '../../components/Autocomplete';
import bookData from '../../bookData';

export class Book extends React.Component {
  render() {
    const { book, onClick } = this.props;
    return (
      <a
        className={styles.book}
        onClick={onClick}
      >
        {book.name}
      </a>
    );
  }
}

Book.propTypes = {
  onClick: PropTypes.func.isRequired,
  book: PropTypes.object.isRequired,
};

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
      return (<Book
        book={x}
        key={x.id}
        onClick={this.handleBookDetails.bind(this, x)}
      />);
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
            Heirtags are a better way to tag, classify and categorize entities in a system.
            Contemporary tagging makes use of many-to-many relationships
            to express attributes on an entity - however these tags are often simple
            strings that can quicky become unmanageable.
          </p>

          <p>
            Heirtags make use of tags that are heirarchical in nature. Each tag looks something like a file path:
          </p>

          <pre className={styles.demo}>
            /core/authors/British/Winston Churchill
          </pre>

          <p>
            Where they build up a heirachy seperated by forward slashes.
            You can have multiple tags per entity (like a conventional tagging system).
            By using paths you can answer questions like - <span className={styles.question}>
                show me all the British authors:
              </span>
          </p>

          <pre className={styles.demo}>
            /core/authors/British/&#42;&#42;
          </pre>

          <p>
            or, <span className={styles.question}>
                show me all authors starting with Winston:
              </span>
          </p>

          <pre className={styles.demo}>
            /core/authors/British/Winston*
          </pre>

          <p className={styles.library}>
            Here&rsquo;s an example of a library of books that have been tagged accordingly by author, type and genre.
            You can drill down on the tags, search through them and add new tags to the existing books.
          </p>
        </div>

        <div className={`${styles.library} ${styles.divider}`} />

        <div className={styles.library}>
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

        <div className={styles.divider} />

        <div>
          <div>
            Heirtags is available in the following languages and backends:
          </div>

          <div>
            <div className={styles.langCont}>
              <div className={styles.langs}>
                Java
                <ol>
                  <li>PostgreSQL</li>
                  <li>In-memory</li>
                </ol>
              </div>

              <div className={styles.langs}>
                Python
                <ol>
                  <li>In-memory</li>
                </ol>
              </div>

              <div className={styles.langs}>
                Javascript
                <ol>
                  <li>In-memory</li>
                </ol>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
