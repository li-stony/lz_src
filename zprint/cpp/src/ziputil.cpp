#include "ziputil.h"

ZipEocd::~ZipEocd()
{
	if (this->comment != NULL) {
		delete this->comment;
		this->comment = NULL;
	}
}

ZipCdHeader::~ZipCdHeader()
{
	if (this->name != NULL) {
		delete this->name;
		this->name = NULL;
	}
	if (this->comment != NULL) {
		delete this->comment;
		this->comment = NULL;
	}
}

ZipEocd* find_eocd(FILE* fp, int64_t cur, int64_t size)
{
	uint8_t* buf = new uint8_t[size - cur];
	int64_t ret = fread(buf, 1, size - cur, fp);
	bool valid = false;
	uint8_t* start = NULL;
	for (int i = 0; i < size - cur; i++) {
		uint32_t* sig = (uint32_t*)(buf + i);
		if (*sig == 0x06054b50) {
			valid = true;
			start = buf + i;
			break;
		}
	}
	if (valid) {
		if ((start + 20) <= (buf + size - cur)) {
			valid = true;
		}
	}
	
	ZipEocd* cd = NULL;
	if (valid == true) {
		// parse
		cd = new ZipEocd;
		cd->signature = 0x06054b50;
		cd->disk_num = *((uint16_t*)(start + 4));
		cd->cd_total = *((uint16_t*)(start + 10));
		cd->cd_start = *((uint32_t*)(start + 16));
		cd->comment_len = *((uint16_t*)(start + 20));
		valid = false;
		if ((start + 22 + cd->comment_len) == (buf + size - cur)) {
			valid = true;
		}
	}
	
	delete buf;

	if (valid) {
		return cd;
	}
	else {
		return NULL;
	}
}

ZipCdHeader* find_cd_header(FILE* fp)
{

	uint8_t* buf = new uint8_t[46];
	fread(buf, 1, 46, fp);
	int* sig = ((int*)(buf));
	if ((*sig) != 0x02014b50) {
		delete buf;
		return NULL;
	}

	ZipCdHeader* cd = new ZipCdHeader;
	cd->signature = *sig;
	cd->file_start = *((uint32_t*)(buf + 42));
	cd->csize = *((uint32_t*)(buf + 20));
	cd->size = *((uint32_t*)(buf + 24));
	cd->name_len = *((uint16_t*)(buf + 28));
	cd->ext_len = *((uint16_t*)(buf + 30));
	cd->comment_len = *((uint16_t*)(buf + 32));
	//
	cd->name = new uint8_t[cd->name_len];
	fread(cd->name, 1, cd->name_len, fp);
	//
	cd->ext_fields = new uint8_t[cd->ext_len];
	fread(cd->ext_fields, 1, cd->ext_len, fp);
	//
	cd->comment = new uint8_t[cd->comment_len];
	fread(cd->comment, 1, cd->comment_len, fp);
	return cd;
}