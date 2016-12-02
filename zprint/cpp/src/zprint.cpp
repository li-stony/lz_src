#include <iomanip>
#include "zprint.h"

ZipPrint::ZipPrint(std::shared_ptr<ZipNode> root)
{
	this->root = root;
}
void ZipPrint::print(ZipNode* item, const int depth, const int level)
{
	
	if (depth < level) {
		for (int i = 0; i < depth; i++) {
			std::cout << "    ";
		}
		std::cout << "|-";
		std::cout << item->get_name().c_str();
		if (item->get_leaf_num() > 0) {
			std::cout << "  (" << item->get_leaf_num() << ")";
		}
		std::cout << std::setw(16) << item->get_csize() << " | " << std::setw(16) << item->get_size() << std::endl;
		// print child
		for (int i = 0; i < item->get_children().size(); i++) {
			ZipNode* child = item->get_children()[i].get();
			print(child, depth + 1, level);
		}
	}
	else {
		return;
	}
}
void ZipPrint::print(int level)
{
	print(root.get(), 0, level);
	
}