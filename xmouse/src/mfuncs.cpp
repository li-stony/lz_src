#include <Windows.h>
#include <WinUser.h>
#include <WinBase.h>
#include <stdio.h>
#include "mfuncs.h"

int mouse_event(DWORD flag) 
{
	INPUT input;
	input.mi.time = 0;
	input.mi.dx = 0;
	input.mi.dy = 0;
	input.mi.mouseData = 0;
	input.mi.dwExtraInfo = NULL;
	input.mi.dwFlags = flag;
	int num = SendInput(1, &input, sizeof(input));
	if (num > 0) {
		// success
		return 0;
	}
	else {
		int error = GetLastError();
		wprintf(L"mosue_move() error: %d flag %d\n", error, flag);
		return __LINE__;
	}
}

int mouse_event(std::wstring ev) 
{
	int ret;
	if (ev.size() == 2) {
		if (ev == L"ld") {
			ret = mouse_event(MOUSEEVENTF_LEFTDOWN);
		}
		else if (ev == L"lu") {
			ret = mouse_event(MOUSEEVENTF_LEFTUP);
		}
		else if (ev == L"rd") {
			ret = mouse_event(MOUSEEVENTF_RIGHTDOWN);
		}
		else if (ev == L"ru") {
			ret = mouse_event(MOUSEEVENTF_RIGHTUP);
		}
		return ret;
	}
	else if (ev.size() == 1) {
		if (ev == L"l") {
			ret = mouse_event(MOUSEEVENTF_LEFTDOWN);
			ret = mouse_event(MOUSEEVENTF_LEFTUP);
		}
		else if (ev == L"r") {
			ret = mouse_event(MOUSEEVENTF_RIGHTDOWN);
			ret = mouse_event(MOUSEEVENTF_RIGHTUP);
		}
	}
	else {

	}
	
}

int mouse_move(int x, int y)
{
	INPUT input;
	input.mi.time = 0;
	input.mi.dx = x;
	input.mi.dy = y;
	input.mi.dwFlags = MOUSEEVENTF_MOVE;
	input.mi.mouseData = 0;
	input.mi.dwExtraInfo = NULL;
	int num = SendInput(1, &input, sizeof(input));
	if (num > 0) {
		// success
		return 0;
	}
	else {
		int error = GetLastError();
		wprintf(L"mosue_move() error: %d\n", error);
		return __LINE__;
	}

}

int mouse_to(int x, int y)
{
	INPUT input;
	input.mi.time = 0;
	input.mi.dx = x;
	input.mi.dy = y;
	input.mi.dwFlags = MOUSEEVENTF_ABSOLUTE;
	input.mi.mouseData = 0;
	input.mi.dwExtraInfo = NULL;
	int num = SendInput(1, &input, sizeof(input));
	if (num > 0) {
		// success
		return 0;
	}
	else {
		int error = GetLastError();
		wprintf(L"mosue_move() error: %d\n", error);
		return __LINE__;
	}
}

BOOL CALLBACK retrive_window(
	_In_ HWND   hwnd,
	_In_ LPARAM lParam)
{

	return TRUE;
}

int mouse_to_window(std::wstring name)
{
	return 0;
}