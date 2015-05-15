#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdio.h>
#include <dirent.h>
#include <sys/types.h>
#include <regex.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>

#define DEBUG_TAG "FromJni"

void Java_com_example_ndksetup_MonitorFragment_printLog(JNIEnv * env,
		jobject this, jstring logString) {
	jboolean isCopy;
	const char * szLogString = (*env)->GetStringUTFChars(env, logString,
			&isCopy);
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: %s", szLogString);
	(*env)->ReleaseStringUTFChars(env, logString, szLogString);
}

int Java_com_nikhil_neom_MonitorFragment_fibonacci(JNIEnv * env, jobject this,
		jint value) {
	int sum;
	sum = value + 100;
	return sum;
}
/*
 *
 */
/*
 *
 */
int getinode(char * pid) {
	//open the folder /proc/pid/fd

	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "getinode PID: %s", pid);

	struct dirent *de; // Pointer for directory entry
	//On 32-bit platfroms, 32768 is the maximum value for pid_max there for size of fdpath =5(pid)+4(proc)+2(fd)+3(/'s) =14
	char fdpath[14] = "/proc/";

	char ln_target[2000]; // the soft link target in /proc/pid/fd folder
	int count; // No.of number of bytes placed in ln_target by readlink,-1 = error

	strncat(fdpath, pid, sizeof(fdpath));
	strncat(fdpath, "/fd", sizeof(fdpath));

	DIR *fd = opendir(fdpath);

	if (fd == NULL) // /proc/pid/fd opening failed
			{
		printf("Could not open current directory: %s\n", fdpath);
		return 0;
	}
	// printf("\nOpened current directory: %s\n", fdpath); // Uncomment to DEBUG

	while ((de = readdir(fd)) != NULL) {
		__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: %s", "In getinode-While");

		if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
			continue;

		//find all the link targets
		struct stat sb; //stat structure have the details of the file/link, lstat populate this sb.
		char *linkname;
		char *inode_path;

		ssize_t r; //This data type represent the sizes of blocks that can be read or written in a single operation.

		//printf("%d%d\n", sizeof(fdpath), (int)sizeof(de->d_name) );
		inode_path = (char *) malloc(sizeof(fdpath) + sizeof(de->d_name) + 1);

		strncat(inode_path, fdpath, sizeof(fdpath));
		strncat(inode_path, "/", 1);
		strncat(inode_path, de->d_name, sizeof(de->d_name));

		//stats the file pointed to by path and fills in struct stat
		//If path is a symbolic link, then the link itself is stat-ed not the file that it refers to.
		if (lstat(inode_path, &sb) == -1) {
			//perror("lstat"); // Uncomment to DEBUG
			continue;
			//exit(EXIT_FAILURE);
		}

		linkname = (char *) malloc(sb.st_size + 1);
		if (linkname == NULL) {
			fprintf(stderr, "insufficient memory\n");
			continue;
			//exit(EXIT_FAILURE);
		}

		r = readlink(inode_path, linkname, sb.st_size + 1);

		if (r < 0) {
			//perror("lstat"); // Uncomment to DEBUG
			continue;
			//exit(EXIT_FAILURE);
		}

		/* Won't be a problem as we need the the latest target
		 if (r > sb.st_size) {
		 fprintf(stderr, "symlink increased in size "
		 "between lstat() and readlink()\n");
		 continue;
		 //exit(EXIT_FAILURE);
		 }
		 */

		linkname[sb.st_size] = '\0';

		if (strstr(linkname, "socket:[") != NULL
				|| strstr(linkname, "pipe:[") != NULL) // filter out non inode entries
						{

			printf("'%s' points to '%s'\n", inode_path, linkname);
		}

	}

	closedir(fd);
	return 1;

}

// both this parameters are must
/*"JNIEnv* env", "jobject javaThis" must have
 * return (*env)->NewStringUTF(env, "text from jni"); this is how return works
 */

jstring Java_com_nikhil_neom_MonitorFragment_pidinodepname(JNIEnv* env,
		jobject javaThis) {

	struct dirent *de; // Pointer for directory entry
	DIR *dr = opendir("/proc"); // opendir() returns a pointer of DIR type.
	regex_t regex; //To hold the compled regex
	int regex_compile_status; //0=regx compile failed.
	int regex_match_status; //0=regx match

	if (dr == NULL) // opendir returns NULL if couldn't open directory
			{
		//printf("Could not open current directory");
		//return 0;
	}

	while ((de = readdir(dr)) != NULL) {
		regex_compile_status = regcomp(&regex, "[0-9]", 0);
		if (regex_compile_status) {
			//fprintf(stderr, "Could not compile regex\n");
			exit(1);
		}
		regex_match_status = regexec(&regex, de->d_name, 0, NULL, 0);
		if (!regex_match_status) { // It's the PID directory
			/*__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: %s", szLogString);

			 (*env)->ReleaseStringUTFChars(env, logString, szLogString);*/
			//printf("%s\n", de->d_name);
			/**
			 * Should un comment
			 */
			__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "pidinodepname d_name: %s", de->d_name);

			getinode(de->d_name);
		}
	}

	//return de->d_name;
	closedir(dr);
	//return (*env)->NewStringUTF(env, de->d_name);
	//return 0;
}
