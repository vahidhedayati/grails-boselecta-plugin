Bo selecta! 
=========

Grails plugin that uses default WebSocket technology to interact with your domainClasses and produce dependent form / options that depend on one another. The format to define select functionality / auto complete are identical. Auto complete requires additional boolean values to be passed to make it auto complete.


##### Select box dependencies data comes from WebSocket Client running within your application (as part of the plugin)
##### AutoComplete input areas use HTML5 dataList, data for dataList provided by backend WebSocket Connection.
 

#### [Walkthrough Video - youtube, Everything you need to know about boselecta](https://www.youtube.com/watch?v=GGB7mtB9nWM)
  
  
![diagram](https://raw.githubusercontent.com/vahidhedayati/grails-boselecta-plugin/grails2/documentation/boselecta.png)

[More information to help with diagram](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/More-information)


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


######[test site for grails 2](https://github.com/vahidhedayati/testbo)

######[test site for grails 3](https://github.com/vahidhedayati/testbo3)


###Latest updates (after video published):
A further lock down has been introduced, with the assumption that most people will be looking up domain objects with single dependencies.
This means, typically I expect an end user to map from continent to country to city and so on. So its one to one relationship between each object. For this reason the default call now only supports one relation, so:
```gsp 
<bo:selecta
...  
domain="package.domainClass1"
..
domain2="package.domainClass2"
..
/>
```
domain will be the primary object itself (so a listing of that object, domain2 is the entity that it has that relationship with.


If you attempt to exceed and use domain3 and on. The plugin will not work. You can from now use selecta2 for those relations with depth 
```gsp 
<bo:selecta2
domainDepth="4"
...  
domain="package.domainClass1"
..
domain2="package.domainClass2"
..
domain3="package.domainClass3"
..
domain4="package.domainClass4"
..
/>
```
 

#### [Testing plugin](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Testing-plugin) 

#### [Config.groovy overrides](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Config.groovy)

#### [domainDepth explained] (https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/domainDepth-explained)

##### [grails BoSelecta Plugin usage](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/grails-BoSelecta-Plugin-usage)

#### [JSON Formatting](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/JSON-Formatting).

##### [What is front-end / back-end WebSocket connections](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/What-is-front-end---back-end-WebSocket-connections) ?


##Examples:
##### Basic example:


[Example 1: Connector / selectPrimary into default g:select box. (found in above testbo project)](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index.gsp)

1. Initiate a master socket connection, <bo:connect handles all the information flowing back from backend websocket. So this is your main and only connection required on any given page. Take a note of the user and job names, they must match all the calls on this page.

Both the two code blocks below in one gsp:

```gsp
<bo:connect user="myuser" job="job1" />
```
Now that we have configured our master listener, lets configure one connection with a relation that is returned to a normal blank select box
```gsp
<form  action="example5">

<bo:selecta 
id="MyCountry1" name="MyCountry1" job= "job1" user="myuser" domainDepth="0" formatting="JSON"
domain='ajaxdependancyselectexample.MyCountry' searchField='countryName' collectField='id'
domain2='ajaxdependancyselectexample.MyCity' bindid="mycountry.id" searchField2='cityName' collectField2='id'
appendValue='' appendName='Updated' noSelection="['': 'Please choose Continent']" setId="MyCity1" />

<g:select name="MyCity1" id="MyCity1" optionKey="id" optionValue="cityName" from="[]" required="required" noSelection="['': 'Please choose Country']" />
<input type=submit value=go> 
</form>
```
If you have used ajaxdependancy selection, a lot of the above input will look familiar.
You are now giving the backend the ID of your primary selection MyCountry1, you are telling it what domain is which makes this a primary call and then returns MyCountry.countryName and MyCountry.id to the primary box, you are then setting the setId as MyCity1 and saying domain2 is MyCity and to search/collect cityName/id.



[Example 2:  Connector / primary / into Secondary into g:select:  (found in above testbo project)](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index2.gsp)


Identical to above, but in this example we iterate over the relations using <bo:select passing domain2 object which be the next object so on the secondary objects there is no domain= value defined.
```gsp
<bo:connect user="randomUser2" job="job2" />

<g:form name="myForm" action="example5">    
<bo:selecta 
  id="MyContinent2" name="MyContinent2" setId="MyCountry11"
  job= "job2" user="randomUser2" domainDepth="0"    
  domain='ajaxdependancyselectexample.MyContinent' searchField='continentName' collectField='id'
  domain2='ajaxdependancyselectexample.MyCountry' bindid="mycontinent.id" searchField2='countryName'
  appendValue='' appendName='Updated' collectField2='id' noSelection="['': 'Please choose Continent']" />

 <bo:selecta id="MyCountry11" name="MyCountry11"
 job= "job2" user="randomUser2" domainDepth="0" setId="MyCity11"
 domain2='ajaxdependancyselectexample.MyCity' bindid="mycountry.id" searchField2='cityName' collectField2='id'
 formatting="JSON" appendValue=''  appendName='Updated' noSelection="['': 'Please choose Continent']" />

 <bo:selecta 
 name="MyCity11" id="MyCity11" job= "job2" user="randomUser2" domainDepth="0" setId="MyShop12" 
 domain2='ajaxdependancyselectexample.MyShops' bindid="mycity.id" searchField2='shopName' collectField2='id'
 appendValue='' appendName='Updated' formatting="JSON" noSelection="['': 'Please choose Country 1111']"/>

 <g:select 
 name="MyShop12" id="MyShop12" optionKey="id" optionValue="shopName" 
 from="[]" required="required" noSelection="['': 'Please choose City']" 
 />
 <g:submitButton name="submit"/>  
 </g:form>
```
    





[Example 3: Defined pre selected values across multiple objects + randomized user within gsp](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues.gsp)

[Example 4: Defined pre selected values across less multiple objects + randomized user within controller](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues2.gsp)

[Example 5: Defined pre selected values same as example3 but with JSON return object](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues3.gsp)

[Example 6: Multiple relationship per domainClass example i.e. object1 has many up to Xth relations with object2 3 4..XXX](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multidomainexample.gsp)

[Example 7: applicable to all methods - reuse of the taglib multiple times on the same page](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multimultidomainexample.gsp)

[Example 8: No reference or loose dependency relation between a secondary called object and the next domainClass](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/noref.gsp)

[Example 9: No reference relationship with secondary object that then returns back into a normal relationship object](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefselectionext.gsp)

[Example 10: Primary object with a No reference relationship ](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefprimary.gsp)

[Example 11: Auto complete a few hasMany to a noref relation](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoComplete.gsp)
Using the same Tag to acheive [autoComplete](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoComplete.gsp)
In this example (not on actual link, the user is being defined as a variable and reused - this now makes it a dynamic user but same on that one page.

The difference with this call and select is that as you can see on the first example it has autoComplete="true" and if this is the primary object then also autoCompletePrimary="true", if second object it just needs the first tag. Follow example below.. There are two additional fields hiddenField and jsonField. In autoComplete, the results are returned from html5 dataList. I have added data-value and parse that into the hidden field for jsonField and hiddenField gets set to the or collectField of the value selected. The rest is like above.
```gsp
<% def myuser = bo.randomizeUser('user': 'random1') %>

<bo:connect user="${myuser}" job="job3"/>

<form action="example5">

<bo:selecta 
    autoComplete="true" autoCompletePrimary="true" 
    job="job3" user="${myuser}" id="MyContinent2" name="MyContinent2" setId="MyCountry11" 
    hiddenField="VahidHidden_" jsonField="VahidJSON_" formatting="JSON"
    domain='ajaxdependancyselectexample.MyContinent' searchField='continentName' collectField='id'
    domain2='ajaxdependancyselectexample.MyCountry' bindid="mycontinent.id" 
    searchField2='countryName' collectField2='id' />
	
    <bo:selecta 
	autoComplete="true" 
	job="job3" user="${myuser}" id="MyCountry11" name="MyCountry11" 
	hiddenField="NextHidden_" jsonField="NextJSON_" formatting="JSON"
	domain2='ajaxdependancyselectexample.MyCity' bindid="mycountry.id"
	searchField2='cityName' collectField2='id' setId="MyCity11" />
	
    <bo:selecta
    	autoComplete="true"
	job="job3" user="${myuser}" formatting="JSON"
	name="MyCity11" id="MyCity11" 
	hiddenField="myHidden_" jsonField="myJSON_"
	domain2='ajaxdependancyselectexample.MyShops' searchField2='shopName' collectField2='id' 
	bindid="mycity.id"  setId="secondarySearch4" 
	/>
	
    <bo:selecta 
    	autoComplete="true"
    	job= "job121" user="${myuser }"	formatting="JSON" id="secondarySearch4" name="NAMEOFBorough"  
	hiddenField="myHidden_" jsonField="myJSON_"
	domain2='ajaxdependancyselectexample.MyBorough' searchField='actualName' collectField='id' 
	bindid='myborough' value=''
    />
    <input type=submit value=go>
</form>
```
[Example 12: Select To AutoComplete](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/selectautoComplete.gsp) 


[Example 13:AutoComplete To Select](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoCompleteToSelect.gsp) 

[Example 14: AutoComplete To Select from select to another select](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/selectautoComplete2.gsp) 


