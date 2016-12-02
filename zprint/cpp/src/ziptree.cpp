#include <stdio.h>
#include "ziptree.h"
#include "ziputil.h"

ZipTree::ZipTree(std::string zip_path)
{
	root = std::shared_ptr<ZipNode>(new ZipNode());
	root->set_name("/");
	root->set_parent(nullptr);
	this->zip = zip_path;
}



int ZipTree::parse()
{
	FILE* fp = fopen(zip.c_str(), "rb");
	if (fp == NULL) {
		return __LINE__;
	}
	//TODO linux use lseek
	int ret = _fseeki64(fp, 0, SEEK_END);
	if (ret != 0) {
		return __LINE__;
	}
	long long  len = _ftelli64(fp);
	if (len > 65535) {
		_fseeki64(fp, -65535, SEEK_END);
	}
	else {
		_fseeki64(fp, 0, SEEK_SET);
	}
	// find eocd
	long long cur = _ftelli64(fp);
	ZipEocd* eocd = find_eocd(fp, cur, len);
	if (eocd == NULL) {
		std::cout << "not found End of Central Directory" << std::endl;
		fclose(fp);
		return __LINE__;
	}
	_fseeki64(fp, eocd->cd_start, SEEK_SET);
	// read central directories
	uint64_t hd_start = eocd->cd_start;
	uint64_t hd_num = eocd->cd_total;
	delete eocd;
	std::cout << "hd_num:"<<hd_num<<" hd_start:" << hd_start << std::endl;
	for (int i = 0; i < hd_num; i++) {
		ZipCdHeader* cd_header = find_cd_header(fp);
		if (cd_header == NULL) {
			std::cout<<"find ZipCdHeader error at "<<i<<std::endl;
			fclose(fp);
			return __LINE__;
		}
		std::shared_ptr<ZipNode> item ( new ZipNode());
		std::string name;
		name.assign((char*)cd_header->name, cd_header->name_len);
		item->set_name(name);
		//
		// std::cout <<"parse done:" << item->get_name().c_str() << std::endl;
		
		// add
		bool ok =root->add_child(item);
		if (!ok) {
			std::cout << "add child error. " << name.c_str() << std::endl;
			delete cd_header;
			fclose(fp);
			return __LINE__;
		} 
		// udpate size and it's parent size
		item->add_size(cd_header->size, cd_header->csize);
		// delete tmp header
		delete cd_header;
		
	}
	fclose(fp);
	return 0;

}

std::shared_ptr<ZipNode> ZipTree::get_root()
{
	return root;
}