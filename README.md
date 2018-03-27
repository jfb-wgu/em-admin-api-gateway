### Synopsis

DM-Admin is a supporting application for Dream Machine.  It provides APIs for user administration and validation of assessment propagation.

### Motivation

User management is provided as a separate application to modularize the Dream Machine.  Changes to user, role and permission features can be deployed without interruption to students and evaluators.

### Installation

Application properties files should be edited to suit each environment, including Cassandra and Oracle credentials.

### Tests

A set of junit/mockito tests are executed with each build.

### Contributors

Please contact jessica.pamdeth@wgu.edu with any questions.

### Swagger Documentation

[ https://dmadmin.dev.wgu.edu/swagger-ui.html ](https://dmadmin.dev.wgu.edu/swagger-ui.html)