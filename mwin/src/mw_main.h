#ifndef __mw_main__
#define __mw_main__

void mw_print_help(wchar_t* app);

// return 
//	0 - ok;
//	-1 - invalid params
//	-2 - error
//	others - line number 
int mw_exec(int param_count, wchar_t* params[]);

#endif
