"""
    Heirtags - classification + categorization.

Heirtags are a better way to tag, classify and categorize entities in your system.
Contemporary tagging makes use of a many-to-many relationship to allow developers
to express multiple attributes on an entity. However these tags are often simple
strings that can quicky become unmanageable (if you've ever stared at a tag cloud to
try generate insight you'll know what I mean).

Heirtags make use of tags that are _heirarchical_ in nature. Each tag looks something like:

    /root/child group/something/else

Kind of like a file system path would look like. From this we can explore the tags far easier,
as they have built-in heirachy. It's kind of like tagging your entities with file paths, and you can
do anything that you can with a file path. Such as show me all the items under /root/child_group/*

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
"""


from itertools import chain


class Entity:
    def __init__(self, entity_id):
        self.entity_id = entity_id

    def __repr__(self):
        return self.__str__

    def __str__(self):
        return "<Entity %s>" % self.entity_id


class TagNode:
    def __init__(self, value, entities=None, parent=None, children=None):
        if children:
            assert(type(children) == set)
            self.children = children
        else:
            self.children = set()

        if entities is None:
            entities = []

        self.entities = entities
        self.parent = parent
        self.value = value

    def is_root(self):
        # FIXME assert that if null that is a root node?
        return self.value.startswith('/')

    def dump_path(self):
        parents = [self]
        prev = self
        while prev.parent:
            prev = prev.parent
            parents.append(prev)

        parents.reverse()
        strings = map(lambda x: x.value, parents)
        return '/'.join(strings)

    def __repr__(self):
        return "<TagNode %s [%s]>" % (self.dump_path(), hex(id(self)))

    def __str__(self):
        return "<TagNode %s>" % self.dump_path()


class TagBag:
    def __init__(self):
        # key is the string (e.g. /root or bar
        # value is a list (set?) of nodes that are
        # under that string
        self.keys = {}

    def insert(self, key, value):
        assert(key.startswith('/'))
        keys = key.split('/')[1:]
        head = "%s" % keys[0]
        rest = keys[1:]

        matches = [x for x in self.keys.get(head, []) if x.is_root()]
        if len(matches) == 1:
            head = matches[0]
        elif len(matches) == 0:
            newNode = TagNode("/%s" % head)
            self.keys[head] = self.keys.get(head, []) + [newNode]
            head = newNode
        else:
            raise RuntimeError("Got non-sensical matches", matches)

        # head is now a tag node
        prev = head

        for child in rest:
            matches = self.keys.get(child, [])  # find all nodes under that key
            matches = [x for x in matches if x.parent == prev]  # filter based on parent
            if len(matches) == 0:   # no matches, add a new node
                match = TagNode(child, parent=prev)
                current = self.keys.get(child, [])
                current.append(match)
                self.keys[child] = current
            elif len(matches) == 1:  # node already exists, noop
                match = matches[0]
            else:  # should never happen
                raise RuntimeError("Found too many matches [%s]" % len(matches))

            prev.children.add(match)
            prev = match

        # prev is the very last item, add the entity
        prev.entities.append(Entity(value))

    def dump(self):
        all_items = chain.from_iterable(self.keys.values())
        all_items = map(lambda x: x.dump_path(), all_items)
        all_items = sorted(all_items)
        all_items.reverse()
        print('\n'.join(all_items))

    def find_matches(self, key):
        """
        Supports queries like:

            /foo/bar/baz (full path)
            /foo/bar/ (gets all the children)
            bar (partial)
            bar/ (partial, gets all children)
        """

        keys = [x for x in key.split('/') if len(x) > 0]
        first = keys[0]
        keys = keys[1:]

        possible = self.keys.get(first, [])

        # doing a root lookup
        if key.startswith('/'):
            possible = [k for k in possible if k.is_root()]
            assert(len(possible) == 1)

        for key in keys:
            new_possible = []
            for pos in possible:
                for children in pos.children:
                    if children.value.startswith(key):
                        new_possible.append(children)

            possible = new_possible

        # Handle asking for the children
        if key.endswith('/'):
            possible = list(chain(*[x.children for x in possible]))

        return possible
