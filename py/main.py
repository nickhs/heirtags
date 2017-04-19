import lib


def main():
    bag = lib.TagBag()
    bag.insert("/group/test", "wat")
    bag.insert("/group/something else", "wat")
    bag.insert("/group/more/specific", "wat")
    bag.insert("/group/xxx", "wat")

    bag.insert("/something/group/blah", lib.Entity(1))
    print(bag.dump())

    print(map(lambda x: x.dump_path(), bag.find_matches("/group/xxx")))
    print(map(lambda x: x.dump_path(), bag.find_matches("/group/")))


if __name__ == "__main__":
    main()
