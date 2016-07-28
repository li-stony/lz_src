#ifndef __ZIPTREE__
#define __ZIPTREE__

#include <iostream>
#include <memory>
#include "zipitem.h"

class ZipTree {
public:
	ZipTree(std::string zip_path);

	int parse();

	std::shared_ptr<ZipItem> get_root();
private:
	std::shared_ptr<ZipItem> root;
	int level;
	std::string zip;
};

#endif

