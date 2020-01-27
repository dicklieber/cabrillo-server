# cabrillo-server
HTTP server for cabrillo data

To build zip that can run with any JVM 


- sbt universal:packageBin
- copy zip from target/universal/wfdcheck-XXXX.zip to target run location.
- unzip
- create wfdvar with wfd.conf (copy from conf/wfd.example.conf) at same level as wfdcheck-XXXX.zip containing customized values.
    Note that the wfd.conf can contain any values from conf/application.conf. You should not edit conf/application.conf as it get overwritten with each new release.
    
- start in unzipped directory:
`    $ nohup bin/wfdcheck &
`    