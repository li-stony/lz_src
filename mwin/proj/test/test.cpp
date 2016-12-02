// test.cpp : Defines the entry point for the console application.
//
#include <Windows.h>
#include <stdio.h>
#include <locale.h>
#include "stdafx.h"
#include "mw_main.h"

int main()
{
	int cp = GetACP();
	char lang[8];
	sprintf(lang, ".%d", cp);
	setlocale(LC_ALL, lang);

	wchar_t* para1[] = { L"-l" };
	mw_exec(1, para1);

	//wchar_t* para2[] = { L"-l" , L"q"};
	//mw_exec(2, para2);

	// test the follow ok! closed a qq tab window.
	// wchar_t* para3[] = { L"-k", L"661030" };
	// mw_exec(2, para3);

    return 0;
}

