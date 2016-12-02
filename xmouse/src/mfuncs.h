#ifndef __mutils__
#define __mutils__

#include <iostream>


int mouse_event(std::wstring ev);

int mouse_move(int x, int y);
int mouse_to(int x, int y);

int mouse_to_window(std::wstring name);

#endif
