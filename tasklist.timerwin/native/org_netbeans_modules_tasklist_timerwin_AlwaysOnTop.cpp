#define _WIN32_WINNT 0x500

#include <assert.h>
#include "windows.h"
#include "jawt.h"
#include "jawt_md.h"
#include "jni.h"
#include "org_netbeans_modules_tasklist_timerwin_AlwaysOnTop.h"

JNIEXPORT jboolean JNICALL Java_org_netbeans_modules_tasklist_timerwin_AlwaysOnTop_setAlwaysOnTopWin32
  (JNIEnv * env, jclass clazz, jobject dialog) {
    jboolean jres = JNI_FALSE;

    // Get the AWT
    JAWT awt;
    awt.version = JAWT_VERSION_1_3;
    jboolean result = JAWT_GetAWT(env, &awt);
    if (result != JNI_FALSE) {
        // Get the drawing surface
        JAWT_DrawingSurface* ds = awt.GetDrawingSurface(env, dialog);
        if (ds != NULL) {
            // Lock the drawing surface
            jint lock = ds->Lock(ds);
            if ((lock & JAWT_LOCK_ERROR) == 0) {
                // Get the drawing surface info
                JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo(ds);

                // Get the platform-specific drawing info
                JAWT_Win32DrawingSurfaceInfo* dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;

                HWND hwnd = dsi_win->hwnd;
                if (SetWindowPos(hwnd, HWND_TOPMOST, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE))
                    jres = JNI_TRUE;

                // Free the drawing surface info
                ds->FreeDrawingSurfaceInfo(dsi);

                // Unlock the drawing surface
                ds->Unlock(ds);
            }

            // Free the drawing surface
            awt.FreeDrawingSurface(ds);
        }
    }
    return jres;
}
