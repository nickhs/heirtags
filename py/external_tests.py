from __future__ import print_function

import unittest
import json
import itertools
import glob

from lib import TagBag

TEST_FILES = [
    '../tests/core/test*.json'
]


class ExternalTests(unittest.TestCase):

    def test(self):
        for fileglob in TEST_FILES:
            for filename in glob.glob(fileglob):
                print("[.] processing file: %s" % filename)
                self._run_test(json.load(open(filename)))

    def _run_test(self, commands):
        bag = TagBag()

        for command in commands:
            print(command)

            if command['command'] == "insert":
                bag.insert(command['key'], command['value'])

            elif command['command'] == "find_matching":
                actual_entities = [x.entities for x in bag.find_matches(command['key'])]
                actual = list(itertools.chain.from_iterable(actual_entities))
                actual = set([x.entity_id for x in actual])
                expected = set(command['results'])
                self.assertSetEqual(actual, expected)

            else:
                raise RuntimeError("unknown command", command)
