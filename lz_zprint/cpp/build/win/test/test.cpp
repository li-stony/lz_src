// test.cpp : Defines the entry point for the console application.
//
#include "stdafx.h"
#include <iostream>
#include <vector>
#include "zprint.h"


int main()
{
	std::vector<char*> files;
	files.push_back("/c");
	files.push_back("b/d/cc.txt");
	files.push_back("/a/x.txt");
	files.push_back("/wuyu/yumen/nihao/wan/s/d/b/aaa.txt");
	files.push_back("b");

	// test tree struct
	std::shared_ptr<ZipNode> root(new ZipNode);
	root->set_name("/");
	root->set_parent(nullptr);
	for (int i = 0; i < files.size(); i++) {
		std::shared_ptr<ZipNode> item(new ZipNode());
		item->set_name(files[i]);
		root->add_child(item);
		item->add_size(i, 1);
	}
	ZipPrint print(root);
	print.print(16);
	print.print(2);
	int ch;
	std::cout << "press any key to exit." << std::endl;
	std::cin >> ch;
    return 0;
}

