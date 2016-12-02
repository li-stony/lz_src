// mwin.cpp : Defines the entry point for the console application.
//

#include <Windows.h>
#include <stdio.h>
#include <locale.h>

#include "mw_main.h"

int wmain(int argc, wchar_t* argv[])
{
	int cp = GetACP();
	char lang[8];
	sprintf(lang, ".%d", cp);
	setlocale(LC_ALL, lang);
	int ret = mw_exec(argc - 1, argv+1);
	if (ret == -1) {
		mw_print_help(argv[0]);
		return -1;
	}
	return ret;
}

