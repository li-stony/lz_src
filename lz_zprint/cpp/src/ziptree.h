#ifndef __ZIPTREE__
#define __ZIPTREE__

#include <iostream>
#include <memory>
#include "zipnode.h"

class ZipTree {
public:
	ZipTree(std::string zip_path);

	int parse();

	std::shared_ptr<ZipNode> get_root();
private:
	std::shared_ptr<ZipNode> root;
	int level;
	std::string zip;
};

#endif

