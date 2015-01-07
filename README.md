Bo selecta! 
=========

Grails websocket Multi select plugin, a secure way of interacting with multi domains. Messages sent from frontend socket connection to backend socket connection which produces JSON list and sends back only result set to frontend.

This is a result of insecurity faced by many who used ajaxdependancyselection plugin.


 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency :
```groovy
	compile ":boselect:0.1" 
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


Example 1: Connector / selectPrimary into default g:select box.
```
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

Example 2:  Connector / primary / into Secondary into g:select:

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


