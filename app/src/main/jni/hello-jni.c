/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>

#include "FANN/floatfann.h"
#include "FANN/fann.h"

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_example_hellojni_MLE_stringFromJNI( JNIEnv* env,
                                                  jobject thiz,
                                                  jfloat a,
                                                  jfloat b,
                                                  jfloat c,
                                                  jfloat d,
                                                  jfloat e,
                                                  jfloat f,
                                                  jfloat g)

{
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__x86_64__)
   #define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
   #define ABI "mips64"
#elif defined(__mips__)
   #define ABI "mips"
#elif defined(__aarch64__)
   #define ABI "arm64-v8a"
#else
   #define ABI "unknown"
#endif

	const unsigned int num_input = 7;
		const unsigned int num_output = 5;
		const unsigned int num_layers = 4;
		const unsigned int num_neurons_hidden = 9;
		const float desired_error = (const float) 0.001;
		const unsigned int max_epochs = 500000;
		const unsigned int epochs_between_reports = 1000;

		struct fann *ann = fann_create_standard(num_layers, num_input, num_neurons_hidden,6, num_output);

	fann_set_activation_function_hidden(ann, FANN_SIGMOID_SYMMETRIC);
	fann_set_activation_function_output(ann, FANN_SIGMOID_SYMMETRIC);

	// train

	fann_train_on_file(ann, "/sdcard/inputNN.txt", max_epochs,
		epochs_between_reports, desired_error);

	fann_save(ann, "/sdcard/test/context.net");

	fann_destroy(ann);


	// run
    fann_type *calc_out;
    fann_type input[8];

    struct fann *ann2 = fann_create_from_file("/sdcard/test/context.net");

    if (ann2 == NULL) {
    	;//__android_log_print(ANDROID_LOG_DEBUG, "error opening file");
    }
        input[0] = a;
    	input[1] = b;
        input[2] = c;
        input[3] = d;
        input[4] = e;
    	input[5] = f;
    	input[6] = g;


    calc_out = fann_run(ann2, input);
int i=0;
for (i=0;i<5;i++){
    __android_log_print(ANDROID_LOG_DEBUG, "output tag", " the result is: %f", calc_out [i] );
       // calc_out++;
}

	char res[100];
	sprintf(res," %f,%f,%f,%f,%f", calc_out[0], calc_out[1], calc_out[2], calc_out[3], calc_out[4]);
	//sprintf(res,"%f, %f, %f", a,b,c);


    //printf("xor test (%f,%f) -> %f\n", input[0], input[1], calc_out[0]);
  //  __android_log_print(ANDROID_LOG_DEBUG, "output tag", " the result is: %f", calc_out [i] );
    fann_destroy(ann2);
    return (*env)->NewStringUTF(env, res);
}



