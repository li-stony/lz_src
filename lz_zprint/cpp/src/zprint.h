#include "zipitem.h"

class ZipPrint {
public:
	ZipPrint(std::shared_ptr<ZipItem> root);
	void print(int level);
private:
	void print(ZipItem* item, int depth, int level);
private:
	std::shared_ptr<ZipItem> root;
};