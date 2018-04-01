# Getting started with applitools #

Includes a JUnit rule for initializing the Applitools SDK, extracting the Applitools test name for each test, and convenience methods for performing validations.

The Rule encapsulates everything needed to integrate with Applitools. All is needed is to import the Rule wit your functional tests and perform validations as needed. 
                                
Also provided a simple demo that performs visual validations with Applitools.

# Building #

`mvn clean install`

# Running #

mvn test -DapplitoolsKey=your-applitools-key-here

_optional_ parameters:
* -DApplicationName (defaults to 'Applitools Test App')
* -DbranchName 
* -DparentBranchName (defaults to master) 
* For branchName and parentBranchName see http://support.applitools.com/customer/en/portal/articles/2142886-using-multiple-branches- for details
on how these work with validations.

