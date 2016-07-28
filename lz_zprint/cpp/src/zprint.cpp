#include <iomanip>
#include "zprint.h"

ZipPrint::ZipPrint(std::shared_ptr<ZipItem> root)
{
	this->root = root;
}
void ZipPrint::print(ZipItem* item, const int depth, const int level)
{
	
	if (depth < level) {
		for (int i = 0; i < depth; i++) {
			std::cout << "    ";
		}
		std::cout << "|-";
		std::cout << item->get_name().c_str() << std::setw(16)<<item->get_csize() << " | " << std::setw(16)<<item->get_size() << std::endl;
		// print child
		for (int i = 0; i < item->get_children().size(); i++) {
			ZipItem* child = item->get_children()[i].get();
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