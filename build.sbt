name := """PDFInvoiceToExcel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq("org.apache.pdfbox" % "xmpbox" % "1.8.13",
							"org.apache.pdfbox" % "preflight" % "1.8.13",
							"org.apache.pdfbox" % "preflight-app" % "1.8.13",
							"org.apache.pdfbox" % "pdfbox" % "1.8.13",
							"org.apache.pdfbox" % "pdfbox-app" % "1.8.13",
							"org.apache.pdfbox" % "jempbox" % "1.8.13",
							"org.apache.pdfbox" % "fontbox" % "1.8.13",
							"org.apache.xmlbeans" % "xmlbeans" % "2.3.0",
							"org.apache.poi" % "poi" % "3.13",
							"org.apache.poi" % "poi-ooxml" % "3.13",
							"org.apache.poi" % "poi-ooxml-schemas" % "3.13",
							"dom4j" % "dom4j" % "1.6.1",
							"commons-logging" % "commons-logging" % "1.2",
							"org.postgresql" % "postgresql" % "9.3-1100-jdbc41"
							
							
							
							)
							
							