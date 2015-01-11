Bo selecta! 
=========

Grails websocket Multi select plugin, a secure way of interacting with multi domains. Messages sent from frontend socket connection to backend socket connection which produces JSON list and sends back only result set to frontend defined divId.

This is a result of security issues faced by many who used [ajaxdependancyselection plugin](https://github.com/vahidhedayati/ajaxdependancyselection). 


 ### Video
 [Video 1 Basic tutorial](https://www.youtube.com/watch?v=wZJl-pPPlOA)
  
 [Video 2 More detailed lookups, defined values, multiple dependencies](https://www.youtube.com/watch?v=qbYvy2Uc_Fc)

[Video 3 no ref/loose dependencies + randomizing username](https://www.youtube.com/watch?v=7FxYJjMLSjQ)

 BoSelecta can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency : (once available) 
```groovy
	compile ":boselect:0.1" 
```

### Testing plugin 

For now you can test it as an line plugin, please refer to [testbo demo project testing this plugin](https://github.com/vahidhedayati/testbo). It has all of the domainClasses and example gsp's in place. The BuildConfig.groovy has this plugin as an inline plugin. So with grails 2.4.4 :


```
git clone https://github.com/vahidhedayati/testbo.git
git clone https://github.com/vahidhedayati/grails-boselecta-plugin.git
cd testbo
grails run-app
```



###Config.groovy overrides:
```groovy

/*
* Very very optional values defaults are as per below _frontend and yes adds localhost:8080/yourappName/ to websocket connection line
*/
boselecta.frontenduser = '_frontend'
boselecta.add.appName = 'yes'


/*
* The 3 items below can be either provided by Config.groovy or remove boselecta and add items to taglib calls 
*/ 


/*
* Websocket hostname by default is localhost:8080
* <bo:connect hostname='something'.... /> 
*/
boselecta.hostname = 'your websockethostname'

/*
* Websocket _socketConnect by default is {/plugin/views}/boselecta/_socketConnect.gsp
*<bo:connect socketConnectTemplate='something'.... /> 
*/
boselecta.socketConnectTemplate = '/path/to/process/template'
 
 /*
 * Websocket socketProcessTemplate by default is {/plugin/views}/boselecta/_socketProcess.gsp
 *<bo:selecta socketProcessTemplate='something'.... /> 
 */
boselecta.socketProcessTemplate = '/path/to/process/template'


```


##### grails Bo Select Plugin usage:
After plugin installtion we have on a gsp an initial backend  connection called boselect:connect, followed by boselect:selectPrimary. initiall connnection sends a connection message - nothing useful the rest is done by taglib call below.

[Example 1: Connector / selectPrimary into default g:select box. (found in above testbo project)](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index.gsp)

[Example 2:  Connector / primary / into Secondary into g:select:  (found in above testbo project)](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index2.gsp)

[Example 3: Defined pre selected values across multiple objects + randomized user within gsp](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues.gsp)

[Example 4: Defined pre selected values across less multiple objects + randomized user within controller](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues2.gsp)

[Example 5: Defined pre selected values same as example3 but with JSON return object](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues3.gsp)

[Example 6: Multiple relationship per domainClass example i.e. object1 has many upto 18th relations with object2 3 4..18](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multidomainexample.gsp)

[Example 7: applicable to all methods - reuse of the taglib multiple times on the same page](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multimultidomainexample.gsp)

[Example 8: No reference or loose dependency relation between a secondary called object and the next domainClass](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/noref.gsp)

[Example 9: No reference relationshiip with secondary object that then returns back into a normal relationship object](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefselectionext.gsp)

[Example 10: Primary object with a No reference relationshiip ](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefprimary.gsp)


##### What is frontend / backend websocket connections ?
Ok apologies since its probably my bad description of the actual process.

By front end I mean a typical web page connection to a websocket session. So this is where the <boselecta:connect tag lib does two websocket tasks

1. It triggers a client connection (described as backend). The client connection is a call to [BoSelectaClientEndpoint.groovy](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/src/groovy/grails/plugin/boselecta/BoSelectaClientEndpoint.groovy). This initiates an internal websocket connection that behaves like a typical client and logs in the user as the user you have defined within your connect tag lib definition. So this is now your backend connected and awaiting response from front end described in next step.

2. Right at the end of the taglib it loads in [_socketConnect.gsp](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/grails-app/views/boselecta/_socketConnect.gsp). This is now your frontend user and logs in as user_frontend unless you override it to be something else. (its all internal so need to touch this to be honest).
None of the users visibily interact on the web interface. The only thing that gets sent is the [_socketProcess.gsp](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/grails-app/views/boselecta/_socketProcess.gsp]). Which gets called in each time you call bo:selecta

With this in place when you update a value on your front end,the backend has a map which  is bound to each users session. So front end sends changedValue + divId to update. Backend knows internally what domains etc it was and it triggers the AutoCompleteService, finally the results is returned with the divId and what the json result set that it fills in the blank from=[] within select tag.

