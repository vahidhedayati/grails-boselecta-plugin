Bo selecta! 
=========

Grails websocket Multi select plugin, a secure way of interacting with multi domains. Messages sent from frontend socket connection to backend socket connection which produces JSON list and sends back only result set to frontend defined divId.

This is a result of insecurity faced by many who used [ajaxdependancyselection plugin](https://github.com/vahidhedayati/ajaxdependancyselection). 


 


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
* <boselect:connect hostname='something'.... /> 
*/
boselecta.hostname = 'your websockethostname'

/*
* Websocket _socketConnect by default is {/plugin/views}/boselecta/_socketConnect.gsp
*<boselect:connect socketConnectTemplate='something'.... /> 
*/
boselecta.socketConnectTemplate = '/path/to/process/template'
 
 /*
 * Websocket socketProcessTemplate by default is {/plugin/views}/boselecta/_socketProcess.gsp
 *<boselect:selectPrimary socketProcessTemplate='something'.... /> 
 */
boselecta.socketProcessTemplate = '/path/to/process/template'


```


##### grails Bo Select Plugin usage:
After plugin installtion we have on a gsp an initial backend  connection called boselect:connect, followed by boselect:selectPrimary. initiall connnection sends a connection message - nothing useful the rest is done by taglib call below.


Example 1: Connector / selectPrimary into default g:select box. (found in above testbo project)
```gsp
<boselecta:connect
user="randomUser"
job="job1"
message="Woot we are connected"
 />

  
<boselecta:selectPrimary id="MyCountry1" name="MyCountry1"
job= "job1"
user="randomUser"
domain='ajaxdependancyselectexample.MyCountry'
searchField='countryName'
collectField='id'
domain2='ajaxdependancyselectexample.MyCity'
bindid="mycountry.id"
searchField2='cityName'
collectField2='id'
noSelection="['': 'Please choose Country']"
setId="MyCity1"
value='1'
secondaryValue='2'
/>


<g:select name="MyCity1" id="MyCity1" optionKey="id" optionValue="cityName" from="[]" required="required" noSelection="['': 'Please choose Country']" />
<input type=submit value=go> 

					
```

Example 2:  Connector / primary / into Secondary into g:select:  (found in above testbo project)

```gsp
<boselecta:connect
user="randomUser2"
job="job2"
message="Woot we are connected"
 />

 <form method=post action=example5>
    
<boselecta:selectPrimary id="MyContinent2" name="MyContinent2"
job= "job2"
user="randomUser2"
    domain='ajaxdependancyselectexample.MyContinent'
    searchField='continentName'
    collectField='id'
    
    domain2='ajaxdependancyselectexample.MyCountry'
    bindid="mycontinent.id"
    searchField2='countryName'
    appendValue=''
    appendName='Updated'
    collectField2='id'

    hidden="hiddenNew"
    noSelection="['': 'Please choose Continent']" 
    setId="MyCountry11"
    />



<boselecta:selectSecondary id="MyCountry11" name="MyCountry11"
job= "job2"
user="randomUser2"
	domain2='ajaxdependancyselectexample.MyCity'
    bindid="mycountry.id"
    searchField2='cityName'
    collectField2='id'
    
    
     appendValue=''
     appendName='Updated'
    
    
    noSelection="['': 'Please choose Continent']" 
    setId="MyCity11"
    />





    <boselecta:selectSecondary name="MyCity11" id="MyCity11"  
    job= "job2"
	user="randomUser2"
    optionKey="id" optionValue="name"
    
    
    domain2='ajaxdependancyselectexample.MyShops'
    bindid="mycity.id"
    searchField2='shopName'
    collectField2='id'
    appendValue=''
    appendName='Updated'
   
    
    setId="MyShop12"
	noSelection="['': 'Please choose Country 1111']" 
	/>



    <g:select name="MyShop12" id="MyShop12"  
    optionKey="id" optionValue="shopName" 
    from="[]" required="required" noSelection="['': 'Please choose City']" 
    />
    

    <input type=submit value=go>  
    </form>

```


##### What the heck is frontend / backend websocket connections ?
Ok apologies since its probably my bad description of the actual process.

By front end I mean a typical web page connection to a websocket session. So this is where the <boselecta:connect tag lib does two websocket tasks

1. It triggers a client connection (described as backend). The client connection is a call to [BoSelectaClientEndpoint.groovy](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/src/groovy/grails/plugin/boselecta/BoSelectaClientEndpoint.groovy). This initiates an internal websocket connection that behaves like a typical client and logs in the user as the user you have defined within your connect tag lib definition. So this is now your backend connected and awaiting response from front end described in next step.

2. Right at the end of the taglib it loads in [_socketConnect.gsp](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/grails-app/views/boselecta/_socketConnect.gsp). This is now your frontend user and logs in as user_frontend unless you override it to be something else. (its all internal so need to touch this to be honest).
None of the users visibily interact on the web interface. The only thing that gets sent is the [_socketProcess.gsp](https://github.com/vahidhedayati/grails-boselecta-plugin/blob/master/grails-app/views/boselecta/_socketProcess.gsp]). Which gets called in with I think selectPrimary only once.

With this in place when you update a value on your front end,the backend has a map which at the moment gets reset within connect call after it has connected. So front end sends changedValue + divId to update. Backend knows internally what domains etc it was and it triggers the AutoCompleteService, finally the results is returned with the divId and what the json result set that it fills in the blank from=[] within select tag.

