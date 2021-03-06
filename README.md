# A Secure Triple A system
(developed as a requirement for the SCC-363 Module at Lancaster University 2021) 

This is an implementation of a secure Authentication, Authorisation and Accountability (AAA) system to store electronic health records.
This system will be used by many user types such as patients, doctors and administration staff, as well as the regulatory body who ensures operations go smoothly.

## System Features
##### Registration & Authentication
1. Support for signup and login operations 
2. Support for password strength evaluation to prevent weak/common passwords
3. Support for salting and secure password storage using the SHA3 Hashing Algorithm
4. Support for multifactor authentication using OTP E-mail keys
5. Session key and credential negotiation 
6. Strong Password Suggestion
#####  Authorisation and secure data exchange
1. Clearly defined and practical roles, assigned with the appropriate permissions following an access control policy
2. Support for least privilege 
3. Support for defining separation of duties 
4. Management procedures & Access revocation
5. Users can securely access (create, read, update, delete) resources on the system 
##### Accountability, Audit and System logs
1. Ability to log and list auditable events (e.g., logins and other high-value events, both successful and unsuccessful)
2. Accurate logging generated by warnings, errors, and other incidents 
3. Mechanism for backing up logs 
4. Non-Passive Logging features
5. Protection of logs against tampering 
6. Use log information in case of cyber-attacks to accurately ascribed responsibility to users

## System Dependencies
- sqlite-jdbc-3.7.2.jar
- activation-jaf1.1.1.jar
- javax.mail.jar
- 


## How to run
Please run in IntelliJ IDE. 

Import as a project.
Build project, Run "Server" then run "Client".


"Server" must be running otherwise "Client" will close.
