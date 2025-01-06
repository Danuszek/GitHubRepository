import com.ibm.dbb.build.*
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.metadata.*

//for global variables
import groovy.transform.Field

def sourceDir = "/u/dantec/dbb2.0/source/"

//Get DBB toolkit product info
version = new VersionInfo()
println(" ")
println("Info about this DBB installation")
println("--------------------------------")
println("DBB Version: " + version.getVersion())
println("Build Number: " + version.getBuild())
println("Release Date: " + version.getDate())
println(" ")

println("avant scanHellod")
scanHellod()
println("avant storeDependenciesinDBB")
storeDependenciesInDBB(logicalFile)
println("avant storeBuildResultInDBB")
storeBuildResultInDBB()

println("avant exit")
System.exit(0);


/**********************************************/
def scanHellod(){
/===============/
List<LogicalFile> logicalFiles = new ArrayList<LogicalFile>()

// indicate that copybooks can be found in sub directory copybook with extension cpy
BuildProperties.setFileProperty("scanner.languageHint", "COB", "**/copybooks/*.cpy")

println("scan hellod ")
//scan files
def logicalFile = scanOneFile("/u/dantec/dbb2.0/source/","cobol/hellod.cbl")
logicalFiles.add(logicalFile)

println("scan cpy1 ")
logicalFile = scanOneFile("/u/dantec/dbb2.0/source/","copybooks/copy1.cpy")
logicalFiles.add(logicalFile)

println("scan cpy2 ")
logicalFile = scanOneFile("/u/dantec/dbb2.0/source/","copybooks/copy2.cpy")
logicalFiles.add(logicalFile)

println("overall, "+logicalFiles)
return logicalFiles

}

def scanOneFile(String sourceDir, String filePath){

    println("Scanning file "+filePath)
    def logicalFile = new DependencyScanner().scan(filePath, sourceDir)
    println("Dependencies found, "+logicalFile)

    return logicalFile;
}
/**********************************************/
def storeDependenciesInDBB(LogicalFile logicalFiles){
    //* v2.0.x
    // load the Db2 connection properties
    File db2ConnectionFile = new File("/u/dantec/dbb2.0/properties/db2Connection.conf")
    Properties db2ConnectionProps = new Properties()
    db2ConnectionProps.load(new FileInputStream(db2ConnectionFile))
    println("storeDep 1")
    def client = MetadataStoreFactory.createDb2MetadataStore("BUILDER","v5vLYoss46GfoTn8WVB59A==",db2ConnectionProps)
    println("storeDep 2")
    def collection;
    if (!client.collectionExists("Bharath1"))
       collection=client.createCollection("Bharath1")
//       println("test collection Bharat1 CREATE")
    else
       collection=client.getCollection("Bharath1")
       println("test collection Bharat1 GET")

    collection.addLogicalFile(logicalFiles)
    println("collection ",+collection)
}

def storeBuildResultInDBB(){
    //* v2.0.x
    // load the Db2 connection properties
    println("storeBuildResultInDBB")
    File db2ConnectionFile = new File("/u/dantec/dbb2.0/properties/db2Connection.conf")
    Properties db2ConnectionProps = new Properties()
    db2ConnectionProps.load(new FileInputStream(db2ConnectionFile))

    def client = MetadataStoreFactory.createDb2MetadataStore("BUILDER","v5vLYoss46GfoTn8WVB59A==",db2ConnectionProps)

    def buildResult = client.createBuildResult("Bharath", "build-${new Date()}")

    def buildReport= BuildReportFactory.getBuildReport()
    def jsonOutputFile = new File(outputDir+"/BuildReport.json")
    def buildReportEncoding = "UTF-8"
    buildReport.save(jsonOutputFile, buildReportEncoding)

    // create a default build report html file
    def htmlOutputFile = new File(outputDir+"/BuildReport.html")
    buildReport.generateHTML(htmlOutputFile)
    buildResult.setBuildReport(new FileInputStream(htmlOutputFile))
    buildResult.setBuildReportData(new FileInputStream(jsonOutputFile))
    buildResult.setState(BuildResult.COMPLETE)
    println("storeBuildResultInDBB OUT")
}
