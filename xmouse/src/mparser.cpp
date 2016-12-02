#include <iostream>
#include "mparser.h"
#include "mfuncs.h"

//!
//! How to use it ?
//! xm [action] [parameter ...]
//! xm m x y [l/r/m]- move mouse with the offset (x,y) with the optional event [l/r/m]
//! xm to x y [l/r/m] - move mouse to the position (x,y)
//! xm w name [l/r/m] - move mouse to the center of window *name* 

void print_usage(const wchar_t* xm)
{
	wprintf(L"%s [m|to|w] [parameter ...] [l|r|m]\n", xm);
	wprintf(L"%s m 100 100 l - move mouse with the offset 100,100 and click the left button\n", xm);
	wprintf(L"%s to 100 100 l - move mouse to position 100,100 and click the left button\n", xm);
	wprintf(L"%s w *notepad* - move mouse to the center of the window that including the string 'notepad'\n", xm);
}


int m_exec(wchar_t* app, int param_count, wchar_t* param[]) 
{
	if (param_count <= 0) {
		return 0;
	} 

	bool valid = false;
	std::wstring action ;
	long x, y;
	std::wstring win_name;
	std::wstring btn_event;
	
	for (int i = 0; i < param_count; i++) {
		if (i == 1) {
			action = param[0];
		}
		else {
			if (action == L"m"
				|| action == L"to"
				) {
				wchar_t* end;
				x = std::wcstol(param[1], &end, 10);
				y = std::wcstol(param[2], &end, 10);
				if (param_count == 4) {
					btn_event = param[3];
				}
				break;
			}
			else if (action == L"w") {
				win_name = param[1];
				if (param_count == 3) {
					btn_event = param[2];
				}
				break;
			}
		}

	}

	if (action== L"m") {
		int ret = mouse_move(x, y);
		mouse_event(btn_event);
	} 
	else if (action == L"to") {
		int ret = mouse_move(x, y);
		mouse_event(btn_event);
	}
	else if (action == L"w") {
		int ret = mouse_to_window(win_name);
		mouse_event(btn_event);
	}
	else {
		print_usage(app);
	}

	
}