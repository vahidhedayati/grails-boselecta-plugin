Bo selecta! 
=========

Grails plugin that uses default WebSocket technology to interact with your domainClasses and produce dependent form / options that depend on one another. The format to define select functionality / auto complete are identical. Auto complete requires additional boolean values to be passed to make it auto complete.


##### Select box dependencies data comes from WebSocket Client running within your application (as part of the plugin)
##### AutoComplete input areas use HTML5 dataList, data for dataList provided by backend WebSocket Connection.
 

 ### [Video Everything you need to know ](https://www.youtube.com/watch?v=CFPe9pSFJ3g)
  
![diagram](https://raw.githubusercontent.com/vahidhedayati/grails-boselecta-plugin/grails2/documentation/boselecta.png)


 BoSelecta can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+

###### Dependency

Dependency Grails 3:
```groovy
    compile "org.grails.plugins:boselecta:3.0.1"
```
[codebase for grails 3.X](https://github.com/vahidhedayati/grails-boselecta-plugin/)

     
Dependency Grails 2: (not released refer to testing Plugin below)     
```groovy
	compile ":boselecta:0.1" 
```
[codebase for grails 2.X](https://github.com/vahidhedayati/grails-boselecta-plugin/tree/grails2)

###Latest updates (after video published):
A further lock down has been introduced, with the assumption that most people will be looking up domain objects with single dependencies.
This means, typically I expect an end user to map from continent to country to city and so on. So its one to one relationship between each object. For this reason the default call now only supports one relation, so:
```gsp 
<bo:selecta
...  
domain=""
..
domain2=""
..
/>
```
If you attempt to exceed and use domain3 and on. The plugin will not work. You can from now use selecta2 for those relations with depth 
```gsp 
<bo:selecta2
domainDepth="4"
...  
domain=""
..
domain2=""
..
domain3=""
..
domain4=""
..
/>
```
 

#### [Testing plugin](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Testing-plugin) 

#### [Config.groovy overrides](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Config.groovy)

#### [domainDepth explained] (https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/domainDepth-explained)

##### [grails BoSelecta Plugin usage](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/grails-BoSelecta-Plugin-usage)

#### [JSON Formatting](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/JSON-Formatting).

##### [What is front-end / back-end WebSocket connections](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/What-is-front-end---back-end-WebSocket-connections) ?
