import unittest
from lib import TagBag, Entity

e = Entity(1)


class TagBagTest(unittest.TestCase):

    def test_insert(self):
        bag = TagBag()
        bag.insert("/group/key", e)

        with self.assertRaises(Exception):
            bag.insert("group/key", e)

        self.assertTrue('group' in bag.keys)
        self.assertTrue('key' in bag.keys)

    def test_insert_complex(self):
        bag = TagBag()
        bag.insert("/group/test", e)
        bag.insert("/group/something else", e)
        bag.insert("/group/more/specific", e)
        bag.insert("/group/xxx", e)

        bag.insert("/something/group/blah", e)

        self.assertEqual(len(bag.keys), 8)
        groups = bag.keys['group']
        self.assertEqual(len(groups), 2)
        self.assertEqual(len(list(filter(lambda x: x.parent is not None, groups))), 1)
        self.assertEqual(len(list(filter(lambda x: x.parent is None, groups))), 1)

    def test_find_match(self):
        bag = TagBag()
        bag.insert("/group/test", e)
        bag.insert("/group/something else", e)
        bag.insert("/group/more/specific", e)
        bag.insert("/group/xxx", e)

        bag.insert("/something/group/blah", e)

        matches = bag.find_matches('/group/xxx')
        self.assertEqual(matches[0].dump_path(), "/group/xxx")
        self.assertEqual(len(matches), 1)

        matches = bag.find_matches('group/xxx')
        self.assertEqual(matches[0].dump_path(), "/group/xxx")
        self.assertEqual(len(matches), 1)

        matches = bag.find_matches('xxx/')
        self.assertEqual(len(matches), 0)

        matches = bag.find_matches('xxx')
        self.assertEqual(len(matches), 1)
        self.assertEqual(matches[0].dump_path(), "/group/xxx")

        matches = bag.find_matches('group')
        self.assertEqual(matches[0].dump_path(), "/group")
        self.assertEqual(len(matches), 2)

        matches = bag.find_matches('group/')
        self.assertEqual(len(matches), 5)

    def test_entry_missing(self):
        bag = TagBag()
        bag.insert("/group/test", e)
        bag.insert("/test", e)

        matches = bag.find_matches("test")
        self.assertEqual(len(matches), 2)
        self.assertEqual(matches[0].dump_path(), "/group/test")
        self.assertEqual(matches[1].dump_path(), "/test")

        matches = bag.find_matches("/test")
        self.assertEqual(len(matches), 1)
        self.assertEqual(matches[0].dump_path(), "/test")
