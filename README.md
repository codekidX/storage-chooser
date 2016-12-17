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

    com.github.codekidX:storage-chooser-library:0.1.12

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

### Storage Chooser Implementation

Nothing fancy to do here, it's straightforward and uses a simple Builder to make everything work smoothly.

```
// a common path flow which does not change whether user chose internal or external storage
private static final String STATIC_PATH = "/Downloads/CodekidLabs";
private SharedPreference yourPreference;

// ---------------------------------------------

// Initialize Builder
StorageChooserBuilder.Builder builder = new StorageChooserBuilder.Builder()
.withActivity(MainActivity.this)
.withFragmentManager(getSupportFragmentManager())
.withMemoryBar(true) // shows a nice visual representation of memory available 
.actionSave(true) // saves the path of internal/external storage to yourPreference
.withPreference(yourPreference) // needed if action save is true
.withPredefinedPath(STATIC_PATH)
.build();

// Show dialog whenever you want by
builder.show();
```


> **What is STATIC_PATH ?:** 
> 
> In the above example of implementation STATIC_PATH is "/Downloads/CodekidLabs". Now when user selects Internal Storage from **StorageChooserDialog** the path will return "/storage/emulated/0/Downloads/CodekidLabs" which will be the directory of operations for your app.

> Same goes if user chooses External Storage. [/storage/**Gibberish SDcard Name**/ + STATIC_PATH ]


LICENSE
-------------

This project is licensed with a modified LGPL-3.0, section 4d and 4e are removed. The reason is that requiring a replaceable linked libraries within APKs while retaining functionality is hard to comply with.

In practice, you can use this library as-is, with a notification of it being used. If you make any changes, you are required to publish your changes under a copyleft license.

Read more about this in wiki and why these changes were made.



### Support Storage Chooser

This is a community based project so help fixing bugs by adding your fixes to it by clicking **Create pull request**