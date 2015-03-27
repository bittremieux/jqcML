# Release notes

## jqcML v1.0.3 (27-03-2015)
* ENHANCEMENT: Parameterizable custom qcDB queries
* ENHANCEMENT: General code style improvements, cyclic dependency fixes, code duplication removals, and vulnerability fixes
* BUGFIX: Fix an issue where a qcML with only SetQualities and no RunQualities was not written to the qcDB properly
* BUGFIX: Remove obsolete tests, add additional tests, and optimize unit test execution

## jqcML v1.0.2 (03-Jun-2014) - qcML v0.0.8
* NEW FEATURE: Support qcML `version` attribute
* NEW FEATURE: Support new `MetaDataParameter`s
* NEW FEATURE: Support parameter `description` attribute

## jqcML v1.0.1 (07-Mar-2014)
* BUGFIX: Simplify executing custom queries
* NEW FEATURE: Explicitly set file contents as a binary attachment

## jqcML v1.0 (11-Jan-2014)

* BUGFIX: Better error handling for the command-line interface
* BUGFIX: Requesting an iterator for an invalid qcML file results in an empty iterator
* BUGFIX: Better error handling when a connection to a qcDB couldn't be established
* BUGFIX: Prevent the removal of a `Cv` when it is still referenced
* BUGFIX: Automatically set the `isSet` flag when adding a `QualityAssessment` as a `setQuality`
* BUGFIX: Automatically set the bidirectional relationship between `AttachmentParameter` and `TableAttachment`
* BUGFIX: Fix SQL error when retrieving a `setQuality` from a qcDB
* BUGFIX: Use a custom table for primary key generation instead of the SQLite reserved table `sqlite_sequence`
* NEW FEATURE: On the fly validition against the XML schema during (un)marshalling to/from a qcML file
* NEW FEATURE: JPA logging to log file `jpa.log`
* NEW FEATURE: Retrieve arbitrary data from a qcDB using custom queries


## jqcML v1.0a (18-Dec-2013) - qcML v0.0.7
