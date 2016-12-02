#ifndef __ZIPUTIL__
#define __ZIPUTIL__

#include <cstdio>
#include <cstdint>

class ZipEocd {
public:
	~ZipEocd();
public:
	uint32_t signature;
	// number of this disk
	uint16_t disk_num;
	// disk where central directory starts
	uint16_t cd_start_disk;
	// number of cetral directory records on this disk
	uint16_t disk_cd_num;
	// total number of central directory records
	uint16_t cd_total;
	// size of central directory (bytes)
	uint32_t cd_size;
	// offset of start of central directory, relative to start of archive
	uint32_t cd_start;

	uint16_t comment_len;
	uint8_t* comment;
};

ZipEocd* find_eocd(FILE* fp, int64_t cur, int64_t size);

class ZipCdHeader {
public:
	~ZipCdHeader();
public:
	uint32_t signature;
	// version made by
	uint16_t ver_made;
	// version needed to extract
	uint16_t ver_need;
	uint16_t bit_flag;
	uint16_t comp_method;
	uint16_t last_time;
	uint16_t last_date;
	uint32_t crc32;
	// compressed size;
	uint32_t csize;
	// uncompressed size;
	uint32_t size;
	uint16_t name_len;
	uint16_t ext_len;
	uint16_t comment_len;
	uint16_t file_start_disk;
	// internal file attributes
	uint16_t file_attr;
	// external file attributes
	uint16_t file_ext_attr;

	// file offset from archive start
	uint32_t file_start;
	//
	uint8_t* name;
	// extra fields
	uint8_t* ext_fields;
	// comment
	uint8_t* comment;

};

ZipCdHeader* find_cd_header(FILE* fp);

#endif