#include <stdio.h>
#include <windows.h>
#include <synchapi.h>
#include <tchar.h>

int main(int argc, char **argv)
{
    int value = 5;

    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    if (!CreateProcess(
            NULL,
            argv[0],
            NULL,
            NULL,
            FALSE,
            0,
            NULL,
            NULL,
            &si,
            &pi))
    {
        fprintf(stderr, "Error forking process (%d).\n", GetLastError());
        exit(-1);
    }

    if (pi.hProcess != NULL)
    {
        value += 15;
        printf("Child process value: %d\n", value);
        ExitProcess(0);
    }

    WaitForSingleObject(pi.hProcess, INFINITE);
    printf("Parent process value: %d\n", value);

    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    exit(0);
}