#include "tasklist_bgthreads.h"
#include <unistd.h>
/*
 * Class:     academy.linux.Kernel
 * Method:    native_nice
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_netbeans_modules_tasklist_core_Background_native_1nice__
 (JNIEnv *environment, jobject instance)
{
    nice(10);
}

/*JNI function definitions end*/
