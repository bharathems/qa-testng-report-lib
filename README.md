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