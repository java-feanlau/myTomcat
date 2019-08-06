# myTomcat
tomcat源码debug搭建

本地搭建时，jvm参数配置:
要配置成一行，此处为了好看，分行写:
```java
-Dcatalina.home=F:/github_code/Mine/myTomcat/catalina-home 
-Dcatalina.base=F:/github_code/Mine/myTomcat/catalina-home 
-Djava.endorsed.dirs=F:/github_code/Mine/myTomcat/catalina-home/endorsed 
-Djava.io.tmpdir=F:/github_code/Mine/myTomcat/catalina-home/temp
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager 
-Djava.util.logging.config.file=F:/github_code/Mine/myTomcat/catalina-home/conf/logging.properties
```

参考博客
[https://my.oschina.net/u/3737136/blog/2992813](https://my.oschina.net/u/3737136/blog/2992813)
[https://juejin.im/post/5b2b5e90e51d4558cc35b89b](https://juejin.im/post/5b2b5e90e51d4558cc35b89b)
