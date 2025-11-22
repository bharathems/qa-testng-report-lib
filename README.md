This project is configured to deploy artifacts to a Nexus repository. T
o deploy the artifacts, use the following Maven command, replacing `your-username` and `your-password` with your Nexus credentials:

mvn deploy -Dnexus.username=your-username -Dnexus.password=your-password

Build and install the library locally:  
Go to the testng-report-lib project directory.
Run:
 >mvn clean install
This will install the JAR to your local Maven repository.

If you want to use it from a remote repository:  
Deploy the library to your Nexus repository:
 >mvn clean deploy
Make sure your pom.xml in testng-report-lib has the correct <distributionManagement> section for Nexus.

Force Maven to update:  
 Run your build with:
 >mvn clean install -U

The -U flag forces Maven to update snapshots and releases.



To use this library in another Maven project:

1. Deploy the library to your Nexus repository (if not already done): 
   mvn clean deploy -Dnexus.username=your-username -Dnexus.password=your-password
2. Add the dependency to your target project's pom.xml:
<dependency>
    <groupId>com.experian.reportservice.testng</groupId>
    <artifactId>qa-testng-report-lib</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
3. Ensure your target project is configured to access the Nexus repository where the library is deployed.
   <repositories>
   <repository>
   <id>nexus</id>
   <url>http://nexus.mstargeting.prod.us.experian.eeca/repository/maven-releases/</url>
   </repository>
   </repositories>
4. Run your build to download the library:
   mvn clean install
5. Import and use the library classes in your target project code.
6. Ensure to keep the library updated by redeploying it to Nexus and updating the version in your target project's pom.xml as needed.
7. For any issues, check the Nexus repository access and Maven configuration.
8. Refer to the library documentation for specific usage instructions and examples.
9. Happy coding!