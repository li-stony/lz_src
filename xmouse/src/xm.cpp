#include <Windows.h>
#include "mparser.h"

int wmain(int argc, wchar_t* argv[])
{
	m_exec(argv[0], argc - 1, argv + 1);
	return 0;
}