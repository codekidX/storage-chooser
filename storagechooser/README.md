Storage Chooser
===================


A simple and carefully implemented library for users to switch their storage between *'Internal'* or *'External*' storage using STATIC path. You can just add it to your Settings Preference to let user switch between Internal/External storages. It takes care of all the heavy Environment changes like 

 - checking if External Storage is present or not
 - checking if STATIC path dir is present or not
 - **TODO** properly implement a memory threshold

and provides a clean and neat library to switch between storages.

----------

Installation
-------------

Add this to your root build.gradle file under repositories:

    allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}

Add this to your app level build.gradle as dependency:

    com.gitlab.codekidX:storage-chooser-library:b0.1

----------
Notes
-------------

Before you implement this library here are some notes that you can follow to avoid errors.

> **Confirm:**

> - You have asked for **Runtime Permission** from the user in the past to avoid read errors.
> - Permissions with "READ_EXTERNAL_STORAGE" and "WRITE_EXTERNAL_STORAGE" is enough for this library to work.
> - This library follows same color scheme as that of the parent app to maintain the aesthetic of the parent app.
> - Header color =>  @colorPrimary
> - Memory Bar color => @colorAccent
> - Memory available text => @colorPrimaryDark

----------


### Implementation

Nothing fancy to do here, it's straightforward and uses a simple Builder to make everything work smoothly.

```
// a common path flow which does not change whether user chose internal or external storage
private static final String STATIC_PATH = "/Downloads/CodekidLabs";
// pass your sharedPreference to actionSave() in builder
private SharedPreference yourPreference;
// with key
private static final String DOWNLOAD_DIR_KEY = "my_download_dir";

// Initialize Builder

StorageChooserBuilder.Builder builder = new StorageChooserBuilder.Builder()
.withActivity(MainActivity.this)
.withFragmentManager(getSupportFragmentManager())
.withMemoryBar(true)
.actionSave(yourPreference, DOWNLOAD_DIR_KEY) // read more about this in the documantation 'Wiki' of library repo
.withPredefinedPath(STATIC_PATH)
.build();

// Show dialog whenever you want by

builder.show();
```


> **What is STATIC_PATH ?:** 
> 
> In the above example of implementation STATIC_PATH is "/Downloads/CodekidLabs". Now when user selects Internal Storage from **StorageChooserDialog** the path will return "/storage/emulated/0/Downloads/CodekidLabs" which will be the directory of operations for your app.

> Same goes if user user chooses External Storage.




### Support Storage Chooser

This is a community based project so help by fixing bugs and adding your ideas to it by clicking **Create pull request**