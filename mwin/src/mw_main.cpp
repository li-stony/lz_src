#include <Windows.h>
#include <WinUser.h>
#include <stdio.h>
#include <iostream>
#include <vector>
#include <boost/algorithm/string.hpp> 
#include "mw_main.h"

void mw_print_help(wchar_t* app)
{
	wprintf(L"%s [[-l (name)] | [-k hwnd ([1|2])]\n", app);
	wprintf(L"for example\n");
	wprintf(L"%s -l\n", app);
	wprintf(L"%s -l notepad\n", app);
	wprintf(L"%s -k 2456\n", app);
}

static int msgs[] = {
	WM_CLOSE,
	WM_DESTROY,
	WM_QUIT
};

struct WinEnumParam {
	int looping = 0;

	wchar_t* filter;
	HANDLE sema;

	int count = 0;
	std::vector<int> hwnds;
	std::vector<std::wstring> titles;


};

static BOOL CALLBACK mw_window_proc(_In_ HWND   hwnd, _In_ LPARAM lParam)
{
	WinEnumParam * proc_param = (WinEnumParam*)lParam;
	if (proc_param->looping == 0) {
		return FALSE;
	}
	wchar_t buf[512];
	int len = GetWindowTextW(hwnd, buf, sizeof(buf));
	if (len > 0) {
		std::wstring name(buf, len);
		std::wstring tmp = name;
		boost::algorithm::to_lower(tmp);
		if (tmp.find(proc_param->filter) != std::wstring::npos) {
			proc_param->hwnds.push_back((int)hwnd);
			proc_param->titles.push_back(name);
			DWORD pid = 0;
			GetWindowThreadProcessId(hwnd, &pid);
			
			WINDOWINFO info;
			BOOL ok = GetWindowInfo(hwnd, &info);
			wprintf(L"%03d %010d %08d (%04d,%04d,%04d,%04d) %08x-%08x %08x %s\n", 
				proc_param->count, 
				hwnd, 
				pid, 
				info.rcWindow.left,
				info.rcWindow.top,
				info.rcWindow.right,
				info.rcWindow.bottom,
				info.dwStyle,
				info.dwExStyle,
				info.dwWindowStatus,
				name.c_str());
			
			proc_param->count++;
		}
	}
	
	long oldcnt;
	ReleaseSemaphore(proc_param->sema, 1,&oldcnt);
	return true;
}


// return 
//	0 - ok;
//	-1 - invalid params
//	-2 - error
//	others - line number 
int mw_exec(int param_count, wchar_t* params[])
{
	if (param_count == 0) {
		return -1;
	}
	if (wcscmp(params[0], L"-l") == 0) {
		WinEnumParam proc_param;
		proc_param.filter = L"";
		if (param_count >= 2) {
			proc_param.filter= params[1];
			boost::algorithm::to_lower(proc_param.filter);
		}
		proc_param.sema = CreateSemaphore(
			NULL,
			0,
			256,
			L"proc semaphore"
			);
		proc_param.looping = 1;
		wprintf(L"% 3s % 10s % 8s % 21s % 17s % 8s %s\n", L"num", L"wnd", L"pid", L"rect", L"style", L"status", L"name");
		EnumWindows(mw_window_proc, (LPARAM)(&proc_param));
		while(1) {
			int ret = WaitForSingleObject(proc_param.sema, 1500);
			if (ret == WAIT_TIMEOUT) {
				break;
			}
			else if (ret == WAIT_FAILED) {
				break;
			}

		}
		proc_param.looping = 0;
		CloseHandle(proc_param.sema);
		return 0;
		
	}
	else if (wcscmp(params[0], L"-k") == 0) {
		if (param_count >= 2) {
			HWND wnd;
			int index = 0;
			swscanf(params[1], L"%d", &wnd);
			if (param_count >= 3) {
				swscanf(params[2], L"%d", &index);
			}
			if (index > 3) {
				return -1;
			}
			LRESULT result = SendMessage(wnd, msgs[index], NULL, NULL);
			int err = GetLastError();
			if (err == 5) {
				return __LINE__;
			}
		}
		else {
			return -1;
		}
		
	}
	else {
		return -1;
	}
	
}