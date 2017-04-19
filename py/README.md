# heirtags

Heirtags are a better way to tag, classify and categorize entities in your system.
Contemporary tagging makes use of a many-to-many relationship to allow developers
to express multiple attributes on an entity. However these tags are often simple
strings that can quicky become unmanageable (if you've ever stared at a tag cloud to
try generate insight you'll know what I mean).

Heirtags make use of tags that are _heirarchical_ in nature. Each tag looks something like:

    /root/child group/something/else

Kind of like a file system path would look like. From this we can explore the tags far easier,
as they have built-in heirachy. It's kind of like tagging your entities with file paths, and you can
do anything that you can with a file path. Such as show me all the items under `/root/child_group/*`

Take a library of books as an example. Previously you would have worked to guess all the attributes up-front
and add them accordingly. With heirtags you could have a list of books (we'll ID them with integers, 1, 2, 3 etc)
and then tags to describe them. For example in our library we could have:

    /core/authors/Fitzgerald
    /core/authors/Wilson
    /core/authors/Poe
    /core/authors/Churchill
    /core/year/2000
    /core/year/2001
    /core/year/2002
    /core/type/Fiction/genre/Mystery
    /core/type/Fiction/genre/Sci-fi
    /core/type/Non-Fiction/genre/Biography
    /core/type/Non-Fiction/genre/Historical

To find all the fiction books you can query

    /core/type/Fiction/

while to get more specific and find all books by Edgar Allen Poe you could do

    /core/authors/Poe

Every book (entity) could have multiple tags accordingly, and it's easy to add new tags!

## Using heirtags

This is a python implementation of heirtags. It's an in-memory data structure that supports adding entities
referenced by ID's to the heirachy tree, finding entities via tags and adding new tags to entities.

See `main.py` for an example.

## Development

### Installing dependencies

    $ pip install -r requirements-dev.txt

### Running Tests

    $ nose

## TODO

* Support deleting entities/tags
* Testing absolute/relative look ups
* Printing none at end of dump?
