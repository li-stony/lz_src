#include "zipnode.h"

class ZipPrint {
public:
	ZipPrint(std::shared_ptr<ZipNode> root);
	void print(int level);
private:
	void print(ZipNode* item, int depth, int level);
private:
	std::shared_ptr<ZipNode> root;
};