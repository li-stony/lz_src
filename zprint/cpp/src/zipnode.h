#ifndef __ZIPITEM__
#define __ZIPITEM__

#include <iostream>
#include <memory>
#include <vector>
#include <cstdint>

class ZipNode {
public:
	ZipNode();
	void add_size(long s, long cs);
	bool add_child(std::shared_ptr<ZipNode> child);

	std::string get_name();
	uint64_t get_size();
	uint64_t get_csize();

	std::vector<std::shared_ptr<ZipNode>> get_children();

	void set_name(std::string n);
	void set_parent(ZipNode* p);

	int get_leaf_num();
private:
	uint64_t size;
	uint64_t csize;

	// weak reference to the parent
	ZipNode* parent;
	// owner children
	std::vector<std::shared_ptr<ZipNode>> children;

	std::string name;
};

#endif