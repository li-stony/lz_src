#include <iostream>
#include "ziptree.h"
#include "zprint.h"
#include <Windows.h>

void print_usage(const char* name) {
	std::cout << name << " [--level|-l number] path" << std::endl;
}

int main(int argc, char* argv[])
{
	int level = 4096;
	std::string path;

	for (int i = 1; i < argc; i++) {
		std::string arg = argv[i];
		if ((arg == "--level") || (arg == "-l")) {
			if (i + 1 >= argc) {
				break;
			}
			std::string level_arg = argv[i + 1];
			level = std::strtol(level_arg.c_str(), NULL, 10);
			i++;
			continue;
		}
		else {
			path = argv[i];
		}
	}
	if (path.length() == 0) {
		print_usage(argv[0]);
		return -1;
	}
	UINT oldcodepage = GetConsoleOutputCP();
	SetConsoleOutputCP(65001);
	//
	ZipTree tree = ZipTree(path);
	int ret = tree.parse();

	if (ret != 0) {
		std::cout << "parse error: " << ret << std::endl;
	}
	else {
		ZipPrint print = ZipPrint(tree.get_root());
		print.print(level);
		
	}
	SetConsoleOutputCP(oldcodepage);
	
	return ret;
}