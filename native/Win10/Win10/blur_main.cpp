#include <jni.h>
#include <Windows.h>
#include <string>
#include "com_kieferlam_javafxblur_NativeBlur.h"

std::string target = "";
int targetAccentState = 0;

void SetWindowBlur(HWND hWnd, int accentState)
{
	const HINSTANCE hModule = LoadLibrary(TEXT("user32.dll"));
	if (hModule)
	{
		struct ACCENTPOLICY
		{
			int nAccentState;
			int nFlags;
			int nColor;
			int nAnimationId;
		};
		struct WINCOMPATTRDATA
		{
			int nAttribute;
			PVOID pData;
			ULONG ulDataSize;
		};
		typedef BOOL(WINAPI*pSetWindowCompositionAttribute)(HWND, WINCOMPATTRDATA*);
		const pSetWindowCompositionAttribute SetWindowCompositionAttribute = (pSetWindowCompositionAttribute)GetProcAddress(hModule, "SetWindowCompositionAttribute");
		if (SetWindowCompositionAttribute)
		{
			ACCENTPOLICY policy = { accentState, 0, 0x00FFFFFF, 0 }; // ACCENT_ENABLE_BLURBEHIND=3 / Colour in ARGB
			WINCOMPATTRDATA data = { 19, &policy, sizeof(ACCENTPOLICY) }; // WCA_ACCENT_POLICY=19
			SetWindowCompositionAttribute(hWnd, &data);


		}
		FreeLibrary(hModule);
	}
}
void enableBlur(HWND hWnd) {
	SetWindowBlur(hWnd, targetAccentState);
}

BOOL CALLBACK EnumWindowsProc(HWND hWnd, LPARAM lParam)
{
	char String[255];

	if (!hWnd)
		return TRUE;		// Not a window
	if (!IsWindowVisible(hWnd))
		return TRUE;		// Not visible
	if (!SendMessage(hWnd, WM_GETTEXT, sizeof(String), (LPARAM)String))
		return TRUE;		// No window title

	char pszClassName[64];
	GetClassName(hWnd, pszClassName, 64);
	if (_stricmp(pszClassName, "shell_traywnd") && _stricmp(pszClassName, "progman")) {
		char windowTitle[64];
		GetWindowText(hWnd, windowTitle, 64);
		if (!target.compare(windowTitle)) {
			enableBlur(hWnd);
		}
	}

	return 1;
}


JNIEXPORT void JNICALL Java_com_kieferlam_javafxblur_NativeBlur__1extApplyBlur
(JNIEnv *env, jobject, jstring title, jint accentState) {

	const jchar *nativeString = env->GetStringChars(title, 0);
	targetAccentState = (int)accentState;

	char chars[256];

	for (int i = 0; i < 256; ++i) {
		const jchar c = nativeString[i];
		if (c == 0) break;
		chars[i] = c;
	}

	std::string string(chars);

	target = string;

	EnumWindows(EnumWindowsProc, 0);
}